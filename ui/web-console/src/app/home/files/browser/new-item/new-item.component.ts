import { Component, OnInit, Input, Output, EventEmitter } from '@angular/core';

@Component({
  selector: 'app-new-item',
  templateUrl: './new-item.component.html',
  styleUrls: ['./new-item.component.css']
})
export class NewItemComponent implements OnInit {

  @Input()
  itemType: string;
  fileName: string;

  @Output()
  dialogCancelled = new EventEmitter<any>();

  @Output()
  fileNameEntered = new EventEmitter<string>();

  constructor() { }

  ngOnInit() {
  }

  createItem() {
    this.fileNameEntered.emit(this.fileName);
  }

  cancelDialog() {
    this.dialogCancelled.emit();
  }

}
