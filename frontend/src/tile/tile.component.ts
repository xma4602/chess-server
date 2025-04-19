import { Component, Input } from '@angular/core';

@Component({
  selector: 'app-tile',
  standalone: true,
  templateUrl: './tile.component.html',
  styleUrls: ['./tile.component.css']
})
export class TileComponent {
  @Input() title: string = '';
  @Input() subtitle: string = '';
}
