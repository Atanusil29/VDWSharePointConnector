package pro.delaware.eim.exstream.javaconnectors.custom.vandewiele;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.microsoft.graph.requests.GraphServiceClient;

import okhttp3.Request;
import pro.delaware.eim.exstream.javaconnectors.sharepoint.UploadToSharepoint;
import pro.delaware.eim.exstream.javaconnectors.sharepoint.auth.SharepointAuthentication;

public class VDWSharepointDocumentRouterTest {

	private String azTenantId;
	private String azClientGuid;
	private String azClientSecret;
	private String azScopeGuid;
	
	private String spTenantUrl;
	private String spSiteName;
	private String spLibraryName;
	
	private String spDocumentRouterUrl;
	private String sapCompanyCode;
	private String sapDocumentType;
	private String sapAccountNumber;
	
	private GraphServiceClient<Request> graphClient;
	
	private File file;
	
	@Before
	public void config() throws URISyntaxException, IOException {
		
		azTenantId = "e16e8dac-e431-416b-8924-39f5a990569d";
		azClientGuid = "66b692f6-8ca0-4ba2-ae31-525f2e20e534";
		azClientSecret = "Wp07Q~1mPEVi58ibx.uw_3XTAL0iRPyj8UUlE";
		azScopeGuid = "graph.microsoft.com";
		
		//vandewiele.sharepoint.com,6f57fb25-290e-4583-8d44-a140a209f4ea,2cf59729-35ee-40e1-8b08-a0dc908f63c3
		spTenantUrl = "vandewiele.sharepoint.com";
		spSiteName = "tst-DropOff";
		spLibraryName = "Incoming Documents";
		
		spDocumentRouterUrl = "https://az-neu-sharepoint-tst-as.azurewebsites.net/api/route/document";
		sapCompanyCode = "0002";
		sapDocumentType = "Contract";
		sapAccountNumber = "0155424";
		
		URL resource = getClass().getClassLoader().getResource("TestUpload.pdf");
		file = new File(resource.toURI());
		
		graphClient = SharepointAuthentication.authViaGraphSDK(azTenantId, azClientGuid, azClientSecret);

		UploadToSharepoint.upload(new FileInputStream(file), graphClient, spTenantUrl, spSiteName, spLibraryName, file.getName(), file.length());
	}
	
	@Test
	@SuppressWarnings("rawtypes")
	public void testSuccessRouteDocument() throws IOException {
		HashMap auth = SharepointAuthentication.authViaREST(azTenantId, azClientGuid, azClientSecret, azScopeGuid);
		String location = VDWSharepointDocumentRouter.routeDocument(spDocumentRouterUrl, (String) auth.get("access_token"), file.getName(), sapAccountNumber, sapCompanyCode, sapDocumentType);
		Assert.assertTrue(location.contains(spTenantUrl));
		Assert.assertTrue(location.contains(sapCompanyCode));
		Assert.assertTrue(location.contains(sapDocumentType));
		Assert.assertTrue(location.contains(sapAccountNumber));
	}
}
