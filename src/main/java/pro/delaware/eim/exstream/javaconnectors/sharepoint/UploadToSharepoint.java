package pro.delaware.eim.exstream.javaconnectors.sharepoint;

import java.io.IOException;
import java.io.InputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonPrimitive;
import com.microsoft.graph.models.DriveItem;
import com.microsoft.graph.models.DriveItemCreateUploadSessionParameterSet;
import com.microsoft.graph.models.DriveItemUploadableProperties;
import com.microsoft.graph.models.Site;
import com.microsoft.graph.models.UploadSession;
import com.microsoft.graph.requests.GraphServiceClient;
import com.microsoft.graph.tasks.IProgressCallback;
import com.microsoft.graph.tasks.LargeFileUploadResult;
import com.microsoft.graph.tasks.LargeFileUploadTask;

import okhttp3.Request;

public class UploadToSharepoint {

	private static final Logger LOG = LoggerFactory.getLogger(UploadToSharepoint.class);

	private UploadToSharepoint() {}

	// create a callback used by the upload provider
	private static IProgressCallback callback = new IProgressCallback() {
		@Override
		// called after each slice of the file is uploaded
		public void progress(final long current, final long max) {
			LOG.trace(String.format("Uploaded %d bytes of %d total bytes", current, max));
		}
	};

	public static LargeFileUploadResult<DriveItem> upload(InputStream fileStream,
			GraphServiceClient<Request> graphClient, String spTenantUrl, String spSiteName, String spLibraryName,
			String fileName, long fileSize) throws IOException {
		
		LOG.debug("UploadToSharepoint.upload()");
		LOG.trace("PARAM spTenantUrl = " + spTenantUrl);
		LOG.trace("PARAM spSiteName = " + spSiteName);
		LOG.trace("PARAM spLibraryName = " + spLibraryName);
		LOG.trace("PARAM fileName = " + fileName);
		LOG.trace("PARAM fileSize = " + fileSize);

		// set the upload parameters
		DriveItemUploadableProperties diup = new DriveItemUploadableProperties();
		diup.additionalDataManager().put("@microsoft.graph.conflictBehavior", new JsonPrimitive("replace"));

		DriveItemCreateUploadSessionParameterSet uploadParams = DriveItemCreateUploadSessionParameterSet.newBuilder()
				.withItem(diup).build();

		// create an upload session
		Site targetSite = graphClient.sites(spTenantUrl + ":/sites/" + spSiteName).buildRequest().get();
		LOG.trace("Target Site ID: " + targetSite.id);

		UploadSession uploadSession = graphClient.sites(targetSite.id).lists(spLibraryName).drive().root()
				.itemWithPath(fileName).createUploadSession(uploadParams).buildRequest().post();
		LOG.trace("Upload URL: " + uploadSession.uploadUrl);

		// create an upload task
		LargeFileUploadTask<DriveItem> largeFileUploadTask = new LargeFileUploadTask<DriveItem>(uploadSession,
				graphClient, fileStream, fileSize, DriveItem.class);

		// perform the upload to Sharepoint, execute the upload task
		LargeFileUploadResult<DriveItem> result = largeFileUploadTask.upload(0, null, callback);
		LOG.trace("Uploaded Web URL: " + result.responseBody.webUrl);

		return result;
	}
}
