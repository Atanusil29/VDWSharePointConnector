package pro.delaware.eim.exstream.javaconnectors.sharepoint.auth;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.azure.identity.ClientSecretCredential;
import com.azure.identity.ClientSecretCredentialBuilder;
import com.microsoft.graph.authentication.TokenCredentialAuthProvider;
import com.microsoft.graph.requests.GraphServiceClient;

import okhttp3.Request;

public class SharepointAuthentication {

	private static final Logger LOG = LoggerFactory.getLogger(SharepointAuthentication.class);

	private SharepointAuthentication() {}

	public static GraphServiceClient<Request> authViaGraphSDK(String azTenantId, String azClientGuid, String azClientSecret) {
		
		LOG.debug("SharepointAuthentication.authViaGraphSDK()");
		LOG.trace("PARAM azTenantId = " + azTenantId);
		LOG.trace("PARAM azClientGuid = " + azClientGuid);
		LOG.trace("PARAM azClientSecret = " + azClientSecret);

		List<String> scopes = new ArrayList<String>();
		scopes.add("https://graph.microsoft.com/.default");
		LOG.trace("scopes = " + scopes.toString());

		ClientSecretCredential clientSecretCredential = new ClientSecretCredentialBuilder().tenantId(azTenantId)
				.clientId(azClientGuid).clientSecret(azClientSecret).build();
		TokenCredentialAuthProvider tokenCredentialAuthProvider = new TokenCredentialAuthProvider(scopes,
				clientSecretCredential);

		GraphServiceClient<Request> graphClient = GraphServiceClient.builder()
				.authenticationProvider(tokenCredentialAuthProvider).buildClient();
		
		LOG.trace("Graph Client Service Root: " + graphClient.getServiceRoot());
		LOG.trace("Graph Client SDK Version: " + graphClient.getServiceSDKVersion());

		return graphClient;
	}

	@SuppressWarnings("rawtypes")
	public static HashMap authViaREST(String azTenantId, String azClientGuid, String azClientSecret, String azScopeGuid)
			throws IOException, UnsupportedOperationException {

		JSONObject jsonObject = null;

		InputStream is = null;
		InputStreamReader isr = null;
		try {
			// create the POST request
			CloseableHttpClient httpClient = HttpClients.createDefault();
			HttpPost httpPost = new HttpPost("https://login.microsoftonline.com/" + azTenantId + "/oauth2/v2.0/token");

			// set the body parameters
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("client_id", azClientGuid));
			params.add(new BasicNameValuePair("scope", "https://" + azScopeGuid + "/.default"));
			params.add(new BasicNameValuePair("client_secret", azClientSecret));
			params.add(new BasicNameValuePair("grant_type", "client_credentials"));
			httpPost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));

			// execute the request
			CloseableHttpResponse response = httpClient.execute(httpPost);

			// retrieve response entity
			HttpEntity responseEntity = response.getEntity();

			if (responseEntity == null) {
				// no response
				throw new IOException("Could not retrieve Microsoft authentication response: no response entity");

			} else if (response.getStatusLine().getStatusCode() != 200) {
				// failed response
				throw new IOException("Could not retrieve Microsoft Graph authentication response: status "
						+ response.getStatusLine().getStatusCode() + ", " + response.getStatusLine().getReasonPhrase());

			} else {
				// retrieve response JSON
				is = responseEntity.getContent();
				isr = new InputStreamReader(is);

				JSONParser jsonParser = new JSONParser();
				jsonObject = (JSONObject) jsonParser.parse(isr);

				if (jsonObject == null) {
					// Could not parse response object
					throw new IOException("Could not parse Microsoft authentication response");

				} else if (jsonObject.get("error") != null) {
					// Microsoft Graph authentication error
					throw new IOException("Could not authenticate Microsoft: " + jsonObject.get("error") + " - "
							+ jsonObject.get("error_description"));
				}
			}
		} catch (ParseException e) {
			throw new IOException(e);

		} finally {
			// close all resources
			if (isr != null)
				isr.close();
			if (is != null)
				is.close();
		}

		return jsonObject;
	}
}
