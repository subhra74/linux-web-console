import { Component, OnInit, OnDestroy } from '@angular/core';
import { ChartType, ChartOptions } from 'chart.js';
import { MultiDataSet, Label, Colors, Color } from 'ng2-charts';
import { DataService } from 'src/app/data.service';
import { utility } from '../../utility/utils';

@Component({
  selector: 'app-monitoring',
  templateUrl: './monitoring.component.html',
  styleUrls: ['./monitoring.component.css'],
  host: {
    '(window:resize)': 'onResize($event)'
  }
})
export class MonitoringComponent implements OnInit, OnDestroy {

  public doughnutChartLabels: Label[] = ['Used', 'Free'];
  public colors: Color[] = [
    {
      backgroundColor: ['Orange', 'SteelBlue'],
      borderColor: ['Orange', 'SteelBlue']
    }
  ];
  public cpuUsageSet: MultiDataSet = [
    [0, 100]
  ];
  public memoryUsageSet: MultiDataSet = [
    [0, 100]
  ];
  public diskUsageSet: MultiDataSet = [
    [0, 100]
  ];
  public swapUsageSet: MultiDataSet = [
    [0, 100]
  ];

  cpu: number;
  memory: number;
  swap: number;
  disk: number;

  public doughnutChartType: ChartType = 'doughnut';
  public options: ChartOptions = {
    maintainAspectRatio: false,
    legend: {
      display: false
    }
  };

  timer: any;

  processList: any[] = [];
  filteredProcessList: any[] = [];
  searchText: string;
  sortingField: number = -1;
  sortAsc: boolean;
  loading: boolean;
  message: string;
  error: boolean;

  constructor(private service: DataService) { }

  ngOnInit() {
    this.getStats();
    this.getProcStats();
    this.timer = setInterval(() => {
      this.getStats();
    }, 5000);
  }

  ngOnDestroy() {
    if (this.timer) {
      clearInterval(this.timer);
    }
  }

  public getProcStats() {
    this.loading = true;
    this.service.getProcessList().subscribe((resp: any[]) => {
      this.processList = resp;
      for (let proc of this.processList) {
        proc.selected = false;
      }
      this.filterProcesses();
      this.loading = false;
    }, err => {
      this.loading = false;
      this.message = "Unable to get process details";
      this.error = true;
    });
  }

  public killSelectedProcesses() {
    let pids: number[] = [];
    for (let proc of this.processList) {
      if (proc.selected) {
        pids.push(proc.pid);
      }
    }
    if (pids.length < 1) {
      alert("Nothing selected to kill");
      return;
    }

    this.loading = true;
    this.service.killProcesses(pids).subscribe((resp: any) => {
      if (!resp.success) {
        //alert("Failed to kill");
        this.message = "Failed to kill one or more selected processes";
        this.error = true;
        this.loading = false;
      } else {
        this.message = "Selected processes are killed successfully";
        this.error = false;
        this.getProcStats();
      }
    }, err => {
      this.message = "Failed to kill one or more selected processes";
      this.error = true;
      this.loading = false;
    })
  }

  setSelected(item: any, selection: boolean) {
    item.selected = selection;
  }

  public getStats() {
    this.service.getSystemStats().subscribe((resp: any) => {
      this.cpu = resp.cpuUsed;
      this.memory = resp.memoryUsed;
      this.disk = resp.diskUsed;
      this.swap = resp.swapUsed;
      this.cpuUsageSet = [
        [resp.cpuUsed, resp.cpuFree]
      ];
      this.memoryUsageSet = [
        [resp.memoryUsed, resp.memoryFree]
      ];
      this.diskUsageSet = [
        [resp.diskUsed, resp.diskFree]
      ];
      this.swapUsageSet = [
        [resp.swapUsed, resp.swapFree]
      ];
    });
  }

  public chartClicked({ event, active }: { event: MouseEvent, active: {}[] }): void {
    console.log(event, active);
  }

  public chartHovered({ event, active }: { event: MouseEvent, active: {}[] }): void {
    console.log(event, active);
  }

  onResize(event: any) {
  }

  numberCompare(n1: number, n2: number) {
    if (this.sortAsc) {
      if (n1 > n2) {
        return 1;
      } else if (n1 < n2) {
        return -1;
      } else {
        return 0;
      }
    } else {
      if (n1 < n2) {
        return 1;
      } else if (n1 > n2) {
        return -1;
      } else {
        return 0;
      }
    }
  }

  filterProcesses() {
    if (this.searchText) {
      this.filteredProcessList = [];
      for (let item of this.processList) {
        if (item.name.includes(this.searchText) ||
          item.command.includes(this.searchText) ||
          item.user.includes(this.searchText) ||
          item.state.includes(this.searchText) ||
          item.pid == this.searchText) {
          this.filteredProcessList.push(item);
        }
      }
    } else {
      this.filteredProcessList = [...this.processList];
    }
    if (this.sortingField != -1) {
      this.filteredProcessList.sort((a: any, b: any) => {
        switch (this.sortingField) {
          case 0:
            return this.sortAsc ? a.name.localeCompare(b.name) : b.name.localeCompare(a.name);
          case 1:
            return this.numberCompare(a.pid, b.pid);
          case 2:
            return this.numberCompare(a.priority, b.priority);
          case 3:
            return this.numberCompare(a.cpuUsage, b.cpuUsage);
          case 4:
            return this.numberCompare(a.memoryUsage, b.memoryUsage);
          case 5:
            return this.numberCompare(a.vmUsage, b.vmUsage);
          case 6:
            return this.sortAsc ? a.user.localeCompare(b.user) : b.user.localeCompare(a.user);
          case 7:
            return this.numberCompare(a.startTime, b.startTime);
          case 8:
            return this.sortAsc ? a.state.localeCompare(b.state) : b.state.localeCompare(a.state);
          case 9:
            return this.sortAsc ? a.command.localeCompare(b.command) : b.command.localeCompare(a.command);
        }
      });
    }
  }

  toPercent(p: number): string {
    if (p) {
      return p.toFixed(1);
    }
    return "";
  }

  setSortField(index: number) {
    this.sortingField = index;
    this.sortAsc = !this.sortAsc;
    this.filterProcesses();
  }

  formatSize(n: number): string {
    return utility.formatSize(n);
  }

  toDate(n: number): Date {
    return new Date(n);
  }
}
