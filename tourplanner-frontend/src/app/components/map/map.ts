import {AfterViewInit, Component, inject, OnDestroy} from '@angular/core';
import {MapFacadeService} from '../../service/map-facade-service';

@Component({
  selector: 'app-map',
  imports: [],
  templateUrl: './map.html',
  styleUrl: './map.css',
})

export class Map implements AfterViewInit, OnDestroy {

  private mapFacade = inject(MapFacadeService);

  ngAfterViewInit(): void {
    this.mapFacade.initMap('map');

    this.mapFacade.setMarker(48.2082, 16.3738); // Vienna
    this.mapFacade.setMarker(47.0707, 15.4395); // Graz

    this.mapFacade.setRoute([
      [16.3738, 48.2082],
      [15.4395, 47.0707]
    ]);
  }

  ngOnDestroy(): void {
    this.mapFacade.destroyMap();
  }
}
