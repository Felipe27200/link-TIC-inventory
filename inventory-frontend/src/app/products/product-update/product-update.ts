import { Component, inject, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { Toast } from 'primeng/toast';
import { CardModule } from 'primeng/card';
import { ButtonModule } from 'primeng/button';

import { CommonResponseService } from '../../services/common-response.service';

import { MessageService } from 'primeng/api';
import { ProductForm } from '../product-form/product-form';
import { ProductService } from '../../services/product.service';
import { Product } from '../../interface/Product';

@Component({
  selector: 'app-product-update',
  imports: [
    ProductForm,
    Toast,
    CardModule,
    ButtonModule,
  ],
  providers: [MessageService],
  templateUrl: './product-update.html',
  styleUrl: './product-update.css'
})
export class ProductUpdate {
  private productService: ProductService = inject(ProductService);
  private commonResponseService = inject(CommonResponseService);
  private messageService = inject(MessageService)

  private router = inject(Router);
  private route: ActivatedRoute = inject(ActivatedRoute)

  product!: Product;
  notFound!: any;

  ngOnInit(): void {
    const id = Number(this.route.snapshot.paramMap.get('id'));

    if (id === undefined || id === null || isNaN(id))
    {
      this.router.navigate(['/products']);
    }
    else
    {
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
          },
          error: (error) => {
            this.notFound = error.error;
          }
        });
    }
  }

  onSubmit(formData: Product)
  {
    let id = this.product.id ? this.product.id : -1;

    this.productService.updateProduct(formData, id)
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
