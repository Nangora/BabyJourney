import { Routes } from '@angular/router';
import { PublicLayout } from './layout/public-layout/public-layout';
import { MemberLayout } from './layout/member-layout/member-layout';
import { Landing } from './pages/landing/landing';
import { Dashboard } from './pages/dashboard/dashboard';
import { Login } from './pages/login/login';
import { Register } from './pages/register/register';
import { ForgotPassword } from './pages/forgot-password/forgot-password';
import { ResetPassword } from './pages/reset-password/reset-password';
import { Profile } from './pages/profile/profile';
import { ChangePassword } from './pages/change-password/change-password';
import { Onboarding } from './pages/onboarding/onboarding';
import { authGuard } from './core/auth/auth-guard';
import { roleGuard } from './core/auth/role-guard';
import { Activities } from './pages/activities/activities';
import { Education } from './pages/education/education';
import { EducationDetail } from './pages/education/education-detail';
import { Journal } from './pages/journal/journal';
import { Schedule } from './pages/schedule/schedule';
import { Health } from './pages/health/health';
import { Help } from './pages/help/help';
import { Notifications } from './pages/notifications/notifications';
import { Settings } from './pages/settings/settings';
import { BirthPrep } from './pages/birth-prep/birth-prep';
import { Nutrition } from './pages/nutrition/nutrition';
import { Gamification } from './pages/gamification/gamification';
import { Community } from './pages/community/community';

const mom = { canActivate: [roleGuard(['USER'])] };

export const routes: Routes = [
  // Trang công khai: header + footer
  { path: '', component: PublicLayout, children: [{ path: '', component: Landing }] },

  // Khu vực thành viên: thanh trên + thanh bên
  {
    path: '',
    component: MemberLayout,
    canActivate: [authGuard],
    children: [
      // Tính năng của mẹ bầu
      { path: 'dashboard', component: Dashboard, ...mom },
      { path: 'education', component: Education, ...mom },
      { path: 'education/:id', component: EducationDetail, ...mom },
      { path: 'activities', component: Activities, ...mom },
      { path: 'schedule', component: Schedule, ...mom },
      { path: 'journal', component: Journal, ...mom },
      { path: 'health', component: Health, ...mom },
      { path: 'help', component: Help, ...mom },
      { path: 'birth-prep', component: BirthPrep, ...mom },
      { path: 'nutrition', component: Nutrition, ...mom },
      { path: 'gamification', component: Gamification, ...mom },

      // Bác sĩ: quản lý lịch khám của mình
      {
        path: 'doctor/appointments',
        loadComponent: () =>
          import('./pages/doctor-appointments/doctor-appointments').then((m) => m.DoctorAppointments),
        canActivate: [roleGuard(['DOCTOR'])],
      },

      // Quản trị: chỉ ADMIN
      {
        path: 'admin',
        loadComponent: () => import('./pages/admin/admin-overview').then((m) => m.AdminOverview),
        canActivate: [roleGuard(['ADMIN'])],
      },
      {
        path: 'admin/users',
        loadComponent: () => import('./pages/admin/admin-users').then((m) => m.AdminUsers),
        canActivate: [roleGuard(['ADMIN'])],
      },
      {
        path: 'admin/doctors',
        loadComponent: () => import('./pages/admin/admin-doctors').then((m) => m.AdminDoctors),
        canActivate: [roleGuard(['ADMIN'])],
      },

      // Dùng chung mọi vai trò
      { path: 'community', component: Community },
      { path: 'notifications', component: Notifications },
      { path: 'settings', component: Settings },
      { path: 'profile', component: Profile },
      { path: 'change-password', component: ChangePassword },
    ],
  },

  // Trang toàn màn hình
  { path: 'login', component: Login },
  { path: 'register', component: Register },
  { path: 'forgot-password', component: ForgotPassword },
  { path: 'reset-password', component: ResetPassword },
  { path: 'onboarding', component: Onboarding, canActivate: [authGuard, roleGuard(['USER'])] },

  { path: '**', redirectTo: '' },
];
