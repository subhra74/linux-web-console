import * as ace from 'ace-builds';

export class EditorContext {
    session: ace.Ace.EditSession;

    public constructor(public name: string, public key: string, text: string) {
        this.session = new ace.EditSession(text);
    }
}