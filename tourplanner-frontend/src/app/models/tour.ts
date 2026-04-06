export type TransportType = 'walk' | 'bike' | 'car' | 'public transport';

export interface Tour {
  id: number;
  userId: number;
  name: string;
  description: string;
  from: string;
  to: string;
  transportType: TransportType;
  distance: number;
  estimatedTime: string;
  mapUrl?: string;
}
