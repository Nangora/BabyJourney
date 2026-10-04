import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface Content {
  id: number;
  kind: 'LESSON' | 'ACTIVITY';
  category: string;
  format: string;
  title: string;
  description: string | null;
  durationMin: number | null;
  weekFrom: number;
  weekTo: number;
  thumbnailUrl: string | null;
  tags: string[];
  status: 'NOT_STARTED' | 'IN_PROGRESS' | 'COMPLETED';
  progressPercent: number;
  saved: boolean;
  body: string | null;      // chỉ có khi lấy chi tiết
  mediaUrl: string | null;  // chỉ có khi lấy chi tiết
}

export interface ContentQuery {
  kind?: 'LESSON' | 'ACTIVITY';
  category?: string;
  week?: number | null;
  trimester?: number | null;
  q?: string;
  saved?: boolean;
}

// 5 nhóm hoạt động của thiết kế
export const ACTIVITY_CATEGORIES = [
  { key: 'MUSIC', label: 'Âm nhạc', icon: 'headphones', tone: 'peach' },
  { key: 'READING', label: 'Đọc sách', icon: 'book', tone: 'butter' },
  { key: 'TALKING_TO_BABY', label: 'Trò chuyện với bé', icon: 'chat', tone: 'mint' },
  { key: 'RELAXATION', label: 'Thư giãn', icon: 'leaf', tone: 'lav' },
  { key: 'MEDITATION', label: 'Thiền', icon: 'sparkle', tone: 'peach' }
];

export const FORMAT_LABELS: Record<string, string> = {
  ARTICLE: 'Bài viết',
  VISUAL_GUIDE: 'Hướng dẫn hình ảnh',
  AUDIO: 'Âm thanh',
  CHECKLIST: 'Danh sách việc',
  PROMPT: 'Gợi ý',
  READING: 'Đọc'
};

const API = 'http://localhost:8080/api';

@Injectable({ providedIn: 'root' })
export class ContentService {
  private http = inject(HttpClient);

  list(query: ContentQuery = {}): Observable<Content[]> {
    let params = new HttpParams();
    for (const [k, v] of Object.entries(query)) {
      if (v !== undefined && v !== null && v !== '') params = params.set(k, String(v));
    }
    return this.http.get<Content[]>(`${API}/contents`, { params });
  }

  get(id: number): Observable<Content> {
    return this.http.get<Content>(`${API}/contents/${id}`);
  }

  setState(id: number, body: { saved?: boolean; progressPercent?: number }): Observable<Content> {
    return this.http.put<Content>(`${API}/contents/${id}/state`, body);
  }

  complete(id: number): Observable<Content> {
    return this.http.post<Content>(`${API}/contents/${id}/complete`, {});
  }
}
