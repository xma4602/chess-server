import {Routes} from '@angular/router';
import {MainComponent} from '../main/main.component';
import {GameConditionsComponent} from '../game_conditions/game-conditions.component';
import {GameRoomComponent} from '../game_room/game-room.component';
import {GamePlayComponent} from '../game_play/game-play.component';
import {AuthGuard} from './auth.guard';
import {LoginComponent} from '../user/login/login.component';
import {RegisterComponent} from '../user/register/register.component';

export const routes: Routes = [
  {path: "login", component: LoginComponent},
  {path: "register", component: RegisterComponent},
  {path: "", component: MainComponent, canActivate: [AuthGuard]},
  {path: "main", component: MainComponent, canActivate: [AuthGuard]},
  {path: 'game-conditions', component: GameConditionsComponent, canActivate: [AuthGuard]},
  {path: 'game-room/:id', component: GameRoomComponent, canActivate: [AuthGuard]},
  {path: 'chess/rooms/:id', component: GameRoomComponent, canActivate: [AuthGuard]},
  {path: 'game-play/:id', component: GamePlayComponent, canActivate: [AuthGuard]},
];
