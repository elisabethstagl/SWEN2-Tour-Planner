import {Routes} from '@angular/router';
import {HomeComponent} from './pages/home/home';
import {TourDetail} from './pages/tour-detail/tour-detail';
import {Register} from './pages/register/register';
import {Profile} from './pages/profile/profile';
import {TourForm} from './pages/tour-form/tour-form';
import {TourLogForm} from './pages/tour-log-form/tour-log-form';

export const routes: Routes = [
  {
    path: '',
    component: HomeComponent
  },
  {
    path: 'register',
    component: Register
  },
  {
    path: 'profile',
    component: Profile
  },
  {
    path: 'new-tour',
    component: TourForm
  },
  {
    path: 'edit-tour/:id',
    component: TourForm
  },
  {
    path: 'tour-detail/:tourId/new-log',
    component: TourLogForm
  },
  {
    path: 'tour/:tourId/log/edit/:logId',
    component: TourLogForm
  },
  {
    path: 'tour-detail/:id',
    component: TourDetail
  },
  {
    path: '**',
    redirectTo: '',
  },
];
