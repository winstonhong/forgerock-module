package com.inbaytech.openam.idqNode;

import static com.inbaytech.openam.idqNode.IdqConstants.*;
import static org.forgerock.openam.auth.node.api.Action.send;

import com.google.inject.assistedinject.Assisted;
import com.sun.identity.authentication.callbacks.HiddenValueCallback;
import com.sun.identity.authentication.callbacks.ScriptTextOutputCallback;
import com.sun.identity.shared.debug.Debug;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import javax.inject.Inject;
import javax.security.auth.callback.Callback;
import org.forgerock.openam.annotations.sm.Attribute;
import org.forgerock.openam.auth.node.api.*;
import org.forgerock.openam.core.CoreWrapper;

import java.util.Set;
import com.inbaytech.idqoauth.IdqOauth;
import com.inbaytech.idqoauth.IdqOauthException;
import org.json.JSONException;
import org.json.JSONObject;

import com.sun.identity.idm.AMIdentity;
import com.sun.identity.idm.IdUtils;

/**
  Copyright 2019 inBay Technologies Inc.

  The contents of this file are subject to the terms of the Common Development and
  Distribution License. You may not use this file except in compliance with the License.
  See the License for the specific language governing permission and limitations under the License.
 
  A copy of the License should have been provided in the LICENSE file in the root of this repository.
  If one was not, then one may be requested by contacting inBay Technologies: https://www.inbaytech.com/contact-us
 */

 @Node.Metadata(outcomeProvider = AbstractDecisionNode.OutcomeProvider.class, configClass = IdqNode.Config.class)

public class IdqNode extends AbstractDecisionNode {

    private final static String DEBUG_FILE = "IdqEnterpriseNode";
    private Debug debug = Debug.getInstance(DEBUG_FILE);
    private String clientID;
    private String clientSecret;
    private String idqOauthEndpoint;
    private String callbackUrl;

    /**
     * Configuration for the Idq node.
     */
    public interface Config {
        @Attribute(order = 100)
        default String clientID() {
            return "";
        }

        @Attribute(order = 200)
        default String clientSecret() {
            return "";
        }

        @Attribute(order = 300)
        default String idqOauthEndpoint() {
            return "https://example.idquanta.com/idqoauth/api/v1";
        }

        @Attribute(order = 400)
        default String callbackUrl() {
            return "https://forgerock.example.com/openam";
        }
    }

    @Inject
    public IdqNode(@Assisted Config config, CoreWrapper coreWrapper) throws NodeProcessException {
        this.clientID = config.clientID();
        this.clientSecret = config.clientSecret();
        this.idqOauthEndpoint = config.idqOauthEndpoint();
        this.callbackUrl = config.callbackUrl();
    }

    @Override
    public Action process(TreeContext context) throws NodeProcessException {
        String state = context.sharedState.get("state").asString();
        if (state == null || state == "") {
            state = IdqOauth.getState();
            context.sharedState.put("state", state);
            debug.message("idq_state: " + state);
        }
        if (context.hasCallbacks()) {
            String idqResponse = context.getCallback(HiddenValueCallback.class).get().getValue();
            debug.message("idQ OAuth Response Received: " + idqResponse);
            try {
                JSONObject oauthJSON = new JSONObject(idqResponse);
                final String oauthState = oauthJSON.getString("state");

                if (state.equalsIgnoreCase(oauthState)) {
                    final String authzCode = oauthJSON.getString("code");
                    String[] idqUserInfo = IdqOauth.getUserInfoByCode(authzCode, this.clientID, this.clientSecret,
                            this.callbackUrl, this.idqOauthEndpoint);
                    debug.message("idQ user ID parsed: " + idqUserInfo[0] + " email: " + idqUserInfo[1]);
                    String organization = context.sharedState.get(SharedStateConstants.REALM).asString();
                    debug.message("idQ user organization: " + organization);

                    Set userAliasSet = null;
                    AMIdentity userObj = IdUtils.getIdentity(idqUserInfo[1], organization, userAliasSet);
                    if (null == userObj) {
                        debug.message("idQ user does NOT exist in the local system.");
                        return goTo(false).build();
                    }

                    String username = userObj.getName();
                    context.sharedState.put(SharedStateConstants.USERNAME, username);
                    debug.message("idQ user organization: " + organization + " idQ user object: " + userObj.toString()
                            + " ForgeRock username: " + username);
                    return goTo(true).build();
                } else {
                    debug.message("idQ OAuth state " + oauthState + " is not equal to the original state " + state);
                    return goTo(false).build();
                }
            } catch(JSONException e) {
                debug.message("JSON Exception: " + e);
                e.printStackTrace();
                return goTo(false).build();
            } catch(IdqOauthException e) {
                debug.message("Idq Oauth Exception while retrieving user by code: " + e);
                e.printStackTrace();
                return goTo(false).build();
            }

        }
        return buildCallbacks(this.clientID, this.callbackUrl, this.idqOauthEndpoint, state);
    }

    private Action buildCallbacks(String clientID, String callbackUrl, String idqOauthEndpoint, String state) {
        debug.message("Sending idQ OAuth Request to idQ Enterprise");
        return send(new ArrayList<Callback>() {{
            add(new ScriptTextOutputCallback(String.format(SETUP_DOM_SCRIPT) + STYLE_SCRIPT +
                    String.format(INIT_SCRIPT, clientID, callbackUrl, idqOauthEndpoint, state)));
            add(new HiddenValueCallback("idq_response"));
        }}).build();
    }

}