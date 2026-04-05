import { ComponentFixture, TestBed } from '@angular/core/testing';

import { NewTour } from './new-tour';

describe('NewTour', () => {
  let component: NewTour;
  let fixture: ComponentFixture<NewTour>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [NewTour]
    })
    .compileComponents();

    fixture = TestBed.createComponent(NewTour);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
