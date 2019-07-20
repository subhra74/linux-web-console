import { Component, OnInit, Input, Output, EventEmitter, OnChanges } from '@angular/core';
import { DataService } from 'src/app/data.service';
import { environment } from 'src/environments/environment';

@Component({
  selector: 'app-media-player',
  templateUrl: './media-player.component.html',
  styleUrls: ['./media-player.component.css']
})
export class MediaPlayerComponent implements OnInit, OnChanges {
  @Input()
  url: string;
  src: string;

  @Output()
  componentClosed = new EventEmitter<any>();
  constructor(private service: DataService) { }

  ngOnInit() {
  }

  ngOnChanges() {
    if (this.url) {
      this.src = environment.BIN_URL + "blob/" + btoa(this.url) + "?token=" + this.service.getJwtToken();
      console.log("image src " + this.src);
    }
  }

  close() {
    this.componentClosed.emit({});
  }

}
