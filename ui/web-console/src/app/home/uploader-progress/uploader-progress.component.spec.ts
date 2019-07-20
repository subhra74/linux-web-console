import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { UploaderProgressComponent } from './uploader-progress.component';

describe('UploaderProgressComponent', () => {
  let component: UploaderProgressComponent;
  let fixture: ComponentFixture<UploaderProgressComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ UploaderProgressComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(UploaderProgressComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
