package com.example.springbootdemo.service;

import com.example.springbootdemo.dto.ContentDtos.PlanItem;
import com.example.springbootdemo.dto.ContentDtos.TodayView;
import com.example.springbootdemo.dto.ContentDtos.WeekProgress;
import com.example.springbootdemo.entity.Content;
import com.example.springbootdemo.entity.UserContent;
import com.example.springbootdemo.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ProgressService {

    private final PregnancyRepository pregnancyRepository;
    private final ContentRepository contentRepository;
    private final UserContentRepository userContentRepository;
    private final ActivityLogRepository activityLogRepository;
    private final JournalEntryRepository journalRepository;

    private int currentWeek(Long userId) {
        return pregnancyRepository.findFirstByUser_IdOrderByIdDesc(userId)
                .map(p -> PregnancyService.weekOf(p.getDueDate())).orElse(0);
    }

    // Tuần tính từ Thứ Hai đến Chủ Nhật, đúng với lịch trong thiết kế
    @Transactional(readOnly = true)
    public WeekProgress weekProgress(Long userId) {
        int week = currentWeek(userId);
        LocalDate monday = LocalDate.now().with(DayOfWeek.MONDAY);
        LocalDate sunday = monday.plusDays(6);

        long total = contentRepository
                .countByKindAndWeekFromLessThanEqualAndWeekToGreaterThanEqual("LESSON", week, week);
        long explored = userContentRepository.countExplored(userId, week);
        long days = activityLogRepository.countPracticeDays(userId, monday, sunday);
        long journals = journalRepository.countByUser_IdAndDeletedFalseAndEntryDateBetween(userId, monday, sunday);

        return new WeekProgress(week, (int) total, (int) explored, (int) days, 7, (int) journals);
    }

    // "Kế hoạch nhẹ nhàng hôm nay": 1 bài học + 1 hoạt động + nhật ký
    @Transactional(readOnly = true)
    public TodayView today(Long userId) {
        int week = currentWeek(userId);
        LocalDate today = LocalDate.now();
        List<PlanItem> items = new ArrayList<>();

        List<Content> lessons = contentRepository.search("LESSON", "", week, week, "");
        if (!lessons.isEmpty()) {
            Map<Long, UserContent> st = states(userId, lessons);
            Content pick = lessons.stream().filter(c -> !isDone(st.get(c.getId())))
                    .findFirst().orElse(lessons.get(0));
            items.add(new PlanItem("Đọc", pick.getId(), pick.getTitle(), meta(pick), isDone(st.get(pick.getId()))));
        }

        List<Content> acts = contentRepository.search("ACTIVITY", "", week, week, "");
        if (!acts.isEmpty()) {
            Content pick = acts.get(today.getDayOfYear() % acts.size());
            boolean done = activityLogRepository
                    .existsByUser_IdAndContent_IdAndCompletedOn(userId, pick.getId(), today);
            items.add(new PlanItem("AUDIO".equals(pick.getType()) ? "Nghe" : "Thực hành",
                    pick.getId(), pick.getTitle(), meta(pick), done));
        }

        items.add(new PlanItem("Ghi lại", null, "Nhật ký hằng ngày của tôi", "3 phút",
                journalRepository.existsByUser_IdAndEntryDateAndDeletedFalse(userId, today)));

        return new TodayView(today, items);
    }

    private Map<Long, UserContent> states(Long userId, List<Content> list) {
        Map<Long, UserContent> map = new HashMap<>();
        userContentRepository.findByUser_IdAndContent_IdIn(userId, list.stream().map(Content::getId).toList())
                .forEach(uc -> map.put(uc.getContent().getId(), uc));
        return map;
    }

    private static boolean isDone(UserContent uc) {
        return uc != null && "COMPLETED".equals(uc.getStatus());
    }

    private static String meta(Content c) {
        return c.getDurationMin() == null ? "" : c.getDurationMin() + " phút";
    }
}