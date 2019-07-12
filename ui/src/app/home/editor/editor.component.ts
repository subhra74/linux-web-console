import { Component, OnInit, ViewChild, ElementRef, AfterViewInit } from '@angular/core';

import * as ace from 'ace-builds';
//import 'ace-builds/src-noconflict/mode-javascript';
import 'ace-builds/src-noconflict/theme-github';

import 'ace-builds/src-noconflict/ext-language_tools';
import 'ace-builds/src-noconflict/ext-beautify';
import { DataService } from 'src/app/data.service';
import { ActivatedRoute, ParamMap } from '@angular/router';

const THEME = 'ace/theme/github';
const LANG = 'ace/mode/javascript';

@Component({
  selector: 'app-editor',
  templateUrl: './editor.component.html',
  styleUrls: ['./editor.component.css'],
  host: {
    '(window:resize)': 'onResize($event)'
  }
})
export class EditorComponent implements OnInit,AfterViewInit {

  @ViewChild('codeEditor') codeEditorElmRef: ElementRef;
  private codeEditor: ace.Ace.Editor;

  tabKeys: string[] = [];
  saving: boolean;

  constructor(public service: DataService, private route: ActivatedRoute) { }

  ngOnInit() {
    console.log("editor: ngOnInit "+this.codeEditorElmRef);
    this.getTabbedSessions();

    this.service.viewTextRequests.subscribe(a=>{
      this.getTabbedSessions();
      this.loadSession(this.service.selectedEditorTab);
    });

      // console.log("selected tab: " + this.service.selectedEditorTab+" tabkexs: "+this.tabKeys);
    if(this.tabKeys.length<1){
      return;
    }
    // if(!this.codeEditorElmRef){
    //   return;
    // }
    // console.log("editor loaded");
    

    // this.codeEditor.on("change", ()=> {
    //   let doc:any = this.codeEditor.getSession().getDocument();
    // let r:any=this.codeEditor.renderer;
    // let lineHeight = r.lineHeight;
    // console.log("doc.getLength(): "+doc.getLength()+" lineHeight: "+lineHeight)
    // this.codeEditorElmRef.nativeElement.style.height = +lineHeight * +doc.getLength() + "px";
    // this.codeEditor.resize();

    // this.codeEditor.setOption("maxLines", doc.getLength());
    //});

    // this.route.params.subscribe((params: ParamMap) => {
    //   // console.log("route init of editor: "+JSON.stringify(Object.keys(this.service.editorContexts)));
    //   // console.log("loading tabs")
      
    // });

    
  }

  shouldShowTab(){
    console.log("Tab count: "+this.tabKeys.length);
    return this.tabKeys.length>0;
  }

  ngAfterViewInit(){
    console.log("editor: ngAfterViewInit "+this.codeEditorElmRef);
    ace.require('ace/ext/language_tools');
    if(!this.codeEditorElmRef){
      console.log("Code editor reference not found");
      return;
    }
    const element = this.codeEditorElmRef.nativeElement;
    const editorOptions = this.getEditorOptions();

    this.codeEditor = ace.edit(element, editorOptions);

    this.codeEditor.setTheme(THEME);
    //this.codeEditor.getSession().setMode(LANG);
    this.codeEditor.setShowFoldWidgets(true); // for the scope fold feature
    this.loadSession(this.service.selectedEditorTab);
  }

  // missing propery on EditorOptions 'enableBasicAutocompletion' so this is a wolkaround still using ts
  private getEditorOptions(): Partial<ace.Ace.EditorOptions> & { enableBasicAutocompletion?: boolean; } {
    const basicEditorOptions: Partial<ace.Ace.EditorOptions> = {
      highlightActiveLine: true,
      //  minLines: 50,
      // maxLines: Infinity,
      autoScrollEditorIntoView: true
    };

    const extraEditorOptions = {
      enableBasicAutocompletion: true
    };
    const margedOptions = Object.assign(basicEditorOptions, extraEditorOptions);
    return margedOptions;
  }

  getTabbedSessions(): void {
    this.tabKeys = Object.keys(this.service.editorContexts);
    console.log("tab keys: "+JSON.stringify(this.tabKeys))
    // this.service.editorContexts.forEach((value: EditorContext, key: string) => {
    //   this.tabKeys.push(key);
    //   console.log("tabbed key: " + key);
    // });
  }

  selectItem(id: string) {
    console.log(id)
    this.loadSession(id);
    this.selectTab(id);
  }

  getTabName(key: string): string {
    return this.service.editorContexts[key].name;
  }

  loadSession(key: string) {
    if (key) {
      this.codeEditor.setSession(this.service.editorContexts[key].session);
    }
  }

  selectTab(key: string) {
    this.service.selectedEditorTab = key;
  }

  onResize(event: any) {
    if(!this.codeEditorElmRef){
      return;
    }
    //console.log("window resized");
    let doc: any = this.codeEditor.getSession().getDocument();
    let r: any = this.codeEditor.renderer;
    let lineHeight = r.lineHeight;
    //console.log("doc.getLength(): "+doc.getLength()+" lineHeight: "+lineHeight)
    this.codeEditorElmRef.nativeElement.style.height = +lineHeight * +doc.getLength() + "px";
    this.codeEditor.resize();
  }

  closeTab(key: string) {
    let index: number = -1;
    for (let i = 0; i < this.tabKeys.length; i++) {
      if (this.tabKeys[i] == key) {
        index = i;
        this.tabKeys.splice(i, 1);
        delete this.service.editorContexts[key];
        break;
      }
    }
    if (index >= this.tabKeys.length && this.tabKeys.length > 0) {
      this.service.selectedEditorTab = this.tabKeys[this.tabKeys.length - 1];
      this.loadSession(this.service.selectedEditorTab);
    }
  }

  save() {
    this.saving = true;
    this.service.setText(this.service.selectedEditorTab).subscribe((resp: any) => {
      console.log("save done");
      this.saving = false;
    },err=>{
      this.saving = false;
      alert("Failed to save file");
    });
  }

  close(){
    this.closeTab(this.service.selectedEditorTab);
  }
}
