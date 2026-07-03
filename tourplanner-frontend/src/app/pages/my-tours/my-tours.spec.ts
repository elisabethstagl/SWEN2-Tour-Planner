import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MyToursComponent } from './my-tours';

describe('MyToursComponent', () => {
  let component: MyToursComponent;
  let fixture: ComponentFixture<MyToursComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [MyToursComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(MyToursComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
