import { Component, inject } from '@angular/core';
import { Router } from '@angular/router';

import { ProductForm } from "../product-form/product-form";

import { ProductService } from '../../services/product.service';
import { CommonResponseService } from '../../services/common-response.service';

import { Toast } from 'primeng/toast';
import { MessageService } from 'primeng/api';

@Component({
  selector: 'app-product-create',
  imports: [
    ProductForm,
    Toast,
  ],
  providers: [MessageService],  
  templateUrl: './product-create.html',
  styleUrl: './product-create.css'
})
export class ProductCreate {
  private productService: ProductService = inject(ProductService);
  private commonResponseService = inject(CommonResponseService);
  private messageService = inject(MessageService)
  private router = inject(Router)

  onSubmit(formData: any)
  {
    console.dir(formData);

    this.productService.createProduct(formData)
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
