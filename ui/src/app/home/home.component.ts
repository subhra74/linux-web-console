import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { TerminalSession } from '../model/terminal-session';
import { DataService } from '../data.service';

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css']
})
export class HomeComponent implements OnInit {

  selectedIndex: number = 0;
  // tabs = [
  //   {
  //     text: 'File browser',
  //     url: '/app/files',
  //     icon: 'fa-folder'
  //   },
  //   {
  //     text: 'Editor',
  //     url: '/app/editor',
  //     icon: 'fa-file'
  //   },
  //   {
  //     text: 'Search',
  //     url: '/app/search',
  //     icon: 'fa-search'
  //   },
  //   {
  //     text: 'Settings',
  //     url: '/app/search',
  //     icon: 'fa-cogs'
  //   }
  // ];

  // selectTab(i: number) {
  //   this.selectedIndex = i;
  //   this.router.navigate([this.tabs[i].url]);
  // }

  session: TerminalSession;

  mobileTerminalVisible: boolean = false;

  view: string;

  constructor(private router: Router, public service: DataService) { }

  ngOnInit() {
    this.service.currentViewChanger.subscribe((view: string) => {
      this.view = view;
    })
    if (!this.service.terminalSession) {
      this.service.connect().subscribe((data: any) => {
        console.log("terminal created on server: " + JSON.stringify(data));
        let instance = new TerminalSession();
        instance.displayMode = 'normal';
        let listener: any = (ev: any) => {
          instance.socket.removeEventListener('open', listener);
          console.log("Socket created: " + JSON.stringify(instance));
          this.service.terminalSession = instance;
          this.session = instance;
        }
        let ws: WebSocket = instance.createSocket(data.id, this.service.getJwtToken());
        ws.addEventListener('open', listener);
        ws.addEventListener('error', err => {
          console.log("Error: " + JSON.stringify(err));
        });
      });
    } else {
      this.session = this.service.terminalSession;
    }
  }

  toggleFullscreen() {
    if (this.session.displayMode == 'fullscreen') {
      this.session.displayMode = this.session.lastDisplayMode;
    } else {
      this.session.lastDisplayMode = this.session.displayMode;
      this.session.displayMode = 'fullscreen';
    }
  }

  toggleMinimize() {
    if (this.session.displayMode == 'minimized') {
      this.session.lastDisplayMode = this.session.displayMode;
      this.session.displayMode = 'normal';
    } else {
      this.session.lastDisplayMode = this.session.displayMode;
      this.session.displayMode = 'minimized';
    }
  }

  logout() {
    this.service.clearOldToken();
    this.router.navigate(["/login"]);
  }

}
