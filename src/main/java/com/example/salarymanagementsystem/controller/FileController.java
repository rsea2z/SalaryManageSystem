package com.example.salarymanagementsystem.controller;

import com.example.salarymanagementsystem.service.FileDownloadService;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/files")
public class FileController {

    private static final Logger logger = LoggerFactory.getLogger(FileController.class);

    private final FileDownloadService fileDownloadService;

    @Autowired
    public FileController(FileDownloadService fileDownloadService) {
        this.fileDownloadService = fileDownloadService;
        logger.info("FileController initialized and FileDownloadService autowired successfully.");
    }

    @GetMapping("/download/template/project")
    @PreAuthorize("hasRole('ADMIN')")
    public void downloadProjectTemplate(HttpServletResponse response) {
        logger.info("Attempting to download project template file."); // Log when method is called
        try {
            fileDownloadService.downloadProjectTemplate(response);
        } catch (Exception e) {
            logger.error("Failed to download project template file", e);
            // Avoid setting status if response is already committed or headers sent
            if (!response.isCommitted()) {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            }
        }
    }
    @GetMapping("/download/template/teacher")
    @PreAuthorize("hasRole('ADMIN')")
    public void downloadTeacherTemplate(HttpServletResponse response) { // Updated method name
        logger.info("Attempting to download teacher template file."); // Log when method is called
        try {
            fileDownloadService.downloadTeacherTemplate(response);
        } catch (Exception e) {
            logger.error("Failed to download teacher template file", e);
            // Avoid setting status if response is already committed or headers sent
            if (!response.isCommitted()) {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            }
        }
    }
}
