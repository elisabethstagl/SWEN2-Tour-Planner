import { Injectable, computed, inject, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Tour } from '../models/tour';
import { TourLog } from '../models/tour-log';

type BackendTour = {
  id: number;
  user?: { id: number };
  name: string;
  description: string;
  fromLocation: string;
  toLocation: string;
  transportType: string;
  distanceKm: number;
  estimatedTime: number;
  mapImagePath?: string;
};

type BackendTourLog = {
  id: number;
  tour?: { id: number };
  logDatetime: string;
  comment: string;
  difficulty: number;
  totalDistance: number;
  totalTime: number;
  rating: number;
};

@Injectable({
  providedIn: 'root'
})
export class TourService {
  private readonly http = inject(HttpClient);
  private readonly apiUrl = 'http://localhost:8080/api';

  private readonly _tours = signal<Tour[]>([]);
  private readonly _logs = signal<TourLog[]>([]);
  private readonly _error = signal('');

  readonly tours = this._tours.asReadonly();
  readonly logs = this._logs.asReadonly();
  readonly error = this._error.asReadonly();

  loadTours(): void {
    this.http.get<BackendTour[]>(`${this.apiUrl}/tours`).subscribe({
      next: tours => this._tours.set(tours.map(t => this.fromBackendTour(t))),
      error: () => this._error.set('Could not load tours.')
    });
  }

  loadLogs(): void {
    this.http.get<BackendTourLog[]>(`${this.apiUrl}/tourLogs`).subscribe({
      next: logs => this._logs.set(logs.map(l => this.fromBackendLog(l))),
      error: () => this._error.set('Could not load tour logs.')
    });
  }

  getTourById(id: number) {
    return computed(() => this._tours().find(tour => tour.id === id) ?? null);
  }

  addTour(tour: Tour): number | null {
    this._error.set('');

    const validatedTour = this.validateTour(tour);
    if (!validatedTour) return null;

    this.http.post<BackendTour>(`${this.apiUrl}/tours`, this.toBackendTour(validatedTour)).subscribe({
      next: savedTour => {
        this._tours.update(tours => [...tours, this.fromBackendTour(savedTour)]);
      },
      error: () => this._error.set('Could not save tour.')
    });

    return null;
  }

  updateTour(updatedTour: Tour): void {
    this._error.set('');

    const validatedTour = this.validateTour(updatedTour);
    if (!validatedTour) return;

    this.http.put<BackendTour>(
      `${this.apiUrl}/tours/${updatedTour.id}`,
      this.toBackendTour(validatedTour)
    ).subscribe({
      next: savedTour => {
        const frontendTour = this.fromBackendTour(savedTour);

        this._tours.update(tours =>
          tours.map(tour => tour.id === frontendTour.id ? frontendTour : tour)
        );
      },
      error: () => this._error.set('Could not update tour.')
    });
  }

  deleteTour(id: number): void {
    this._error.set('');

    this.http.delete<void>(`${this.apiUrl}/tours/${id}`).subscribe({
      next: () => {
        this._tours.update(tours => tours.filter(tour => tour.id !== id));
        this._logs.update(logs => logs.filter(log => log.tourId !== id));
      },
      error: () => this._error.set('Could not delete tour.')
    });
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

    this.http.post<BackendTourLog>(`${this.apiUrl}/tourLogs`, this.toBackendLog(log)).subscribe({
      next: savedLog => {
        this._logs.update(logs => [...logs, this.fromBackendLog(savedLog)]);
      },
      error: () => this._error.set('Could not save tour log.')
    });

    return null;
  }

  updateLog(updatedLog: TourLog): void {
    this._error.set('');

    this.http.put<BackendTourLog>(
      `${this.apiUrl}/tourLogs/${updatedLog.id}`,
      this.toBackendLog(updatedLog)
    ).subscribe({
      next: savedLog => {
        const frontendLog = this.fromBackendLog(savedLog);

        this._logs.update(logs =>
          logs.map(log => log.id === frontendLog.id ? frontendLog : log)
        );
      },
      error: () => this._error.set('Could not update tour log.')
    });
  }

