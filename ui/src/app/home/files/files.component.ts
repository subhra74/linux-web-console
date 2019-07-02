import { Component, OnInit, ViewChild, ElementRef, OnDestroy, Output, EventEmitter } from '@angular/core';
import { TabItem } from 'src/app/model/tab-item';
import { FileItem } from 'src/app/model/file-item';
import { DataService } from 'src/app/data.service';
import { Subject, Subscriber, Subscription } from 'rxjs';
import { TreeComponent } from './tree/tree.component';
import { FolderTab } from 'src/app/model/folder-tab';
import { FormControl } from '@angular/forms';

import { utility } from '../../utility/utils';


import * as ace from 'ace-builds';
import { Router, ActivatedRoute, ParamMap } from '@angular/router';
import { EditorContext } from 'src/app/model/editor-context';

@Component({
  selector: 'app-files',
  templateUrl: './files.component.html',
  styleUrls: ['./files.component.css'],
  host: {
    '(window:resize)': 'onResize($event)'
  }
})
export class FilesComponent implements OnInit, OnDestroy {

  toastVisible: boolean = false;
  toastMessage: string;

  @ViewChild("list")
  list: ElementRef;

  @ViewChild("header")
  header: ElementRef;

  @Output()
  viewChanged = new EventEmitter<string>();

  uploadPopup: boolean;
  hoverIndex: number = -1;
  showRenameDialog: boolean = false;
  loading: boolean = false;
  newTabListener: Subscription;
  currentTabListener: Subscription;
  showInfoDialog: boolean;
  multipleSelectionMode: boolean = false;
  showDropdownCtx: boolean = false;
  previewer: string;
  sortColumn: number;
  ascendingSort: boolean;
  showNewFolderItem: boolean;
  showNewFileItem: boolean;
  posix: boolean;

  constructor(public service: DataService, private router: Router) { }

  ngOnInit() {
    console.log("File browser init")
    this.posix = this.service.posix;
    if (this.service.tabs.length < 1) {
      this.loading = true;
      this.service.listHome().subscribe((data: any) => {
        //console.log("files: " + JSON.stringify(data));
        this.service.tabs.push({
          files: data.files,
          currentDirectory: data.folder,
          selected: false,
          folderName: data.folderName,
          posix: data.posix
        });
        this.service.selectedTab = 0;
        this.loading = false;
      }, err => {
        this.showError("Unable to list files");
        this.loading = false;
      });
    } else {
      this.navigateTo(this.service.tabs[this.service.selectedTab].currentDirectory);
    }

    this.newTabListener = this.service.newTabListener.subscribe((tab: FolderTab) => {
      this.openNewTab(tab);
    });

    this.currentTabListener = this.service.currentTabListener.subscribe((path: any) => {
      if (path.file) {
        this.openItem(path);
      } else {
        this.navigateTo(path.path);
      }

    })
  }

  ngOnDestroy() {
    if (this.newTabListener) {
      this.newTabListener.unsubscribe();
    }

    if (this.currentTabListener) {
      this.currentTabListener.unsubscribe();
    }
  }

  openItem(fileItem: FileItem) {
    let file: string = fileItem.path;
    this.loading = true;
    console.log("Opening file: " + file)
    if (fileItem.type.startsWith("image")) {
      this.previewer = "image";
      this.loading = false;
      return;
    }
    if (fileItem.type.startsWith("video")) {
      this.previewer = "video";
      this.loading = false;
      return;
    }
    if (!fileItem.type.startsWith("text")) {
      this.loading = false;
      this.previewer = "unsupported";
      console.log("Previewer: " + this.previewer);
      return;
    }
    this.openWithTextEditor(file);
  }

  openWithTextEditor(file: string) {
    this.service.getText(file).subscribe((text: string) => {
      this.service.selectedEditorTab = file;
      let ctx = new EditorContext(utility.getFileName(file), file, text);
      ctx.session.setUseWrapMode(false);
      this.service.editorContexts[file] = ctx;
      this.loading = false;
      console.log("before route init of editor: " + JSON.stringify(Object.keys(this.service.editorContexts)))
      this.viewChanged.emit("editor");
      //this.router.navigate(["/app/editor"]);
    }, err => {
      this.showError("Unable to open file");
      this.loading = false;
    });
  }

