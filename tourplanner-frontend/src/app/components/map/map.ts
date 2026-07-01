import {AfterViewInit, Component, effect, inject, input, OnDestroy} from '@angular/core';
import {MapFacadeService} from '../../service/map-facade-service';

@Component({
  selector: 'app-map',
  imports: [],
  templateUrl: './map.html',
  styleUrl: './map.css',
})

export class Map implements AfterViewInit, OnDestroy {

  private mapFacade = inject(MapFacadeService);

  readonly routePoints = input<[number, number][] | null>(null);

  ngAfterViewInit(): void {
    this.mapFacade.initMap('map');

    const points = this.routePoints();
    if (points && points.length > 0) {
      this.mapFacade.setRoute(points);
    }
  }

  constructor() {
    effect(() => {
      const points = this.routePoints();

      if (points && points.length > 0) {
        this.mapFacade.setRoute(points);
      }
    });
  }

  ngOnDestroy(): void {
    this.mapFacade.destroyMap();
  }
}
