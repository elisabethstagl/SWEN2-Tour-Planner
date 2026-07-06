export type TransportType = 'bike' | 'hike' | 'walk' | 'wheelchair' | 'car';


export interface Tour {
  id: number;
  userId: number;
  name: string;
  description: string;
  from: string;
  to: string;
  transportType: TransportType;
  distance: number;
  estimatedTime: number;
  routeGeometry?: [number, number][] | null;
  popularity?: number;
  childFriendliness?: number | null;
  favorite?: boolean;
}
