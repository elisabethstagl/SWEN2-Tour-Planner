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


  readonly totalTours = computed(() => this._tours().length);

  getTourById(id: number) {
    return computed(() => this._tours().find(tour => tour.id === id) ?? null);
  }

  getLogsForTour(tourId: number) {
    return computed(() =>
      this._logs().filter(log => log.tourId === tourId)
    );
  }

  addTour(input: {
    userId: number;
    name: string;
    description: string;
    from: string;
    to: string;
    transportType: TransportType;
    distance: number;
    estimatedTime: string;
    mapUrl?: string;
  }): number | null {
    this._error.set('');

    const name = input.name.trim();
    const description = input.description.trim();
    const from = input.from.trim();
    const to = input.to.trim();
    const timeRegex = /^\d{1,2}:[0-5]\d$/;
    const estimatedTime = input.estimatedTime.trim();
    const mapUrl = (input.mapUrl ?? '').trim();

    if (name.length < 2) {
      this._error.set('Tour name must be at least 2 characters.');
      return null;
    }

    if (description.length < 2) {
      this._error.set('Description must be at least 2 characters.');
      return null;
    }

    if (from.length < 2) {
      this._error.set('From must be at least 2 characters.');
      return null;
    }

    if (to.length < 2) {
      this._error.set('To must be at least 2 characters.');
      return null;
    }

    if (input.distance === null || isNaN(Number(input.distance))) {
      this._error.set('Distance must be a valid number.');
      return null;
    }

    if (input.distance <= 0) {
      this._error.set('Distance must be greater than 0.');
      return null;
    }

    if (estimatedTime.length === 0) {
      this._error.set('Estimated time is required.');
      return null;
    }

    if (!timeRegex.test(estimatedTime)) {
      this._error.set('Estimated time must be in format HH:MM (e.g., 4:24 or 12:05).');
      return null;
    }

    const nextId =
      this._tours().length > 0
        ? Math.max(...this._tours().map(tour => tour.id)) + 1
        : 1;

    const tour: Tour = {
      id: nextId,
      userId: input.userId,
      name,
      description,
      from,
      to,
      transportType: input.transportType,
      distance: input.distance,
      estimatedTime,
      mapUrl: mapUrl || undefined,
    };

    this._tours.update(tours => [...tours, tour]);
    return nextId;
  }

  updateTour(updatedTour: Tour) {
    this._tours.update(tours =>
      tours.map(tour => tour.id === updatedTour.id ? updatedTour : tour)
    );
  }

  addLog(input: {
    tourId: number;
    date: string;
    time: string;
    comment: string;
    difficulty: 'easy' | 'medium' | 'hard';
    totalDistance: number;
    totalTime: string;
    rating: number;
  }): number | null {
    this._error.set('');

    const timeRegex = /^\d{1,2}:[0-5]\d$/;
    if (!timeRegex.test(input.totalTime.trim())) {
      this._error.set('Duration must be in format H:mm (e.g., 4:30).');
      return null;
    }

    if (!input.date) {
      this._error.set('Date is required.');
      return null;
    }

    if (input.totalDistance === null || isNaN(input.totalDistance) || input.totalDistance <= 0) {
      this._error.set('Distance must be a valid number greater than 0.');
      return null;
    }

    if (input.rating < 1 || input.rating > 5) {
      this._error.set('Please provide a rating between 1 and 5.');
      return null;
    }

    const nextId =
      this._logs().length > 0
        ? Math.max(...this._logs().map(log => log.id)) + 1
        : 1;

    const newLog: TourLog = {
      ...input,
      id: nextId
    };

    this._logs.update(logs => [...logs, newLog]);
    return nextId;
  }

  deleteTour(id: number) {
    this._tours.update(tours => tours.filter(tour => tour.id !== id));
    this._logs.update(logs => logs.filter(log => log.tourId !== id));
  }

  clearError() {
    this._error.set('');
  }
}
