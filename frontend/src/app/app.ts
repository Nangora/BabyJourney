import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { Header } from './layout/header/header';
import { Footer } from './layout/footer/footer';
import { Sidebar } from './layout/sidebar/sidebar';

@Component({
  selector: 'app-root',
  standalone: true,
  // Phai khai bao du 4 thu nay trong imports vi Angular 22 dung standalone
  // component (khong con NgModule trung gian de "dang ky" component nua).
  // Neu thieu 1 cai nao trong day, Angular se bao loi khong nhan dien duoc
  // the <app-header>/<app-footer>/<app-sidebar>/<router-outlet> trong HTML.
  imports: [RouterOutlet, Header, Footer, Sidebar],
  templateUrl: './app.html',
  styleUrl: './app.scss'
})
export class App {}
