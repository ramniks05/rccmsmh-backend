package com.maharashtra.rccms.dto;

import org.springframework.core.io.Resource;

public class StoredFileResource {

    private final Resource resource;
    private final String fileName;
    private final String mimeType;
    private final long size;

    public StoredFileResource(Resource resource, String fileName, String mimeType, long size) {
        this.resource = resource;
        this.fileName = fileName;
        this.mimeType = mimeType;
        this.size = size;
    }

    public Resource getResource() {
        return resource;
    }

    public String getFileName() {
        return fileName;
    }

    public String getMimeType() {
        return mimeType;
    }

    public long getSize() {
        return size;
    }
}
