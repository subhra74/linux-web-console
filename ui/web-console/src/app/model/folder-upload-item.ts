import { Subscription } from "rxjs";

export class FolderUploadItem {
    name: string;
    size: number;
    percent: number;
    bytesUploaded: number;
    files: Blob[];
    relatvePaths: string[];
    status: string;
    needle: number;
    basePath: string;
    subscription: Subscription;

    constructor() {
        this.percent = 0;
        this.bytesUploaded = 0;
    }
}