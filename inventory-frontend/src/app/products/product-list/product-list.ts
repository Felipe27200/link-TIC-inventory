import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';

import { Card } from 'primeng/card';
import { Toast } from 'primeng/toast';
import { TableModule } from 'primeng/table';
import { IconFieldModule } from 'primeng/iconfield';
import { InputIconModule } from 'primeng/inputicon';
import { Button, ButtonDirective } from 'primeng/button';
import { ConfirmDialogModule } from 'primeng/confirmdialog';
import { InputNumberModule } from 'primeng/inputnumber';

import { ConfirmationService, MessageService } from 'primeng/api';

import { ProductService } from '../../services/product.service';
import { CommonResponseService } from '../../services/common-response.service';

@Component({
  selector: 'app-product-list',
  imports: [
    CommonModule,
    FormsModule,
    Card,
    Toast,
    TableModule,
    IconFieldModule,
    InputIconModule,
    InputNumberModule,
    Button,
    ConfirmDialogModule,
    RouterLink,
],
  providers: [
    ConfirmationService,
    MessageService
  ],
  templateUrl: './product-list.html',
  styleUrl: './product-list.css'
})
export class ProductList implements OnInit {
  private productService: ProductService = inject(ProductService);
  private messageService: MessageService = inject(MessageService);
  private confirmationService: ConfirmationService = inject(ConfirmationService)
  private commonResponseService: CommonResponseService = inject(CommonResponseService);
  private router: Router = inject(Router)

  products: any[] = [];
  response: any;

  totalElements = 0;
  totalPages = 0;
  size = 10;
  page = 0;

  ngOnInit(): void {
    this.productService.findAll()
      .subscribe({
        next: (response) => {
          this.products = response.data;
        },
        error: (error) => {
          this.messageService.addAll(this.commonResponseService.setToastErrorMessage(error));
        }
      });
  }

  getProducts(page: number, size: number): void {
    this.productService.findAll(page, size)
      .subscribe((res: any) => {
        this.response = res;
        this.products = res.data;
        this.totalElements = res.meta.totalElements;
        this.totalPages = res.meta.totalPages;
        this.size = res.meta.size;
        this.page = res.meta.number;
      });
  }

  nextPage() {
    if ((this.page + 1) * this.size < this.totalElements) 
    {
      this.getProducts(this.page + 1, this.size);
    }
  }

  prevPage() {
    if (this.page > 0) 
    {
      this.getProducts(this.page - 1, this.size);
    }
  }

  changeSize()
  {
    this.getProducts(0, this.size);
  }

  dialogDelete(product: any, event: Event) 
  {
    this.confirmationService.confirm({
      target: event.target as EventTarget,
      message: `Do you want to delete ${product.attributes.name}?`,
      header: 'Delete Product',
      icon: 'pi pi-info-circle',
      acceptButtonStyleClass: "p-button-danger p-button-text",
      rejectButtonStyleClass: "p-button-text p-button-text",
      acceptIcon: "none",
      rejectIcon: "none",

      accept: () => {
        this.deleteProduct(product);
      },
      reject: () => { }
    });
  }

  deleteProduct(product: any) {
    this.productService.deleteById(product.id)
      .subscribe({
        next: (response) => {
          this.messageService.add({ severity: 'info', summary: 'Deleted', detail: response.body });
          this.ngOnInit();
        },
        error: (error) => {
          if (error.hasOwnProperty("error") && error.error.hasOwnProperty("message"))
            this.messageService.add({ severity: 'error', summary: 'Error', detail: error.error.message });
        }
      });
  }
}
