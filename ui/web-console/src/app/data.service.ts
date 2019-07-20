import { Injectable } from '@angular/core';
import { HttpClient, HttpRequest, HttpEventType } from '@angular/common/http';
import { environment } from '../environments/environment';
import { Observable, Subject } from 'rxjs';
import { FileUploadItem } from './model/file-upload-item';
import { FolderUploadItem } from './model/folder-upload-item';
import { TerminalSession } from './model/terminal-session';
import { FolderTab } from './model/folder-tab';
import { NavigationTreeNode } from './model/navigation-tree-node';
import { EditorContext } from './model/editor-context';
import { FileOperationItem } from './model/file-operation-item';
import { SearchContext } from './model/search-context';
import { PosixPermissions } from './model/posix-permissions';
import { FileItem } from './model/file-item';


const MAX_ACTIVE_UPLOAD = 5;

@Injectable({
  providedIn: 'root'
})
export class DataService {

  jwtToken: string;

  searchContext: SearchContext = new SearchContext();

  editorContexts = {};

  selectedEditorTab: string;

  copiedFilePath = {
    files: [],
    cut: false
  };

  sharedMenuListener = new Subject();
  newTabListener = new Subject<FolderTab>();
  currentTabListener = new Subject<any>();

  tree1: NavigationTreeNode = {
    path: null,
    name: "Bookmarks",
    expanded: false,
    children: null,
    leafNode: false
  };

  tree2: NavigationTreeNode = {
    path: null,
    name: "Home",
    expanded: true,
    children: null,
    leafNode: false
  };

  tree3: NavigationTreeNode = {
    path: null,
    name: "File system",
    expanded: true,
    children: null,
    leafNode: false
  };

  posix: boolean;

  tabs: FolderTab[] = [];
  selectedTab: number;

  uploads: any[] = [];
  activeUploads: number = 0;

  fileOperations: FileOperationItem[] = [];

  fileOpMonitor: any;

  fileOpenRequests = new Subject<FileItem>();

  currentViewChanger = new Subject<string>();

  viewTextRequests = new Subject<string>();

  terminalSession: TerminalSession;

  constructor(private http: HttpClient) {
    this.fileOpMonitor = setInterval(() => {
      if (this.fileOperations && this.fileOperations.length > 0) {
        this.updateFileOpProgress();
      }
    }, 1000);
  }

  public connect(): Observable<any> {
    return this.http.post<any>(environment.BASE_URL + "app/terminal", {});
  }

  public listFiles(path: string): Observable<any> {
    return this.http.get<any>(environment.BASE_URL + "app/folders/" + btoa(path));
  }

  public goUp(path: string): Observable<any> {
    return this.http.get<any>(environment.BASE_URL + "app/folders/" + btoa(path)+"/parent");
  }

  public listHome(): Observable<any> {
    return this.http.get<any>(environment.BASE_URL + "app/folders/home");
  }

  public downloadFiles(folder: string, files: string, tempToken: string) {
    window.location.href = environment.BIN_URL + "download?folder=" + btoa(folder) + "&files=" + btoa(files) + "&token=" + tempToken;
  }

  public uploadNext() {
    if (this.uploads.length > 0 && this.activeUploads < MAX_ACTIVE_UPLOAD) {
      for (let i = 0; i < this.uploads.length; i++) {
        if (!this.uploads[i].status) {
          if (this.uploads[i] instanceof FileUploadItem) {
            this.uploadFile(this.uploads[i]);
          }
          else {
            this.uploadFolderPart(this.uploads[i]);
          }
          break;
        }
      }
    }
  }

  public uploadFile(item: FileUploadItem) {
    item.status = "Inprogress";
    console.log("Starting upload of " + item.name);
    let url = environment.BASE_URL + "app/upload/" + btoa(item.folder) + "/" + btoa(item.relativePath);
    this.activeUploads++;
    let req = new HttpRequest<any>("POST", url, item.file, {
      reportProgress: true
    });
    item.subscription = this.http.request(req).subscribe((event: any) => {
      if (event.type === HttpEventType.UploadProgress) {
        item.bytesUploaded += event.loaded;
        item.percent = Math.floor((item.bytesUploaded * 100) / item.size);
      }
      else if (event.type === HttpEventType.Response) {
        for (let i = 0; i < this.uploads.length; i++) {
          if (this.uploads[i] == item) {
            this.uploads.splice(i, 1);
            break;
          }
        }
        this.activeUploads--;
        this.uploadNext();
      }
    }, (error => {
      item.status = 'Failed';
      console.log("Error");
      this.activeUploads--;
    }));
  }

