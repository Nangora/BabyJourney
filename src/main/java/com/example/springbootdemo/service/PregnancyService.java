package com.example.springbootdemo.service;

import com.example.springbootdemo.dto.PregnancyRequest;
import com.example.springbootdemo.dto.PregnancyResponse;
import com.example.springbootdemo.entity.Pregnancy;
import com.example.springbootdemo.entity.User;
import com.example.springbootdemo.repository.PregnancyRepository;
import com.example.springbootdemo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
@RequiredArgsConstructor
public class PregnancyService {

    private static final Set<String> GOALS = Set.of("UNDERSTAND", "CONNECT", "CALM", "PREPARED", "REFLECT");
    private static final Set<String> STYLES = Set.of("READING", "AUDIO", "VIDEO");
    private static final Set<Integer> MINUTES = Set.of(5, 10, 20);

    private final PregnancyRepository pregnancyRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public Optional<PregnancyResponse> getMine(Long userId) {
        return pregnancyRepository.findFirstByUser_IdOrderByIdDesc(userId).map(this::toResponse);
    }

    @Transactional
    public PregnancyResponse save(Long userId, PregnancyRequest r) {
        if (r.getDueDate() == null) throw bad("Vui lòng nhập ngày dự sinh");
        int week = weekOf(r.getDueDate());
        if (week < 0 || week > 42) throw bad("Ngày dự sinh không hợp lệ");

        if (r.getDailyMinutes() != null && !MINUTES.contains(r.getDailyMinutes())) {
            throw bad("Thời lượng mỗi ngày không hợp lệ");
        }

        User user = userRepository.findById(userId)
                .filter(User::isActive)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Phiên đăng nhập không hợp lệ"));

        Pregnancy p = pregnancyRepository.findFirstByUser_IdOrderByIdDesc(userId)
                .orElseGet(() -> Pregnancy.builder().user(user).build());

        p.setDueDate(r.getDueDate());
        p.setGoals(join(r.getGoals(), GOALS));
        p.setDailyMinutes(r.getDailyMinutes());
        p.setLearningStyles(join(r.getLearningStyles(), STYLES));
        p.setEmailReminder(Boolean.TRUE.equals(r.getEmailReminder()));

        return toResponse(pregnancyRepository.save(p));
    }

    public static int weekOf(LocalDate dueDate) {
        long daysUntilDue = ChronoUnit.DAYS.between(LocalDate.now(), dueDate);
        return (int) Math.floorDiv(280 - daysUntilDue, 7L);
    }

    private static String join(List<String> values, Set<String> allowed) {
        if (values == null || values.isEmpty()) return null;
        Set<String> clean = new LinkedHashSet<>();
        for (String v : values) {
            if (!allowed.contains(v)) throw bad("Giá trị lựa chọn không hợp lệ");
            clean.add(v);
        }
        return String.join(",", clean);
    }

    private static List<String> split(String s) {
        return (s == null || s.isBlank()) ? List.of() : Arrays.asList(s.split(","));
    }

    private static ResponseStatusException bad(String msg) {
        return new ResponseStatusException(HttpStatus.BAD_REQUEST, msg);
    }

    private PregnancyResponse toResponse(Pregnancy p) {
        int week = weekOf(p.getDueDate());
        return PregnancyResponse.builder()
                .dueDate(p.getDueDate())
                .goals(split(p.getGoals()))
                .dailyMinutes(p.getDailyMinutes())
                .learningStyles(split(p.getLearningStyles()))
                .emailReminder(p.isEmailReminder())
                .currentWeek(week)
                .trimester(week < 14 ? 1 : week < 28 ? 2 : 3)
                .build();
    }
}