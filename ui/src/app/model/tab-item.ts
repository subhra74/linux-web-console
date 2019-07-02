import { FileItem } from './file-item';

export class TabItem {
    name: string;
    path: string;
    files: FileItem[];
    selected: boolean;
}