import {Routes} from '@angular/router';
import {MainComponent} from '../main/main.component';
import {GameConditionsComponent} from '../game_conditions/game-conditions.component';

export const routes: Routes = [
  {path: "", component: MainComponent},
  {path: 'game-conditions', component: GameConditionsComponent},
];
