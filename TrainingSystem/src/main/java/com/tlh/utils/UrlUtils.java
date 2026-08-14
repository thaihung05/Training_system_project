package com.tlh.utils;

import java.net.URI;
import java.net.URISyntaxException;

public final class UrlUtils {

    private UrlUtils() {
    }

    public static String normalizeHttpUrl(String value, String fieldName, int maxLength, boolean required) {
        if (value == null || value.trim().isEmpty()) {
            if (required) {
                throw new IllegalArgumentException(fieldName + " không được để trống");
            }
            return null;
        }

        String normalized = value.trim();
        if (normalized.length() > maxLength) {
            throw new IllegalArgumentException(fieldName + " tối đa " + maxLength + " ký tự");
        }

        try {
            URI uri = new URI(normalized);
            String scheme = uri.getScheme();
            if (scheme == null
                    || !("http".equalsIgnoreCase(scheme) || "https".equalsIgnoreCase(scheme))
                    || uri.getHost() == null) {
                throw new IllegalArgumentException(fieldName + " phải là đường dẫn HTTP hoặc HTTPS hợp lệ");
            }
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException(fieldName + " không hợp lệ");
        }
        return normalized;
    }
}
