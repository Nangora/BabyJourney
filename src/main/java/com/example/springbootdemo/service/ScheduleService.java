package com.example.springbootdemo.service;

import com.example.springbootdemo.dto.ScheduleDtos.ReminderRequest;
import com.example.springbootdemo.dto.ScheduleDtos.ScheduleItem;
import com.example.springbootdemo.entity.Appointment;
import com.example.springbootdemo.entity.Reminder;
import com.example.springbootdemo.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ScheduleService {

    private final AppointmentRepository appointmentRepository;
    private final ReminderRepository reminderRepository;
    private final PregnancyRepository pregnancyRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public List<ScheduleItem> month(Long userId, Integer year, Integer month) {
        YearMonth ym = (year == null || month == null) ? YearMonth.now() : YearMonth.of(year, month);
        return range(userId, ym.atDay(1).atStartOfDay(), ym.atEndOfMonth().atTime(LocalTime.MAX));
    }

    @Transactional(readOnly = true)
    public List<ScheduleItem> upcoming(Long userId, Integer limit) {
        int size = limit == null ? 3 : Math.max(1, Math.min(20, limit));
        LocalDate today = LocalDate.now();
        LocalDateTime now = LocalDateTime.now();
        return range(userId, today.atStartOfDay(), today.plusDays(60).atTime(LocalTime.MAX)).stream()
                .filter(i -> i.allDay() ? !i.at().toLocalDate().isBefore(today) : !i.at().isBefore(now))
                .limit(size)
                .toList();
    }

    @Transactional
    public ScheduleItem addReminder(Long userId, ReminderRequest r) {
        String title = r.title() == null ? "" : r.title().strip();
        if (title.isEmpty()) throw bad("Vui lòng nhập tiêu đề");
        if (title.length() > 150) throw bad("Tiêu đề quá dài");
        if (r.remindAt() == null) throw bad("Vui lòng chọn thời gian");
        String note = r.note() == null || r.note().isBlank() ? null : r.note().strip();
        if (note != null && note.length() > 300) throw bad("Ghi chú quá dài");

        Reminder saved = reminderRepository.save(Reminder.builder()
                .user(userRepository.getReferenceById(userId))
                .title(title).note(note).remindAt(r.remindAt()).build());
        return new ScheduleItem("R" + saved.getId(), "REMINDER", saved.getTitle(), saved.getNote(), saved.getRemindAt(), false);
    }

    @Transactional
    public void deleteReminder(Long userId, Long id) {
        Reminder r = reminderRepository.findByIdAndUser_Id(id, userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy nhắc việc"));
        reminderRepository.delete(r);
    }

    // Gộp lịch hẹn khám + nhắc việc + mốc đầu mỗi tuần thai (tính từ ngày dự sinh)
    private List<ScheduleItem> range(Long userId, LocalDateTime from, LocalDateTime to) {
        List<ScheduleItem> items = new ArrayList<>();

        for (Appointment a : appointmentRepository.findByUser_IdAndAppointmentTimeBetween(userId, from, to)) {
            if (!List.of("PENDING", "CONFIRMED").contains(a.getStatus())) continue;
            items.add(new ScheduleItem("A" + a.getId(), "APPOINTMENT",
                    "Khám với " + a.getDoctor().getFullName(), a.getDoctor().getClinicName(),
                    a.getAppointmentTime(), false));
        }

        for (Reminder r : reminderRepository.findByUser_IdAndRemindAtBetween(userId, from, to)) {
            items.add(new ScheduleItem("R" + r.getId(), "REMINDER", r.getTitle(), r.getNote(), r.getRemindAt(), false));
        }

        pregnancyRepository.findFirstByUser_IdOrderByIdDesc(userId).ifPresent(p -> {
            LocalDate start = p.getDueDate().minusDays(280);
            for (int w = 1; w <= 40; w++) {
                LocalDateTime at = start.plusDays(7L * w).atStartOfDay();
                if (!at.isBefore(from) && !at.isAfter(to)) {
                    items.add(new ScheduleItem("W" + w, "MILESTONE", "Chào tuần " + w,
                            "Khám phá nội dung mới của tuần", at, true));
                }
            }
        });

        items.sort(Comparator.comparing(ScheduleItem::at));
        return items;
    }

    private static ResponseStatusException bad(String msg) {
        return new ResponseStatusException(HttpStatus.BAD_REQUEST, msg);
    }
}