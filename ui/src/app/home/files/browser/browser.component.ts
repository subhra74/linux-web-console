import { Component, OnInit } from '@angular/core';
import { TabItem } from 'src/app/model/tab-item';

@Component({
  selector: 'app-browser',
  templateUrl: './browser.component.html',
  styleUrls: ['./browser.component.css']
})
export class BrowserComponent implements OnInit {

  tabs:TabItem;
  constructor() { }

  ngOnInit() {
  }

}
