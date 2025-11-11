import { Routes } from "@angular/router";
import { InventoryCenter } from "../inventory/inventory-center/inventory-center";
import { InventoryCreate } from "../inventory/inventory-create/inventory-create";


export const INVENTORY_ROUTES: Routes = [
    {
        path: '',
        component: InventoryCenter,
        children: [            
            { path: ':productId', component: InventoryCreate },
        ]        
    }
];