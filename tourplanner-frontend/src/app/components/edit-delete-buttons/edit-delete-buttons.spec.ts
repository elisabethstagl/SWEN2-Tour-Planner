import { ComponentFixture, TestBed } from '@angular/core/testing';

import { EditDeleteButtons } from './edit-delete-buttons';

describe('EditDeleteButtons', () => {
  let component: EditDeleteButtons;
  let fixture: ComponentFixture<EditDeleteButtons>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [EditDeleteButtons]
    })
    .compileComponents();

    fixture = TestBed.createComponent(EditDeleteButtons);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
