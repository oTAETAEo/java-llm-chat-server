package com.example.aisocket.project.application.internal.fit;

import org.springframework.web.multipart.MultipartFile;

public interface FitFileParser {

    FitParseResult parse(MultipartFile file);
}
