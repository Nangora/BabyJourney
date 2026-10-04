import { Component, computed, inject, signal } from '@angular/core';
import { RouterLink } from '@angular/router';
import { Auth } from '../../core/auth/auth';
import { Appointment, AppointmentService } from '../../core/appointment/appointment';
import { PregnancyService, TRIMESTER_NAMES, formatVi } from '../../core/pregnancy/pregnancy';
import { Icon } from '../../shared/icon/icon';

const HERO_TEXT: Record<number, string> = {
  1: 'Cơ thể bạn đang thay đổi từng ngày. Tuần này, hãy nghỉ ngơi, uống đủ nước và làm quen với những cảm giác mới.',
  2: 'Các giác quan của bé đang phát triển. Tuần này, hãy khám phá âm thanh, tìm một khoảnh khắc yên tĩnh và tự hào về chặng đường đã qua.',
  3: 'Bé sắp chào đời. Tuần này, hãy dành chỗ cho nghỉ ngơi, chuẩn bị nhẹ nhàng và tin vào bản thân.'
};

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [RouterLink, Icon],
  templateUrl: './dashboard.html',
  styleUrl: './dashboard.scss'
})
export class Dashboard {
  auth = inject(Auth);
  private pregnancy = inject(PregnancyService);
  private appointmentApi = inject(AppointmentService);

  profile = this.pregnancy.profile; // undefined = đang tải, null = chưa có hồ sơ

  appointments = signal<Appointment[]>([]);

  moods = [
    { key: 'HAPPY', emoji: '😊', label: 'Vui' },
    { key: 'CALM', emoji: '😌', label: 'Bình yên' },
    { key: 'OKAY', emoji: '😐', label: 'Bình thường' },
    { key: 'LOW', emoji: '😔', label: 'Thấp' },
    { key: 'ANXIOUS', emoji: '😟', label: 'Lo lắng' }
  ];
  mood = signal<string | null>(null);

  // Dữ liệu minh hoạ cho đến khi các module học tập/nhật ký hoàn thiện
  plan = [
    { icon: 'book', title: 'Tìm hiểu về giác quan của bé', meta: '6 phút • Tiếp tục bài học', action: 'Đang học', state: 'progress' },
    { icon: 'headphones', title: 'Một bài hát cho bé yêu', meta: '10 phút • Âm nhạc • Nhẹ nhàng', action: 'Bắt đầu', state: 'start' },
    { icon: 'pen', title: 'Dành một khoảng lặng để ghi lại', meta: '3 phút • Nhật ký riêng tư', action: 'Viết', state: 'start' }
  ];
  picks = [
    { img: 'images/dashboard-pick-1.png', meta: 'Tuần 24 • Bài viết • 6 phút', title: 'Giác quan đang phát triển của bé', link: 'Tiếp tục bài học →' },
    { img: 'images/dashboard-pick-2.png', meta: 'Sức khoẻ • Âm thanh • 8 phút', title: 'Dành chỗ cho một chút nghỉ ngơi', link: 'Bắt đầu học →' }
  ];

  greeting = (() => {
    const h = new Date().getHours();
    return h < 12 ? 'Chào buổi sáng' : h < 18 ? 'Chào buổi chiều' : 'Chào buổi tối';
  })();

  firstName = computed(() => {
    const name = this.auth.currentUser()?.fullName?.trim();
    return name ? name.split(/\s+/).pop()! : 'bạn';
  });

  week = computed(() => this.profile()?.currentWeek ?? 0);
  weeksLeft = computed(() => Math.max(0, 40 - this.week()));
  trimester = computed(() => this.profile()?.trimester ?? 0);
  trimesterText = computed(() => TRIMESTER_NAMES[this.trimester()] ?? '');
  heroText = computed(() => HERO_TEXT[this.trimester()] ?? '');
  dueText = computed(() => (this.profile() ? formatVi(this.profile()!.dueDate) : ''));

  moodLabel = computed(() => this.moods.find((m) => m.key === this.mood())?.label ?? '');

  // Lịch hẹn sắp tới gần nhất (chưa hủy, chưa qua)
  next = computed(() => {
    const now = Date.now();
    return (
      this.appointments()
        .filter((a) => ['PENDING', 'CONFIRMED'].includes(a.status) && new Date(a.appointmentTime).getTime() >= now)
        .sort((a, b) => new Date(a.appointmentTime).getTime() - new Date(b.appointmentTime).getTime())[0] ?? null
    );
  });

  constructor() {
    this.mood.set(this.readMood());
    this.appointmentApi.getMine().subscribe({
      next: (list) => this.appointments.set(list),
      error: () => this.appointments.set([])
    });
  }

  whenText(iso: string): string {
    return new Date(iso).toLocaleString('vi-VN', {
      weekday: 'long', day: 'numeric', month: 'numeric', hour: '2-digit', minute: '2-digit'
    });
  }

  pickMood(key: string): void {
    this.mood.set(key);
    try { localStorage.setItem(this.moodKey(), key); } catch { /* bỏ qua */ }
  }

  private moodKey(): string {
    const d = new Date().toISOString().slice(0, 10);
    return `bj_mood_${this.auth.currentUser()?.id ?? 'x'}_${d}`;
  }

  private readMood(): string | null {
    try { return localStorage.getItem(this.moodKey()); } catch { return null; }
  }
}
