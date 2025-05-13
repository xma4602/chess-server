import {Component, Input, OnInit} from '@angular/core';
import {HttpClient, HttpParams} from '@angular/common/http';
import {restUsers} from '../../data.service';
import {NgIf} from '@angular/common';
import {User} from '../user';
import {RequestService} from '../../request.service';

// Убедитесь, что путь правильный

@Component({
  selector: 'app-user-avatar',
  standalone: true,
  imports: [NgIf],
  templateUrl: './user-avatar.component.html',
  styleUrls: ['user-avatar.component.css']
})
export class UserAvatarComponent implements OnInit {
  @Input() user: User | null = null;

  constructor(private requestService: RequestService) {
  }

  ngOnInit() {
    if (!this.user!.avatarUrl) {
      this.requestService.get<Blob>(`${restUsers}/${this.user?.id}/avatar`, new HttpParams(), 'blob')
        .subscribe(blob => {
          this.user!.avatarUrl = URL.createObjectURL(blob);
        }, error => {
          console.error('Error loading avatar:', error);
        });
    }
  }
}
