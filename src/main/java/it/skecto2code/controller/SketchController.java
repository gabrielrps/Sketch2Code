package it.skecto2code.controller;

import it.skecto2code.service.SketchToCodeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.Objects;

@RestController
@RequestMapping("/api/sketch")
@RequiredArgsConstructor
public class SketchController {

    private final SketchToCodeService sketchService;

    @PostMapping("/generate")
    public String uploadSketch(@RequestParam("file") MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "File is empty");
        }
        if (!Objects.equals(file.getContentType(), "image/jpeg") && !Objects.equals(file.getContentType(), "image/png")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unsupported Content-Type: only png and jpeg are accepted");
        }

        return sketchService.generateHtmlFromSketch(file.getBytes(), file.getContentType());
    }
}