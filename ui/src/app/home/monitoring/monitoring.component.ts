import { Component, OnInit } from '@angular/core';
import { ChartType, ChartOptions } from 'chart.js';
import { MultiDataSet, Label, Colors, Color } from 'ng2-charts';
import { DataService } from 'src/app/data.service';

@Component({
  selector: 'app-monitoring',
  templateUrl: './monitoring.component.html',
  styleUrls: ['./monitoring.component.css'],
  host: {
    '(window:resize)': 'onResize($event)'
  }
})
export class MonitoringComponent implements OnInit {

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
  public doughnutChartType: ChartType = 'doughnut';
  public options: ChartOptions = {
    maintainAspectRatio: false,
    legend: {
      display: false
    }
  };

  timer: any;

  processList: any[] = [];

  constructor(private service: DataService) { }

  ngOnInit() {
    this.getStats();
    this.getProcStats();
    this.timer = setInterval(() => {
      this.getStats();
    }, 5000);
  }

  public getProcStats() {
    this.service.getProcessList().subscribe((resp: any[]) => {
      this.processList = resp;
      for (let proc of this.processList) {
        proc.selected = false;
      }
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

    this.service.killProcesses(pids).subscribe((resp: any) => {
      if (!resp.success) {
        alert("Failed to kill");
      } else {
        this.getProcStats();
      }
    })
  }

  setSelected(item: any, selection: boolean) {
    item.selected = selection;
  }

  public getStats() {
    this.service.getSystemStats().subscribe((resp: any) => {
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

}
