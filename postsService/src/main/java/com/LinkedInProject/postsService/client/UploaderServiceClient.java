package com.LinkedInProject.postsService.client;

import com.LinkedInProject.postsService.config.FeignMultipartConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

@FeignClient(
        name = "uploader-service",
        path = "/uploads",
        url = "${UPLOADER_SERVICE_URI:http://uploader-service}",
        configuration = FeignMultipartConfig.class
)
public interface UploaderServiceClient {

    @PostMapping(value = "/file", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ResponseEntity<String> uploadFile(@RequestPart("file") MultipartFile file);
}