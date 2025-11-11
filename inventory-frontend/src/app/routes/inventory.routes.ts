import { Routes } from "@angular/router";
import { InventoryCenter } from "../inventory/inventory-center/inventory-center";
import { InventoryCreate } from "../inventory/inventory-create/inventory-create";
import { PurchaseCreate } from "../inventory/purchase-create/purchase-create";


export const INVENTORY_ROUTES: Routes = [
    {
        path: '',
        component: InventoryCenter,
        children: [            
            { path: 'purchase/:productId', component: PurchaseCreate },
            { path: ':productId', component: InventoryCreate },
        ]        
    }
];