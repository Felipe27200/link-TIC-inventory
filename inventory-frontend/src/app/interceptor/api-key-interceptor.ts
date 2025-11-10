import { HttpHandlerFn, HttpInterceptorFn, HttpRequest } from '@angular/common/http';
import { environment } from '../../environments/environment.development';

export const apiKeyInterceptor: HttpInterceptorFn = (req: HttpRequest<any>, next: HttpHandlerFn) => {
  let headerName: string | undefined;
  let headerValue: string | undefined;

  // --- 1. Identify the Target Service based on the URL ---

  if (req.url.startsWith(environment.inventoryUrl)) 
  {
    // Inventory Service Request
    headerName = environment.inventoryHeaderName;
    headerValue = environment.inventoryApiKey;

  }
  else if (req.url.startsWith(environment.productUrl)) 
  {
    // Product Service Request
    headerName = environment.productHeaderName;
    headerValue = environment.productApiKey;

  }

  if (headerName && headerValue) {
    const clonedRequest = req.clone({
      headers: req.headers.set(headerName, headerValue),
      withCredentials: false,
    });

    return next(clonedRequest);
  }

  // If no matching URL is found, pass the original request untouched
  return next(req);
};
