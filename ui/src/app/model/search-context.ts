export class SearchContext {
    id: string;
    searchText: string;
    files: string[]=[];
    folders: string[]=[];
    searching: boolean;
    isDone: boolean;
    folder: string;
}