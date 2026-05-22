package com.maharashtra.rccms.controller;

import com.maharashtra.rccms.dto.FileUploadResponse;
import com.maharashtra.rccms.service.FileStorageService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.support.MissingServletRequestPartException;

import java.util.List;
import java.util.Map;

/**
 * Stores uploaded files on disk and returns a storageKey for registration / filing payloads.
 * POST /api/files/upload is public so advocate registration (pre-login) can upload certificates.
 */
@RestController
@RequestMapping("/api/files")
public class FileUploadController {

    private static final List<String> FILE_PART_NAMES = List.of(
            "file",
            "upload",
            "document",
            "attachment",
            "barEnrollmentCertificate",
            "certificate"
    );

    private final FileStorageService fileStorageService;

    public FileUploadController(FileStorageService fileStorageService) {
        this.fileStorageService = fileStorageService;
    }

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> upload(
            HttpServletRequest request,
            @RequestParam(value = "file", required = false) MultipartFile file,
            @RequestParam(value = "category", required = false) String category,
            @RequestParam(value = "purpose", required = false) String purpose
    ) {
        try {
            MultipartFile resolved = file != null && !file.isEmpty() ? file : resolveFileFromRequest(request);
            String resolvedCategory = firstNonBlank(category, purpose);
            FileUploadResponse result = fileStorageService.store(resolved, resolvedCategory);
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
        } catch (IllegalStateException ex) {
            return ResponseEntity.internalServerError().body(Map.of("error", ex.getMessage()));
        }
    }

    @ExceptionHandler(MissingServletRequestPartException.class)
    public ResponseEntity<?> handleMissingPart(MissingServletRequestPartException ex) {
        return ResponseEntity.badRequest().body(Map.of(
                "error",
                "Multipart file is required. Send form field name 'file' (or 'upload' / 'document') with the PDF/image."
        ));
    }

    private static MultipartFile resolveFileFromRequest(HttpServletRequest request) {
        if (request instanceof MultipartHttpServletRequest multipart) {
            for (String partName : FILE_PART_NAMES) {
                MultipartFile candidate = multipart.getFile(partName);
                if (candidate != null && !candidate.isEmpty()) {
                    return candidate;
                }
            }
            for (MultipartFile candidate : multipart.getFileMap().values()) {
                if (candidate != null && !candidate.isEmpty()) {
                    return candidate;
                }
            }
        }
        throw new IllegalArgumentException(
                "File is required. Use multipart/form-data with field 'file' (PDF, JPG, JPEG, or PNG)."
        );
    }

    private static String firstNonBlank(String... values) {
        if (values == null) {
            return null;
        }
        for (String value : values) {
            if (value != null && !value.trim().isEmpty()) {
                return value.trim();
            }
        }
        return null;
    }
}
