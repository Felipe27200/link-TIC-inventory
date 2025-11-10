import { Routes } from "@angular/router";

import { ProductCenter } from "../products/product-center/product-center";
import { ProductCreate } from "../products/product-create/product-create";
import { ProductUpdate } from "../products/product-update/product-update";
import { ProductList } from "../products/product-list/product-list";

export const PRODUCT_ROUTES: Routes = [
    {
        path: '',
        component: ProductCenter,
        children: [            
            { path: 'create', component: ProductCreate },
            { path: ':id', component: ProductUpdate },
            { path: '', component: ProductList },
        ]        
    }
];