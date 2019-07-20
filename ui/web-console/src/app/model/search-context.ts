import { FileItem } from './file-item';

export class SearchContext {
    id: string;
    searchText: string;
    files: FileItem[]=[];
    folders: FileItem[]=[];
    searching: boolean;
    isDone: boolean;
    folder: string;
}