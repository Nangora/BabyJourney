import { Routes } from '@angular/router';
import { PublicLayout } from './layout/public-layout/public-layout';
import { MemberLayout } from './layout/member-layout/member-layout';
import { Landing } from './pages/landing/landing';
import { Dashboard } from './pages/dashboard/dashboard';
import { ComingSoon } from './pages/coming-soon/coming-soon';
import { Login } from './pages/login/login';
import { Register } from './pages/register/register';
import { ForgotPassword } from './pages/forgot-password/forgot-password';
import { ResetPassword } from './pages/reset-password/reset-password';
import { Profile } from './pages/profile/profile';
import { ChangePassword } from './pages/change-password/change-password';
import { Onboarding } from './pages/onboarding/onboarding';
import { authGuard } from './core/auth/auth-guard';
import { Activities } from './pages/activities/activities';

export const routes: Routes = [
  // Trang công khai: header + footer
  { path: '', component: PublicLayout, children: [{ path: '', component: Landing }] },

  // Khu vực thành viên: thanh trên + thanh bên
  {
    path: '',
    component: MemberLayout,
    canActivate: [authGuard],
    children: [
      { path: 'dashboard', component: Dashboard },
      { path: 'education', component: ComingSoon, data: { title: 'Kiến thức' } },
      { path: 'activities', component: Activities, data: { title: 'Hoạt động' } },
      { path: 'schedule', component: ComingSoon, data: { title: 'Lịch & tiến độ' } },
      { path: 'journal', component: ComingSoon, data: { title: 'Tâm trạng & nhật ký' } },
      { path: 'help', component: ComingSoon, data: { title: 'Trợ giúp & hỗ trợ' } },
      { path: 'profile', component: Profile },
      { path: 'change-password', component: ChangePassword },
    ],
  },

  // Trang toàn màn hình
  { path: 'login', component: Login },
  { path: 'register', component: Register },
  { path: 'forgot-password', component: ForgotPassword },
  { path: 'reset-password', component: ResetPassword },
  { path: 'onboarding', component: Onboarding, canActivate: [authGuard] },

  { path: '**', redirectTo: '' },
];
