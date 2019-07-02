import { Component, OnInit, Input, Output, EventEmitter } from '@angular/core';

@Component({
  selector: 'app-unsupported-content-viewer',
  templateUrl: './unsupported-content-viewer.component.html',
  styleUrls: ['./unsupported-content-viewer.component.css']
})
export class UnsupportedContentViewerComponent implements OnInit {
  @Input()
  file: string;
  @Input()
  url: string;

  @Output()
  componentClosed = new EventEmitter<any>();

  @Output()
  onDownload = new EventEmitter<any>();

  @Output()
  onOpen = new EventEmitter<any>();

  constructor() { }

  ngOnInit() {
  }

  close() {
    this.componentClosed.emit({});
  }

  download(){
    this.onDownload.emit({});
  }

  open(){
    this.onOpen.emit({});
  }

}
