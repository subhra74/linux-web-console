import { Component, OnInit, Input, OnChanges, Output, EventEmitter } from '@angular/core';
import { FileInfo } from 'src/app/model/file-info';
import { DataService } from 'src/app/data.service';
import { PosixPermissions } from 'src/app/model/posix-permissions';
import { FileItem } from 'src/app/model/file-item';

@Component({
  selector: 'app-info',
  templateUrl: './info.component.html',
  styleUrls: ['./info.component.css']
})
export class InfoComponent implements OnInit {

  info: FileItem;
  posixPerm: PosixPermissions;

  @Output()
  dialogClosed = new EventEmitter<any>();

  constructor(private service: DataService) { }

  ngOnInit() {
    for (let i = 0; i < this.service.tabs[this.service.selectedTab].files.length; i++) {
      if (this.service.tabs[this.service.selectedTab].files[i].selected) {
        this.info = this.service.tabs[this.service.selectedTab].files[i];
        break;
      }
    }
    if (this.info) {
      this.service.getPermissionDetails(this.info.path).subscribe((resp: PosixPermissions) => {
        this.posixPerm = resp;
      });
    }
  }

  saveAndClose() {
    this.service.setPermissionDetails(this.info.path, this.posixPerm).subscribe((resp: PosixPermissions) => {
      this.dialogClosed.emit({});
    });
  }

  closeDialog() {
    this.dialogClosed.emit({});
  }

}
