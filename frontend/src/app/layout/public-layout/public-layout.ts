import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { Header } from '../header/header';
import { Footer } from '../footer/footer';

@Component({
  selector: 'app-public-layout',
  imports: [RouterOutlet, Header, Footer],
  template: `<app-header /><main><router-outlet /></main><app-footer />`
})
export class PublicLayout {}
