import {Routes} from '@angular/router';
import {HomeComponent} from './pages/home/home';
import {TourDetail} from './pages/tour-detail/tour-detail';
import {Register} from './pages/register/register';
import {Profile} from './pages/profile/profile';
import {NewTour} from './pages/new-tour/new-tour';
import {NewTourLog} from './pages/new-tour-log/new-tour-log';

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
    component: NewTour
  },
  {
    path: 'new-tour-log',
    component: NewTourLog
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
