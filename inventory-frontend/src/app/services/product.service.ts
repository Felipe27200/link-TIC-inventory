import { inject, Injectable } from '@angular/core';
import { environment } from '../../environments/environment';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { ErrorHandlerService } from './error-handler.service';
import { catchError } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class ProductService {
  private baseUrl = environment.productUrl + "/api/products";

  private http: HttpClient = inject(HttpClient);
  private errorHandler: ErrorHandlerService = inject(ErrorHandlerService);

  httpOptions = {
    headers: new HttpHeaders({ 'Content-Type': 'application/json' }),
    withCredentials: true
  };

  createProduct(product: any)
  {
    let url = this.baseUrl + "/";

    return this.http.post<any>(url, product, this.httpOptions)
      .pipe(catchError(this.errorHandler.handleError));
  }

  updateProduct(product: any)
  {
    let url = this.baseUrl + "/";

    return this.http.put<any>(url, product, this.httpOptions)
      .pipe(catchError(this.errorHandler.handleError));
  }

  findAll() {
    let url = `${this.baseUrl}/`;

    return this.http.get<any>(url, this.httpOptions)
      .pipe(
        catchError(this.errorHandler.handleError)
      );
  }

  findById(id: number) {
    let url = `${this.baseUrl}/${+id}`;

    return this.http.get<any>(url, this.httpOptions)
      .pipe(
        catchError(this.errorHandler.handleError)
      );
  }

  deleteById(id: number) {
    let url = `${this.baseUrl}/${+id}`;

    return this.http.delete<any>(url, this.httpOptions)
      .pipe(
        catchError(this.errorHandler.handleError)
      );
  }
}