  openAsText() {
    let file: string;
    for (let i = 0; i < this.service.tabs[this.service.selectedTab].files.length; i++) {
      if (this.service.tabs[this.service.selectedTab].files[i].selected) {
        file = this.service.tabs[this.service.selectedTab].files[i].path;
        this.openWithTextEditor(file);
        break;
      }
    }
  }

  navigateTo(file: string) {
    this.loading = true;
    console.log("Navigating to: " + file);
    this.service.listFiles(file).subscribe((data: any) => {
      //console.log("files: " + JSON.stringify(data));
      this.service.tabs[this.service.selectedTab] = {
        files: data.files,
        currentDirectory: data.folder,
        selected: false,
        folderName: data.folderName,
        posix: data.posix
      };
      this.loading = false;
    }, err => {
      this.showError("Unable to list files");
      this.loading = false;
    });
  }

  navigateHome() {
    this.loading = true;
    this.service.listHome().subscribe((data: any) => {
      this.service.tabs[this.service.selectedTab] = {
        files: data.files,
        currentDirectory: data.folder,
        selected: false,
        folderName: data.folderName,
        posix: data.posix
      };
      this.loading = false;
    }, err => {
      this.showError("Unable to list files");
      this.loading = false;
    });
  }

  navigateUp(path: string) {
    this.loading = true;
    this.service.goUp(this.service.tabs[this.service.selectedTab].currentDirectory).subscribe((data: any) => {
      this.service.tabs[this.service.selectedTab] = {
        files: data.files,
        currentDirectory: data.folder,
        selected: false,
        folderName: data.folderName,
        posix: data.posix
      };
      this.loading = false;
    }, err => {
      this.showError("Unable to list files");
      this.loading = false;
    });
  }

  downloadFiles() {
    let files: string[] = [];
    for (let i = 0; i < this.service.tabs[this.service.selectedTab].files.length; i++) {
      if (this.service.tabs[this.service.selectedTab].files[i].selected) {
        files.push(this.service.tabs[this.service.selectedTab].files[i].name);
      }
    }
    if (files.length < 1) {
      return;
    }
    console.log("To download - files: " + files.join("/"));
    this.loading = true;
    this.service.getTempToken().subscribe((data: any) => {
      let tempToken = data.token;
      this.loading = false;
      this.service.downloadFiles(this.service.tabs[this.service.selectedTab].currentDirectory, files.join("/"), tempToken);
    }, err => {
      console.log(err);
      this.showError("Unable to download files");
      this.loading = false;
    });

  }

  selectAll(val: boolean) {
    console.log("Selected value: " + val)
    if (this.service.tabs[this.service.selectedTab].files) {
      for (let i = 0; i < this.service.tabs[this.service.selectedTab].files.length; i++) {
        this.service.tabs[this.service.selectedTab].files[i].selected = val;
      }
    }
  }

  fileSelectedForUpload(event: any) {
    console.log("Uploading files: " + JSON.stringify(event.target.files));
    if (event.target.files && event.target.files.length) {
      this.service.uploadItem(event.target.files[0], this.service.tabs[this.service.selectedTab].currentDirectory, event.target.files[0].name);
    }
  }

  folderSelectedForUpload(event: any) {
    console.log(JSON.stringify(event.target.files));
    if (event.target.files && event.target.files.length) {
      let fileNames: string[] = [];
      let name: string = event.target.files[0].webkitRelativePath.split("/")[0];

      for (let i = 0; i < event.target.files.length; i++) {
        let f: any = event.target.files[i];
        console.log(f.webkitRelativePath);
        fileNames.push(f.webkitRelativePath);
      }
      this.service.uploadFolder(fileNames, event.target.files, this.service.tabs[this.service.selectedTab].currentDirectory, name);
    }
  }

