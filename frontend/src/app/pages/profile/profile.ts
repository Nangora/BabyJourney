import { Component, OnInit, inject, signal } from '@angular/core';
import { ReactiveFormsModule, FormBuilder, Validators } from '@angular/forms';
import { Auth } from '../../core/auth/auth';

@Component({
  selector: 'app-profile',
  standalone: true,
  imports: [ReactiveFormsModule],
  templateUrl: './profile.html',
  styleUrl: '../login/login.scss'
})
export class Profile implements OnInit {
  private fb = inject(FormBuilder);
  auth = inject(Auth);

  form = this.fb.nonNullable.group({
    fullName: ['', [Validators.required, Validators.maxLength(150)]],
    phone: ['', [Validators.pattern(/^[0-9+\s-]{9,15}$/)]]
  });

  loading = signal(false);
  errorMessage = signal<string | null>(null);
  successMessage = signal<string | null>(null);

  ngOnInit(): void {
    const u = this.auth.currentUser();
    if (u) {
      this.fill(u.fullName, u.phone);
    } else {
      this.auth.fetchMe().subscribe((me) => this.fill(me.fullName, me.phone));
    }
  }

  private fill(fullName: string, phone: string | null): void {
    this.form.patchValue({ fullName, phone: phone ?? '' });
  }

  submit(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }
    this.loading.set(true);
    this.errorMessage.set(null);
    this.successMessage.set(null);

    this.auth.updateProfile(this.form.getRawValue()).subscribe({
      next: () => {
        this.loading.set(false);
        this.successMessage.set('Cập nhật thông tin thành công');
      },
      error: (err) => {
        this.loading.set(false);
        this.errorMessage.set(err.error?.message ?? 'Có lỗi xảy ra, vui lòng thử lại');
      }
    });
  }
}
