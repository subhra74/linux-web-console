import { Component, OnInit } from '@angular/core';
import { DataService } from 'src/app/data.service';

@Component({
  selector: 'app-uploader-progress',
  templateUrl: './uploader-progress.component.html',
  styleUrls: ['./uploader-progress.component.css']
})
export class UploaderProgressComponent implements OnInit {
  showPopup: boolean = false;
  hoverIndex: number;
  constructor(public service: DataService) { }

  ngOnInit() {
  }

  hasAnyTransfers() {
    let t: boolean = (this.service.uploads && this.service.uploads.length > 0) || (this.service.fileOperations && this.service.fileOperations.length > 0);
    return t;
  }

  getTransferCount() {
    let c: number = 0;
    if (this.service.uploads && this.service.uploads.length > 0) {
      c += this.service.uploads.length;
    }
    if (this.service.fileOperations && this.service.fileOperations.length > 0) {
      c += this.service.fileOperations.length;
    }
    return c;
  }

  togglePopup() {
    console.log("toggle popup");
    this.showPopup = !this.showPopup;
  }

}
