package com.example.salarymanagementsystem.service;

import jakarta.servlet.http.HttpServletResponse;

public interface FileDownloadService {
    void downloadProjectTemplate(HttpServletResponse response);
    void downloadTeacherTemplate(HttpServletResponse response); // Updated method signature
}
