import { Component, computed, inject } from '@angular/core';
import { RouterLink } from '@angular/router';
import { Auth } from '../../core/auth/auth';
import { Icon } from '../../shared/icon/icon';

@Component({
  selector: 'app-landing',
  standalone: true,
  imports: [RouterLink, Icon],
  templateUrl: './landing.html',
  styleUrl: './landing.scss'
})
export class Landing {
  private auth = inject(Auth);

  // Đã đăng nhập thì các nút kêu gọi đưa vào bảng điều khiển, chưa thì đưa đi đăng ký
  cta = computed(() => (this.auth.currentUser() ? '/dashboard' : '/register'));
  ctaLabel = computed(() => (this.auth.currentUser() ? 'Vào bảng điều khiển' : 'Bắt đầu ngay'));

  strip = [
    { title: 'Học dễ hiểu', text: 'Giải thích rõ ràng, không gây quá tải.' },
    { title: 'Nhịp độ phù hợp', text: 'Hoạt động ngắn. Có khoảng nghỉ.' },
    { title: 'Đồng hành mọi giai đoạn', text: 'Từ tam cá nguyệt đầu đến những gì tiếp theo.' }
  ];

  trimesters = [
    { icon: 'sprout', weeks: 'Tuần 1–13', title: 'Tam cá nguyệt thứ nhất', text: 'Khởi đầu mới, năng lượng thay đổi và làm quen với cơ thể mình.', tone: 'peach' },
    { icon: 'flower', weeks: 'Tuần 14–27', title: 'Tam cá nguyệt thứ hai', text: 'Cùng lớn lên, kết nối với bé và tìm nhịp điệu của riêng mình.', tone: 'mint' },
    { icon: 'sun', weeks: 'Tuần 28–40+', title: 'Tam cá nguyệt thứ ba', text: 'Chuẩn bị cho ngày chào đời, dành chỗ cho nghỉ ngơi và nhìn về phía trước.', tone: 'lav' }
  ];

  steps = [
    { no: '01', title: 'Cá nhân hoá', text: 'Cho chúng tôi biết tuần thai và điều bạn muốn khám phá.' },
    { no: '02', title: 'Học một chút', text: 'Khám phá các bài học dễ theo dõi, chọn theo giai đoạn của bạn.' },
    { no: '03', title: 'Tìm nhịp của mình', text: 'Thử một hoạt động nhẹ nhàng, ghi lại cảm xúc và theo dõi hành trình.' }
  ];

  activities = [
    { icon: 'headphones', title: 'Âm nhạc', meta: '10 phút • Nghe và thư giãn', tone: 'peach' },
    { icon: 'book', title: 'Đọc sách', meta: '8 phút • Một khoảnh khắc yên tĩnh', tone: 'butter' },
    { icon: 'chat', title: 'Trò chuyện với bé', meta: '5 phút • Kết nối', tone: 'mint' },
    { icon: 'leaf', title: 'Thư giãn', meta: '10 phút • Chậm lại', tone: 'lav' },
    { icon: 'sparkle', title: 'Thiền', meta: '5 phút • Tìm sự tĩnh lặng', tone: 'peach' }
  ];

  articles = [
    { img: 'images/article-1.png', topic: 'Sự phát triển của bé', time: '6 phút đọc', title: 'Hiểu về các giác quan của bé' },
    { img: 'images/article-2.png', topic: 'Sức khoẻ hằng ngày', time: '4 phút đọc', title: 'Dành chỗ cho nghỉ ngơi' },
    { img: 'images/article-3.png', topic: 'Chăm sóc thai kỳ', time: '5 phút đọc', title: 'Câu hỏi nên mang theo cho lần khám tới' }
  ];

  checklist = [
    'Nội dung giáo dục rõ ràng, dễ tiếp cận',
    'Hoạt động bạn có thể điều chỉnh hoặc bỏ qua',
    'Không gian riêng tư cho những suy ngẫm của bạn',
    'Thông tin hồ sơ của bạn được giữ kín'
  ];
}
