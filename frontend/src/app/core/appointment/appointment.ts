import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface Appointment {
  id: number;
  doctorId: number;
  doctorName: string;
  clinicName: string | null;
  appointmentTime: string;
  status: string;
  notes: string | null;
  resultNotes: string | null;
  createdAt: string;
}

export interface Doctor {
  id: number;
  fullName: string;
  specialty: string;
  clinicName: string | null;
  clinicAddress: string | null;
  pricePerSession: number | null;
}

const API = 'http://localhost:8080/api';

@Injectable({ providedIn: 'root' })
export class AppointmentService {
  private http = inject(HttpClient);

  getMine(): Observable<Appointment[]> {
    return this.http.get<Appointment[]>(`${API}/appointments/me`);
  }

  getDoctors(): Observable<Doctor[]> {
    return this.http.get<Doctor[]>(`${API}/doctors`);
  }

  book(doctorId: number, appointmentTime: string, notes: string): Observable<Appointment> {
    return this.http.post<Appointment>(`${API}/appointments`, { doctorId, appointmentTime, notes: notes || null });
  }
}
