import { Component, computed, inject, signal } from '@angular/core';
import { Router, RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';
import { Auth } from '../../core/auth/auth';
import { PregnancyService, TRIMESTER_NAMES, formatVi } from '../../core/pregnancy/pregnancy';
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

  p = this.pregnancy.profile;
  navOpen = signal(false);

  nav = [
    { path: '/dashboard', label: 'Bảng điều khiển', icon: 'grid' },
    { path: '/education', label: 'Kiến thức', icon: 'book' },
    { path: '/activities', label: 'Hoạt động', icon: 'heart' },
    { path: '/schedule', label: 'Lịch & tiến độ', icon: 'calendar' },
    { path: '/journal', label: 'Tâm trạng & nhật ký', icon: 'pen' }
  ];

  initial = computed(() => (this.auth.currentUser()?.fullName?.trim().charAt(0) ?? '?').toUpperCase());
  trimesterText = computed(() => TRIMESTER_NAMES[this.p()?.trimester ?? 0] ?? '');
  dueText = computed(() => (this.p() ? formatVi(this.p()!.dueDate) : ''));

  constructor() {
    if (!this.auth.currentUser()) this.auth.fetchMe().subscribe({ error: () => {} });
    if (this.pregnancy.profile() === undefined) {
      this.pregnancy.load().subscribe({ error: () => this.pregnancy.profile.set(null) });
    }
  }

  logout(): void {
    this.pregnancy.reset();
    this.auth.logout().subscribe(() => this.router.navigateByUrl('/'));
  }
}
