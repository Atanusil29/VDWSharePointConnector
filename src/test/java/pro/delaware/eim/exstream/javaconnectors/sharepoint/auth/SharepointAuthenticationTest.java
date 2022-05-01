package pro.delaware.eim.exstream.javaconnectors.sharepoint.auth;

import java.io.IOException;
import java.util.HashMap;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.microsoft.graph.requests.GraphServiceClient;

import junit.framework.Assert;
import okhttp3.Request;

public class SharepointAuthenticationTest {

	private static final Logger LOG = LoggerFactory.getLogger(SharepointAuthenticationTest.class);

	private static String azTenantId;
	private static String azClientGuid;
	private static String azClientSecret;
	private static String azScopeGuid;
	
	@Before
	public void config() {
		azTenantId = "e16e8dac-e431-416b-8924-39f5a990569d";
		azClientGuid = "66b692f6-8ca0-4ba2-ae31-525f2e20e534";
		azClientSecret = "Wp07Q~1mPEVi58ibx.uw_3XTAL0iRPyj8UUlE";
		azScopeGuid = "graph.microsoft.com";
	}
	
	@Test
	public void testSuccessAuthViaGraphSDK() {
		
		GraphServiceClient<Request> graphClient = SharepointAuthentication.authViaGraphSDK(azTenantId, azClientGuid, azClientSecret);
		Assert.assertNotNull(graphClient);
	}

	@Test
	@SuppressWarnings("unchecked")
	public void testSuccessAuthViaREST() throws IOException {
		HashMap<String, String> authObject = SharepointAuthentication.authViaREST(azTenantId, azClientGuid, azClientSecret, azScopeGuid);
		String token = authObject.get("access_token");
		Assert.assertNotNull(token);
		LOG.info("Token: " + token);
	}

	@Test(expected = IOException.class)
	public void testFailAuthViaRESTOnInvalidAuthTenant() throws IOException {
		azTenantId = "12345678-9012-3456-7890-123456789012";
		SharepointAuthentication.authViaREST(azTenantId, azClientGuid, azClientSecret, azScopeGuid);
	}

	@Test(expected = IOException.class)
	public void testFailAuthViaRESTOnInvalidClientGUID() throws IOException {
		azClientGuid = "12345678-9012-3456-7890-123456789012";
		SharepointAuthentication.authViaREST(azTenantId, azClientGuid, azClientSecret, azScopeGuid);
	}

	@Test(expected = IOException.class)
	public void testFailAuthViaRESTOnInvalidClientSecret() throws IOException {
		azClientSecret = "wrong_password";
		SharepointAuthentication.authViaREST(azTenantId, azClientGuid, azClientSecret, azScopeGuid);
	}

	@Test(expected = IOException.class)
	public void testFailAuthViaRESTOnInvalidScopeGUID() throws IOException {
		azScopeGuid = "12345678-9012-3456-7890-123456789012";
		SharepointAuthentication.authViaREST(azTenantId, azClientGuid, azClientSecret, azScopeGuid);
	}
}
