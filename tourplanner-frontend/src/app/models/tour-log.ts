export interface TourLog {
  id: number;
  tourId: number;
  date: string;
  time: string;
  comment: string;
  difficulty: 'easy' | 'medium' | 'hard';
  totalDistance: number;
  totalTime: string;
  rating: number;
}
