import { QwcHotReloadElement, html, css} from 'qwc-hot-reload-element';
import { JsonRpc } from 'jsonrpc';
import '@vaadin/icon';
import 'qui-badge';

/**
 * This component adds a custom link on the Extension card
 */
export class QwcExtensionLink extends QwcHotReloadElement {
  
    static styles = css`
        .extensionLink {
            display: flex;
            flex-direction: row;
            justify-content: space-between;
            align-items: center;
            color: var(--lumo-contrast);
            font-size: small;
            padding: 2px 5px;
            cursor: pointer;
            text-decoration: none;
        }
        .extensionLink:hover {
            filter: brightness(80%);
        }
        .icon {
            padding-right: 5px;
        }
        .iconAndName {
            display: flex;
            flex-direction: row;
            justify-content: flex-start;
            align-items: center;
            color: var(--lumo-contrast-80pct);
        }
    `;

    static properties = {
        extensionName: {type: String},
        iconName: {type: String},
        displayName: {type: String},
        staticLabel: {type: String},
        dynamicLabel: {type: String},
        streamingLabel: {type: String},
        path:  {type: String},
        webcomponent: {type: String},
        embed: {type: Boolean},
        externalUrl: {type: String},
        _effectiveLabel: {state: true},
        _observer: {state: false},
    };
  
    _staticLabel = null;
    _dynamicLabel = null;
    _streamingLabel = null;

    set staticLabel(val) {
        if(!this._staticLabel || (this._staticLabel && this._staticLabel != val)){
            let oldVal = this._staticLabel;
            this._staticLabel = val;
            this.requestUpdate('staticLabel', oldVal);
            this.hotReload();
        }
    }
      
    get staticLabel() { 
        return this._staticLabel; 
    }

    set dynamicLabel(val) {
        if(!this._dynamicLabel || (this._dynamicLabel && this._dynamicLabel != val)){
            let oldVal = this._dynamicLabel;
            this._dynamicLabel = val;
            this.requestUpdate('dynamicLabel', oldVal);
            this.hotReload();
        }
    }
      
    get dynamicLabel() { 
        return this._dynamicLabel; 
    }
    
    set streamingLabel(val) {
        if(!this._streamingLabel || (this._streamingLabel && this._streamingLabel != val)){
            let oldVal = this._streamingLabel;
            this._streamingLabel = val;
            this.requestUpdate('streamingLabel', oldVal);
            this.hotReload();
        }
    }
      
    get streamingLabel() { 
        return this._streamingLabel; 
    }

    connectedCallback() {
        super.connectedCallback();
        this.hotReload();
    }

    hotReload(){
        if(this._observer){
            this._observer.cancel();
        }
        this._effectiveLabel = null;
        if(this.streamingLabel){
            this.jsonRpc = new JsonRpc(this);
            this._observer = this.jsonRpc[this.streamingLabel]().onNext(jsonRpcResponse => {
                let oldVal = this._effectiveLabel;
                this._effectiveLabel = jsonRpcResponse.result;
                this.requestUpdate('_effectiveLabel', oldVal);
            });
        }else if(this.dynamicLabel){
            this.jsonRpc = new JsonRpc(this);
            this.jsonRpc[this.dynamicLabel]().then(jsonRpcResponse => {
                let oldVal = this._effectiveLabel;
                this._effectiveLabel = jsonRpcResponse.result;
                this.requestUpdate('_effectiveLabel', oldVal);
            });
        }else if(this.staticLabel){
            let oldVal = this._effectiveLabel;
            this._effectiveLabel = this.staticLabel;
            this.requestUpdate('_effectiveLabel', oldVal);
        }else{
            let oldVal = this._effectiveLabel;
            this._effectiveLabel = null;
            this.requestUpdate('_effectiveLabel', oldVal);
        }
    }

    _getEffectiveLabel(){
        if(this.streamingLabel){
            this.jsonRpc = new JsonRpc(this);
            this._observer = this.jsonRpc[this.streamingLabel]().onNext(jsonRpcResponse => {
                this._effectiveLabel = jsonRpcResponse.result;
            });
        }else if(this.dynamicLabel){
            this.jsonRpc = new JsonRpc(this);
            this.jsonRpc[this.dynamicLabel]().then(jsonRpcResponse => {
                this._effectiveLabel = jsonRpcResponse.result;
            });
        }else if(this.staticLabel){
            this._effectiveLabel = this.staticLabel;
        }
    }

    disconnectedCallback() {
        if(this._observer){
            this._observer.cancel();
        }
        super.disconnectedCallback();
    }

    render() {
        if(this.path){
            let routerIgnore = false;

            let p = this.path;
            let t = "_self";
            if(!this.embed){
                routerIgnore = true;
                p = this.externalUrl;
                t = "_blank";
            }
            return html`
            <a class="extensionLink" href="${p}" ?router-ignore=${routerIgnore} target="${t}">
                <span class="iconAndName">
                    <vaadin-icon class="icon" icon="${this.iconName}"></vaadin-icon>
                    ${this.displayName} 
                </span>
                ${this._renderBadge()} 
            </a>
            `;
        }
    }
    
    _renderBadge() {
        if (this._effectiveLabel) {
            return html`<qui-badge tiny pill><span>${this._effectiveLabel}</span></qui-badge>`;
        }
    }
}
customElements.define('qwc-extension-link', QwcExtensionLink);