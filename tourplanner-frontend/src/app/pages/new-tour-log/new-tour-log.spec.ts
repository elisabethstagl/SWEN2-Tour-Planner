import { ComponentFixture, TestBed } from '@angular/core/testing';

import { NewTourLog } from './new-tour-log';

describe('NewTourLog', () => {
  let component: NewTourLog;
  let fixture: ComponentFixture<NewTourLog>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [NewTourLog]
    })
    .compileComponents();

    fixture = TestBed.createComponent(NewTourLog);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
