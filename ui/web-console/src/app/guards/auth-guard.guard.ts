import { Injectable } from '@angular/core';
import { CanActivate, ActivatedRouteSnapshot, RouterStateSnapshot, Router } from '@angular/router';
import { Observable, Subject } from 'rxjs';
import { DataService } from '../data.service';

@Injectable({
  providedIn: 'root'
})
export class AuthGuardGuard implements CanActivate {
  private sub = new Subject<boolean>();
  constructor(private router: Router, private service: DataService) {
  }
  canActivate(
    next: ActivatedRouteSnapshot,
    state: RouterStateSnapshot): Observable<boolean> | Promise<boolean> | boolean {

    if (this.service.getJwtToken()) {
      this.service.checkToken().subscribe((resp: any) => {
        this.sub.next(true);
      }, (err: any) => {
        this.sub.next(false);
        this.service.clearOldToken();
        this.router.navigate(["/login"]);
      });
      return this.sub;
    } else {
      this.router.navigate(["/login"]);
      return;
      return false;
    }
  }
}
