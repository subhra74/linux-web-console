import { Component, OnInit, ViewChild, ElementRef, OnDestroy, AfterViewInit, Input, OnChanges } from '@angular/core';
import { Terminal, ITerminalOptions } from 'xterm';
import * as attach from 'xterm/lib/addons/attach/attach'
import * as fit from 'xterm/lib/addons/fit/fit'
import { container } from '@angular/core/src/render3';
import { DataService } from '../../data.service';
import { Observable } from 'rxjs';

@Component({
  selector: 'app-terminal',
  templateUrl: './terminal.component.html',
  styleUrls: ['./terminal.component.css'],
  host: {
    '(window:resize)': 'onResize($event)'
  }
})
export class TerminalComponent implements OnInit, OnDestroy, AfterViewInit, OnChanges {

  @ViewChild('terminal')
  container: ElementRef;

  @Input()
  public socket: WebSocket;

  @Input()
  public oldText: string

  @Input()
  public appId: string;

  @Input()
  displayMode: string;

  viewInit: boolean = false;

  lastDisplayMode: string;

  // @ViewChild('parent')
  // parent: ElementRef;

  private xterm: any;

  private textInit: boolean = false;
  private socketAttached: boolean = false;

  constructor(public service: DataService) {
    console.log("constructor called")
    Terminal.applyAddon(attach);
    Terminal.applyAddon(fit);

    this.xterm = new Terminal({
      convertEol: false,
      fontFamily: `"Courier New", Courier, monospace`,
      rendererType: 'canvas',
      cols: 80,
      rows: 24
    });

  }

  ngOnChanges() {
    if ((!this.textInit) && this.oldText) {
      this.xterm.write(this.oldText);
      this.textInit = true;
    }

    if ((!this.socketAttached) && this.socket) {
      console.log("attaching socket")
      this.xterm.attach(this.socket);
      this.socketAttached = true;
    }

    console.log("this.displayMode: "+this.displayMode+" this.lastDisplayMode: "+this.lastDisplayMode);

    if (this.displayMode != this.lastDisplayMode) {
      if (this.viewInit) {
        console.log("fitting")
        this.xterm.fit();
        this.lastDisplayMode = this.displayMode;
      }
    }
  }

  ngOnInit() {
    console.log("terminal component ngOnInit");
  }

  ngOnDestroy() {
    if (this.socketAttached && this.socket) {
      this.xterm.detach(this.socket);
    }

    if (this.xterm) {
      this.xterm.dispose();
    }

  }

  ngAfterViewInit() {
    let el = this.container.nativeElement as HTMLElement;
    this.xterm.open(el);
    this.xterm.fit();
    let rows = this.xterm.proposeGeometry().rows;
    let cols = this.xterm.proposeGeometry().cols;
    console.log("Rows: " + rows + " cols: " + cols + " real-rows: " + this.xterm.getOption("rows") + " real-cols: " + this.xterm.getOption("cols"));
    this.service.resizePty(this.appId, cols, rows, el.offsetWidth, el.offsetHeight);
    this.viewInit = true;
  }

  onResize(event: any) {
    if (this.displayMode == 'normal' && this.lastDisplayMode == 'normal') {
      return;
    }
    let el = this.container.nativeElement as HTMLElement;
    this.xterm.fit();
    let rows = this.xterm.proposeGeometry().rows;
    let cols = this.xterm.proposeGeometry().cols;
    this.service.resizePty(this.appId, cols, rows, el.offsetWidth, el.offsetHeight);
    console.log("after window resize - Rows: " + rows + " cols: " + cols + " real-rows: " + this.xterm.getOption("rows") + " real-cols: " + this.xterm.getOption("cols"));
  }

}
