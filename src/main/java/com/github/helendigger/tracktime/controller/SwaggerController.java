package com.github.helendigger.tracktime.controller;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
public class SwaggerController {
    @GetMapping("/swagger-ui")
    void redirectIndex(HttpServletResponse response) throws IOException {
        response.sendRedirect("/swagger-ui/index.html");
    }

    @GetMapping("/swagger-ui/")
    void redirectIndexSlash(HttpServletResponse response) throws IOException {
        response.sendRedirect("/swagger-ui/index.html");
    }
}
