package com.example.springbootdemo.service;

import com.example.springbootdemo.dto.WellbeingDtos.JournalRequest;
import com.example.springbootdemo.dto.WellbeingDtos.JournalView;
import com.example.springbootdemo.dto.WellbeingDtos.MoodView;
import com.example.springbootdemo.entity.JournalEntry;
import com.example.springbootdemo.entity.MoodEntry;
import com.example.springbootdemo.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class WellbeingService {

    private static final Set<String> MOODS = Set.of("HAPPY", "CALM", "OKAY", "LOW", "ANXIOUS");

    private final MoodEntryRepository moodRepository;
    private final JournalEntryRepository journalRepository;
    private final PregnancyRepository pregnancyRepository;
    private final UserRepository userRepository;

    // ---------- Tâm trạng hôm nay ----------
    @Transactional(readOnly = true)
    public Optional<MoodView> todayMood(Long userId) {
        LocalDate today = LocalDate.now();
        return moodRepository.findByUser_IdAndEntryDate(userId, today)
                .map(m -> new MoodView(m.getEntryDate(), m.getMood()));
    }

    @Transactional
    public MoodView setTodayMood(Long userId, String mood) {
        checkMood(mood);
        LocalDate today = LocalDate.now();
        MoodEntry entry = moodRepository.findByUser_IdAndEntryDate(userId, today)
                .orElseGet(() -> MoodEntry.builder()
                        .user(userRepository.getReferenceById(userId)).entryDate(today).build());
        entry.setMood(mood);
        moodRepository.save(entry);
        return new MoodView(today, mood);
    }

    // ---------- Nhật ký ----------
    @Transactional
    public JournalView addJournal(Long userId, JournalRequest r) {
        String body = r.body() == null ? "" : r.body().strip();
        if (body.isEmpty()) throw bad("Hãy viết vài dòng trước khi lưu");
        if (body.length() > 5000) throw bad("Nội dung quá dài (tối đa 5000 ký tự)");

        LocalDate date = r.entryDate() == null ? LocalDate.now() : r.entryDate();
        if (date.isAfter(LocalDate.now())) throw bad("Không thể ghi nhật ký cho ngày trong tương lai");

        String mood = r.mood();
        if (mood != null && !mood.isBlank()) {
            checkMood(mood);
        } else {
            // Nếu hôm đó đã chọn tâm trạng thì gắn vào bài nhật ký
            mood = moodRepository.findByUser_IdAndEntryDate(userId, date).map(MoodEntry::getMood).orElse(null);
        }

        Integer week = pregnancyRepository.findFirstByUser_IdOrderByIdDesc(userId)
                .map(p -> (int) Math.floorDiv(280 - ChronoUnit.DAYS.between(date, p.getDueDate()), 7L))
                .orElse(null);

        JournalEntry saved = journalRepository.save(JournalEntry.builder()
                .user(userRepository.getReferenceById(userId))
                .entryDate(date).weekNumber(week).title(titleOf(body)).body(body).mood(mood).build());
        return toView(saved);
    }

    @Transactional(readOnly = true)
    public List<JournalView> listJournal(Long userId, String mood, Integer limit) {
        int size = limit == null ? 20 : Math.max(1, Math.min(50, limit));
        var page = PageRequest.of(0, size);
        List<JournalEntry> list;
        if (mood != null && !mood.isBlank()) {
            checkMood(mood);
            list = journalRepository.findByUser_IdAndMoodOrderByEntryDateDescIdDesc(userId, mood, page);
        } else {
            list = journalRepository.findByUser_IdOrderByEntryDateDescIdDesc(userId, page);
        }
        return list.stream().map(this::toView).toList();
    }

    @Transactional(readOnly = true)
    public JournalView getJournal(Long userId, Long id) {
        return toView(findOwned(userId, id));
    }

    @Transactional
    public void deleteJournal(Long userId, Long id) {
        journalRepository.delete(findOwned(userId, id));
    }

    // ---------- tiện ích ----------
    private JournalEntry findOwned(Long userId, Long id) {
        return journalRepository.findByIdAndUser_Id(id, userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy bài nhật ký"));
    }

    private static void checkMood(String mood) {
        if (mood == null || !MOODS.contains(mood)) throw bad("Tâm trạng không hợp lệ");
    }

    private static ResponseStatusException bad(String msg) {
        return new ResponseStatusException(HttpStatus.BAD_REQUEST, msg);
    }

    // Tiêu đề lấy từ câu đầu tiên của bài viết
    private static String titleOf(String body) {
        String first = body.split("[\\n.!?]", 2)[0].strip();
        if (first.isEmpty()) first = body;
        return first.length() > 60 ? first.substring(0, 57) + "..." : first;
    }

    private JournalView toView(JournalEntry j) {
        return new JournalView(j.getId(), j.getEntryDate(), j.getWeekNumber(), j.getTitle(), j.getBody(), j.getMood());
    }
}