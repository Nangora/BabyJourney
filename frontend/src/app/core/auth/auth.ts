import { Injectable, inject, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, tap } from 'rxjs';

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
  email: string;
  password: string;
  fullName: string;
}

const API_BASE_URL = 'http://localhost:8080/api';
const TOKEN_KEY = 'babyjourney_token';

@Injectable({ providedIn: 'root' })
export class Auth {
  private http = inject(HttpClient);

  // signal luu thong tin user dang dang nhap, cac component khac
  // (header, sidebar...) co the doc truc tiep de hien thi ten/avatar
  currentUser = signal<UserResponse | null>(null);

  login(data: LoginRequest): Observable<AuthResponse> {
    return this.http
      .post<AuthResponse>(`${API_BASE_URL}/auth/login`, data)
      .pipe(tap((res) => this.setSession(res)));
  }

  register(data: RegisterRequest): Observable<UserResponse> {
    return this.http.post<UserResponse>(`${API_BASE_URL}/auth/register`, data);
  }

  fetchMe(): Observable<UserResponse> {
    return this.http
      .get<UserResponse>(`${API_BASE_URL}/auth/me`)
      .pipe(tap((user) => this.currentUser.set(user)));
  }

  logout(): void {
    localStorage.removeItem(TOKEN_KEY);
    this.currentUser.set(null);
  }

  getToken(): string | null {
    return localStorage.getItem(TOKEN_KEY);
  }

  isLoggedIn(): boolean {
    return !!this.getToken();
  }

  private setSession(res: AuthResponse): void {
    localStorage.setItem(TOKEN_KEY, res.token);
    this.currentUser.set(res.user);
  }
}
