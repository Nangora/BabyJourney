import { Injectable, inject, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, catchError, finalize, of, tap } from 'rxjs';

export interface UserResponse {
  id: number;
  fullName: string;
  email: string;
  phone: string | null;
  role: string;
  createdAt: string;
}

export interface AuthResponse {
  token: string;
  user: UserResponse;
}

export interface LoginRequest {
  email: string;
  password: string;
}

export interface RegisterRequest {
  fullName: string;
  email: string;
  password: string;
  confirmPassword: string;
}

export interface ResetPasswordRequest {
  token: string;
  newPassword: string;
  confirmPassword: string;
}

export interface ChangePasswordRequest {
  currentPassword: string;
  newPassword: string;
  confirmPassword: string;
}

export interface UpdateProfileRequest {
  fullName: string;
  phone: string;
}

export interface MessageResponse {
  message: string;
}

const API_BASE_URL = 'http://localhost:8080/api';
const TOKEN_KEY = 'babyjourney_token';

@Injectable({ providedIn: 'root' })
export class Auth {
  private http = inject(HttpClient);

  currentUser = signal<UserResponse | null>(null);

  login(data: LoginRequest, remember = true): Observable<AuthResponse> {
    return this.http
      .post<AuthResponse>(`${API_BASE_URL}/auth/login`, data)
      .pipe(tap((res) => this.setSession(res, remember)));
  }

  googleLogin(idToken: string, remember = true): Observable<AuthResponse> {
    return this.http
      .post<AuthResponse>(`${API_BASE_URL}/auth/google`, { idToken })
      .pipe(tap((res) => this.setSession(res, remember)));
  }

  register(data: RegisterRequest): Observable<UserResponse> {
    return this.http.post<UserResponse>(`${API_BASE_URL}/auth/register`, data);
  }

  forgotPassword(email: string): Observable<MessageResponse> {
    return this.http.post<MessageResponse>(`${API_BASE_URL}/auth/forgot-password`, { email });
  }

  resetPassword(data: ResetPasswordRequest): Observable<MessageResponse> {
    return this.http.post<MessageResponse>(`${API_BASE_URL}/auth/reset-password`, data);
  }

  changePassword(data: ChangePasswordRequest): Observable<AuthResponse> {
    // Server trả token mới (token cũ bị vô hiệu) nên phải lưu lại
    return this.http
      .post<AuthResponse>(`${API_BASE_URL}/auth/change-password`, data)
      .pipe(tap((res) => this.setSession(res)));
  }

  fetchMe(): Observable<UserResponse> {
    return this.http
      .get<UserResponse>(`${API_BASE_URL}/auth/me`)
      .pipe(tap((user) => this.currentUser.set(user)));
  }

  updateProfile(data: UpdateProfileRequest): Observable<UserResponse> {
    return this.http
      .put<UserResponse>(`${API_BASE_URL}/auth/me`, data)
      .pipe(tap((user) => this.currentUser.set(user)));
  }

  // Báo server hủy token, dù server lỗi thì vẫn xóa phiên ở máy
  logout(): Observable<unknown> {
    return this.http.post(`${API_BASE_URL}/auth/logout`, {}).pipe(
      catchError(() => of(null)),
      finalize(() => this.clearSession())
    );
  }

  clearSession(): void {
    localStorage.removeItem(TOKEN_KEY);
    sessionStorage.removeItem(TOKEN_KEY);
    this.currentUser.set(null);
  }

  getToken(): string | null {
    return localStorage.getItem(TOKEN_KEY) ?? sessionStorage.getItem(TOKEN_KEY);
  }

  isLoggedIn(): boolean {
    return !!this.getToken();
  }

  // remember = true: lưu lâu dài (localStorage). false: hết khi đóng tab (sessionStorage).
  // Không truyền remember (vd: đổi mật khẩu) thì giữ nguyên nơi đang lưu.
  private setSession(res: AuthResponse, remember?: boolean): void {
    const persist = remember ?? !!localStorage.getItem(TOKEN_KEY);
    localStorage.removeItem(TOKEN_KEY);
    sessionStorage.removeItem(TOKEN_KEY);
    (persist ? localStorage : sessionStorage).setItem(TOKEN_KEY, res.token);
    this.currentUser.set(res.user);
  }
}
