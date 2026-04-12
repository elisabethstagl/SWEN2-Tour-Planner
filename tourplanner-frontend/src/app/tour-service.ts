import {Injectable, computed, signal} from '@angular/core';
import {Tour, TransportType} from './models/tour';
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

  private readonly _error = signal('');
  readonly tours = this._tours.asReadonly();
  readonly logs = this._logs.asReadonly();
  readonly error = this._error.asReadonly();

  getTourById(id: number) {
    return computed(() => this._tours().find(tour => tour.id === id) ?? null);
  }

  addTour(tour: Tour): number | null {
    this._error.set('');

    const name = tour.name.trim();
    const description = tour.description.trim();
    const from = tour.from.trim();
    const to = tour.to.trim();
    const estimatedTime = tour.estimatedTime.trim();

    if (name.length < 2) {
      this._error.set('Tour name must be at least 2 characters.');
      return null;
    }
    if (description.length < 2) {
      this._error.set('Description must be at least 2 characters.');
      return null;
    }

    if (tour.distance === null || isNaN(tour.distance) || tour.distance <= 0) {
      this._error.set('Distance must be a valid number greater than 0.');
      return null;
    }

    const nextId = this._tours().length > 0
      ? Math.max(...this._tours().map(t => t.id)) + 1
      : 1;

    const tourToSave: Tour = {
      ...tour,
      id: nextId,
      name,
      description,
      from,
      to,
      estimatedTime,
      mapUrl: tour.mapUrl?.trim() || undefined
    };

    this._tours.update(tours => [...tours, tourToSave]);
    return nextId;
  }

  updateTour(updatedTour: Tour) {
    this._tours.update(tours =>
      tours.map(tour => tour.id === updatedTour.id ? updatedTour : tour)
    );
  }

  deleteTour(id: number) {
    this._tours.update(tours => tours.filter(tour => tour.id !== id));
    this._logs.update(logs => logs.filter(log => log.tourId !== id));
  }

  getLogById(id: number) {
    return computed(() => this._logs().find(l => l.id === id) ?? null);
  }

  addLog(log: TourLog): number | null {
    this._error.set('');

    if (log.totalDistance === null || isNaN(log.totalDistance) || log.totalDistance <= 0) {
      this._error.set('Distance must be a valid number.');
      return null;
    }

    const nextId = this._logs().length > 0
      ? Math.max(...this._logs().map(l => l.id)) + 1
      : 1;

    const newLog: TourLog = {...log, id: nextId};

    this._logs.update(logs => [...logs, newLog]);
    return nextId;
  }

  updateLog(updatedLog: TourLog): void {
    this._error.set('');

    this._logs.update(logs =>
      logs.map(log => log.id === updatedLog.id ? {...updatedLog} : log)
    );
  }

  deleteLog(logId: number) {
    this._logs.update(logs => logs.filter(log => log.id !== logId));
  }

  clearError() {
    this._error.set('');
  }
}
