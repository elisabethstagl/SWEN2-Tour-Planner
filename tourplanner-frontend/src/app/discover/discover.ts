import { Component } from "@angular/core";
import { CardComponent } from "../card/card";

@Component({
    selector: 'app-discover',
    standalone: true,
    // imports: [CardComponent],
    templateUrl: './discover.html',
    styleUrls: ['./discover.css'],
})

export class DiscoverComponent {
    title = 'Discover Most Popular Tours';
    tours = [
  {
    id: 1,
    title: "Inca Trail",
    description: "Famous trek through the Andes leading to Machu Picchu with ancient ruins and mountain scenery.",
    from: "Cusco, Peru",
    to: "Machu Picchu, Peru",
    distance: 42,
    duration: "4 days"
  },
  {
    id: 2,
    title: "Tour du Mont Blanc",
    description: "Classic alpine circuit passing through France, Italy, and Switzerland with stunning mountain views.",
    from: "Chamonix, France",
    to: "Chamonix, France",
    distance: 170,
    duration: "10–12 days"
  },
  {
    id: 3,
    title: "Everest Base Camp Trek",
    description: "Iconic Himalayan trek with breathtaking views of Mount Everest and Sherpa culture.",
    from: "Lukla, Nepal",
    to: "Everest Base Camp, Nepal",
    distance: 130,
    duration: "12–14 days"
  },
  {
    id: 4,
    title: "Appalachian Trail (Section)",
    description: "Part of one of the longest hiking trails in the world, passing forests and mountains in the USA.",
    from: "Springer Mountain, USA",
    to: "Various sections",
    distance: 3500,
    duration: "5–7 months (full trail)"
  },
  {
    id: 5,
    title: "Milford Track",
    description: "Renowned New Zealand hike through fjords, rainforests, and waterfalls.",
    from: "Glade Wharf, New Zealand",
    to: "Sandfly Point, New Zealand",
    distance: 53.5,
    duration: "4 days"
  },
  {
    id: 6,
    title: "Route 66",
    description: "Legendary road through the USA featuring deserts, small towns, and retro charm.",
    from: "Chicago, USA",
    to: "Santa Monica, USA",
    distance: 3940,
    duration: "2–3 weeks"
  }
];

}