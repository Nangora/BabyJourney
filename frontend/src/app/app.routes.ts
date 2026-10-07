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
      { path: 'education', component: Education },
      { path: 'education/:id', component: EducationDetail },
      { path: 'activities', component: Activities },
      { path: 'schedule', component: Schedule },
      { path: 'journal', component: Journal },
      { path: 'health', component: Health },
      { path: 'help', component: Help },
      { path: 'birth-prep', component: BirthPrep },
      { path: 'nutrition', component: Nutrition },
      { path: 'gamification', component: Gamification },
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
  { path: 'onboarding', component: Onboarding, canActivate: [authGuard] },

  { path: '**', redirectTo: '' },
];
