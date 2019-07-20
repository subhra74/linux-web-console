import { Component, OnInit, OnDestroy } from '@angular/core';
import { DataService } from 'src/app/data.service';
import { SearchContext } from 'src/app/model/search-context';
import { EditorContext } from 'src/app/model/editor-context';
import { utility } from '../../utility/utils';
import { Router } from '@angular/router';
import { FileItem } from 'src/app/model/file-item';

@Component({
  selector: 'app-search',
  templateUrl: './search.component.html',
  styleUrls: ['./search.component.css']
})
export class SearchComponent implements OnInit, OnDestroy {
  ctx: SearchContext;
  timer: any;
  loading: boolean;

  constructor(public service: DataService, private router: Router) { }

  ngOnInit() {
    this.ctx = this.service.searchContext;
    if (this.ctx.searching && !this.ctx.isDone) {
      this.timer = setInterval(() => {
        this.getUpdates();
      }, 3000);
    }
  }

  ngOnDestroy() {
    if (this.timer) {
      clearInterval(this.timer);
    }
  }

  cancel() {
    this.service.cancelSearch(this.ctx.id).subscribe((res) => {
      console.log("cancelled success!");
      if (this.timer) {
        clearInterval(this.timer);
        this.timer = null;
        this.ctx.isDone = true;
      }
    });
  }

  search(txt: string, folder: string) {
    console.log("Search: " + txt + " folder: " + folder);
    let ctx = new SearchContext();
    ctx.isDone = false;
    ctx.searchText = txt;
    ctx.files = [];
    ctx.folders = [];
    ctx.folder = folder;
    ctx.searching = true;
    this.ctx = ctx;

    this.service.startSearch(folder, txt).subscribe((result: any) => {
      let id: string = result.id;
      console.log("id of search: " + id);
      this.ctx.id = id;
      this.service.searchContext = ctx;
      this.timer = setInterval(() => {
        this.getUpdates();
      }, 3000);
    });
  }

  getUpdates() {
    this.service.getSearchResults(this.ctx.id, this.ctx.folders.length, this.ctx.files.length).subscribe((result: any) => {
      this.ctx.folders = this.ctx.folders.concat(result.folders);
      this.ctx.files = this.ctx.files.concat(result.files);
      this.ctx.isDone = result.done;
      if (this.ctx.isDone) {
        clearInterval(this.timer);
        this.timer = null;
      }
      console.log(JSON.stringify(this.ctx));
    });
  }

  openFile(file: FileItem) {
    this.service.fileOpenRequests.next(file);
    this.service.currentViewChanger.next(null);
    // this.loading = true;
    // console.log("Opening file: " + file)
    // this.service.getText(file).subscribe((text: string) => {
    //   this.service.selectedEditorTab = file;
    //   let ctx = new EditorContext(utility.getFileName(file), file, text);
    //   ctx.session.setUseWrapMode(false);
    //   this.service.editorContexts[file] = ctx;
    //   this.loading = false;
    //   console.log("before route init of editor: " + JSON.stringify(Object.keys(this.service.editorContexts)));
    //   this.router.navigate(["/app/editor"]);
    // });
  }

  // openFolder(folder: string) {
  //   this.service.tabs.push({
  //     files: null,
  //     currentDirectory: folder,
  //     selected: true,
  //     folderName: utility.getFileName(folder),
  //     posix: false
  //   });
  //   this.service.selectedTab = this.service.tabs.length - 1;
  //   this.router.navigate(["/app/files"]);
  // }

  getItemCount() {
    let count = 0;
    if (this.ctx.files) {
      count += this.ctx.files.length;
    }
    if (this.ctx.folders) {
      count += this.ctx.folders.length;
    }
    return "Total " + count + " item(s) found.";
  }

  newSearch() {
    this.ctx.searching = false;
  }

}
