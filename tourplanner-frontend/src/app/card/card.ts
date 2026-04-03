import { Component } from '@angular/core';

export interface TourProps {
    id: number;
    title: string;
    description: string;
    from: string;
    to: string;
    distance: number;
    duration: string;
}

@Component({
    selector: 'app-card',
    standalone: true,
    templateUrl: './card.html',
    styleUrls: ['./card.css'],
})
export class CardComponent {
    // id: number;
    // title: string;
    // description: string;
    // from: string;
    // to: string;
    // distance: number;
    // duration: string;
    
    // constructor({ id, title, description, from, to, distance, duration }: TourProps) {
    //     this.id = id;
    //     this.title = title;
    //     this.description = description;
    //     this.from = from;
    //     this.to = to;
    //     this.distance = distance;
    //     this.duration = duration;
    // }

}