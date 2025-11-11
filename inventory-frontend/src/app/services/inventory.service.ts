import { inject, Injectable } from '@angular/core';
import { environment } from '../../environments/environment';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { ErrorHandlerService } from './error-handler.service';
import { catchError } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class InventoryService {
  private baseUrl = environment.inventoryUrl + "/api/inventory";

  private http: HttpClient = inject(HttpClient);
  private errorHandler: ErrorHandlerService = inject(ErrorHandlerService);

  httpOptions = {
    headers: new HttpHeaders({ 'Content-Type': 'application/json' }),
    withCredentials: true
  };

  createInventory(inventory: any)
  {
    let url = this.baseUrl + "/";

    return this.http.post<any>(url, inventory, this.httpOptions)
      .pipe(catchError(this.errorHandler.handleError));
  }

  updateInventory(inventory: any)
  {
    let url = this.baseUrl + "/update";

    return this.http.put<any>(url, inventory, this.httpOptions)
      .pipe(catchError(this.errorHandler.handleError));
  }

  findByProductFK(productFK: any) {
    let url = `${this.baseUrl}/product/${+productFK}`;

    return this.http.get<any>(url, this.httpOptions)
      .pipe(
        catchError(this.errorHandler.handleError)
      );
  }
}
