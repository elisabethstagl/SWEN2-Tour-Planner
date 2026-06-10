import { Injectable } from '@angular/core';
import * as L from 'leaflet';

// delete all default markers and add the same marker icons in assets,
// because of a marker-shadow error
delete (L.Icon.Default.prototype as any)._getIconUrl;

L.Icon.Default.mergeOptions({
  iconRetinaUrl: '/assets/marker-icon-2x.png',
  iconUrl: '/assets/marker-icon.png',
  shadowUrl: '/assets/marker-shadow.png',
});

@Injectable({
  providedIn: 'root'
})

export class MapFacadeService {
  private map: L.Map | null = null;

  initMap(containerId: string): void {
    if (this.map) return;

    this.map = L.map(containerId, {
      zoomControl: true,
      attributionControl: true,
    });

    L.tileLayer(
      'https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png',
      { attribution: '© OpenStreetMap contributors' }
    ).addTo(this.map);

    this.map.setView([48.2082, 16.3738], 12);

    setTimeout(() => {
      this.map?.invalidateSize();
    }, 0);
  }

  setMarker(lat: number, lng: number): void {
    if (!this.map) return;
    L.marker([lat, lng]).addTo(this.map);
  }

  destroyMap(): void {
    this.map?.remove();
    this.map = null;
  }
}