  itemClicked(file: FileItem, ctrl: boolean, cls: string) {
    console.log("width: " + window.innerWidth);
    if (window.innerWidth < 800) {
      if (this.multipleSelectionMode) {
        file.selected = !file.selected;
        return;
      }

      if (file.type === 'Directory') {
        this.navigateTo(file.path);
      }
      else {
        this.openItem(file);
      }

      return;
    }

    if (ctrl) {
      file.selected = !file.selected;
    }
    else {
      for (let fileItem of this.service.tabs[this.service.selectedTab].files) {
        fileItem.selected = false;
      }
      file.selected = true;
    }
  }

  onKeyDown(event: any) {
    console.log("onKeyDown: " + event.ctrlKey + " " + event.key)
    if (event.ctrlKey && event.key == "a") {
      for (let fileItem of this.service.tabs[this.service.selectedTab].files) {
        fileItem.selected = true;
        event.preventDefault();
      }
    }
  }

  preventTextSelection(event: any) {
    if (event.detail > 1) {
      event.preventDefault();
      // of course, you still do not know what you prevent here...
      // You could also check event.ctrlKey/event.shiftKey/event.altKey
      // to not prevent something useful.
    }
  }

  showTab(i: number) {
    console.log("loading folder: " + this.service.tabs[i].currentDirectory);
    this.service.selectedTab = i;
    if (!this.service.tabs[i].files) {
      this.navigateTo(this.service.tabs[i].currentDirectory);
    }
  }

  openNewTab(tab: FolderTab) {
    console.log("files: opening new tab");
    let n = this.service.tabs.length;
    this.service.tabs.push(tab);
    console.log("showing new tab at index: " + n);
    this.showTab(n);
  }

  onContextMenu(a, b, c, d) {
    console.log("Context menu");
    console.log(c);
    this.onWindowClicked(c);
    if (!d.selected) {
      for (let fileItem of this.service.tabs[this.service.selectedTab].files) {
        fileItem.selected = false;
      }
      d.selected = true;
    }

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

    // c.style.left = a.pageX + "px";
    // c.style.top = a.pageY + "px";

    //  c.style.left = (a.pageX - c.offsetWidth) + "px";
    //  c.style.top = (a.pageY - c.offsetHeight) + "px";

    a.preventDefault();
  }

  showContextMenu(a, b, c, d, e) {
    console.log("Context menu");
    console.log(c);
    if (!d.selected) {
      for (let fileItem of this.service.tabs[this.service.selectedTab].files) {
        fileItem.selected = false;
      }
      d.selected = true;
    }



    c.style.display = 'flex';

    c.style.left = "400px";
    c.style.top = (a.offsetTop + a.offsetHeight) + "px";

    console.log("Top: " + a.offsetTop + " " + (a.offsetTop + a.offsetHeight));

    e.preventDefault();
  }

  onResize(event: any) {
    let content: HTMLElement = this.list.nativeElement as HTMLElement;
    let h: HTMLElement = this.header.nativeElement as HTMLElement;
    console.log("window resized " + content.clientWidth);
    h.style.width = content.clientWidth + "px";
    console.log("Header width: " + h.style.width);
  }

  onWindowClicked(c) {
    console.log("window clicked");
    c.style.display = 'none';
    this.uploadPopup = false;
    this.showDropdownCtx = false;
    this.service.sharedMenuListener.next();
  }

  copyFiles(cut: boolean) {
    let list: string[] = [];
    for (let fileItem of this.service.tabs[this.service.selectedTab].files) {
      if (fileItem.selected) {
        list.push(fileItem.path);
      }
    }
    this.service.copiedFilePath.files = list;
    this.service.copiedFilePath.cut = cut;
  }

  pasteFiles() {
    console.log("Files to paste: " + JSON.stringify(this.service.copiedFilePath.files));
    this.service.initMoveOrCopy(this.service.tabs[this.service.selectedTab].currentDirectory, this.service.copiedFilePath.files, this.service.copiedFilePath.cut);
  }

