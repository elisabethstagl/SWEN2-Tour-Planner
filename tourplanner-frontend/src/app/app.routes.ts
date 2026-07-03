import {Routes} from '@angular/router';
import {HomeComponent} from './pages/home/home';
import {TourDetail} from './pages/tour-detail/tour-detail';
import {Register} from './pages/register/register';
import {TourForm} from './pages/tour-form/tour-form';
import {TourLogForm} from './pages/tour-log-form/tour-log-form';
import {LoginComponent} from './pages/login/login';
import {MyToursComponent} from './pages/my-tours/my-tours';
import {DashboardComponent} from './pages/dashboard/dashboard';
import {authGuard, publicOnlyGuard} from './guards/auth-guard';

export const routes: Routes = [
  { path: '', component: HomeComponent, canActivate: [publicOnlyGuard] },
  { path: 'login', component: LoginComponent, canActivate: [publicOnlyGuard] },
  { path: 'register', component: Register, canActivate: [publicOnlyGuard] },

  { path: 'app', component: DashboardComponent, canActivate: [authGuard] },
  { path: 'my-tours', component: MyToursComponent, canActivate: [authGuard] },
  { path: 'new-tour', redirectTo: 'app', pathMatch: 'full' },
  { path: 'edit-tour/:id', component: TourForm, canActivate: [authGuard] },
  { path: 'tour-detail/:tourId/new-log', component: TourLogForm, canActivate: [authGuard] },
  { path: 'tour/:tourId/log/edit/:logId', component: TourLogForm, canActivate: [authGuard] },
  { path: 'tour-detail/:id', component: TourDetail, canActivate: [authGuard] },

  { path: '**', redirectTo: '' },
];
