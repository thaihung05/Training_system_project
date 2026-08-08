/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tlh.service.impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.tlh.service.FileUploadService;
import java.io.IOException;
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

    @Autowired
    private Cloudinary cloudinary;

    @Override
    public String uploadPdf(MultipartFile file) {
        if (file == null || file.isEmpty())
            throw new IllegalArgumentException("Vui lòng chọn file để tải lên");
        if (!"application/pdf".equals(file.getContentType()))
            throw new IllegalArgumentException("Chỉ chấp nhận file PDF");
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
}