  closeTab(i: number) {
    this.service.tabs.splice(i, 1);
    if (this.service.selectedTab >= this.service.tabs.length) {
      this.showTab(this.service.tabs.length - 1);
    } else {
      this.showTab(this.service.selectedTab);
    }
  }

  openInTab() {
    for (let i = 0; i < this.service.tabs[this.service.selectedTab].files.length; i++) {
      if (this.service.tabs[this.service.selectedTab].files[i].selected) {
        let path = this.service.tabs[this.service.selectedTab].files[i].path;
        this.navigateTo(path);
        break;
      }
    }
  }

  openInNewTab() {
    for (let i = 0; i < this.service.tabs[this.service.selectedTab].files.length; i++) {
      if (this.service.tabs[this.service.selectedTab].files[i].selected) {
        let path = this.service.tabs[this.service.selectedTab].files[i].path;
        let name = this.service.tabs[this.service.selectedTab].files[i].name;
        let tab: FolderTab = new FolderTab();
        tab.currentDirectory = path;
        tab.folderName = name;
        tab.files = null;
        this.service.newTabListener.next(tab);
        break;
      }
    }
  }

  isSingleSelection(): boolean {
    if (this.service.tabs && this.service.tabs[this.service.selectedTab] && this.service.tabs[this.service.selectedTab].files) {
      let c: number = 0;
      for (let i = 0; i < this.service.tabs[this.service.selectedTab].files.length; i++) {
        if (this.service.tabs[this.service.selectedTab].files[i].selected) {
          if (c > 0) {
            return false;
          }
          c++;
        }
      }
      if (c == 1) {
        return true;
      }
    }
    return false;
  }

  isAnySelection(): boolean {
    if (this.service.tabs && this.service.tabs[this.service.selectedTab] && this.service.tabs[this.service.selectedTab].files) {
      let c: number = 0;
      for (let i = 0; i < this.service.tabs[this.service.selectedTab].files.length; i++) {
        if (this.service.tabs[this.service.selectedTab].files[i].selected) {
          return true;
        }
      }
    }
    return false;
  }

  renameFile(newName: string) {
    if (newName && newName.length > 0) {
      for (let i = 0; i < this.service.tabs[this.service.selectedTab].files.length; i++) {
        if (this.service.tabs[this.service.selectedTab].files[i].selected) {
          let name = this.service.tabs[this.service.selectedTab].files[i].name;
          this.loading = true;
          console.log("Renaming: new-" + newName)
          this.loading = true;
          this.service.renameFile(newName, name, this.service.tabs[this.service.selectedTab].currentDirectory).subscribe((rest: any) => {
            this.loading = false;
            this.navigateTo(this.service.tabs[this.service.selectedTab].currentDirectory);
          }, err => {
            this.showError("Unable to rename files");
            this.loading = false;
          })
          break;
        }
      }
    }
  }

  deleteFiles() {
    let items: string[] = [];
    for (let i = 0; i < this.service.tabs[this.service.selectedTab].files.length; i++) {
      if (this.service.tabs[this.service.selectedTab].files[i].selected) {
        items.push(this.service.tabs[this.service.selectedTab].files[i].path);
      }
    }
    this.loading = true;
    if(!confirm("Are you sure, you want to delete?")){
      this.loading = false;
      return;
    }
    this.service.deleteFiles(items).subscribe((rest: any) => {
      this.loading = false;
      this.navigateTo(this.service.tabs[this.service.selectedTab].currentDirectory);
    }, err => {
      this.showError("Unable to delete files");
      this.loading = false;
    })
  }

  showInfo() {
    console.log("show info")
    this.showInfoDialog = true;
  }

  formatFileSize(size: number) {
    return utility.formatSize(size);
  }

  getSelectedFile(): FileItem {
    for (let i = 0; i < this.service.tabs[this.service.selectedTab].files.length; i++) {
      if (this.service.tabs[this.service.selectedTab].files[i].selected) {
        return this.service.tabs[this.service.selectedTab].files[i];
      }
    }
    return null;
  }

