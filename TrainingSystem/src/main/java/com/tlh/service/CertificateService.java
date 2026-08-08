/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.tlh.service;

import com.tlh.pojo.Certificate;
import java.util.List;

/**
 *
 * @author LENOVO
 */
public interface CertificateService {
    List<Certificate> getMyCertificates(long userId);
    Certificate getById(long id);
    List<Certificate> getByCourse(long courseId);
    Certificate updatePdfUrl(long id, String pdfUrl);
    Certificate checkAndIssue(Long userId, Long courseId);
}
