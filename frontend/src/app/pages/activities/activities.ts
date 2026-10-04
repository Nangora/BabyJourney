import { Component, OnDestroy, computed, effect, inject, signal, untracked } from '@angular/core';
import { Icon } from '../../shared/icon/icon';
import { ACTIVITY_CATEGORIES, Content, ContentService, FORMAT_LABELS } from '../../core/content/content';
import { PregnancyService } from '../../core/pregnancy/pregnancy';

@Component({
  selector: 'app-activities',
  standalone: true,
  imports: [Icon],
  templateUrl: './activities.html',
  styleUrl: './activities.scss'
})
export class Activities implements OnDestroy {
  private api = inject(ContentService);
  private pregnancy = inject(PregnancyService);

  categories = ACTIVITY_CATEGORIES;

  items = signal<Content[]>([]);
  loading = signal(true);
  category = signal('');
  savedOnly = signal(false);

  selectedId = signal<number | null>(null);
  detail = signal<Content | null>(null);
  busy = signal(false);
  doneToday = signal<number[]>([]);

  // Trình phát âm thanh
  playing = signal(false);
  cur = signal(0);
  total = signal(0);
  audioError = signal(false);
  private audio: HTMLAudioElement | null = null;

  week = computed(() => this.pregnancy.profile()?.currentWeek ?? null);
  savedCount = computed(() => this.items().filter((i) => i.saved).length);

  view = computed(() =>
    this.items().filter(
      (i) => (!this.category() || i.category === this.category()) && (!this.savedOnly() || i.saved)
    )
  );

  steps = computed(() => (this.detail()?.body ?? '').split('\n').map((s) => s.trim()).filter(Boolean));
  pct = computed(() => {
    const t = this.totalSeconds();
    return t > 0 ? Math.min(100, (this.cur() / t) * 100) : 0;
  });
  totalSeconds = computed(() => this.total() || (this.detail()?.durationMin ?? 0) * 60);

  statusText = computed(() => {
    if (this.audioError()) return 'Chưa có tệp âm thanh cho hoạt động này.';
    if (this.playing()) return 'Đang phát';
    return this.cur() > 0 ? 'Đã tạm dừng • Tiếp tục khi bạn sẵn sàng' : 'Sẵn sàng khi bạn muốn';
  });

  constructor() {
    // Chờ hồ sơ thai kỳ tải xong (để biết tuần thai) rồi mới lấy danh sách
    effect(() => {
      const profile = this.pregnancy.profile();
      if (profile === undefined) return;
      untracked(() => this.load(profile?.currentWeek ?? null));
    });
  }

  ngOnDestroy(): void {
    this.teardownAudio();
  }

  private load(week: number | null): void {
    this.loading.set(true);
    this.api.list({ kind: 'ACTIVITY', week }).subscribe({
      next: (list) => {
        this.items.set(list);
        this.loading.set(false);
        const current = this.selectedId();
        if (list.length && (current === null || !list.some((i) => i.id === current))) this.select(list[0]);
      },
      error: () => {
        this.items.set([]);
        this.loading.set(false);
      }
    });
  }

  // ---------- Bộ lọc ----------
  setCategory(key: string): void {
    this.category.set(key);
    this.savedOnly.set(false);
  }

  showSaved(): void {
    this.category.set('');
    this.savedOnly.set(!this.savedOnly());
  }

  // ---------- Chọn, lưu, hoàn thành ----------
  select(a: Content): void {
    this.selectedId.set(a.id);
    this.api.get(a.id).subscribe((d) => {
      this.detail.set(d);
      this.setupAudio(d.format === 'AUDIO' ? d.mediaUrl : null, d.format === 'AUDIO');
    });
  }

  toggleSave(a: Content, ev?: Event): void {
    ev?.stopPropagation();
    this.api.setState(a.id, { saved: !a.saved }).subscribe((res) => this.merge(res));
  }

  complete(): void {
    const d = this.detail();
    if (!d || this.busy()) return;
    this.busy.set(true);
    this.api.complete(d.id).subscribe({
      next: (res) => {
        this.merge(res);
        this.doneToday.update((ids) => [...ids, res.id]);
        this.busy.set(false);
      },
      error: () => this.busy.set(false)
    });
  }

  private merge(res: Content): void {
    this.items.update((arr) =>
      arr.map((i) => (i.id === res.id
        ? { ...i, saved: res.saved, status: res.status, progressPercent: res.progressPercent }
        : i))
    );
    this.detail.update((d) => (d && d.id === res.id ? res : d));
  }

  // ---------- Hiển thị ----------
  meta(c: Content): string {
    const parts = [c.durationMin ? `${c.durationMin} phút` : '', c.tags[0] ?? '', FORMAT_LABELS[c.format] ?? ''];
    return parts.filter(Boolean).join(' • ');
  }

  detailMeta(c: Content): string {
    return [c.durationMin ? `${c.durationMin} phút` : '', ...c.tags].filter(Boolean).join(' • ');
  }

  cat(key: string) {
    return this.categories.find((c) => c.key === key);
  }

  fmt(sec: number): string {
    const s = Math.max(0, Math.floor(sec));
    return `${String(Math.floor(s / 60)).padStart(2, '0')}:${String(s % 60).padStart(2, '0')}`;
  }

  // ---------- Âm thanh ----------
  private setupAudio(url: string | null, isAudio: boolean): void {
    this.teardownAudio();
    this.cur.set(0);
    this.total.set(0);
    this.playing.set(false);
    this.audioError.set(false);
    if (!isAudio) return;
    if (!url) {
      this.audioError.set(true);
      return;
    }
    const a = new Audio(url);
    a.preload = 'metadata';
    a.addEventListener('loadedmetadata', () => this.total.set(Number.isFinite(a.duration) ? a.duration : 0));
    a.addEventListener('timeupdate', () => this.cur.set(a.currentTime));
    a.addEventListener('ended', () => this.playing.set(false));
    a.addEventListener('error', () => this.audioError.set(true));
    this.audio = a;
  }

  togglePlay(): void {
    const a = this.audio;
    if (!a || this.audioError()) return;
    if (a.paused) {
      a.play().then(() => this.playing.set(true)).catch(() => this.audioError.set(true));
    } else {
      a.pause();
      this.playing.set(false);
    }
  }

  private teardownAudio(): void {
    if (this.audio) {
      this.audio.pause();
      this.audio.removeAttribute('src');
      this.audio.load();
      this.audio = null;
    }
  }
}
