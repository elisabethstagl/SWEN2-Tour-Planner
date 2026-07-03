import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MyTours } from './my-tours';

describe('MyTours', () => {
  let component: MyTours;
  let fixture: ComponentFixture<MyTours>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [MyTours]
    })
    .compileComponents();

    fixture = TestBed.createComponent(MyTours);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
