package com.inbaytech.idqoauth;

import java.util.UUID;
import org.apache.oltu.oauth2.client.OAuthClient;
import org.apache.oltu.oauth2.client.URLConnectionClient;
import org.apache.oltu.oauth2.client.request.OAuthBearerClientRequest;
import org.apache.oltu.oauth2.client.request.OAuthClientRequest;
import org.apache.oltu.oauth2.client.response.OAuthJSONAccessTokenResponse;
import org.apache.oltu.oauth2.client.response.OAuthResourceResponse;
import org.apache.oltu.oauth2.common.exception.OAuthProblemException;
import org.apache.oltu.oauth2.common.exception.OAuthSystemException;
import org.apache.oltu.oauth2.common.message.types.GrantType;
import org.apache.oltu.oauth2.common.OAuth;
import org.json.JSONException;
import org.json.JSONObject;

/**
  Copyright 2019 inBay Technologies Inc.

  The contents of this file are subject to the terms of the Common Development and
  Distribution License. You may not use this file except in compliance with the License.
  See the License for the specific language governing permission and limitations under the License.
 
  A copy of the License should have been provided in the LICENSE file in the root of this repository.
  If one was not, then one may be requested by contacting inBay Technologies: https://www.inbaytech.com/contact-us
*/

public final class IdqOauth {

	public static String getState() {
		return UUID.randomUUID().toString().replace("-", "");
	}

	/**
	 * Use authorization code to get user information
	 * @param authzCode
	 * @param callbackUrl
	 * @return
	 * @throws OAuthSystemException
	 * @throws OAuthProblemException
	 * @throws JSONException
	 */
	public static String[] getUserInfoByCode(String authzCode, String clientID, String clientSecret, String callbackUrl,
								String oauthHostName) throws IdqOauthException {
		final String oauthTokenUrl = oauthHostName + "/token";
		final String oauthUserUrl = oauthHostName + "/user";
		String userInfoBody;
		String[] userInfo;

		try {
			// Get access token using authz code
			OAuthClientRequest oauth2request = OAuthClientRequest
					.tokenLocation(oauthTokenUrl)
					.setClientId(clientID)
					.setClientSecret(clientSecret)
					.setRedirectURI(callbackUrl)
					.setCode(authzCode)
					.setGrantType(GrantType.AUTHORIZATION_CODE)
					.buildBodyMessage();
			OAuthClient oAuthClient = new OAuthClient(new URLConnectionClient());
			OAuthJSONAccessTokenResponse oAuthResponse = oAuthClient.accessToken(oauth2request, OAuthJSONAccessTokenResponse.class);
			final String accessToken = oAuthResponse.getAccessToken();

			// Get idQ user info using access token
			OAuthClientRequest bearerClientRequest = new OAuthBearerClientRequest(oauthUserUrl)
					.setAccessToken(accessToken)
					.buildQueryMessage();
			OAuthResourceResponse resourceResponse = oAuthClient.resource(bearerClientRequest, OAuth.HttpMethod.GET, OAuthResourceResponse.class);
			userInfoBody = resourceResponse.getBody();
		} catch (OAuthSystemException e) {
			throw new IdqOauthException("OAuth System Exception: " + e);
		} catch (OAuthProblemException e) {
			throw new IdqOauthException("OAuth Problem Exception: " + e);
		}

		try {
			JSONObject resourceObj = new JSONObject(userInfoBody);
			final String idqUsername = resourceObj.getString("username");
			final String idqEmail = resourceObj.getString("email");

			userInfo = new String[2];
			userInfo[0] = idqUsername;
			userInfo[1] = idqEmail;
		} catch (JSONException e) {
			throw new IdqOauthException("JSON Exception: " + e);
		}

		return userInfo;
	}

}
