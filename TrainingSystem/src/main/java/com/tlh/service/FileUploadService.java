/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.tlh.service;

import org.springframework.web.multipart.MultipartFile;

/**
 *
 * @author LENOVO
 */
public interface FileUploadService {
    String uploadPdf(MultipartFile file);
}
