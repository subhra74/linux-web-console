const GB = 1024 * 1000 * 1000;
const MB = 1024 * 1000;
const KB = 1024;
export const utility = {

    getFileName(path: string): string {
        let fragments = path.split(/\\|\//);
        return fragments.pop();
    },

    formatSize(sz: number): string {
        if (sz > GB) {
            return (sz / GB).toFixed(1) + " GB";
        } else if (sz > MB) {
            return (sz / MB).toFixed(1) + " MB";
        } else if (sz > KB) {
            return (sz / KB).toFixed(1) + " KB";
        } else {
            return sz + " B";
        }
    }
}