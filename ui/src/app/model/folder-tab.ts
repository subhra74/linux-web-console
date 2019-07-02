import { FileItem } from './file-item';

export class FolderTab {
    files: FileItem[] = [];
    currentDirectory: string;
    selected: boolean;
    folderName: string;
    posix: boolean;
}