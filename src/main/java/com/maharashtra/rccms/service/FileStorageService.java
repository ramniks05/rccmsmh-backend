package com.maharashtra.rccms.service;

import com.maharashtra.rccms.dto.FileUploadResponse;
import com.maharashtra.rccms.dto.StoredFileResource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

@Service
public class FileStorageService {

    private static final Set<String> ALLOWED_CATEGORIES = Set.of(
            "advocate-registration",
            "filing-attachment",
            "general"
    );

    private static final Set<String> ALLOWED_EXTENSIONS = Set.of(
            "pdf", "jpg", "jpeg", "png"
    );

    private final Path uploadRoot;
    private final long maxSizeBytes;

    public FileStorageService(
            @Value("${rccms.files.upload-dir:uploads}") String uploadDir,
            @Value("${rccms.files.max-size-bytes:5242880}") long maxSizeBytes
    ) {
        this.uploadRoot = Paths.get(uploadDir).toAbsolutePath().normalize();
        this.maxSizeBytes = maxSizeBytes;
    }

    public FileUploadResponse store(MultipartFile file, String category) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File is required.");
        }
        String normalizedCategory = normalizeCategory(category);
        if (file.getSize() > maxSizeBytes) {
            throw new IllegalArgumentException(
                    "File exceeds maximum allowed size of " + (maxSizeBytes / 1024 / 1024) + " MB."
            );
        }

        String originalName = sanitizeOriginalFileName(file.getOriginalFilename());
        String extension = extractExtension(originalName);
        if (extension.isEmpty()) {
            extension = extensionFromContentType(file.getContentType());
        }
        if (!ALLOWED_EXTENSIONS.contains(extension)) {
            throw new IllegalArgumentException(
                    "Allowed file types: PDF, JPG, JPEG, PNG. "
                            + "Send a file with a valid extension or Content-Type."
            );
        }
        if ("upload.bin".equals(originalName) || !originalName.contains(".")) {
            originalName = "upload." + extension;
        }

        String storedFileName = UUID.randomUUID() + "." + extension;
        LocalDate today = LocalDate.now();
        Path targetDir = uploadRoot
                .resolve(normalizedCategory)
                .resolve(String.valueOf(today.getYear()))
                .resolve(String.format(Locale.ROOT, "%02d", today.getMonthValue()))
                .resolve(String.format(Locale.ROOT, "%02d", today.getDayOfMonth()));

        try {
            Files.createDirectories(targetDir);
            Path targetFile = targetDir.resolve(storedFileName);
            file.transferTo(targetFile);

            Path relative = uploadRoot.relativize(targetFile);
            String storageKey = uploadRoot.getFileName().toString() + "/" + relative.toString().replace('\\', '/');

            FileUploadResponse response = new FileUploadResponse();
            response.setStorageKey(storageKey);
            response.setFileName(originalName);
            response.setMimeType(file.getContentType() != null ? file.getContentType() : guessMimeType(extension));
            response.setSize(file.getSize());
            return response;
        } catch (IOException ex) {
            throw new IllegalStateException("Failed to store uploaded file.", ex);
        }
    }

    /**
     * Loads a previously stored file by {@code storageKey} returned from {@link #store}.
     * Resolves paths only under {@code rccms.files.upload-dir}; rejects path traversal.
     */
    public StoredFileResource load(String storageKey, String preferredFileName) {
        if (storageKey == null || storageKey.isBlank()) {
            throw new IllegalArgumentException("storageKey is required.");
        }
        Path filePath = resolveStoragePath(storageKey.trim());
        if (!Files.isRegularFile(filePath)) {
            throw new IllegalArgumentException("File not found for storageKey.");
        }

        String fileName = sanitizeOriginalFileName(
                preferredFileName != null && !preferredFileName.isBlank()
                        ? preferredFileName
                        : filePath.getFileName().toString()
        );
        String extension = extractExtension(fileName);
        if (extension.isEmpty()) {
            extension = extractExtension(filePath.getFileName().toString());
        }
        String mimeType = probeMimeType(filePath, extension);

        try {
            long size = Files.size(filePath);
            Resource resource = new FileSystemResource(filePath);
            return new StoredFileResource(resource, fileName, mimeType, size);
        } catch (IOException ex) {
            throw new IllegalStateException("Failed to read stored file.", ex);
        }
    }

    /**
     * Maps a persisted storageKey to an on-disk path under the configured upload root.
     * Supports keys with or without the upload-root folder prefix (e.g. {@code uploads/...}).
     */
    public Path resolveStoragePath(String storageKey) {
        String normalized = storageKey.trim().replace('\\', '/');
        while (normalized.startsWith("/")) {
            normalized = normalized.substring(1);
        }

        String rootFolder = uploadRoot.getFileName().toString();
        String relativeKey = normalized;
        if (normalized.startsWith(rootFolder + "/")) {
            relativeKey = normalized.substring(rootFolder.length() + 1);
        } else if (normalized.startsWith("uploads/")) {
            relativeKey = normalized.substring("uploads/".length());
        }

        Path resolved = uploadRoot.resolve(relativeKey.replace('/', java.io.File.separatorChar)).normalize();
        if (!resolved.startsWith(uploadRoot)) {
            throw new IllegalArgumentException("Invalid storageKey.");
        }
        return resolved;
    }

    private static String probeMimeType(Path filePath, String extension) {
        try {
            String probed = Files.probeContentType(filePath);
            if (probed != null && !probed.isBlank()) {
                return probed;
            }
        } catch (IOException ignored) {
            // fall back to extension guess
        }
        if (!extension.isEmpty()) {
            return guessMimeType(extension);
        }
        return "application/octet-stream";
    }

    private static String normalizeCategory(String category) {
        String value = category == null || category.isBlank() ? "general" : category.trim().toLowerCase(Locale.ROOT);
        value = switch (value) {
            case "advocate", "advocate_registration", "advocate-registration-certificate", "bar-enrollment",
                    "bar_enrollment", "bar-enrollment-certificate", "registration" -> "advocate-registration";
            case "filing", "filing_attachment", "attachment-filing" -> "filing-attachment";
            default -> value;
        };
        if (!ALLOWED_CATEGORIES.contains(value)) {
            throw new IllegalArgumentException(
                    "Invalid category. Allowed: advocate-registration, filing-attachment, general "
                            + "(aliases: advocate, registration, bar-enrollment-certificate)."
            );
        }
        return value;
    }

    private static String sanitizeOriginalFileName(String raw) {
        if (raw == null || raw.isBlank()) {
            return "upload.bin";
        }
        String name = Paths.get(raw).getFileName().toString().trim();
        if (name.isEmpty()) {
            return "upload.bin";
        }
        return name.replaceAll("[^a-zA-Z0-9._\\- ]", "_");
    }

    private static String extractExtension(String fileName) {
        int dot = fileName.lastIndexOf('.');
        if (dot < 0 || dot == fileName.length() - 1) {
            return "";
        }
        return fileName.substring(dot + 1).toLowerCase(Locale.ROOT);
    }

    private static String guessMimeType(String extension) {
        return switch (extension) {
            case "pdf" -> "application/pdf";
            case "jpg", "jpeg" -> "image/jpeg";
            case "png" -> "image/png";
            default -> "application/octet-stream";
        };
    }

    private static String extensionFromContentType(String contentType) {
        if (contentType == null || contentType.isBlank()) {
            return "";
        }
        String type = contentType.toLowerCase(Locale.ROOT).split(";")[0].trim();
        return switch (type) {
            case "application/pdf" -> "pdf";
            case "image/jpeg" -> "jpeg";
            case "image/jpg" -> "jpg";
            case "image/png" -> "png";
            default -> "";
        };
    }
}
