import { Component, inject, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { Toast } from 'primeng/toast';
import { CardModule } from 'primeng/card';
import { ButtonModule } from 'primeng/button';

import { CommonResponseService } from '../../services/common-response.service';

import { MessageService } from 'primeng/api';
import { InventoryForm } from '../inventory-form/inventory-form';

import { InventoryService } from '../../services/inventory.service';
import { ProductService } from '../../services/product.service';
import { Product } from '../../interface/Product';

@Component({
  selector: 'app-inventory-create',
  imports: [
    InventoryForm,
    Toast,
    CardModule,
    ButtonModule
  ],
  providers: [MessageService],
  templateUrl: './inventory-create.html',
  styleUrl: './inventory-create.css',
})
export class InventoryCreate implements OnInit {
  private inventoryService: InventoryService = inject(InventoryService);
  private commonResponseService = inject(CommonResponseService);
  private messageService = inject(MessageService)

  private router = inject(Router);
  private route: ActivatedRoute = inject(ActivatedRoute);

  productId!: any;

  ngOnInit(): void {
    const id = Number(this.route.snapshot.paramMap.get('productId'));

    if (id === undefined || id === null || isNaN(id)) {
      this.router.navigate(['/products']);
    }
    else {
      this.productId = id;
    }
  }

  onSubmit(formData: any) {
    console.log(formData)

    if (formData.update)
    {
      this.inventoryService.updateInventory(formData.formData)
        .subscribe({
          next: (response: any) => {
            console.log(response);
  
            this.router.navigate(["/products"])
          },
          error: (error) => {
            this.messageService.addAll(this.commonResponseService.setToastErrorMessage(error));
          }
        })
    }
    else
    {
      this.inventoryService.createInventory(formData.formData)
        .subscribe({
          next: (response: any) => {
            console.log(response);
  
            this.router.navigate(["/products"])
          },
          error: (error) => {
            this.messageService.addAll(this.commonResponseService.setToastErrorMessage(error));
          }
        })
    }
  }
}
