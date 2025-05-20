import {Component, Input, OnDestroy} from '@angular/core';
import {NgIf} from '@angular/common';


@Component({
  selector: 'app-timer',
  standalone: true,
  templateUrl: './countdown-timer.component.html',
  imports: [NgIf],
  styleUrls: ['./countdown-timer.component.css']
})
export class CountdownTimerComponent implements OnDestroy {
  @Input() userLogin: string | null = null;
  userId: string | null = null;
  gameId: string | null = null;

  countdown: number = 0; // Время в секундах
  interval: any;
  minus: number = 1;
  visible: boolean = true;
  timeout = 1000;

  startTimer(gameId: string, userId: string, timeSeconds: number, endCallback: () => void): void {
    this.userId = userId;
    this.gameId = gameId;

    const savedCountdown = localStorage.getItem(this.getCountKey());
    const savedStopTime = localStorage.getItem(this.getTimeKey());

    if (savedCountdown && savedStopTime) {
      this.countdown = parseInt(savedCountdown, 10);
      const stopTime = parseInt(savedStopTime, 10);
      const elapsedTime = Math.floor((Date.now() - stopTime) / this.timeout); // Прошедшее время в секундах
      this.countdown = Math.max(0, this.countdown - elapsedTime); // Убедитесь, что countdown не отрицательный
    } else {
      this.countdown = timeSeconds;      // Сохраняем начальное значение в localStorage
      localStorage.setItem(this.getCountKey(), this.countdown.toString());
    }

    this.interval = setInterval(() => {
      if (this.countdown! > 0) {
        this.countdown! -= this.minus;
        // Обновляем значение в localStorage
        localStorage.setItem(this.getCountKey(), this.countdown.toString());
        localStorage.setItem(this.getTimeKey(), Date.now().toString());
      } else {
        endCallback();
        clearInterval(this.interval);
        localStorage.removeItem(this.getCountKey()); // Удаляем значение из localStorage
        localStorage.removeItem(this.getTimeKey()); // Удаляем время остановки
      }
    }, this.timeout); // Обновление каждую секунду
  }

  getCountKey() {
    return `${this.gameId}.${this.userId}.countdown`;
  }

  getTimeKey() {
    return `${this.gameId}.${this.userId}.time`;
  }


  stopTimer(): void {
    this.minus = 0;
    // Сохраняем текущее время остановки
    localStorage.setItem(this.getTimeKey(), Date.now().toString());
  }

  resumeTimer() {
    this.minus = 1;
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
    localStorage.removeItem(this.getCountKey()); // Удаляем значение из localStorage при уничтожении компонента
    localStorage.removeItem(this.getTimeKey()); // Удаляем время остановки
  }
}