  deleteLog(logId: number): void {
    this._error.set('');

    this.http.delete<void>(`${this.apiUrl}/tourLogs/${logId}`).subscribe({
      next: () => this._logs.update(logs => logs.filter(log => log.id !== logId)),
      error: () => this._error.set('Could not delete tour log.')
    });
  }

  clearError(): void {
    this._error.set('');
  }

  private validateTour(tour: Tour): Tour | null {
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

    return {
      ...tour,
      name,
      description,
      from,
      to,
      estimatedTime,
      mapUrl: tour.mapUrl?.trim() || undefined
    };
  }

  private toBackendTour(tour: Tour): BackendTour {
    return {
      id: tour.id,
      user: { id: tour.userId },
      name: tour.name,
      description: tour.description,
      fromLocation: tour.from,
      toLocation: tour.to,
      transportType: tour.transportType,
      distanceKm: tour.distance,
      estimatedTime: this.parseEstimatedTimeToMinutes(tour.estimatedTime),
      mapImagePath: tour.mapUrl
    };
  }

  private fromBackendTour(tour: BackendTour): Tour {
    return {
      id: tour.id,
      userId: tour.user?.id ?? 1,
      name: tour.name,
      description: tour.description,
      from: tour.fromLocation,
      to: tour.toLocation,
      transportType: tour.transportType as Tour['transportType'],
      distance: tour.distanceKm,
      estimatedTime: this.formatMinutes(tour.estimatedTime),
      mapUrl: tour.mapImagePath
    };
  }

  private toBackendLog(log: TourLog): BackendTourLog {
    return {
      id: log.id,
      tour: { id: log.tourId },
      logDatetime: `${log.date}T${log.time}:00Z`,
      comment: log.comment,
      difficulty: this.difficultyToNumber(log.difficulty),
      totalDistance: log.totalDistance,
      totalTime: this.parseTotalTimeToMinutes(log.totalTime),
      rating: log.rating
    };
  }

  private fromBackendLog(log: BackendTourLog): TourLog {
    const date = new Date(log.logDatetime);

    return {
      id: log.id,
      tourId: log.tour?.id ?? 0,
      date: date.toISOString().slice(0, 10),
      time: date.toISOString().slice(11, 16),
      comment: log.comment,
      difficulty: this.numberToDifficulty(log.difficulty),
      totalDistance: log.totalDistance,
      totalTime: this.formatHoursMinutes(log.totalTime),
      rating: log.rating
    };
  }

  private parseEstimatedTimeToMinutes(value: string): number {
    const lower = value.toLowerCase();

    if (lower.includes('day')) {
      const days = parseFloat(lower);
      return isNaN(days) ? 0 : days * 24 * 60;
    }

    if (lower.includes(':')) {
      return this.parseTotalTimeToMinutes(value);
    }

    const hours = parseFloat(lower);
    return isNaN(hours) ? 0 : hours * 60;
  }

  private parseTotalTimeToMinutes(value: string): number {
    const [hours, minutes] = value.split(':').map(Number);
    return (hours || 0) * 60 + (minutes || 0);
  }

  private formatMinutes(minutes: number): string {
    if (!minutes) return '';

    if (minutes >= 1440 && minutes % 1440 === 0) {
      return `${minutes / 1440} days`;
    }

    const hours = Math.floor(minutes / 60);
    const restMinutes = minutes % 60;

    if (restMinutes === 0) {
      return `${hours} hours`;
    }

    return `${hours}:${restMinutes.toString().padStart(2, '0')}`;
  }

  private formatHoursMinutes(minutes: number): string {
    const hours = Math.floor(minutes / 60);
    const restMinutes = minutes % 60;

    return `${hours.toString().padStart(2, '0')}:${restMinutes.toString().padStart(2, '0')}`;
  }

  private difficultyToNumber(difficulty: TourLog['difficulty']): number {
    switch (difficulty) {
      case 'easy':
        return 1;
      case 'medium':
        return 2;
      case 'hard':
        return 3;
    }
  }

  private numberToDifficulty(difficulty: number): TourLog['difficulty'] {
    switch (difficulty) {
      case 1:
        return 'easy';
      case 2:
        return 'medium';
      case 3:
        return 'hard';
      default:
        return 'medium';
    }
  }
}
