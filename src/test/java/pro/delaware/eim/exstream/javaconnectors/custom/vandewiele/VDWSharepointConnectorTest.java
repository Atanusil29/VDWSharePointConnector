package pro.delaware.eim.exstream.javaconnectors.custom.vandewiele;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.rmi.RemoteException;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import pro.delaware.eim.exstream.javaconnectors.custom.vandewiele.config.VDWExstreamProperties;
import streamserve.connector.StrsConfigVals;

public class VDWSharepointConnectorTest {

	private StrsConfigVals configVals;
	private BufferedInputStream bis;

	@Before
	public void config() throws URISyntaxException, IOException {

		configVals = new StrsConfigVals();
		configVals.setValue(VDWExstreamProperties.AZURE_TENANT_GUID, "e16e8dac-e431-416b-8924-39f5a990569d");
		configVals.setValue(VDWExstreamProperties.AZURE_CLIENT_GUID, "66b692f6-8ca0-4ba2-ae31-525f2e20e534");
		configVals.setValue(VDWExstreamProperties.AZURE_CLIENT_SECRET, "Wp07Q~1mPEVi58ibx.uw_3XTAL0iRPyj8UUlE");
		configVals.setValue(VDWExstreamProperties.AZURE_SCOPE_GUID, "graph.microsoft.com");

		// vandewiele.sharepoint.com,6f57fb25-290e-4583-8d44-a140a209f4ea,2cf59729-35ee-40e1-8b08-a0dc908f63c3
		configVals.setValue(VDWExstreamProperties.SHAREPOINT_TENANT_URL, "vandewiele.sharepoint.com");
		configVals.setValue(VDWExstreamProperties.SHAREPOINT_SITE_NAME, "tst-DropOff");
		configVals.setValue(VDWExstreamProperties.SHAREPOINT_LIBRARY_NAME, "Incoming Documents");

		configVals.setValue(VDWExstreamProperties.SHAREPOINT_DOCUMENT_ROUTER_URL,
				"https://az-neu-sharepoint-tst-as.azurewebsites.net/api/route/document");
		configVals.setValue(VDWExstreamProperties.SAP_COMPANY_CODE, "0002");
		configVals.setValue(VDWExstreamProperties.SAP_DOCUMENT_TYPE, "Contract");
		configVals.setValue(VDWExstreamProperties.SAP_ACCOUNT_NUMBER, "0155424");

		configVals.setValue(VDWExstreamProperties.UPLOAD_FILE_NAME, "TestUpload.pdf");

		bis = new BufferedInputStream(getClass().getClassLoader().getResourceAsStream("TestUpload.pdf"));
	}

	@Test
	public void testSuccessSharepointConnector() throws IOException, RemoteException {
		VDWSharepointConnector sc = new VDWSharepointConnector();
		Assert.assertTrue(sc.strsoOpen(configVals));
		Assert.assertTrue(sc.strsoStartJob(configVals));
		while (bis.available() > 0) {
			Assert.assertTrue(sc.strsoWrite(bis.readNBytes(1024)));
		}
		Assert.assertTrue(sc.strsoEndJob());
		Assert.assertTrue(sc.strsoClose(configVals));
	}

	@After
	public void close() throws IOException {
		if (bis != null)
			bis.close();
	}
}