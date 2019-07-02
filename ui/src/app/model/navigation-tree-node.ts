export class NavigationTreeNode {
    name: string;
    path: string;
    expanded: boolean;
    leafNode: false;
    children: NavigationTreeNode[];
}