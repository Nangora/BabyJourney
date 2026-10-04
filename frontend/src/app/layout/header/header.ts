import { Component, inject, signal } from '@angular/core';
import { Router, RouterLink, RouterLinkActive } from '@angular/router';
import { Auth } from '../../core/auth/auth';
import { Icon } from '../../shared/icon/icon';
import { PregnancyService } from '../../core/pregnancy/pregnancy';


@Component({
  selector: 'app-header',
  imports: [RouterLink, RouterLinkActive, Icon],
  templateUrl: './header.html',
  styleUrl: './header.scss',
})
export class Header {
  auth = inject(Auth);
  private router = inject(Router);

  open = signal(false);
  // true khi có token nhưng chưa lấy xong thông tin người dùng (tránh nháy nút Đăng nhập)
  checking = signal(false);

  constructor() {
    if (this.auth.isLoggedIn() && !this.auth.currentUser()) {
      this.checking.set(true);
      this.auth.fetchMe().subscribe({
        next: () => this.checking.set(false),
        error: () => this.checking.set(false)
      });
    }
  }

  private pregnancy = inject(PregnancyService);

  logout(): void {
    this.pregnancy.reset();
    this.auth.logout().subscribe(() => this.router.navigateByUrl('/'));
  }
}
