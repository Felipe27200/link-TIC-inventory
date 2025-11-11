import { Routes } from '@angular/router';

export const routes: Routes = [
    {
        path: 'products',
        loadChildren: () => import('./routes/product.routes').then(m => m.PRODUCT_ROUTES)
    },
    {
        path: 'inventory',
        loadChildren: () => import('./routes/inventory.routes').then(m => m.INVENTORY_ROUTES)
    },
    { path: "", redirectTo: '/products', pathMatch: 'full' },
];
