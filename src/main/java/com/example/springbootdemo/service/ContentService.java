package com.example.springbootdemo.service;

import com.example.springbootdemo.dto.ContentDtos.ContentView;
import com.example.springbootdemo.dto.ContentDtos.StateRequest;
import com.example.springbootdemo.entity.ActivityLog;
import com.example.springbootdemo.entity.Content;
import com.example.springbootdemo.entity.UserContent;
import com.example.springbootdemo.repository.ActivityLogRepository;
import com.example.springbootdemo.repository.ContentRepository;
import com.example.springbootdemo.repository.UserContentRepository;
import com.example.springbootdemo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ContentService {

    private static final Set<String> KINDS = Set.of("LESSON", "ACTIVITY");

    private final ContentRepository contentRepository;
    private final UserContentRepository userContentRepository;
    private final ActivityLogRepository activityLogRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public List<ContentView> list(Long userId, String kind, String category, Integer week,
                                  Integer trimester, String q, boolean savedOnly) {
        String k = clean(kind);
        if (!k.isEmpty() && !KINDS.contains(k)) throw bad("Loại nội dung không hợp lệ");

        int wMin = 0;
        int wMax = 99;
        if (week != null) {
            wMin = week;
            wMax = week;
        } else if (trimester != null) {
            switch (trimester) {
                case 1 -> { wMin = 1; wMax = 13; }
                case 2 -> { wMin = 14; wMax = 27; }
                case 3 -> { wMin = 28; wMax = 42; }
                default -> throw bad("Tam cá nguyệt không hợp lệ");
            }
        }

        List<Content> found = contentRepository.search(k, clean(category), wMin, wMax, clean(q));
        Map<Long, UserContent> states = states(userId, found);

        return found.stream()
                .filter(c -> !savedOnly || (states.get(c.getId()) != null && states.get(c.getId()).isSaved()))
                .map(c -> toView(c, states.get(c.getId()), false))
                .toList();
    }

    @Transactional(readOnly = true)
    public ContentView get(Long userId, Long id) {
        Content c = find(id);
        UserContent uc = userContentRepository.findByUser_IdAndContent_Id(userId, id).orElse(null);
        return toView(c, uc, true);
    }

    @Transactional
    public ContentView updateState(Long userId, Long id, StateRequest r) {
        Content c = find(id);
        UserContent uc = getOrCreate(userId, c);

        if (r.saved() != null) uc.setSaved(r.saved());

        if (r.progressPercent() != null) {
            int p = Math.max(0, Math.min(100, r.progressPercent()));
            // Bài đã hoàn thành thì không bị kéo lùi khi xem lại
            if (!(uc.getStatus().equals("COMPLETED") && p < 100)) {
                uc.setProgressPercent(p);
                uc.setStatus(p == 0 ? "NOT_STARTED" : p >= 100 ? "COMPLETED" : "IN_PROGRESS");
            }
        }
        return toView(c, userContentRepository.save(uc), true);
    }

    @Transactional
    public ContentView complete(Long userId, Long id) {
        Content c = find(id);
        UserContent uc = getOrCreate(userId, c);
        uc.setStatus("COMPLETED");
        uc.setProgressPercent(100);
        userContentRepository.save(uc);

        // Hoàn thành hoạt động: ghi một dòng cho hôm nay để đếm "số ngày có thực hành"
        if ("ACTIVITY".equals(c.getKind())) {
            LocalDate today = LocalDate.now();
            if (!activityLogRepository.existsByUser_IdAndContent_IdAndCompletedOn(userId, id, today)) {
                activityLogRepository.save(ActivityLog.builder()
                        .user(uc.getUser()).content(c).completedOn(today).build());
            }
        }
        return toView(c, uc, true);
    }

    // ---------- tiện ích ----------
    private Content find(Long id) {
        return contentRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy nội dung"));
    }

    private UserContent getOrCreate(Long userId, Content c) {
        return userContentRepository.findByUser_IdAndContent_Id(userId, c.getId())
                .orElseGet(() -> UserContent.builder()
                        .user(userRepository.getReferenceById(userId)).content(c).build());
    }

    private Map<Long, UserContent> states(Long userId, List<Content> list) {
        Map<Long, UserContent> map = new HashMap<>();
        if (list.isEmpty()) return map;
        userContentRepository.findByUser_IdAndContent_IdIn(userId, list.stream().map(Content::getId).toList())
                .forEach(uc -> map.put(uc.getContent().getId(), uc));
        return map;
    }

    private static String clean(String s) {
        return s == null ? "" : s.trim();
    }

    private static ResponseStatusException bad(String msg) {
        return new ResponseStatusException(HttpStatus.BAD_REQUEST, msg);
    }

    static ContentView toView(Content c, UserContent uc, boolean full) {
        List<String> tags = (c.getTags() == null || c.getTags().isBlank())
                ? List.of() : Arrays.asList(c.getTags().split(","));
        return new ContentView(c.getId(), c.getKind(), c.getCategory(), c.getType(), c.getTitle(),
                c.getDescription(), c.getDurationMin(), c.getWeekFrom(), c.getWeekTo(),
                c.getThumbnailUrl(), tags,
                uc == null ? "NOT_STARTED" : uc.getStatus(),
                uc == null ? 0 : uc.getProgressPercent(),
                uc != null && uc.isSaved(),
                full ? c.getBody() : null,
                full ? c.getMediaUrl() : null);
    }
}