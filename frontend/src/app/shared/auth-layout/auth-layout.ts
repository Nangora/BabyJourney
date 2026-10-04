import { Component, input } from '@angular/core';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-auth-layout',
  imports: [RouterLink],
  templateUrl: './auth-layout.html',
  styleUrl: './auth-layout.scss'
})
export class AuthLayout {
  badge = input('Một chút đồng hành mỗi ngày');
  heading = input('Dành cho bạn. Cho hành trình phía trước.');
  subtitle = input('Một không gian bình yên để học hỏi, kết nối với bé và dành thời gian cho chính mình.');
  image = input('images/auth-hero.png');
  caption = input('Nhịp độ của bạn. Lựa chọn của bạn. Thai kỳ của bạn.');
}
