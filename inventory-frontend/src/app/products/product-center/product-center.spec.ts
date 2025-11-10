import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ProductCenter } from './product-center';

describe('ProductCenter', () => {
  let component: ProductCenter;
  let fixture: ComponentFixture<ProductCenter>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ProductCenter]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ProductCenter);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
