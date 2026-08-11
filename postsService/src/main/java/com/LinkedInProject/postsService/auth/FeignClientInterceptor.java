package com.LinkedInProject.postsService.auth;
import feign.RequestInterceptor;
import feign.RequestTemplate;

public class FeignClientInterceptor implements RequestInterceptor {

    @Override
    public void apply(RequestTemplate template) {
        // Implementation for intercepting Feign client requests
    }

}
