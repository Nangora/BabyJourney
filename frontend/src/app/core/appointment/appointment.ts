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
  createdAt: string;
}

@Injectable({ providedIn: 'root' })
export class AppointmentService {
  private http = inject(HttpClient);

  getMine(): Observable<Appointment[]> {
    return this.http.get<Appointment[]>('http://localhost:8080/api/appointments/me');
  }
}
