import { Component, WritableSignal, computed, inject, signal } from '@angular/core';
import { Router } from '@angular/router';
import { AuthLayout } from '../../shared/auth-layout/auth-layout';
import {
  PregnancyService, dueFromWeek, formatVi, toIso, trimesterOf, weekFromDue
} from '../../core/pregnancy/pregnancy';

@Component({
  selector: 'app-onboarding',
  standalone: true,
  imports: [AuthLayout],
  templateUrl: './onboarding.html'
})
export class Onboarding {
  private pregnancy = inject(PregnancyService);
  private router = inject(Router);

  goalOptions = [
    { key: 'UNDERSTAND', icon: '📖', title: 'Hiểu về thai kỳ của tôi', desc: 'Bài học ngắn cho từng giai đoạn.' },
    { key: 'CONNECT', icon: '💗', title: 'Kết nối với bé', desc: 'Âm nhạc, đọc truyện và những lời trò chuyện nhỏ.' },
    { key: 'CALM', icon: '🍃', title: 'Dành thời gian thư giãn', desc: 'Thư giãn và chánh niệm nhẹ nhàng.' },
    { key: 'PREPARED', icon: '📅', title: 'Chuẩn bị tốt hơn', desc: 'Lịch khám, nhắc nhở và hướng dẫn thực tế.' },
    { key: 'REFLECT', icon: '📝', title: 'Ghi lại cảm xúc', desc: 'Theo dõi tâm trạng và nhật ký riêng tư.' }
  ];
  styleOptions = [
    { key: 'READING', label: 'Bài viết ngắn & đọc' },
    { key: 'AUDIO', label: 'Âm thanh & hoạt động có hướng dẫn' },
    { key: 'VIDEO', label: 'Video & hướng dẫn hình ảnh' }
  ];
  minuteOptions = [5, 10, 20];
  trimesterNames = ['', 'Tam cá nguyệt thứ nhất', 'Tam cá nguyệt thứ hai', 'Tam cá nguyệt thứ ba'];

  step = signal(1);
  mode = signal<'due' | 'week'>('due');
  dueDate = signal('');
  weekInput = signal<number | null>(null);
  goals = signal<string[]>([]);
  minutes = signal<number | null>(10);
  styles = signal<string[]>(['READING', 'AUDIO']);
  reminder = signal(false);

  loading = signal(false);
  error = signal<string | null>(null);

  effectiveDue = computed(() => {
    if (this.mode() === 'due') return this.dueDate();
    const w = this.weekInput();
    return w !== null && w >= 1 && w <= 42 ? dueFromWeek(w) : '';
  });

  week = computed(() => {
    const d = this.effectiveDue();
    if (!d) return null;
    const w = weekFromDue(d);
    return w >= 0 && w <= 42 ? w : null;
  });

  trimesterText = computed(() => {
    const w = this.week();
    return w === null ? '' : this.trimesterNames[trimesterOf(w)];
  });

  todayText = formatVi(toIso(new Date()));
  dueText = computed(() => (this.effectiveDue() ? formatVi(this.effectiveDue()) : ''));

  goalsText = computed(() =>
    this.goalOptions.filter((g) => this.goals().includes(g.key)).map((g) => g.title).join(' • ') || 'Chưa chọn mục tiêu'
  );
  stylesText = computed(() =>
    this.styleOptions.filter((s) => this.styles().includes(s.key)).map((s) => s.label).join(' • ')
  );

  onDue(e: Event): void {
    this.dueDate.set((e.target as HTMLInputElement).value);
  }

  onWeek(e: Event): void {
    const v = (e.target as HTMLInputElement).valueAsNumber;
    this.weekInput.set(Number.isNaN(v) ? null : v);
  }

  toggle(list: WritableSignal<string[]>, key: string): void {
    list.update((arr) => (arr.includes(key) ? arr.filter((k) => k !== key) : [...arr, key]));
  }

  next(): void {
    if (this.step() === 1 && this.week() === null) return;
    if (this.step() < 3) this.step.update((s) => s + 1);
    else this.finish();
  }

  back(): void {
    this.step.update((s) => Math.max(1, s - 1));
  }

  // "Để sau": bỏ qua toàn bộ, không lưu gì
  later(): void {
    this.pregnancy.markSkipped();
    this.router.navigateByUrl('/dashboard');
  }

  // "Lưu & thoát": lưu những gì đã nhập (nếu đã có ngày dự sinh hợp lệ)
  saveAndExit(): void {
    if (this.week() === null) this.later();
    else this.finish();
  }

  private finish(): void {
    this.loading.set(true);
    this.error.set(null);
    this.pregnancy
      .save({
        dueDate: this.effectiveDue(),
        goals: this.goals(),
        dailyMinutes: this.minutes(),
        learningStyles: this.styles(),
        emailReminder: this.reminder()
      })
      .subscribe({
        next: () => this.router.navigateByUrl('/dashboard'),
        error: (err) => {
          this.loading.set(false);
          this.error.set(err.error?.message ?? 'Không thể lưu hồ sơ, vui lòng thử lại');
        }
      });
  }
}
