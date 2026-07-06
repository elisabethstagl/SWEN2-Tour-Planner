import {Component, OnDestroy, signal} from '@angular/core';
import {NavigationEnd, Router, RouterOutlet} from '@angular/router';
import {filter, Subscription} from 'rxjs';
import {HeaderComponent} from './components/header/header';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, HeaderComponent],
  templateUrl: './app.html',
  styleUrl: './app.css'
})
export class App implements OnDestroy {
  protected readonly title = signal('tourplanner-frontend');
  protected readonly currentUrl = signal('/');
  private readonly subscription: Subscription;

  constructor(private router: Router) {
    // splits URL for checking the route -> for header visibility or FullScreenPage (no padding, padding)
    this.currentUrl.set(this.router.url.split('?')[0]);


    // Observable subscription with NavigationEnd to filter the last part of the URL
    this.subscription = this.router.events
      .pipe(filter((event): event is NavigationEnd => event instanceof NavigationEnd))
      .subscribe(event => this.currentUrl.set(event.urlAfterRedirects.split('?')[0]));
  }

  showHeader(): boolean {
    return !['/', '/login', '/register'].includes(this.currentUrl());
  }

  isFullScreenPage(): boolean {
    return this.currentUrl() === '/app';
  }

  ngOnDestroy(): void {
    this.subscription.unsubscribe();
  }
}
