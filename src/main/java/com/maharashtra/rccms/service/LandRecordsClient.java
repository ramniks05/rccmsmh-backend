package com.maharashtra.rccms.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;
import java.util.zip.GZIPInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * Backend proxy for Mahabhumi Land Records APIs.
 *
 * The upstream API returns a base64 encoded payload in the {@code data} field.
 * This client attempts to decode it to JSON and returns the decoded {@link JsonNode}.
 */
@Service
@SuppressWarnings("null")
public class LandRecordsClient {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final String baseUrl;
    private final String bearerToken;
    private final String apiKey;
    private final String secretKey;

    public LandRecordsClient(
            RestTemplate restTemplate,
            ObjectMapper objectMapper,
            @Value("${rccms.land-records.base-url:https://api.mahabhumi.gov.in/api}") String baseUrl,
            @Value("${rccms.land-records.bearer-token:}") String bearerToken,
            @Value("${rccms.land-records.api-key:}") String apiKey,
            @Value("${rccms.land-records.secret-key:}") String secretKey
    ) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
        this.baseUrl = stripTrailingSlash(baseUrl);
        this.bearerToken = bearerToken;
        this.apiKey = apiKey;
        this.secretKey = secretKey;
    }

    public JsonNode postForm(String path, Map<String, String> formFields) {
        String url = baseUrl + normalizePath(path);
        HttpHeaders headers = buildHeaders();

        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        if (formFields != null) {
            formFields.forEach((k, v) -> {
                if (v != null) form.add(k, v);
            });
        }

        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(form, headers);

        try {
            ResponseEntity<String> res = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
            return handleUpstreamResponse(res.getStatusCode().value(), res.getBody());
        } catch (HttpStatusCodeException ex) {
            return handleUpstreamResponse(ex.getStatusCode().value(), ex.getResponseBodyAsString());
        } catch (RestClientException ex) {
            return errorNode(500, "Upstream call failed: " + ex.getMessage());
        }
    }

    private HttpHeaders buildHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.setAccept(java.util.List.of(MediaType.APPLICATION_JSON));

        if (bearerToken != null && !bearerToken.isBlank()) {
            headers.set(HttpHeaders.AUTHORIZATION, "Bearer " + bearerToken.trim());
        }
        if (apiKey != null && !apiKey.isBlank()) {
            headers.set("API-KEY", apiKey.trim());
        }
        if (secretKey != null && !secretKey.isBlank()) {
            headers.set("SECRET-KEY", secretKey.trim());
        }

        return headers;
    }

    private JsonNode handleUpstreamResponse(int httpStatus, String body) {
        if (body == null || body.isBlank()) {
            return errorNode(httpStatus, "Empty response from upstream");
        }
        try {
            JsonNode root = objectMapper.readTree(body);
            JsonNode decoded = decodeUpstreamData(root);
            if (decoded != null && decoded.isObject() && decoded.get("httpStatus") == null) {
                ((ObjectNode) decoded).put("httpStatus", httpStatus);
            }
            return decoded;
        } catch (Exception ex) {
            ObjectNode node = objectMapper.createObjectNode();
            node.put("httpStatus", httpStatus);
            node.put("status", httpStatus);
            node.put("message", "Upstream returned non-JSON response");
            node.put("raw", body);
            node.set("data", objectMapper.createArrayNode());
            return node;
        }
    }

    /**
     * Mahabhumi sample responses show:
     * { "status": 200, "data": "base64..." }
     * We decode base64 and if it is JSON, we replace data with decoded JSON.
     */
    private JsonNode decodeUpstreamData(JsonNode root) {
        if (root == null || !root.isObject()) return root;

        JsonNode dataNode = root.get("data");
        if (dataNode == null || !dataNode.isTextual()) {
            return root;
        }

        String encoded = dataNode.asText();
        JsonNode decodedJson = tryDecodeToJson(encoded);
        if (decodedJson == null) return root;

        ObjectNode copy = ((ObjectNode) root).deepCopy();
        copy.set("data", decodedJson);
        return copy;
    }

    private JsonNode tryDecodeToJson(String encoded) {
        // Upstream returns base64-encoded ciphertext.
        // Some endpoints might still return base64(JSON) or gzipped JSON, so we try in stages.
        DecodedPayload first = decodeBase64ToTextMaybeGunzip(encoded);
        JsonNode node = tryParseJson(first);
        if (node != null) return node;

        // Most likely: AES-256-CBC decrypt using key/iv derived from Authorization token.
        String aes = tryAes256CbcDecryptFromToken(first == null ? null : first.text);
        if (aes != null) {
            node = tryParseJson(new DecodedPayload(aes));
            if (node != null) return node;
        }

        if (first != null && looksLikeBase64(first.text)) {
            DecodedPayload second = decodeBase64ToTextMaybeGunzip(first.text);
            node = tryParseJson(second);
            if (node != null) return node;

            String aes2 = tryAes256CbcDecryptFromToken(second == null ? null : second.text);
            if (aes2 != null) {
                node = tryParseJson(new DecodedPayload(aes2));
                if (node != null) return node;
            }
        }

        return null;
    }

    private static class DecodedPayload {
        final String text;

        DecodedPayload(String text) {
            this.text = text;
        }
    }

    private static DecodedPayload decodeBase64ToTextMaybeGunzip(String encoded) {
        if (encoded == null) return null;
        String cleaned = encoded.replaceAll("\\s+", "");
        try {
            byte[] bytes = Base64.getDecoder().decode(cleaned);
            String text;
            if (isGzip(bytes)) {
                byte[] unzipped = gunzip(bytes);
                text = unzipped == null ? new String(bytes, StandardCharsets.UTF_8) : new String(unzipped, StandardCharsets.UTF_8);
                return new DecodedPayload(text);
            }
            text = new String(bytes, StandardCharsets.UTF_8);
            return new DecodedPayload(text);
        } catch (IllegalArgumentException ex) {
            return null;
        }
    }

    /**
     * As per "API Encryption and Decryption Document.pdf":
     * - Base64 decode API "data" first (done before calling this method)
     * - Then AES-256-CBC decrypt using:
     *   - Key = last 32 characters of Authorization token
     *   - IV  = last 16 characters of Authorization token
     */
    private String tryAes256CbcDecryptFromToken(String cipherText) {
        String token = bearerToken == null ? null : bearerToken.trim();
        if (token == null || token.isEmpty()) return null;
        if (cipherText == null || cipherText.isBlank()) return null;
        if (token.length() < 32 || token.length() < 16) return null;

        String keyStr = token.substring(token.length() - 32);
        String ivStr = token.substring(token.length() - 16);

        try {
            byte[] keyBytes = keyStr.getBytes(StandardCharsets.UTF_8);
            byte[] ivBytes = ivStr.getBytes(StandardCharsets.UTF_8);
            SecretKeySpec key = new SecretKeySpec(keyBytes, "AES");
            IvParameterSpec iv = new IvParameterSpec(ivBytes);

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, key, iv);

            // The PDF examples show openssl_decrypt(base64_decode($data), ...)
            // We already base64-decoded "data" once. If it is still base64-like, decode again.
            byte[] inputBytes;
            if (looksLikeBase64(cipherText)) {
                inputBytes = Base64.getDecoder().decode(cipherText.replaceAll("\\s+", ""));
            } else {
                inputBytes = cipherText.getBytes(StandardCharsets.ISO_8859_1);
            }

            byte[] plain = cipher.doFinal(inputBytes);
            return new String(plain, StandardCharsets.UTF_8).trim();
        } catch (Exception ex) {
            return null;
        }
    }

    private JsonNode tryParseJson(DecodedPayload payload) {
        if (payload == null || payload.text == null) return null;
        String trimmed = payload.text.trim();
        if (!(trimmed.startsWith("{") || trimmed.startsWith("["))) return null;
        try {
            return objectMapper.readTree(trimmed);
        } catch (Exception ignore) {
            return null;
        }
    }

    private static boolean looksLikeBase64(String s) {
        if (s == null) return false;
        String t = s.trim();
        if (t.length() < 16) return false;
        // quick heuristic: only base64 alphabet + padding, and length multiple of 4
        if (t.length() % 4 != 0) return false;
        return t.matches("^[A-Za-z0-9+/=\\r\\n]+$");
    }

    private static boolean isGzip(byte[] bytes) {
        return bytes != null && bytes.length >= 2 && (bytes[0] == (byte) 0x1F) && (bytes[1] == (byte) 0x8B);
    }

    private static byte[] gunzip(byte[] gzipped) {
        if (gzipped == null) return null;
        try (GZIPInputStream gis = new GZIPInputStream(new ByteArrayInputStream(gzipped));
             ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            byte[] buffer = new byte[4096];
            int read;
            while ((read = gis.read(buffer)) > 0) {
                baos.write(buffer, 0, read);
            }
            return baos.toByteArray();
        } catch (Exception ex) {
            return null;
        }
    }

    private JsonNode errorNode(int status, String message) {
        ObjectNode node = objectMapper.createObjectNode();
        node.put("httpStatus", status);
        node.put("status", status);
        node.put("message", message);
        node.set("data", objectMapper.createArrayNode());
        return node;
    }

    private static String stripTrailingSlash(String s) {
        if (s == null) return "";
        String out = s.trim();
        while (out.endsWith("/")) out = out.substring(0, out.length() - 1);
        return out;
    }

    private static String normalizePath(String path) {
        if (path == null || path.isBlank()) return "";
        String p = path.trim();
        return p.startsWith("/") ? p : "/" + p;
    }
}

