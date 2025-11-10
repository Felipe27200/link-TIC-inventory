import { Component, inject } from '@angular/core';

import { RouterOutlet, RouterLink, Router } from '@angular/router';

import { Button } from 'primeng/button';

@Component({
  selector: 'app-product-center',
  imports: [
    RouterOutlet,
    Button,
    RouterLink
  ],
  templateUrl: './product-center.html',
  styleUrl: './product-center.css'
})
export class ProductCenter {
}
