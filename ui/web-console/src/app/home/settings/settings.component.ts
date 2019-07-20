import { Component, OnInit } from '@angular/core';
import { DataService } from 'src/app/data.service';

@Component({
  selector: 'app-settings',
  templateUrl: './settings.component.html',
  styleUrls: ['./settings.component.css']
})
export class SettingsComponent implements OnInit {

  userName: string;
  password: string;
  shell: string;
  error: string;
  saving: boolean;

  constructor(private service: DataService) { }

  ngOnInit() {
    this.service.getConfig().subscribe((res: any) => {
      this.userName = res["app.default-user"];
      this.shell = res["app.default-shell"];
    })
  }

  save() {
    if (!this.userName) {
      this.error = "Please enter username";
      return;
    }

    if (!this.shell) {
      this.error = "Please enter shell";
      return;
    }
    this.saving = true;
    this.error = null;
    let obj = {};
    obj["app.default-user"] = this.userName;
    if (this.password && this.password.length > 0) {
      obj["app.default-pass"] = this.password;
    }
    obj["app.default-shell"] = this.shell;

    console.log(JSON.stringify(obj))

    this.service.setConfig(obj).subscribe((res: any) => {
      this.saving = false;
      this.error = null;
    }, err => {
      this.saving = false;
      this.error = "Error saving configuration";
    })
  }

}
