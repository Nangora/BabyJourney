import { Component, computed, inject, signal } from '@angular/core';
import { Router, RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';
import { Auth } from '../../core/auth/auth';
import { PregnancyService, TRIMESTER_NAMES, formatVi } from '../../core/pregnancy/pregnancy';
import { NotificationService } from '../../core/notification/notification';
import { Icon } from '../../shared/icon/icon';

@Component({
  selector: 'app-member-layout',
  imports: [RouterOutlet, RouterLink, RouterLinkActive, Icon],
  templateUrl: './member-layout.html',
  styleUrl: './member-layout.scss'
})
export class MemberLayout {
  auth = inject(Auth);
  private pregnancy = inject(PregnancyService);
  private router = inject(Router);
  private notifService = inject(NotificationService);

  p = this.pregnancy.profile;
  navOpen = signal(false);
  unread = this.notifService.unreadCount;

  private momNav = [
    { path: '/dashboard', label: 'Bảng điều khiển', icon: 'grid' },
    { path: '/education', label: 'Kiến thức', icon: 'book' },
    { path: '/activities', label: 'Hoạt động', icon: 'heart' },
    { path: '/schedule', label: 'Lịch & tiến độ', icon: 'calendar' },
    { path: '/journal', label: 'Tâm trạng & nhật ký', icon: 'pen' },
    { path: '/health', label: 'Sức khỏe', icon: 'heart' },
    { path: '/community', label: 'Cộng đồng', icon: 'heart' },
    { path: '/birth-prep', label: 'Chuẩn bị sinh', icon: 'book' },
    { path: '/nutrition', label: 'Dinh dưỡng', icon: 'book' },
    { path: '/gamification', label: 'Thành tích', icon: 'grid' },
    { path: '/help', label: 'Trợ giúp', icon: 'info' }
  ];

  private doctorNav = [
    { path: '/doctor/appointments', label: 'Lịch khám của tôi', icon: 'calendar' },
    { path: '/community', label: 'Cộng đồng', icon: 'heart' }
  ];

  private adminNav = [
    { path: '/admin', label: 'Tổng quan', icon: 'shield' },
    { path: '/admin/users', label: 'Người dùng', icon: 'users' },
    { path: '/admin/doctors', label: 'Bác sĩ', icon: 'briefcase' },
    { path: '/community', label: 'Cộng đồng', icon: 'heart' }
  ];

  nav = computed(() => {
    const role = this.auth.currentUser()?.role;
    if (!role) return [];
    if (role === 'DOCTOR') return this.doctorNav;
    if (role === 'ADMIN') return this.adminNav;
    if (role === 'USER') return this.momNav;
    return [{ path: '/community', label: 'Cộng đồng', icon: 'heart' }];
  });

  isMom = computed(() => this.auth.currentUser()?.role === 'USER');

  initial = computed(() => (this.auth.currentUser()?.fullName?.trim().charAt(0) ?? '?').toUpperCase());
  trimesterText = computed(() => TRIMESTER_NAMES[this.p()?.trimester ?? 0] ?? '');
  dueText = computed(() => (this.p() ? formatVi(this.p()!.dueDate) : ''));

  constructor() {
    if (!this.auth.currentUser()) this.auth.fetchMe().subscribe({ error: () => {} });
    const role = this.auth.currentUser()?.role;
    const isMom = !role || role === 'USER';
    if (isMom && this.pregnancy.profile() === undefined) {
      this.pregnancy.load().subscribe({ error: () => this.pregnancy.profile.set(null) });
    }
    this.notifService.fetchUnreadCount();
  }

  logout(): void {
    this.pregnancy.reset();
    this.auth.logout().subscribe(() => this.router.navigateByUrl('/'));
  }
}
