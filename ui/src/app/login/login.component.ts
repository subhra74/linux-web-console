import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { DataService } from '../data.service';
import { HttpErrorResponse } from '@angular/common/http';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent implements OnInit {
  errorMessage: string;
  busy: boolean = false;
  constructor(private router: Router, public service: DataService) { }

  ngOnInit() {
  }

  signIn(user: string, pass: string, chk: boolean) {
    console.log(user + " " + pass + " " + chk);
    this.busy = true;
    this.service.signIn(user, pass).subscribe((resp: any) => {
      console.log(resp.token);
      this.service.posix = resp.posix;
      this.service.setToken(resp.token, chk);
      this.router.navigate(["/"]);
    }, (error: HttpErrorResponse) => {
      if (error.status == 401) {
        this.errorMessage = "Invalid username/password";
      } else {
        this.errorMessage = "Unable to login, error: " + error.statusText;
      }
      this.busy = false;
    });
  }

}