  public uploadItem(file: File, folder: string, relativePath: string): void {
    console.log("adding for upload: " + file.name)
    let item: FileUploadItem = new FileUploadItem();
    item.relativePath = relativePath;
    item.folder = folder;
    item.file = file;
    item.size = file.size;
    item.name = file.name;
    this.uploads.push(item);
    this.uploadNext();
  }

  public cancelUpload(item: any) {
    console.log("Cancelling: " + item.name);
    item.subscription.unsubscribe();
    for (let i = 0; i < this.uploads.length; i++) {
      if (this.uploads[i] == item) {
        this.uploads.splice(i, 1);
        break;
      }
    }
    this.uploadNext();
  }

  public uploadFolderPart(item: FolderUploadItem) {
    item.status = "Inprogress";
    console.log("Starting upload of " + item.name);
    this.activeUploads++;
    let url = environment.BASE_URL +
      "app/upload/" +
      btoa(item.basePath) + "/" + btoa(item.relatvePaths[item.needle]);

    let req = new HttpRequest<any>("POST", url, item.files[item.needle], {
      reportProgress: true
    });

    item.subscription = this.http.request(req).subscribe((event: any) => {
      if (event.type === HttpEventType.UploadProgress) {
        item.bytesUploaded += event.loaded;
        item.percent = Math.floor((item.bytesUploaded * 100) / item.size);
      }
      else if (event.type === HttpEventType.Response) {
        this.activeUploads--;
        item.needle++;
        if (item.needle < item.files.length) {
          this.uploadFolderPart(item);
        } else {
          for (let i = 0; i < this.uploads.length; i++) {
            if (this.uploads[i] == item) {
              this.uploads.splice(i, 1);
              break;
            }
          }
          this.uploadNext();
        }
      }
    }, (error => {
      item.status = 'Failed';
      console.log("Error");
      this.activeUploads--;
    }));
  }

  public uploadFolder(relatvePaths: string[], files: File[], basePath: string, name: string) {
    let item = new FolderUploadItem();
    item.name = name;
    item.basePath = basePath;
    item.bytesUploaded = 0;
    item.files = files;
    item.needle = 0;
    item.percent = 0;
    item.relatvePaths = relatvePaths;
    item.size = 0;
    for (let i = 0; i < files.length; i++) {
      item.size += files[i].size;
    }
    this.uploads.push(item);
    this.uploadNext();
  }

  public resizePty(appId: string, cols: number, rows: number, wp: number, hp: number) {
    return this.http.post(environment.BASE_URL + "app/terminal/" + appId + "/resize", { "row": rows, "col": cols, "wp": wp, "hp": hp }).subscribe((rest: any) => {
      console.log("Resized successfully");
    });
  }

  public getHomeChildren(): Observable<any> {
    return this.http.get(environment.BASE_URL + "app/folder/tree/home");
  }

  public getDirectoryChildren(path: string): Observable<any> {
    return this.http.get(environment.BASE_URL + "app/folder/tree/path/" + btoa(path));
  }

  public getFsChildren(): Observable<any> {
    return this.http.get(environment.BASE_URL + "app/folder/tree/fs");
  }

  public initMoveOrCopy(targetFolder: string, files: string[], cut: boolean) {
    return this.http.post(environment.BASE_URL + "app/fs/" + cut, { "sourceFile": files, "targetFolder": targetFolder }).subscribe((resp: any) => {
      let fileop: FileOperationItem = new FileOperationItem();
      fileop.id = resp.id;
      fileop.name = resp.name;
      this.fileOperations.push(fileop);
      console.log("Resized successfully");
      if (cut) {
        this.copiedFilePath.files = [];
        this.copiedFilePath.cut = false;
      }
    });
  }

  public updateFileOpProgress() {
    let idList: string[] = [];
    for (let item of this.fileOperations) {
      idList.push(item.id);
    }
    this.http.post(environment.BASE_URL + "app/fs/progress", idList).subscribe((resp: any[]) => {
      console.log("update: " + JSON.stringify(resp));
      for (let item of resp) {
        let fileop: FileOperationItem = this.findFileOpItem(item.id);
        if (fileop) {
          fileop.progress = item.progress;
          fileop.hasError = item.hasError;
          fileop.name = item.name;
          fileop.errors = item.errors;
          if (item.status == "Finished") {
            for (let i = 0; i < this.fileOperations.length; i++) {
              if (this.fileOperations[i] === fileop) {
                this.fileOperations.splice(i, 1);
                break;
              }
            }
          }
        }
      }
    });
  }

