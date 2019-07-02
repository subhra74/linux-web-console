import { Component, OnInit, Input, OnChanges, Output, EventEmitter } from '@angular/core';
import { environment } from 'src/environments/environment';
import { DataService } from 'src/app/data.service';

@Component({
  selector: 'app-image-viewer',
  templateUrl: './image-viewer.component.html',
  styleUrls: ['./image-viewer.component.css']
})
export class ImageViewerComponent implements OnInit, OnChanges {

  @Input()
  url: string;
  src: string;

  @Output()
  componentClosed = new EventEmitter<any>();

  constructor(private service: DataService) { }

  ngOnInit() {
    console.log("image viewer init");
  }

  ngOnChanges() {
    if (this.url) {
      this.src = environment.BIN_URL + "image/" + btoa(this.url) + "?token=" + this.service.getJwtToken();
      console.log("image src " + this.src);
    }
  }

  close() {
    this.componentClosed.emit({});
  }

}
