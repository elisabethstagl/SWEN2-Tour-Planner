import {Routes} from '@angular/router';
import {HomeComponent} from './pages/home/home';
import {DiscoverComponent} from './pages/discover/discover';
import {TourDetail} from './pages/tour-detail/tour-detail';
import {Register} from './pages/register/register';

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
    path: 'discover',
    component: DiscoverComponent
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
