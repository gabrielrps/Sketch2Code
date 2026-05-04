package it.skecto2code.controller;

import it.skecto2code.service.SketchToCodeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.rmi.RemoteException;
import java.util.Objects;

@RestController
@RequestMapping("/api/sketch")
@RequiredArgsConstructor
public class SketchController {

    private final SketchToCodeService sketchService;

    @PostMapping("/generate")
    public String uploadSketch(@RequestParam("file") MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            throw new RemoteException("File is empty");
        }
        if (!Objects.equals(file.getContentType(), "image/jpeg") && !Objects.equals(file.getContentType(), "image/png")) {
            throw new RemoteException("Unsupported Content-Type, you need to upload a png or jpeg file");
        }

        return sketchService.generateHtmlFromSketch(file.getBytes(), file.getContentType());
    }
}