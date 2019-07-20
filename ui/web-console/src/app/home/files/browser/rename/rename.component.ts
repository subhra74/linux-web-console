import { Component, OnInit, Input, OnChanges, Output, EventEmitter } from '@angular/core';
import { FormControl } from '@angular/forms';
import { DataService } from 'src/app/data.service';

@Component({
  selector: 'app-rename',
  templateUrl: './rename.component.html',
  styleUrls: ['./rename.component.css']
})
export class RenameComponent implements OnInit {

  @Output()
  fileNameChanged = new EventEmitter<string>();

  @Output()
  dialogCancelled = new EventEmitter<any>();

  newFileName: string;

  constructor(private service:DataService) { }

  ngOnInit() {
    for (let i = 0; i < this.service.tabs[this.service.selectedTab].files.length; i++) {
      if (this.service.tabs[this.service.selectedTab].files[i].selected) {
       this.newFileName = this.service.tabs[this.service.selectedTab].files[i].name;
       console.log("new filename: "+this.newFileName);
       break;
      }
    }
  }

  renameFile() {
    this.fileNameChanged.emit(this.newFileName);
  }

  cancelDialog() {
    this.dialogCancelled.emit();
  }

}
