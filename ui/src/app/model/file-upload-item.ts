import { Subscription } from "rxjs";

export class FileUploadItem {
    name: string;
    size: number;
    chunkCount: number;
    chunkFinished: number;
    percent: number;
    bytesUploaded: number;
    file: Blob;
    subscription: Subscription;
    relativePath: string;
    folder:string;
    status: string;

    constructor() {
        this.percent = 0;
        this.chunkFinished = 0;
        this.chunkCount = 0;
        this.bytesUploaded = 0;
    }
}