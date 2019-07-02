import { Component, OnInit, Input, OnChanges, ViewChild, ElementRef, OnDestroy, Output, EventEmitter } from '@angular/core';
import { DataService } from 'src/app/data.service';
import { NavigationTreeNode } from 'src/app/model/navigation-tree-node';
import { Subscriber, Subscription } from 'rxjs';
import { FolderTab } from 'src/app/model/folder-tab';

@Component({
  selector: 'app-tree',
  templateUrl: './tree.component.html',
  styleUrls: ['./tree.component.css']
})
export class TreeComponent implements OnInit, OnChanges, OnDestroy {

  @Input()
  model: NavigationTreeNode;

  @Input()
  icon: string;

  @Input()
  parent: NavigationTreeNode;

  showCtx: boolean = false;

  menuEventSubsciption: Subscription;

  constructor(public service: DataService) { }

  ngOnInit() {
  }

  ngOnChanges() {
    if (this.model && this.model.expanded) {
      this.loadChildren();
    }
  }

  ngOnDestroy() {
    if (this.menuEventSubsciption) {
      this.menuEventSubsciption.unsubscribe();
    }
  }

  toggleState() {
    if (!this.model) {
      return;
    }
    if (this.model.leafNode) {
      return;
    }
    console.log("Togging state")
    this.model.expanded = !this.model.expanded;
    if (this.model.expanded) {
      this.loadChildren();
    }
  }

  loadChildren() {
    if (!this.model.children) {
      if (!this.parent && this.model.name == "Home") {
        console.log("loading children for: " + this.model.name);
        this.service.getHomeChildren().subscribe((data: NavigationTreeNode[]) => {
          //console.log("loading children for: " + JSON.stringify(data));
          this.model.children = data;
        });
      }
      else if (this.parent) {
        console.log("loading children for: " + this.model.name);
        this.service.getDirectoryChildren(this.model.path).subscribe((data: NavigationTreeNode[]) => {
          //console.log("loading children for: " + JSON.stringify(data));
          this.model.children = data;
        });
      }
      else if (!this.parent && this.model.name == "File system") {
        console.log("loading children for: " + this.model.name);
        this.service.getFsChildren().subscribe((data: NavigationTreeNode[]) => {
          // console.log("loading children for: " + JSON.stringify(data));
          this.model.children = data;
        });
      }
    }
  }

  onClick() {
    console.log("tree clicked: " + this.model.path);
    this.openInTab();
  }

  onContextMenu(a: any, c: any) {
    if (this.model.leafNode) {
      a.preventDefault();
      return;
    }
    this.showCtx = true;
    c.style.display = 'flex';

    console.log("Ww: " + window.innerWidth + " Wh: " + window.innerHeight + " height: " + c.offsetHeight);

    if (a.pageX > window.innerWidth / 2) {
      c.style.left = (a.pageX - c.offsetWidth) + "px";
    } else {
      c.style.left = a.pageX + "px";
    }

    if (a.pageY > window.innerHeight / 2) {
      c.style.top = (a.pageY - c.offsetHeight) + "px";
    } else {
      c.style.top = a.pageY + "px";
    }

    this.service.sharedMenuListener.next();
    this.menuEventSubsciption = this.service.sharedMenuListener.subscribe((event) => {
      this.hideContextMenu();
      this.menuEventSubsciption.unsubscribe();
      this.menuEventSubsciption = null;
    })
    console.log("context menu on tree clicked: " + this.model.path);
    a.preventDefault();
  }

  hideContextMenu() {
    this.showCtx = false;
    console.log("Hiding context menu")
  }

  openInNewTab() {
    console.log("openInNewTab " + this.model.path);
    let tab: FolderTab = new FolderTab();
    tab.currentDirectory = this.model.path;
    tab.folderName = this.model.name;
    tab.files = null;
    this.service.newTabListener.next(tab);
  }

  openInTab() {
    console.log("openInTab " + this.model.path);
    this.service.currentTabListener.next({ path: this.model.path, file: this.model.leafNode });
  }

  copyPath() {
    console.log("copyPath " + this.model.path);
  }

}
