import { Component, EventEmitter, inject, Input, OnInit, Output } from '@angular/core';

import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';

import { CardModule } from 'primeng/card';
import { ButtonModule } from 'primeng/button';
import { Router, RouterLink, ActivatedRoute } from '@angular/router';
import { Toast } from 'primeng/toast';

import { Product } from '../../interface/Product';

import { InventoryService } from '../../services/inventory.service';
import { ProductService } from '../../services/product.service';
import { CommonResponseService } from '../../services/common-response.service';

import { MessageService } from 'primeng/api';

@Component({
  selector: 'app-purchase-create',
  imports: [
    ReactiveFormsModule,
    CommonModule,
    CardModule,
    ButtonModule,
    RouterLink,
    Toast
  ],
  providers: [MessageService],
  templateUrl: './purchase-create.html',
  styleUrl: './purchase-create.css',
})
export class PurchaseCreate implements OnInit {
  @Input() title: string = '';

  @Output() eventEmitter = new EventEmitter();
  errors: any[] = [];

  product!: Product | null;
  inventory!: any;
  currentStock = 0;

  private router: Router = inject(Router)
  private route: ActivatedRoute = inject(ActivatedRoute)
  private fb: FormBuilder = inject(FormBuilder)
  private inventoryService = inject(InventoryService)
  private productService = inject(ProductService)
  private commonResponseService = inject(CommonResponseService);
  private messageService = inject(MessageService)

  purchaseForm = this.fb.group({
    quantity: ['', Validators.required],
  });

  ngOnInit(): void 
  {
    const id = Number(this.route.snapshot.paramMap.get('productId'));

    if (id === undefined || id === null || isNaN(id)) 
    {
      this.router.navigate(['/products']);
    }

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

          this.inventoryService.findByProductFK(this.product.id)
            .subscribe({
              next: (response) => {
                this.inventory = response;

                this.currentStock = this.inventory?.data.attributes.quantity;
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

  onSubmit() {
    this.errors = [];

    if (!this.purchaseForm.valid || (this.product == undefined || this.inventory == undefined))
      return;

    console.log("enter", this.product, this.inventory)

    if (this.quantity == null || this.quantity.value == null || this.quantity.value == undefined)
      this.errors.push("The quantity is required");

    if (isNaN(Number(this.quantity?.value)))
      this.errors.push('The quantity must be a number');
    if ((Number(this.quantity?.value)) <= 0)
      this.errors.push('the quantity must be greater than zero');


    if (this.errors.length > 0)
      return;

    let formData = {
      quantity: this.quantity?.value,
      productFK: this.product.id,
    }

    this.inventoryService.purchase(formData)
      .subscribe({
        next: (response) => {
          let data = response.data

          this.messageService.add({ 
            severity: 'info', 
            summary: 'Purchase Successfully', 
            detail: "Total Amount: $" + data.totalPurchase
          });

          this.currentStock = data.currentStock;
        },
        error: (error) => {
          this.messageService.addAll(this.commonResponseService.setToastErrorMessage(error));
        }
      })
  }

  backProducts() {
    this.router.navigate(['/products'])
  }

  get quantity() { return this.purchaseForm.get('quantity'); }
}
