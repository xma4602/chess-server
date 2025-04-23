import {Routes} from '@angular/router';
import {MainComponent} from '../main/main.component';
import {GameConditionsComponent} from '../game_conditions/game-conditions.component';
import {GameRoomComponent} from '../game_room/game-room.component';
import {GamePlayComponent} from '../game_play/game-play.component';

export const routes: Routes = [
  // {path: "", component: MainComponent},
  {path: "", component: GamePlayComponent},

  {path: 'game-conditions', component: GameConditionsComponent},
  { path: 'game-room/:id', component: GameRoomComponent },
  { path: 'game-play/:id', component: GamePlayComponent },
];
