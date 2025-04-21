import {Routes} from '@angular/router';
import {MainComponent} from '../main/main.component';
import {GameConditionsComponent} from '../game_conditions/game-conditions.component';
import {GameRoomComponent} from '../game_room/game-room.component';

export const routes: Routes = [
  {path: "", component: MainComponent},
  {path: 'game-conditions', component: GameConditionsComponent},
  { path: 'game-room/:id', component: GameRoomComponent },
];