  getSelectedIndex(): number {
    for (let i = 0; i < this.service.tabs[this.service.selectedTab].files.length; i++) {
      if (this.service.tabs[this.service.selectedTab].files[i].selected) {
        return i;
      }
    }
    return -1;
  }

  keyup(e: KeyboardEvent) {
    if (e.keyCode == 40 || e.keyCode == 38) {
      let index = this.getSelectedIndex();
      if (index < 0) return;
      let container = this.list;
      let children = container.nativeElement.children;
      if (this.service.tabs[this.service.selectedTab].files.length - 1 == index || (e.keyCode == 38 && index == 0)) {
        return;
      }
      for (let i = 0; i < this.service.tabs[this.service.selectedTab].files.length; i++) {
        this.service.tabs[this.service.selectedTab].files[i].selected = false;
      }
      this.service.tabs[this.service.selectedTab].files[e.keyCode == 40 ? index + 1 : index - 1].selected = true;
      let item: HTMLElement = <HTMLElement>children.item(e.keyCode == 40 ? index + 1 : index - 1);
      item.focus();
    }
  }

  sort(col) {
    this.sortColumn = col;
    // if (this.ascendingSort == undefined) {
    //   this.ascendingSort = true;
    // } else {
    //   this.ascendingSort = !this.ascendingSort;
    // }

    this.ascendingSort = !this.ascendingSort;

    console.log("this.sortColumn: " + this.sortColumn + " this.ascendingSort: " + this.ascendingSort);
    switch (this.sortColumn) {
      case 0: {
        this.service.tabs[this.service.selectedTab].files.sort((a: FileItem, b: FileItem) => {
          if (a.type === "Directory" && b.type === "Directory") {
            return this.ascendingSort ? a.name.localeCompare(b.name) : b.name.localeCompare(a.name);
          } else if (a.type === "Directory") {
            return this.ascendingSort ? 1 : -1;
          } else if (b.type === "Directory") {
            return this.ascendingSort ? -1 : 1;
          } else {
            return this.ascendingSort ? a.name.localeCompare(b.name) : b.name.localeCompare(a.name);
          }
        });
        break;
      }
      case 1: {
        this.service.tabs[this.service.selectedTab].files.sort((a: FileItem, b: FileItem) => {
          if (a.type === "Directory" && b.type === "Directory") {
            return 0;
          } else if (a.type === "Directory") {
            return this.ascendingSort ? 1 : -1;
          } else if (b.type === "Directory") {
            return this.ascendingSort ? -1 : 1;
          } else {
            return this.ascendingSort ? a.size - b.size : b.size - a.size;
          }
        });
        break;
      }
      case 2: {
        this.service.tabs[this.service.selectedTab].files.sort((a: FileItem, b: FileItem) => {
          if (a.type === "Directory" && b.type === "Directory") {
            if (this.ascendingSort) {
              return a.lastModified > b.lastModified ? 1 : -1;
            } else {
              return a.lastModified > b.lastModified ? -1 : 1;
            }
          } else if (a.type === "Directory") {
            return this.ascendingSort ? 1 : -1;
          } else if (b.type === "Directory") {
            return this.ascendingSort ? -1 : 1;
          } else {
            if (this.ascendingSort) {
              return a.lastModified > b.lastModified ? 1 : -1;
            } else {
              return a.lastModified > b.lastModified ? -1 : 1;
            }
          }
        });
        break;
      }
      case 3: {
        this.service.tabs[this.service.selectedTab].files.sort((a: FileItem, b: FileItem) => {
          if (a.type === "Directory" && b.type === "Directory") {
            if (this.ascendingSort) {
              return a.permissionString.localeCompare(b.permissionString);
            } else {
              return b.permissionString.localeCompare(a.permissionString);
            }
          } else if (a.type === "Directory") {
            return this.ascendingSort ? 1 : -1;
          } else if (b.type === "Directory") {
            return this.ascendingSort ? -1 : 1;
          } else {
            if (this.ascendingSort) {
              return a.permissionString.localeCompare(b.permissionString);
            } else {
              return b.permissionString.localeCompare(a.permissionString);
            }
          }
        });
        break;
      }
      case 4: {
        this.service.tabs[this.service.selectedTab].files.sort((a: FileItem, b: FileItem) => {
          if (a.type === "Directory" && b.type === "Directory") {
            if (this.ascendingSort) {
              return a.user.localeCompare(b.user);
            } else {
              return b.user.localeCompare(a.user);
            }
          } else if (a.type === "Directory") {
            return this.ascendingSort ? 1 : -1;
          } else if (b.type === "Directory") {
            return this.ascendingSort ? -1 : 1;
          } else {
            if (this.ascendingSort) {
              return a.user.localeCompare(b.user);
            } else {
              return b.user.localeCompare(a.user);
            }
          }
        });
        break;
      }
    }
  }

