package org.mcintyrelab.controller;

import org.mcintyrelab.dto.CreateUntrainedRequest;
import org.mcintyrelab.dto.UntrainedResponse;
import org.mcintyrelab.service.UntrainedService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/mcintyre-lab/v1/untrained")
public class UntrainedController {
    private final UntrainedService untrainedService;

    public UntrainedController(UntrainedService untrainedService) {
        this.untrainedService = untrainedService;
    }

    @PostMapping("/upload")
    public ResponseEntity<String> uploadVideo(@RequestBody CreateUntrainedRequest createUntrainedRequest) {
        untrainedService.createUntrained(createUntrainedRequest);
        return ResponseEntity.ok("Video(s) uploaded successfully");
    }

    @GetMapping("/allUploads")
    public ResponseEntity<List<UntrainedResponse>> getAllUploads(@RequestParam("userId") Integer userId){
        List<UntrainedResponse> uploads = untrainedService.getAllUploads(userId);
        return ResponseEntity.ok(uploads);
    }
}