  findFileOpItem(id: string) {
    for (let item of this.fileOperations) {
      if (item.id === id) {
        return item;
      }
    }
  }

  cancelFileOperation(id: string) {
    return this.http.post(environment.BASE_URL + "app/fs/cancel/" + id, {}).subscribe((rest: any) => {
      for (let item of this.fileOperations) {
        for (let i = 0; i < this.fileOperations.length; i++) {
          if (this.fileOperations[i].id === id) {
            this.fileOperations.splice(i, 1);
            break;
          }
        }
      }
      console.log("cancellation request sent successfully");
    });
  }

  renameFile(newName: string, oldName: string, folder: string): Observable<any> {
    return this.http.post(environment.BASE_URL + "app/fs/rename", { oldName: oldName, newName: newName, folder: folder });
  }

  deleteFiles(items: string[]): Observable<any> {
    return this.http.post(environment.BASE_URL + "app/fs/delete", items);
  }

  getText(path: string): Observable<string> {
    return this.http.get<string>(environment.BASE_URL + "app/fs/files/" + btoa(path), { responseType: 'text' as 'json' });
  }

  setText(key: string): Observable<any> {
    return this.http.post<any>(environment.BASE_URL + "app/fs/files/" + btoa(key), this.editorContexts[key].session.getValue(), {
      headers: {
        "Content-Type": "text/plain"
      }
    });
  }

  startSearch(folder: string, searchText: string): Observable<any> {
    return this.http.post<any>(environment.BASE_URL + "app/fs/search", { folder, searchText });
  }

  getSearchResults(id: string, folderIndex = 0, fileIndex = 0): Observable<any> {
    return this.http.get(environment.BASE_URL + "app/fs/search/" + id + "?fileIndex=" + fileIndex + "&folderIndex=" + folderIndex);
  }

  cancelSearch(id: string): Observable<any> {
    return this.http.delete(environment.BASE_URL + "app/fs/search/" + id);
  }

  getPermissionDetails(file: string): Observable<PosixPermissions> {
    return this.http.get<PosixPermissions>(environment.BASE_URL + "app/fs/posix/" + btoa(file));
  }

  setPermissionDetails(file: string, perm: PosixPermissions): Observable<any> {
    return this.http.post<any>(environment.BASE_URL + "app/fs/posix/" + btoa(file), perm);
  }

  getJwtToken() {
    if (!this.jwtToken) {
      this.jwtToken = window.localStorage.getItem("easy-web-shell.jwt-token");
    }
    return this.jwtToken;
  }

  signIn(user: string, pass: string): Observable<any> {
    return this.http.post<any>(environment.BASE_URL + "token", {}, { headers: { "Authorization": "Basic " + btoa(user + ":" + pass) } });
  }

  setToken(token: string, remember: boolean) {
    this.jwtToken = token;
    if (remember) {
      window.localStorage.setItem("easy-web-shell.jwt-token", token);
    }
  }

  checkToken(): Observable<any> {
    return this.http.post<any>(environment.BASE_URL + "auth", {});
  }

  clearOldToken() {
    this.jwtToken = null;
    window.localStorage.removeItem("easy-web-shell.jwt-token");
  }

  getConfig() {
    return this.http.get<any>(environment.BASE_URL + "app/config");
  }

  setConfig(config: any) {
    return this.http.post<any>(environment.BASE_URL + "app/config", config);
  }

  mkdir(dir: string, name: string): Observable<any> {
    return this.http.post<any>(environment.BASE_URL + "app/fs/mkdir", { name, dir });
  }

  touch(dir: string, name: string): Observable<any> {
    return this.http.post<any>(environment.BASE_URL + "app/fs/touch", { name, dir });
  }

  getTempToken(): Observable<any> {
    return this.http.get<any>(environment.BASE_URL + "token/temp");
  }

  getSystemStats(): Observable<any> {
    return this.http.get<any>(environment.BASE_URL + "app/sys/stats");
  }

  getProcessList(): Observable<any[]> {
    return this.http.get<any[]>(environment.BASE_URL + "app/sys/procs");
  }

  killProcesses(pids: any[]): Observable<any> {
    return this.http.post<any>(environment.BASE_URL + "app/sys/procs", pids);
  }

}
