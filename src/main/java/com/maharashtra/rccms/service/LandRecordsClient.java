package com.maharashtra.rccms.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
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
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;

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
        return executePostForm(path, formFields, true);
    }

    public JsonNode postFormRaw(String path, Map<String, String> formFields) {
        return executePostForm(path, formFields, false);
    }

    private JsonNode executePostForm(String path, Map<String, String> formFields, boolean decodeResponseData) {
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
            return handleUpstreamResponse(res.getStatusCode().value(), res.getBody(), decodeResponseData);
        } catch (HttpStatusCodeException ex) {
            return handleUpstreamResponse(ex.getStatusCode().value(), ex.getResponseBodyAsString(), decodeResponseData);
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

    private JsonNode handleUpstreamResponse(int httpStatus, String body, boolean decodeResponseData) {
        if (body == null || body.isBlank()) {
            return errorNode(httpStatus, "Empty response from upstream");
        }
        try {
            JsonNode root = objectMapper.readTree(body);
            JsonNode out = decodeResponseData ? decodeUpstreamData(root) : root;
            if (out != null && out.isObject() && out.get("httpStatus") == null) {
                ((ObjectNode) out).put("httpStatus", httpStatus);
            }
            return out;
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
        JsonNode decodedData = tryDecodeData(encoded);
        if (decodedData == null) return root;

        ObjectNode copy = ((ObjectNode) root).deepCopy();
        copy.set("data", decodedData);
        return copy;
    }

    private JsonNode tryDecodeData(String encoded) {
        // As per API doc:
        // 1) base64_decode(data) => bytes
        // 2) AES-256-CBC decrypt on those bytes
        byte[] firstBytes = decodeBase64Bytes(encoded);
        if (firstBytes == null) return null;

        byte[] firstPlainBytes = isGzip(firstBytes) ? gunzip(firstBytes) : firstBytes;
        DecodedPayload first = firstPlainBytes == null ? null : new DecodedPayload(new String(firstPlainBytes, StandardCharsets.UTF_8));

        JsonNode node = tryParseJson(first);
        if (node != null) return node;

        // Most likely: AES-256-CBC decrypt using one of configured secrets.
        String aes = tryAes256CbcDecrypt(firstBytes);
        if (aes != null) {
            node = tryParseJson(new DecodedPayload(aes));
            if (node != null) return node;
            if (!aes.isBlank()) return JsonNodeFactory.instance.textNode(aes.trim());
        }

        if (first != null && looksLikeBase64(first.text)) {
            byte[] secondBytes = decodeBase64Bytes(first.text);
            byte[] secondPlainBytes = isGzip(secondBytes) ? gunzip(secondBytes) : secondBytes;
            DecodedPayload second = secondPlainBytes == null ? null : new DecodedPayload(new String(secondPlainBytes, StandardCharsets.UTF_8));
            node = tryParseJson(second);
            if (node != null) return node;

            String aes2 = tryAes256CbcDecrypt(secondBytes);
            if (aes2 != null) {
                node = tryParseJson(new DecodedPayload(aes2));
                if (node != null) return node;
                if (!aes2.isBlank()) return JsonNodeFactory.instance.textNode(aes2.trim());
            }

            if (second != null && second.text != null && !second.text.isBlank()) {
                return JsonNodeFactory.instance.textNode(second.text.trim());
            }
        }

        // Last resort: return base64-decoded text only when decryption attempts fail.
        if (first != null && first.text != null && !first.text.isBlank()) {
            return JsonNodeFactory.instance.textNode(first.text.trim());
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

    private static byte[] decodeBase64Bytes(String encoded) {
        if (encoded == null) return null;
        try {
            return Base64.getDecoder().decode(encoded.replaceAll("\\s+", ""));
        } catch (Exception ex) {
            return null;
        }
    }

    private String tryAes256CbcDecrypt(byte[] cipherBytes) {
        if (cipherBytes == null || cipherBytes.length == 0) return null;

        for (KeyIvPair pair : candidateKeysAndIvs()) {
            String plain = decryptAesCbc(cipherBytes, pair.key(), pair.iv());
            if (plain != null && !plain.isBlank()) {
                return plain.trim();
            }
        }
        return null;
    }

    public JsonNode debugDecryptData(String encoded) {
        ObjectNode out = objectMapper.createObjectNode();
        out.put("encodedLength", encoded == null ? 0 : encoded.length());
        out.put("looksLikeBase64", looksLikeBase64(encoded));

        DecodedPayload first = decodeBase64ToTextMaybeGunzip(encoded);
        if (first != null && first.text != null) {
            out.put("base64DecodedText", first.text);
            JsonNode firstJson = tryParseJson(first);
            if (firstJson != null) out.set("base64DecodedJson", firstJson);
        }

        byte[] firstBytes = decodeBase64Bytes(encoded);
        ArrayNode aesAttempts = objectMapper.createArrayNode();
        for (KeyIvPair pair : candidateKeysAndIvs()) {
            String plain = decryptAesCbc(firstBytes, pair.key(), pair.iv());
            if (plain != null && !plain.isBlank()) {
                ObjectNode item = objectMapper.createObjectNode();
                item.put("mode", pair.label());
                item.put("decryptedText", plain);
                JsonNode asJson = tryParseJson(new DecodedPayload(plain));
                if (asJson != null) item.set("decryptedJson", asJson);
                aesAttempts.add(item);
            }
        }
        out.set("aesAttempts", aesAttempts);
        return out;
    }

    private record KeyIvPair(String label, byte[] key, byte[] iv) {}

    private List<KeyIvPair> candidateKeysAndIvs() {
        List<KeyIvPair> out = new ArrayList<>();
        addTailDerivedCandidate(out, "bearer-tail", bearerToken);
        addTailDerivedCandidate(out, "secret-tail", secretKey);
        addTailDerivedCandidate(out, "api-tail", apiKey);

        // Some integrations use raw secret as AES key (with deterministic IV from same secret).
        addDirectSecretCandidate(out, "secret-sha256", secretKey);
        addDirectSecretCandidate(out, "bearer-sha256", bearerToken);
        return out;
    }

    private void addTailDerivedCandidate(List<KeyIvPair> out, String label, String source) {
        String s = source == null ? "" : source.trim();
        if (s.length() < 32) return;
        byte[] key = s.substring(s.length() - 32).getBytes(StandardCharsets.UTF_8);
        byte[] iv = s.substring(s.length() - 16).getBytes(StandardCharsets.UTF_8);
        out.add(new KeyIvPair(label, key, iv));
    }

    private void addDirectSecretCandidate(List<KeyIvPair> out, String label, String source) {
        String s = source == null ? "" : source.trim();
        if (s.isEmpty()) return;
        try {
            byte[] digest = MessageDigest.getInstance("SHA-256").digest(s.getBytes(StandardCharsets.UTF_8));
            byte[] key = digest; // 32 bytes for AES-256
            byte[] iv = new byte[16];
            System.arraycopy(digest, 0, iv, 0, 16);
            out.add(new KeyIvPair(label, key, iv));
        } catch (Exception ignore) {
            // no-op
        }
    }

    private String decryptAesCbc(byte[] cipherBytes, byte[] keyBytes, byte[] ivBytes) {
        if (cipherBytes == null || cipherBytes.length == 0) return null;
        try {
            SecretKeySpec key = new SecretKeySpec(keyBytes, "AES");
            IvParameterSpec iv = new IvParameterSpec(ivBytes);
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, key, iv);
            return new String(cipher.doFinal(cipherBytes), StandardCharsets.UTF_8);
        } catch (Exception ignore) {
            // try next candidate
        }
        return null;
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

