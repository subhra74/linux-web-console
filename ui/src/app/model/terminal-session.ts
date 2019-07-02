import { environment } from "../../environments/environment";

export class TerminalSession {
    socket: WebSocket;
    appId: string;
    bufferText: string;
    displayMode: string;
    lastDisplayMode: string;

    getWS_Url(): string {
        console.log("Return prod ws url")
        return "wss://" + window.location.host + "/";
    }

    public createSocket(id: string, token: string): WebSocket {
        this.appId = id;
        this.socket = new WebSocket((environment.production ? this.getWS_Url() : environment.TERMINAL_URL) + 'term?id=' + id + '&token=' + token);
        this.socket.addEventListener('message', (ev: MessageEvent) => {
            if (!this.bufferText) {
                this.bufferText = ev.data;
            } else {
                this.bufferText += ev.data;
            }
        });
        return this.socket;
    }
}