import { Component, inject } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { toSignal } from '@angular/core/rxjs-interop';
import { map } from 'rxjs';

@Component({
  selector: 'app-coming-soon',
  standalone: true,
  imports: [RouterLink],
  template: `
    <div class="box">
      <h1>{{ title() }}</h1>
      <p>Phần này đang được xây dựng và sẽ sớm có mặt.</p>
      <a class="btn btn-solid" routerLink="/dashboard">Về bảng điều khiển</a>
    </div>
  `,
  styles: `
    .box { max-width: 520px; padding: 36px; border-radius: 24px; background: #fff; }
    h1 { margin: 0 0 8px; font-size: 28px; }
    p { margin: 0 0 20px; color: var(--muted); }
  `
})
export class ComingSoon {
  title = toSignal(inject(ActivatedRoute).data.pipe(map((d) => d['title'] as string)), { initialValue: '' });
}
