import { ComponentFixture, TestBed } from '@angular/core/testing';

import { InventoryCenter } from './inventory-center';

describe('InventoryCenter', () => {
  let component: InventoryCenter;
  let fixture: ComponentFixture<InventoryCenter>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [InventoryCenter]
    })
    .compileComponents();

    fixture = TestBed.createComponent(InventoryCenter);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
