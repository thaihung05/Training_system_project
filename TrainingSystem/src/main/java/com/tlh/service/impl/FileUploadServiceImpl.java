/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tlh.service.impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.tlh.service.FileUploadService;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/**
 *
 * @author LENOVO
 */
@Service
public class FileUploadServiceImpl implements FileUploadService {

    private static final long MAX_PDF_SIZE = 10L * 1024 * 1024;
    private static final long MAX_IMAGE_SIZE = 5L * 1024 * 1024;

    @Autowired
    private Cloudinary cloudinary;

    @Override
    public String uploadPdf(MultipartFile file) {
        if (file == null || file.isEmpty())
            throw new IllegalArgumentException("Vui lòng chọn file để tải lên");
        if (file.getSize() > MAX_PDF_SIZE)
            throw new IllegalArgumentException("File PDF tối đa 10 MB");
        if (!"application/pdf".equals(file.getContentType()))
            throw new IllegalArgumentException("Chỉ chấp nhận file PDF");
        assertPdfSignature(file);
        try {
            String publicId = "training-system/lessons/" + UUID.randomUUID();
            Map<?, ?> result = this.cloudinary.uploader().upload(file.getBytes(), ObjectUtils.asMap(
                    "resource_type", "raw",
                    "public_id", publicId,
                    "format", "pdf"
            ));
            return (String) result.get("secure_url");
        } catch (IOException e) {
            throw new IllegalArgumentException("Tải file lên thất bại: " + e.getMessage());
        }
    }

    private static final List<String> ALLOWED_IMAGE_TYPES = Arrays.asList("image/jpeg", "image/png", "image/webp");

    @Override
    public String uploadImage(MultipartFile file) {
        if (file == null || file.isEmpty())
            throw new IllegalArgumentException("Vui lòng chọn ảnh để tải lên");
        if (file.getSize() > MAX_IMAGE_SIZE)
            throw new IllegalArgumentException("Ảnh tối đa 5 MB");
        if (!ALLOWED_IMAGE_TYPES.contains(file.getContentType()))
            throw new IllegalArgumentException("Chỉ chấp nhận ảnh định dạng JPEG, PNG hoặc WEBP");
        try {
            String publicId = "training-system/courses/" + UUID.randomUUID();
            Map<?, ?> result = this.cloudinary.uploader().upload(file.getBytes(), ObjectUtils.asMap(
                    "resource_type", "image",
                    "public_id", publicId
            ));
            return (String) result.get("secure_url");
        } catch (IOException e) {
            throw new IllegalArgumentException("Tải ảnh lên thất bại: " + e.getMessage());
        }
    }

    private void assertPdfSignature(MultipartFile file) {
        try (InputStream input = file.getInputStream()) {
            byte[] signature = input.readNBytes(5);
            if (signature.length != 5
                    || signature[0] != '%'
                    || signature[1] != 'P'
                    || signature[2] != 'D'
                    || signature[3] != 'F'
                    || signature[4] != '-') {
                throw new IllegalArgumentException("Nội dung file không phải định dạng PDF hợp lệ");
            }
        } catch (IOException e) {
            throw new IllegalArgumentException("Không đọc được file PDF");
        }
    }
}