  showDropDownMenu(contextmenu: any, $event: any) {
    this.onWindowClicked(contextmenu);
    this.showDropdownCtx = !this.showDropdownCtx;
    $event.stopPropagation();
  }

  createFolder($event: any) {
    console.log($event);
    this.loading = true;
    this.service.mkdir(this.service.tabs[this.service.selectedTab].currentDirectory, $event).subscribe((resp: any) => {
      console.log("success");
      this.navigateTo(this.service.tabs[this.service.selectedTab].currentDirectory);
    }, err => {
      this.showError("Unable to create folder");
      this.loading = false;
    })
  }

  createFile($event: any) {
    console.log($event);
    this.loading = true;
    this.service.touch(this.service.tabs[this.service.selectedTab].currentDirectory, $event).subscribe((resp: any) => {
      console.log("success");
      this.navigateTo(this.service.tabs[this.service.selectedTab].currentDirectory);
    }, err => {
      this.showError("Unable to create file");
      this.loading = false;
    })
  }

  newFolder() {
    this.showNewFolderItem = true;
  }

  newFile() {
    this.showNewFileItem = true;
  }

  showError(msg: string) {
    this.toastMessage = msg;
    this.toastVisible = true;
  }

  getFileIcon(file: FileItem): string {
    if ((file.type == 'Directory' || file.type == 'DirLink')) {
      return 'fa fa-folder';
    }
    else if (file.type.startsWith("image")) {
      return 'fa fa-file-image-o';
    }
    else if (file.type.startsWith("video")) {
      return 'fa fa-file-movie-o';
    }
    else if (file.type.startsWith("audio")) {
      return 'fa fa-file-audio-o';
    }
    else if (file.type.startsWith("text/")) {
      return 'fa fa-file-text-o';
    }
    else if (file.type.includes("application/zip") ||
      file.type.includes("application/x-freearc") ||
      file.type.includes("application/x-bzip") ||
      file.type.includes("application/java-archive") ||
      file.type.includes("application/x-rar-compressed") ||
      file.type.includes("application/x-tar") ||
      file.type.includes("application/x-7z-compressed") ||
      file.type.includes("application/x-gzip") ||
      file.type.includes("application/gzip") ||
      file.type.includes("application/vnd.android.package-archive") ||
      file.type.includes("application/x-bcpio") ||
      file.type.includes("application/x-cpio") ||
      file.type.includes("application/x-stuffit") ||
      file.type.includes("application/x-xz") ||
      file.type.includes("application/x-debian-package") ||
      file.type.includes("application/x-redhat-package-manager")) {
      return 'fa fa-file-archive-o';
    }
    else if (file.type.startsWith("application/pdf")) {
      return 'fa fa-file-pdf-o';
    }
    else if (file.type.startsWith("application/javascript") ||
      file.type.includes("application/json")||
      file.type.includes("application/xml")) {
      return 'fa fa-file-text-o';
    }
    else if (file.type.includes("application/vnd.openxmlformats-officedocument") ||
      file.type.includes("application/msword")) {
      return 'fa fa-file-text-o';
    }
    else {
      return 'fa fa-file-o';
    }
  }

}

