import { Injectable, inject, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { Observable, tap } from 'rxjs';
import { Auth } from '../auth/auth';

export interface PregnancyProfile {
  dueDate: string;
  goals: string[];
  dailyMinutes: number | null;
  learningStyles: string[];
  emailReminder: boolean;
  currentWeek: number;
  trimester: number;
}

export interface PregnancyRequest {
  dueDate: string;
  goals: string[];
  dailyMinutes: number | null;
  learningStyles: string[];
  emailReminder: boolean;
}

export const TRIMESTER_NAMES = ['', 'Tam cá nguyệt thứ nhất', 'Tam cá nguyệt thứ hai', 'Tam cá nguyệt thứ ba'];

const API = 'http://localhost:8080/api';
const DAY = 86400000;

function parse(iso: string): Date {
  const [y, m, d] = iso.split('-').map(Number);
  return new Date(y, m - 1, d);
}

function today0(): Date {
  const n = new Date();
  return new Date(n.getFullYear(), n.getMonth(), n.getDate());
}

export function toIso(d: Date): string {
  const mm = String(d.getMonth() + 1).padStart(2, '0');
  const dd = String(d.getDate()).padStart(2, '0');
  return `${d.getFullYear()}-${mm}-${dd}`;
}

// Tuần thai = (280 ngày - số ngày còn lại đến ngày dự sinh) / 7
export function weekFromDue(iso: string): number {
  const days = Math.round((parse(iso).getTime() - today0().getTime()) / DAY);
  return Math.floor((280 - days) / 7);
}

export function dueFromWeek(week: number): string {
  const d = today0();
  d.setDate(d.getDate() + 280 - week * 7);
  return toIso(d);
}

export function trimesterOf(week: number): 1 | 2 | 3 {
  return week < 14 ? 1 : week < 28 ? 2 : 3;
}

export function formatVi(iso: string): string {
  const d = parse(iso);
  return `${d.getDate()} tháng ${d.getMonth() + 1}, ${d.getFullYear()}`;
}

@Injectable({ providedIn: 'root' })
export class PregnancyService {
  private http = inject(HttpClient);
  private router = inject(Router);
  private auth = inject(Auth);

  // Hồ sơ dùng chung cho toàn app. undefined = chưa tải, null = người dùng chưa có hồ sơ
  profile = signal<PregnancyProfile | null | undefined>(undefined);

  getMine(): Observable<PregnancyProfile | null> {
    return this.http.get<PregnancyProfile | null>(`${API}/pregnancy/me`);
  }

  load(): Observable<PregnancyProfile | null> {
    return this.getMine().pipe(tap((p) => this.profile.set(p)));
  }

  save(data: PregnancyRequest): Observable<PregnancyProfile> {
    return this.http.put<PregnancyProfile>(`${API}/pregnancy/me`, data).pipe(tap((p) => this.profile.set(p)));
  }

  // Gọi khi đăng xuất để người dùng sau không thấy dữ liệu của người trước
  reset(): void {
    this.profile.set(undefined);
  }

  // Người dùng bấm "Để sau": không ép quay lại màn hồ sơ ở các lần đăng nhập tiếp theo
  markSkipped(): void {
    try { localStorage.setItem(this.skipKey(), '1'); } catch { /* bỏ qua */ }
  }

  private isSkipped(): boolean {
    try { return !!localStorage.getItem(this.skipKey()); } catch { return false; }
  }

  private skipKey(): string {
    return `bj_onboarding_skipped_${this.auth.currentUser()?.id ?? 'x'}`;
  }

  // Sau khi đăng nhập: chưa có hồ sơ thai kỳ thì sang /onboarding, có rồi thì vào bảng điều khiển
  redirectAfterLogin(): void {
    this.load().subscribe({
      next: (p) => this.router.navigateByUrl(p || this.isSkipped() ? '/dashboard' : '/onboarding'),
      error: () => this.router.navigateByUrl('/dashboard')
    });
  }
}
