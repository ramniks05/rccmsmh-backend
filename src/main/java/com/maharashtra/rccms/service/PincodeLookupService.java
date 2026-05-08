package com.maharashtra.rccms.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.maharashtra.rccms.dto.PincodeLookupResponse;
import com.maharashtra.rccms.dto.PincodePostOfficeOption;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Service
public class PincodeLookupService {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final String postalApiBaseUrl;

    public PincodeLookupService(
            RestTemplate restTemplate,
            ObjectMapper objectMapper,
            @Value("${rccms.postal-api.base-url:https://api.postalpincode.in}") String postalApiBaseUrl
    ) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
        this.postalApiBaseUrl = normalizeBaseUrl(postalApiBaseUrl);
    }

    public PincodeLookupResponse lookupByPincode(String pincode) {
        String normalizedPincode = normalizePincode(pincode);
        if (!normalizedPincode.matches("\\d{6}")) {
            throw new IllegalArgumentException("Pincode must be exactly 6 digits.");
        }

        String rawResponse = fetchPostalData(normalizedPincode);

        if (rawResponse == null || rawResponse.isBlank()) {
            throw new IllegalStateException("Postal API returned empty response.");
        }

        JsonNode firstEnvelope = parseFirstEnvelope(rawResponse);
        String status = text(firstEnvelope.get("Status"));
        String message = text(firstEnvelope.get("Message"));
        JsonNode postOfficeNode = firstEnvelope.get("PostOffice");

        if (!"Success".equalsIgnoreCase(status) || postOfficeNode == null || !postOfficeNode.isArray() || postOfficeNode.isEmpty()) {
            throw new IllegalArgumentException(message == null || message.isBlank()
                    ? "Please enter a valid pincode."
                    : message);
        }

        List<PincodePostOfficeOption> postOffices = new ArrayList<>();
        Set<String> talukas = new LinkedHashSet<>();
        Set<String> districts = new LinkedHashSet<>();
        Set<String> states = new LinkedHashSet<>();

        for (JsonNode officeNode : postOfficeNode) {
            String name = text(officeNode.get("Name"));
            String block = text(officeNode.get("Block"));
            String district = text(officeNode.get("District"));
            String state = text(officeNode.get("State"));
            String value = String.join("#", safe(name), safe(block), safe(district), safe(state));

            postOffices.add(new PincodePostOfficeOption(name, block, district, state, value));
            if (!block.isBlank()) talukas.add(block);
            if (!district.isBlank()) districts.add(district);
            if (!state.isBlank()) states.add(state);
        }

        return new PincodeLookupResponse(
                normalizedPincode,
                status,
                message,
                postOffices,
                List.copyOf(talukas),
                List.copyOf(districts),
                List.copyOf(states)
        );
    }

    private String fetchPostalData(String pincode) {
        String primaryUrl = postalApiBaseUrl + "/pincode/" + pincode;
        String fallbackUrl = "http://api.postalpincode.in/pincode/" + pincode;
        List<String> candidateUrls = primaryUrl.equalsIgnoreCase(fallbackUrl)
                ? List.of(primaryUrl)
                : List.of(primaryUrl, fallbackUrl);

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        headers.set("User-Agent", "Mozilla/5.0");
        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);

        RestClientException lastException = null;
        for (String url : candidateUrls) {
            for (int attempt = 1; attempt <= 3; attempt++) {
                try {
                    ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, requestEntity, String.class);
                    return response.getBody();
                } catch (RestClientException ex) {
                    lastException = ex;
                    if (attempt < 3) {
                        sleepQuietly(250L * attempt);
                    }
                }
            }
        }

        String detail = (lastException == null || lastException.getMessage() == null || lastException.getMessage().isBlank())
                ? "unknown network error"
                : lastException.getMessage();
        throw new IllegalStateException("Postal API temporarily unavailable. Please try again. (" + detail + ")");
    }

    private JsonNode parseFirstEnvelope(String rawResponse) {
        try {
            JsonNode root = objectMapper.readTree(rawResponse);
            if (!root.isArray() || root.isEmpty()) {
                throw new IllegalStateException("Postal API returned unexpected payload.");
            }
            return root.get(0);
        } catch (IllegalStateException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new IllegalStateException("Postal API returned invalid JSON.");
        }
    }

    private static String normalizePincode(String pincode) {
        if (pincode == null) return "";
        return pincode.trim();
    }

    private static String normalizeBaseUrl(String baseUrl) {
        if (baseUrl == null || baseUrl.isBlank()) return "https://api.postalpincode.in";
        String value = baseUrl.trim();
        while (value.endsWith("/")) {
            value = value.substring(0, value.length() - 1);
        }
        return value;
    }

    private static String text(JsonNode node) {
        return node == null || node.isNull() ? "" : node.asText("");
    }

    private static String safe(String value) {
        return value == null ? "" : value.trim();
    }

    private static void sleepQuietly(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
    }
}
