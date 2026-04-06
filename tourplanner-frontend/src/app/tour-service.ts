import { Injectable, computed, signal } from '@angular/core';
import {Tour} from './models/tour';
import {TourLog} from './models/tour-log';

@Injectable({
  providedIn: 'root'
})
export class TourService {
  private readonly _tours = signal<Tour[]>([
    {
      id: 1,
      userId: 1,
      name: 'Inca Trail',
      description: 'Famous trek through the Andes leading to Machu Picchu with ancient ruins and mountain scenery.',
      from: 'Cusco, Peru',
      to: 'Machu Picchu, Peru',
      transportType: 'walk',
      distance: 42,
      estimatedTime: '4 days'
    },
    {
      id: 2,
      userId: 1,
      name: 'Tour du Mont Blanc',
      description: 'Classic alpine circuit passing through France, Italy, and Switzerland with stunning mountain views.',
      from: 'Chamonix, France',
      to: 'Chamonix, France',
      transportType: 'walk',
      distance: 170,
      estimatedTime: '10–12 days'
    }
  ]);

  private readonly _logs = signal<TourLog[]>([
    {
      id: 1,
      tourId: 1,
      date: '2026-04-01',
      time: '08:30',
      comment: 'Amazing weather and great views.',
      difficulty: 'medium',
      totalDistance: 41.5,
      totalTime: '04:45',
      rating: 5
    },
    {
      id: 2,
      tourId: 1,
      date: '2026-04-03',
      time: '09:00',
      comment: 'A bit exhausting but beautiful.',
      difficulty: 'hard',
      totalDistance: 42,
      totalTime: '05:10',
      rating: 4
    }
  ]);

  readonly tours = this._tours.asReadonly();
  readonly logs = this._logs.asReadonly();

  readonly totalTours = computed(() => this._tours().length);

  getTourById(id: number) {
    return computed(() => this._tours().find(tour => tour.id === id) ?? null);
  }

  getLogsForTour(tourId: number) {
    return computed(() =>
      this._logs().filter(log => log.tourId === tourId)
    );
  }

  addTour(tour: Tour) {
    this._tours.update(tours => [...tours, tour]);
  }

  addLog(log: TourLog) {
    this._logs.update(logs => [...logs, log]);
  }

  deleteTour(id: number) {
    this._tours.update(tours => tours.filter(tour => tour.id !== id));
    this._logs.update(logs => logs.filter(log => log.tourId !== id));
  }
}
