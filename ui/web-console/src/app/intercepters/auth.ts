import { Injectable } from '@angular/core';
import { HttpInterceptor, HttpRequest, HttpHandler } from '@angular/common/http';
import { DataService } from '../data.service';

import { HTTP_INTERCEPTORS } from '@angular/common/http';

@Injectable()
export class AuthInterceptor implements HttpInterceptor {

    constructor(private service: DataService) { }

    intercept(req: HttpRequest<any>, next: HttpHandler) {
        // Get the auth token from the service.
        const authToken = this.service.getJwtToken();

        if (authToken) {
            // Clone the request and replace the original headers with
            // cloned headers, updated with the authorization.
            const authReq = req.clone({
                headers: req.headers.set('Authorization', "Bearer " + authToken)
            });

            // send cloned request with header to the next handler.
            return next.handle(authReq);
        } else {
            return next.handle(req);
        }
    }
}

export const httpInterceptorProviders = [
    { provide: HTTP_INTERCEPTORS, useClass: AuthInterceptor, multi: true },
];