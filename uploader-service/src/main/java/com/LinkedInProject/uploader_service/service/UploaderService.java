package com.LinkedInProject.uploader_service.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface UploaderService {
    String upload(MultipartFile file) ;
}
