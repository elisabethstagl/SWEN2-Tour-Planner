import {Injectable, computed, inject, signal} from '@angular/core';
import {HttpClient, HttpParams} from '@angular/common/http';
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
  routeGeometry?: string | null;
  popularity?: number;
  childFriendliness?: number | null;
  favorite?: boolean;
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

type TourLogExportDto = {
  logDatetime: string;
  comment: string;
  difficulty: number;
  totalDistance: number;
  totalTime: number;
  rating: number;
};

type TourExportDto = {
  name: string;
  description: string;
  from: string;
  to: string;
  transportType: TransportType;
  distance: number;
  estimatedTime: number;
  routeGeometry?: string | null;
  logs: TourLogExportDto[];
};

type TourExportFile = {
  tours: TourExportDto[];
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

  toggleFavorite(id: number): void {
    this._error.set('');

    this.http.patch<TourDto>(`${this.apiUrl}/tours/${id}/favorite`, {}).subscribe({
      next: savedTour => {
        const frontendTour = this.convertTourDtoToTour(savedTour);

        this._tours.update(tours =>
          tours.map(tour => tour.id === frontendTour.id ? frontendTour : tour)
        );
      },
      error: () => this._error.set('Could not update favorite.')
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
        this.loadTours();

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
          logs.map(log => log.id === frontendLog.id ? frontendLog : log));
        this.loadTours();

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
        this.loadTours();
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
    const estimatedTime = tour.estimatedTime;

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
      estimatedTime: estimatedTime
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
      estimatedTime: tour.estimatedTime,
      routeGeometry: tour.routeGeometry ? JSON.stringify(tour.routeGeometry) : undefined
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
      estimatedTime: tourDto.estimatedTime,
      routeGeometry: this.parseRouteGeometry(tourDto.routeGeometry),
      popularity: tourDto.popularity,
      childFriendliness: tourDto.childFriendliness,
      favorite: tourDto.favorite ?? false
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
    const match = value.trim().match(/^([0-9]+):([0-5][0-9])$/);

    if (!match) {
      throw new Error('Invalid duration format. Use HH:mm.');
    }

    const hours = Number(match[1]);
    const minutes = Number(match[2]);

    return hours * 60 + minutes;
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

  searchTours(query: string): void {
    const params = new HttpParams().set('query', query ?? '');

    this.http.get<TourDto[]>(`${this.apiUrl}/tours/search`, {params}).subscribe({
      next: tours => {
        const frontendTours = tours.map(tour => this.convertTourDtoToTour(tour));
        this._tours.set(frontendTours);
      },
      error: () => this._error.set('Could not search tours.')
    });
  }

  private parseRouteGeometry(raw: string | null | undefined): [number, number][] | null {
    if (!raw) {
      return null;
    }

    try {
      return JSON.parse(raw) as [number, number][];
    } catch {
      return null;
    }
  }

  exportTours(): void {
    this._error.set('');

    this.http.get<TourExportFile>(`${this.apiUrl}/tours/export`).subscribe({
      next: file => {
        const date = new Date().toISOString().slice(0, 10);
        this.downloadAsJsonFile(file, `tourplanner-export-${date}.json`);
      },
      error: () => this._error.set('Could not export tours.')
    });
  }

  importTours(file: File, onSuccess?: (importedCount: number) => void): void {
    this._error.set('');

    file.text().then(text => {
      let parsed: unknown;

      try {
        parsed = JSON.parse(text);
      } catch {
        this._error.set('Selected file is not a valid JSON.');
        return;
      }

      this.http.post<TourDto[]>(`${this.apiUrl}/tours/import`, parsed).subscribe({
        next: importedTours => {
          this.loadTours();
          this.loadLogs();

          if (onSuccess) {
            onSuccess(importedTours.length);
          }
        },
        error: () => this._error.set('Could not import tours. Check the file format.')
      });
    });
  }

  private downloadAsJsonFile(data: unknown, filename: string): void {
    const blob = new Blob([JSON.stringify(data, null, 2)], {type: 'application/json'});
    const url = URL.createObjectURL(blob);

    const link = document.createElement('a');
    link.href = url;
    link.download = filename;
    link.click();

    URL.revokeObjectURL(url);
  }
}
