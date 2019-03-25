package com.inbaytech.openam.idqNode;

/**
  Copyright 2019 inBay Technologies Inc.

  The contents of this file are subject to the terms of the Common Development and
  Distribution License. You may not use this file except in compliance with the License.
  See the License for the specific language governing permission and limitations under the License.
 
  A copy of the License should have been provided in the LICENSE file in the root of this repository.
  If one was not, then one may be requested by contacting inBay Technologies: https://www.inbaytech.com/contact-us
 */

 final class IdqConstants {
    static final String SETUP_DOM_SCRIPT =
            "var scriptj = document.createElement('script');\n" +
            "scriptj.src = 'https://taas.idquanta.com/content/js/jquery-3.2.1.min.js';\n" +
            "var scriptq = document.createElement('script');\n" +
            "scriptq.src = 'https://taas.idquanta.com/content/js/jquery-qrcode-0.14.0-idq.js';\n" +
            "var script = document.createElement('script');\n" +
            "document.getElementById('loginButton_0').style.display = 'none';\n" +
            "script.src = 'https://idquanta.com/content/js/idq-20181122-1100.js';\n";
    static final String STYLE_SCRIPT =
            "var divcontainer = document.createElement('div');\n" +
            "var dividq = document.createElement('div');\n" +
            "var divauth = document.createElement('div');\n" +
            "divcontainer.className='page-header';\n" +
            "dividq.className='idq';\n" +
            "dividq.style='margin-left:auto;margin-right:auto';\n"+
            "divauth.className='authenticated';\n" +
            "divcontainer.appendChild(dividq);\n" +
            "divcontainer.appendChild(divauth);\n" +
            "document.getElementById('loginButton_0').parentElement.appendChild(divcontainer);\n";
    static final String INIT_SCRIPT = "scriptj.onload = function() {\n" +
            "       console.debug('Load jquery');\n" +
            "       document.body.appendChild(scriptq);\n" +
            "};\n" +
            "scriptq.onload = function(){\n" +
            "       console.debug('Load qrcode');\n" +
            "       document.body.appendChild(script);\n" +
            "};\n" +
            "script.onload = function() {\n" +
            "       var clientId = '%s';\n" +
            "       var callbackUrl = '%s';\n" +
            "       // State should be a random value obtained from Relying Party backend.\n" +
            "       var idqOauthEndpoint = '%s';\n" +
            "       var state = '%s';\n" +
            "       var callbackFunction = loggedIn;\n" +
            "       // Create and initialize the idQ object.\n" +
            "       idq = new IDQ();\n" +
            "       console.debug('new IDQ module');\n" +
            "       idq.network.oauthUrl = idqOauthEndpoint;\n" +
            "       idq.widgetInit(clientId, callbackUrl, state, callbackFunction);\n" +
            "};\n" +
            "// This function is called back when the OAuth 2.0 code is returned.\n" +
            "function loggedIn(state, code) {\n" +
            "       var authJSON = { 'code': code , 'state': state };\n" +
            "       var authString = JSON.stringify(authJSON);\n" +
            "       document.getElementById('idq_response').setAttribute('value', authString);\n" +
            "       $('.authenticated')\n" +
            "       .hide()\n" +
            "       .fadeIn();\n" +
            "       document.getElementById('loginButton_0').click()" +
            "}\n" +
            "document.body.appendChild(scriptj);\n";
}