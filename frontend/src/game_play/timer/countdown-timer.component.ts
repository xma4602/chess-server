import {Component, Input, OnDestroy} from '@angular/core';
import {NgIf} from '@angular/common';
import {AuthModule} from '../../app/auth.module';

@Component({
  selector: 'app-timer',
  standalone: true,
  templateUrl: './countdown-timer.component.html',
  imports: [AuthModule,NgIf],
  styleUrls: ['./countdown-timer.component.css']
})
export class CountdownTimerComponent implements OnDestroy {
  @Input() userLogin: string | null = null
  userId: string | null = null
  countdown: number = 0; // Время в секундах
  interval: any;
  minus: number = 1
  visible: boolean = true;

  startTimer(login: string, userId: string, timeSeconds: number, endCallback: () => void): void {
    this.countdown = timeSeconds
    this.userLogin = login
    this.userId = userId
    this.interval = setInterval(() => {
      if (this.countdown! > 0) {
        this.countdown! -= this.minus;
      } else {
        endCallback();
        clearInterval(this.interval);
      }
    }, 1000); // Обновление каждую секунду
  }

  stopTimer(): void {
    this.minus = 0
  }

  resumeTimer() {
    this.minus = 1
  }

  getTimeString() {
    const minutes = Math.floor(this.countdown! / 60); // Получаем целые минуты
    const seconds = this.countdown! % 60; // Получаем оставшиеся секунды

    // Форматируем секунды с ведущим нулем, если они меньше 10
    const formattedSeconds = seconds < 10 ? `0${seconds}` : seconds;

    return `${minutes}:${formattedSeconds}`; // Возвращаем строку в формате "MM:SS"
  }

  ngOnDestroy(): void {
    clearInterval(this.interval);
  }
}
