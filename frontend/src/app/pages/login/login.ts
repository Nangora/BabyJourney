import { Component, inject, signal } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { ReactiveFormsModule, FormBuilder, Validators } from '@angular/forms';
import { Auth } from '../../core/auth/auth';
import { PregnancyService } from '../../core/pregnancy/pregnancy';
import { AuthLayout } from '../../shared/auth-layout/auth-layout';
import { GoogleButton } from '../../shared/google-button/google-button';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [ReactiveFormsModule, RouterLink, AuthLayout, GoogleButton],
  templateUrl: './login.html'
})
export class Login {
  private fb = inject(FormBuilder);
  private auth = inject(Auth);
  private pregnancy = inject(PregnancyService);
  private route = inject(ActivatedRoute);

  form = this.fb.nonNullable.group({
    email: ['', [Validators.required, Validators.email]],
    password: ['', [Validators.required]],
    remember: [true]
  });

  loading = signal(false);
  errorMessage = signal<string | null>(null);
  notice = signal<string | null>(null);

  constructor() {
    const q = this.route.snapshot.queryParamMap;
    if (q.get('registered')) this.notice.set('Đăng ký thành công! Hãy đăng nhập bằng tài khoản vừa tạo.');
    else if (q.get('reset')) this.notice.set('Đặt lại mật khẩu thành công! Hãy đăng nhập bằng mật khẩu mới.');
    else if (q.get('expired')) this.notice.set('Phiên đăng nhập đã hết hạn, vui lòng đăng nhập lại.');
  }

  submit(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    this.loading.set(true);
    this.errorMessage.set(null);
    this.notice.set(null);

    const { email, password, remember } = this.form.getRawValue();
    this.auth.login({ email, password }, remember).subscribe({
      next: () => {
        this.loading.set(false);
        this.pregnancy.redirectAfterLogin();
      },
      error: (err) => {
        this.loading.set(false);
        this.errorMessage.set(
          err.status === 401 || err.status === 400
            ? 'Email hoặc mật khẩu không đúng'
            : 'Có lỗi xảy ra, vui lòng thử lại'
        );
      }
    });
  }
}
