import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface RouteRequest {
  from: string;
  to: string;
  transportType: string;
}

export interface RouteResponse {
  distanceKm: number;
  durationMinutes: number;
  coordinates: number[][];
}

@Injectable({
  providedIn: 'root'
})
export class RouteService {

  private apiUrl = 'http://localhost:8080/api/routes';

  constructor(private http: HttpClient) {}

  calculateRoute(request: RouteRequest): Observable<RouteResponse> {
    return this.http.post<RouteResponse>(`${this.apiUrl}/calculate`, request);
  }
}
