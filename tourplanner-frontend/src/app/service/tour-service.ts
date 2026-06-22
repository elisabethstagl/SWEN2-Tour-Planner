import {Injectable, computed, inject, signal} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Tour, TransportType} from '../models/tour';
import {TourLog} from '../models/tour-log';

type TourDto = {
  id: number;
  userId: number;
  name: string;
  description: string;
  from: string;
  to: string;
  transportType: TransportType;
  distance: number;
  estimatedTime: number;
  mapUrl?: string;
};

type TourLogDto = {
  id: number;
  tourId: number;
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
    this.http.get<TourDto[]>(`${this.apiUrl}/tours`).subscribe({
      next: tours => {
        const frontendTours = tours.map(tour => this.convertTourDtoToTour(tour));
        this._tours.set(frontendTours);
      },
      error: () => this._error.set('Could not load tours.')
    });
  }

  loadLogs(): void {
    this.http.get<TourLogDto[]>(`${this.apiUrl}/tourLogs`).subscribe({
      next: logs => {
        const frontendLogs = logs.map(log => this.convertTourLogDtoToTourLog(log));
        this._logs.set(frontendLogs);
      },
      error: () => this._error.set('Could not load tour logs.')
    });
  }

  getTourById(id: number) {
    return computed(() => this._tours().find(tour => tour.id === id) ?? null);
  }

  addTour(tour: Tour, onSuccess?: () => void): void {
    this._error.set('');

    const validatedTour = this.validateTour(tour);

    if (validatedTour === null) {
      return;
    }

    const tourDto = this.convertTourToTourDto(validatedTour);

    this.http.post<TourDto>(`${this.apiUrl}/tours`, tourDto).subscribe({
      next: savedTour => {
        const frontendTour = this.convertTourDtoToTour(savedTour);
        this._tours.update(tours => [...tours, frontendTour]);

        if (onSuccess) {
          onSuccess();
        }
      },
      error: () => this._error.set('Could not save tour.')
    });
  }

  updateTour(updatedTour: Tour, onSuccess?: () => void): void {
    this._error.set('');

    const validatedTour = this.validateTour(updatedTour);

    if (validatedTour === null) {
      return;
    }

    const tourDto = this.convertTourToTourDto(validatedTour);

    this.http.put<TourDto>(`${this.apiUrl}/tours/${updatedTour.id}`, tourDto).subscribe({
      next: savedTour => {
        const frontendTour = this.convertTourDtoToTour(savedTour);

        this._tours.update(tours =>
          tours.map(tour => tour.id === frontendTour.id ? frontendTour : tour)
        );

        if (onSuccess) {
          onSuccess();
        }
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
    return computed(() => this._logs().find(log => log.id === id) ?? null);
  }

  addLog(log: TourLog, onSuccess?: () => void): void {
    this._error.set('');

    if (log.totalDistance === null || isNaN(log.totalDistance) || log.totalDistance <= 0) {
      this._error.set('Distance must be a valid number.');
      return;
    }

    const logDto = this.convertTourLogToTourLogDto(log);

    this.http.post<TourLogDto>(`${this.apiUrl}/tourLogs`, logDto).subscribe({
      next: savedLog => {
        const frontendLog = this.convertTourLogDtoToTourLog(savedLog);
        this._logs.update(logs => [...logs, frontendLog]);

        if (onSuccess) {
          onSuccess();
        }
      },
      error: () => this._error.set('Could not save tour log.')
    });
  }

  updateLog(updatedLog: TourLog, onSuccess?: () => void): void {
    this._error.set('');

    const logDto = this.convertTourLogToTourLogDto(updatedLog);

    this.http.put<TourLogDto>(`${this.apiUrl}/tourLogs/${updatedLog.id}`, logDto).subscribe({
      next: savedLog => {
        const frontendLog = this.convertTourLogDtoToTourLog(savedLog);

        this._logs.update(logs =>
          logs.map(log => log.id === frontendLog.id ? frontendLog : log)
        );

        if (onSuccess) {
          onSuccess();
        }
      },
      error: () => this._error.set('Could not update tour log.')
    });
  }

  deleteLog(logId: number): void {
    this._error.set('');

    this.http.delete<void>(`${this.apiUrl}/tourLogs/${logId}`).subscribe({
      next: () => {
        this._logs.update(logs => logs.filter(log => log.id !== logId));
      },
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
      name: name,
      description: description,
      from: from,
      to: to,
      estimatedTime: estimatedTime,
      mapUrl: tour.mapUrl?.trim() || undefined
    };
  }

  private convertTourToTourDto(tour: Tour): TourDto {
    return {
      id: tour.id,
      userId: tour.userId,
      name: tour.name,
      description: tour.description,
      from: tour.from,
      to: tour.to,
      transportType: tour.transportType,
      distance: tour.distance,
      estimatedTime: this.convertTimeStringToMinutes(tour.estimatedTime),
      mapUrl: tour.mapUrl
    };
  }

  private convertTourDtoToTour(tourDto: TourDto): Tour {
    return {
      id: tourDto.id,
      userId: tourDto.userId,
      name: tourDto.name,
      description: tourDto.description,
      from: tourDto.from,
      to: tourDto.to,
      transportType: tourDto.transportType,
      distance: tourDto.distance,
      estimatedTime: this.convertMinutesToTimeString(tourDto.estimatedTime),
      mapUrl: tourDto.mapUrl
    };
  }

  private convertTourLogToTourLogDto(log: TourLog): TourLogDto {
    return {
      id: log.id,
      tourId: log.tourId,
      logDatetime: `${log.date}T${log.time}:00Z`,
      comment: log.comment,
      difficulty: this.convertDifficultyToNumber(log.difficulty),
      totalDistance: log.totalDistance,
      totalTime: this.convertTimeStringToMinutes(log.totalTime),
      rating: log.rating
    };
  }

  private convertTourLogDtoToTourLog(logDto: TourLogDto): TourLog {
    const date = new Date(logDto.logDatetime);

    return {
      id: logDto.id,
      tourId: logDto.tourId,
      date: date.toISOString().slice(0, 10),
      time: date.toISOString().slice(11, 16),
      comment: logDto.comment,
      difficulty: this.convertNumberToDifficulty(logDto.difficulty),
      totalDistance: logDto.totalDistance,
      totalTime: this.convertMinutesToHoursMinutes(logDto.totalTime),
      rating: logDto.rating
    };
  }

  private convertTimeStringToMinutes(value: string): number {
    const lower = value.toLowerCase();

    if (lower.includes('day')) {
      const days = parseFloat(lower);

      if (isNaN(days)) {
        return 0;
      }

      return days * 24 * 60;
    }

    if (lower.includes(':')) {
      const parts = lower.split(':');
      const hours = Number(parts[0]);
      const minutes = Number(parts[1]);

      return hours * 60 + minutes;
    }

    const hours = parseFloat(lower);

    if (isNaN(hours)) {
      return 0;
    }

    return hours * 60;
  }

  private convertMinutesToTimeString(minutes: number): string {
    if (!minutes) {
      return '';
    }

    if (minutes >= 1440 && minutes % 1440 === 0) {
      return `${minutes / 1440} days`;
    }

    const hours = Math.floor(minutes / 60);
    const remainingMinutes = minutes % 60;

    if (remainingMinutes === 0) {
      return `${hours} hours`;
    }

    return `${hours}:${remainingMinutes.toString().padStart(2, '0')}`;
  }

  private convertMinutesToHoursMinutes(minutes: number): string {
    const hours = Math.floor(minutes / 60);
    const remainingMinutes = minutes % 60;

    return `${hours.toString().padStart(2, '0')}:${remainingMinutes.toString().padStart(2, '0')}`;
  }

  private convertDifficultyToNumber(difficulty: TourLog['difficulty']): number {
    if (difficulty === 'easy') {
      return 1;
    }

    if (difficulty === 'medium') {
      return 2;
    }

    return 3;
  }

  private convertNumberToDifficulty(difficulty: number): TourLog['difficulty'] {
    if (difficulty === 1) {
      return 'easy';
    }

    if (difficulty === 2) {
      return 'medium';
    }

    if (difficulty === 3) {
      return 'hard';
    }

    return 'medium';
  }
}
