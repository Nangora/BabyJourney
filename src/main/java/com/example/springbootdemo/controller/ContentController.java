// ContentController.java
package com.example.springbootdemo.controller;

import com.example.springbootdemo.dto.ContentDtos.ContentView;
import com.example.springbootdemo.dto.ContentDtos.StateRequest;
import com.example.springbootdemo.service.ContentService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Noi dung (Content)")
@RestController
@RequestMapping("/api/contents")
@RequiredArgsConstructor
public class ContentController {

    private final ContentService contentService;

    // kind=LESSON|ACTIVITY, category, week, trimester (1-3), q (tìm kiếm), saved=true
    @GetMapping
    public List<ContentView> list(@AuthenticationPrincipal Long userId,
                                  @RequestParam(required = false) String kind,
                                  @RequestParam(required = false) String category,
                                  @RequestParam(required = false) Integer week,
                                  @RequestParam(required = false) Integer trimester,
                                  @RequestParam(required = false) String q,
                                  @RequestParam(defaultValue = "false") boolean saved) {
        return contentService.list(userId, kind, category, week, trimester, q, saved);
    }

    @GetMapping("/{id}")
    public ContentView get(@AuthenticationPrincipal Long userId, @PathVariable Long id) {
        return contentService.get(userId, id);
    }

    // Lưu / bỏ lưu, cập nhật phần trăm đã học
    @PutMapping("/{id}/state")
    public ContentView state(@AuthenticationPrincipal Long userId, @PathVariable Long id,
                             @RequestBody StateRequest request) {
        return contentService.updateState(userId, id, request);
    }

    @PostMapping("/{id}/complete")
    public ContentView complete(@AuthenticationPrincipal Long userId, @PathVariable Long id) {
        return contentService.complete(userId, id);
    }
}