import { BrowserModule } from '@angular/platform-browser';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations'
import { NgModule } from '@angular/core';

import { HttpClientModule } from '@angular/common/http';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { ChartsModule } from 'ng2-charts';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { HomeComponent } from './home/home.component';
import { FilesComponent } from './home/files/files.component';
import { SearchComponent } from './home/search/search.component';
import { MonitoringComponent } from './home/monitoring/monitoring.component';
import { BrowserComponent } from './home/files/browser/browser.component';
import { UploaderProgressComponent } from './home/uploader-progress/uploader-progress.component';
import { TerminalComponent } from './home/terminal/terminal.component';
import { TreeComponent } from './home/files/tree/tree.component';
import { EditorComponent } from './home/editor/editor.component';
import { RenameComponent } from './home/files/browser/rename/rename.component';
import { InfoComponent } from './home/files/browser/info/info.component';
import { LoginComponent } from './login/login.component';
import { httpInterceptorProviders } from './intercepters/auth';
import { ImageViewerComponent } from './home/files/viewer/image-viewer/image-viewer.component';
import { MediaPlayerComponent } from './home/files/viewer/media-player/media-player.component';
import { SettingsComponent } from './home/settings/settings.component';
import { UnsupportedContentViewerComponent } from './home/files/viewer/unsupported-content-viewer/unsupported-content-viewer.component';
import { NewItemComponent } from './home/files/browser/new-item/new-item.component';

@NgModule({
  declarations: [
    AppComponent,
    HomeComponent,
    FilesComponent,
    SearchComponent,
    MonitoringComponent,
    TerminalComponent,
    BrowserComponent,
    UploaderProgressComponent,
    TreeComponent,
    EditorComponent,
    RenameComponent,
    InfoComponent,
    LoginComponent,
    ImageViewerComponent,
    MediaPlayerComponent,
    SettingsComponent,
    UnsupportedContentViewerComponent,
    NewItemComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    FormsModule,
    ReactiveFormsModule,
    NgbModule,
    HttpClientModule,
    BrowserAnimationsModule, 
    ChartsModule
  ],
  providers: [httpInterceptorProviders],
  bootstrap: [AppComponent]
})
export class AppModule { }
