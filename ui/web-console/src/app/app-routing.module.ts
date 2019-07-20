import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { HomeComponent } from './home/home.component';
import { FilesComponent } from './home/files/files.component';
import { SearchComponent } from './home/search/search.component';
import { MonitoringComponent } from './home/monitoring/monitoring.component';
import { EditorComponent } from './home/editor/editor.component';
import { LoginComponent } from './login/login.component';
import { AuthGuardGuard } from './guards/auth-guard.guard';
import { SettingsComponent } from './home/settings/settings.component';

const routes: Routes = [
  {
    path: 'app',
    component: HomeComponent,
    canActivate: [AuthGuardGuard],
    // children: [
    //   {
    //     path: 'files',
    //     component: FilesComponent
    //   },
    //   {
    //     path: 'search',
    //     component: SearchComponent
    //   },
    //   {
    //     path: 'monitoring',
    //     component: MonitoringComponent
    //   },
    //   {
    //     path: 'editor',
    //     component: EditorComponent
    //   },
    //   {
    //     path: 'settings',
    //     component: SettingsComponent
    //   }
    // ]
  },
  {
    path: 'login',
    component: LoginComponent,
  },
  {
    path: '',
    redirectTo: 'app',
    pathMatch: 'full'
  }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
