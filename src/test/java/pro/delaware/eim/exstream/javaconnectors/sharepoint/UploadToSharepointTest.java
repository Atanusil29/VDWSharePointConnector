package pro.delaware.eim.exstream.javaconnectors.sharepoint;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.microsoft.graph.models.DriveItem;
import com.microsoft.graph.requests.GraphServiceClient;
import com.microsoft.graph.tasks.LargeFileUploadResult;

import okhttp3.Request;
import pro.delaware.eim.exstream.javaconnectors.sharepoint.auth.SharepointAuthentication;

public class UploadToSharepointTest {
	
	private String azTenantId;
	private String azClientGuid;
	private String azClientSecret;
	
	private String spTenantUrl;
	private String spSiteName;
	private String spLibraryName;
	
	private File file;
	
	private GraphServiceClient<Request> graphClient;
	
	@Before
	public void config() throws URISyntaxException, FileNotFoundException {
		
		azTenantId = "e16e8dac-e431-416b-8924-39f5a990569d";
		azClientGuid = "66b692f6-8ca0-4ba2-ae31-525f2e20e534";
		azClientSecret = "Wp07Q~1mPEVi58ibx.uw_3XTAL0iRPyj8UUlE";
		
		//vandewiele.sharepoint.com,6f57fb25-290e-4583-8d44-a140a209f4ea,2cf59729-35ee-40e1-8b08-a0dc908f63c3
		spTenantUrl = "vandewiele.sharepoint.com";
		spSiteName = "tst-DropOff";
		spLibraryName = "Incoming Documents";
		
		URL resource = getClass().getClassLoader().getResource("TestUpload.pdf");
		file = new File(resource.toURI());
		
		graphClient = SharepointAuthentication.authViaGraphSDK(azTenantId, azClientGuid, azClientSecret);
	}
	
	@Test
	public void testUploadToSharepoint() throws IOException {
		
		LargeFileUploadResult<DriveItem> result = UploadToSharepoint.upload(new FileInputStream(file), graphClient, spTenantUrl, spSiteName, spLibraryName, file.getName(), file.length());
		DriveItem item = result.responseBody;
		String location = item.webUrl;
		Assert.assertTrue(location.contains(URLEncoder.encode(spTenantUrl, "UTF-8").replace("+", "%20")));
		Assert.assertTrue(location.contains(URLEncoder.encode(spSiteName, "UTF-8").replace("+", "%20")));
		Assert.assertTrue(location.contains(URLEncoder.encode(spLibraryName, "UTF-8").replace("+", "%20")));
	}
}
