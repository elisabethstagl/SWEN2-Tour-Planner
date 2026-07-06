import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TourDetailsCard } from './tour-details-card';

describe('TourDetailsCard', () => {
  let component: TourDetailsCard;
  let fixture: ComponentFixture<TourDetailsCard>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [TourDetailsCard]
    })
    .compileComponents();

    fixture = TestBed.createComponent(TourDetailsCard);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
