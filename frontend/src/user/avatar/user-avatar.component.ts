import {Component, Input, OnInit} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {restUsers} from '../../data.service';
import {NgIf} from '@angular/common';
import {User} from '../user';
import {AuthModule} from '../../app/auth.module'; // Убедитесь, что путь правильный

@Component({
  selector: 'app-user-avatar',
  standalone: true,
  imports: [NgIf, AuthModule],
  templateUrl: './user-avatar.component.html',
  styleUrls: ['user-avatar.component.css']
})
export class UserAvatarComponent implements OnInit {
  avatarUrl: string | null = null;
  @Input() user: User | null = null;

  constructor(private http: HttpClient) {
  }

  ngOnInit() {
    const userId = this.user!.id;
    if (userId) {
      this.http.get<Blob>(`${restUsers}/${userId}/avatar`, { responseType: 'blob' as 'json' }).subscribe(blob => {
        this.avatarUrl = URL.createObjectURL(blob);
      }, error => {
        console.error('Error loading avatar:', error);
      });
    }
  }
}
