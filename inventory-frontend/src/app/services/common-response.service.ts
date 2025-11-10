import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class CommonResponseService {

  constructor() { }

    setToastErrorMessage(error: any)
  {
    console.log(error);
    let listErrors = [];

    if (error.hasOwnProperty("error") 
      && (error.error !== null && error.error !== undefined) 
      && error.error.hasOwnProperty("message"))
      listErrors.push({ severity: 'error', summary: 'Error!', detail: error.error.message });

    if (
      error.hasOwnProperty("error") 
      && (error.error !== null && error.error !== undefined) 
      && error.error.hasOwnProperty("errors")
      && error.error.errors !== null && error.error.errors !== undefined
      && Array.isArray(error.error.errors)
    ) {
      error.error.errors.forEach((element: any) => {
        listErrors.push({ severity: 'error', summary: 'Error!', detail: element });
      });
    }

    return listErrors;
  }
}