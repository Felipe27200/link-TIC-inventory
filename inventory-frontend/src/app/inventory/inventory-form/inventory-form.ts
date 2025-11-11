import { Component, EventEmitter, inject, Input, OnChanges, Output, SimpleChanges } from '@angular/core';

import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';

import { CardModule } from 'primeng/card';
import { ButtonModule } from 'primeng/button';
import { Router, RouterLink } from '@angular/router';
import { Toast } from 'primeng/toast';

import { Product } from '../../interface/Product';

import { InventoryService } from '../../services/inventory.service';
import { ProductService } from '../../services/product.service';
import { CommonResponseService } from '../../services/common-response.service';

import { MessageService } from 'primeng/api';

@Component({
  selector: 'app-inventory-form',
  imports: [
    ReactiveFormsModule,
    CommonModule,
    CardModule,
    ButtonModule,
    RouterLink,
    Toast
  ],
  providers: [MessageService],
  templateUrl: './inventory-form.html',
  styleUrl: './inventory-form.css',
})
export class InventoryForm implements OnChanges {
  @Input() title: string = '';
  @Input() productId!: any;

  @Output() eventEmitter = new EventEmitter();
  errors: any[] = [];

  product!: Product | null;
  inventory!: any;

  private router: Router = inject(Router)
  private fb: FormBuilder = inject(FormBuilder)
  private inventoryService = inject(InventoryService)
  private productService = inject(ProductService)
  private commonResponseService = inject(CommonResponseService);
  private messageService = inject(MessageService)

  productForm = this.fb.group({
    quantity: ['', Validators.required],
  });

  onSubmit() {
    this.errors = [];

    if (!this.productForm.valid)
      return;

    if (this.quantity == null || this.quantity.value == null || this.quantity.value == undefined)
      this.errors.push("The name is required");

    if (isNaN(Number(this.quantity?.value)))
      this.errors.push('The quantity must be a number');
    if ((Number(this.quantity?.value)) <= 0)
      this.errors.push('the quantity must be greater than zero');


    if (this.errors.length > 0)
      return;

    let formData = {
      quantity: this.quantity?.value,
      productFK: this.productId,
    }

    this.eventEmitter.emit({
      formData,
      update: this.inventory ? true : false
    });
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes["productId"] === null || changes["productId"] === undefined
      || changes["productId"].currentValue === null || changes["productId"].currentValue === undefined) {
      return;
    }

    let id = changes["productId"].currentValue;

    this.productService.findById(id)
      .subscribe({
        next: (response) => {
          let data = response.data;

          this.product = {
            id: data.id,
            name: data.attributes.name,
            description: data.attributes.description,
            price: data.attributes.price
          };

          this.inventoryService.findByProductFK(this.productId)
            .subscribe({
              next: (response) => {
                this.inventory = response;

                this.quantity?.setValue(this.inventory.data.attributes.quantity);
              },
              error: (error) => {
                this.messageService.addAll(this.commonResponseService.setToastErrorMessage(error));
              }
            })

        },
        error: (error) => {
          this.messageService.addAll(this.commonResponseService.setToastErrorMessage(error));
        }
      });
  }

  backProducts() {
    this.router.navigate(['/products'])
  }

  get quantity() { return this.productForm.get('quantity'); }
}
