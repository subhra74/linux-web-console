import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { UnsupportedContentViewerComponent } from './unsupported-content-viewer.component';

describe('UnsupportedContentViewerComponent', () => {
  let component: UnsupportedContentViewerComponent;
  let fixture: ComponentFixture<UnsupportedContentViewerComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ UnsupportedContentViewerComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(UnsupportedContentViewerComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
