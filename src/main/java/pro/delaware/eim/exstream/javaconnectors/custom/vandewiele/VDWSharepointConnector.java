package pro.delaware.eim.exstream.javaconnectors.custom.vandewiele;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.rmi.RemoteException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.microsoft.graph.requests.GraphServiceClient;

import okhttp3.Request;
import pro.delaware.eim.exstream.javaconnectors.common.Utils;
import pro.delaware.eim.exstream.javaconnectors.custom.vandewiele.config.VDWExstreamProperties;
import pro.delaware.eim.exstream.javaconnectors.sharepoint.UploadToSharepoint;
import pro.delaware.eim.exstream.javaconnectors.sharepoint.auth.SharepointAuthentication;
import pro.delaware.eim.exstream.javaconnectors.sharepoint.config.ExstreamProperties;
import streamserve.connector.StrsConfigVals;
import streamserve.connector.StrsConnectable;
import streamserve.connector.StrsServiceable;

public class VDWSharepointConnector implements StrsConnectable {

	private static final Logger LOG = LoggerFactory.getLogger(VDWSharepointConnector.class);

	private ByteArrayOutputStream dataStream = new ByteArrayOutputStream();

	/**
	 * StrsConnectable implementation
	 * 
	 * The StreamServer calls this method each time it starts processing output
	 * data. Can be used to initialise resources according to connector properties
	 * set in Design Center.
	 * 
	 * @param configVals contains the connector's runtime properties
	 * @return boolean Status code
	 */
	public boolean strsoOpen(StrsConfigVals configVals) throws RemoteException {
		LOG.debug("VDWSharepointConnector.strsoOpen()");
		return true;
	}

	/**
	 * StrsConnectable implementation
	 * 
	 * The StreamServer calls this method directly after the connector has been
	 * created. Use this method to initialise resources according to the connector
	 * properties set in Design Center.
	 * 
	 * @param configVals contains the connector's runtime properties
	 * @return boolean Status code
	 */
	public boolean strsoStartJob(StrsConfigVals configVals) throws RemoteException {
		LOG.debug("VDWSharepointConnector.strsoStartJob()");
		return true;
	}

	/**
	 * StrsConnectable implementation
	 * 
	 * This method is called between a pair of strsoOpen() and strsoClose() calls.
	 * It can be called several times or only once, depending on the amount of data
	 * to be written. Each strsoWrite() call provides buffered output data.
	 * 
	 * @param data is a byte array of the data to write
	 * @return boolean Status code
	 */
	public boolean strsoWrite(byte[] data) throws RemoteException {
		LOG.debug("VDWSharepointConnector.strsoWrite()");
		LOG.trace("Data packet length: " + data.length);

		try {
			dataStream.write(data);

		} catch (IOException e) {
			LOG.error(e.getMessage(), e);
			return false;
		}
		return true;
	}

	/**
	 * StrsConnectable implementation
	 * 
	 * The StreamServer calls this method when all data has been delivered by the
	 * output connector and before the connector is removed. Use this method to
	 * release the resources used by the connector.
	 * 
	 * @return boolean Status code
	 */
	public boolean strsoEndJob() throws RemoteException {
		LOG.debug("VDWSharepointConnector.strsoEndJob()");
		return true;
	}

	/**
	 * StrsConnectable implementation
	 * 
	 * The StreamServer calls this method at the end of the Process, Document or
	 * Job. Use this method to perform the final delivery.
	 * 
	 * @param configVals contains the connector's runtime properties
	 * @return boolean Status code
	 */
	public boolean strsoClose(StrsConfigVals configVals) throws RemoteException {
		String msg = "VDWSharepointConnector.strsoClose()";
		LOG.debug(msg);
		Utils.logToExstream(configVals.getStrsService(), StrsServiceable.MSG_DEBUG, 1, msg);
		msg = String.format("%s: %s", ExstreamProperties.AZURE_TENANT_GUID,
				configVals.getValue(ExstreamProperties.AZURE_TENANT_GUID));
		LOG.trace(msg);
		Utils.logToExstream(configVals.getStrsService(), StrsServiceable.MSG_DEBUG, 1, msg);
		msg = String.format("%s: %s", ExstreamProperties.AZURE_CLIENT_GUID,
				configVals.getValue(ExstreamProperties.AZURE_CLIENT_GUID));
		LOG.trace(msg);
		Utils.logToExstream(configVals.getStrsService(), StrsServiceable.MSG_DEBUG, 1, msg);
		msg = String.format("%s: %s", ExstreamProperties.AZURE_CLIENT_SECRET,
				configVals.getValue(ExstreamProperties.AZURE_CLIENT_SECRET));
		LOG.trace(msg);
		Utils.logToExstream(configVals.getStrsService(), StrsServiceable.MSG_DEBUG, 1, msg);
		msg = String.format("%s: %s", ExstreamProperties.UPLOAD_FILE_NAME,
				configVals.getValue(ExstreamProperties.UPLOAD_FILE_NAME));
		LOG.trace(msg);
		Utils.logToExstream(configVals.getStrsService(), StrsServiceable.MSG_DEBUG, 1, msg);
		msg = String.format("%s: %s", ExstreamProperties.SHAREPOINT_TENANT_URL,
				configVals.getValue(ExstreamProperties.SHAREPOINT_TENANT_URL));
		LOG.trace(msg);
		Utils.logToExstream(configVals.getStrsService(), StrsServiceable.MSG_DEBUG, 1, msg);
		msg = String.format("%s: %s", ExstreamProperties.SHAREPOINT_SITE_NAME,
				configVals.getValue(ExstreamProperties.SHAREPOINT_SITE_NAME));
		LOG.trace(msg);
		Utils.logToExstream(configVals.getStrsService(), StrsServiceable.MSG_DEBUG, 1, msg);
		msg = String.format("%s: %s", ExstreamProperties.SHAREPOINT_LIBRARY_NAME,
				configVals.getValue(ExstreamProperties.SHAREPOINT_LIBRARY_NAME));
		LOG.trace(msg);
		Utils.logToExstream(configVals.getStrsService(), StrsServiceable.MSG_DEBUG, 1, msg);
		msg = String.format("%s: %s", VDWExstreamProperties.SHAREPOINT_DOCUMENT_ROUTER_URL,
				configVals.getValue(VDWExstreamProperties.SHAREPOINT_DOCUMENT_ROUTER_URL));
		LOG.trace(msg);
		Utils.logToExstream(configVals.getStrsService(), StrsServiceable.MSG_DEBUG, 1, msg);
		msg = String.format("%s: %s", VDWExstreamProperties.SAP_ACCOUNT_NUMBER,
				configVals.getValue(VDWExstreamProperties.SAP_ACCOUNT_NUMBER));
		LOG.trace(msg);
		Utils.logToExstream(configVals.getStrsService(), StrsServiceable.MSG_DEBUG, 1, msg);
		msg = String.format("%s: %s", VDWExstreamProperties.SAP_COMPANY_CODE,
				configVals.getValue(VDWExstreamProperties.SAP_COMPANY_CODE));
		LOG.trace(msg);
		Utils.logToExstream(configVals.getStrsService(), StrsServiceable.MSG_DEBUG, 1, msg);
		msg = String.format("%s: %s", VDWExstreamProperties.SAP_DOCUMENT_TYPE,
				configVals.getValue(VDWExstreamProperties.SAP_DOCUMENT_TYPE));
		LOG.trace(msg);
		Utils.logToExstream(configVals.getStrsService(), StrsServiceable.MSG_DEBUG, 1, msg);

		if (dataStream != null) {
			try (InputStream is = new ByteArrayInputStream(dataStream.toByteArray())) {

				// close the data stream
				dataStream.close();

				// authenticate to Microsoft Graph SDK and create a client
				msg = "Start Microsoft Graph SDK authentication";
				LOG.info(msg);
				Utils.logToExstream(configVals.getStrsService(), StrsServiceable.MSG_INFO, 1, msg);
				GraphServiceClient<Request> graphClient = SharepointAuthentication.authViaGraphSDK(
						configVals.getValue(ExstreamProperties.AZURE_TENANT_GUID),
						configVals.getValue(ExstreamProperties.AZURE_CLIENT_GUID),
						configVals.getValue(ExstreamProperties.AZURE_CLIENT_SECRET));
				msg = "Finished Microsoft Graph SDK authentication";
				LOG.info(msg);
				Utils.logToExstream(configVals.getStrsService(), StrsServiceable.MSG_INFO, 1, msg);

				// get the file name
				String fileName = configVals.getValue(ExstreamProperties.UPLOAD_FILE_NAME);

				// upload the document to Sharepoint
				msg = "Start document upload to Sharepoint";
				LOG.info(msg);
				Utils.logToExstream(configVals.getStrsService(), StrsServiceable.MSG_INFO, 1, msg);
				UploadToSharepoint.upload(is, graphClient,
						configVals.getValue(ExstreamProperties.SHAREPOINT_TENANT_URL),
						configVals.getValue(ExstreamProperties.SHAREPOINT_SITE_NAME),
						configVals.getValue(ExstreamProperties.SHAREPOINT_LIBRARY_NAME), fileName, is.available());
				msg = "Finished document upload to Sharepoint";
				LOG.info(msg);
				Utils.logToExstream(configVals.getStrsService(), StrsServiceable.MSG_INFO, 1, msg);

				// route the document to the correct location within Sharepoint
				msg = "Start route document in Sharepoint";
				LOG.info(msg);
				Utils.logToExstream(configVals.getStrsService(), StrsServiceable.MSG_INFO, 1, msg);
				String location = VDWSharepointDocumentRouter.routeDocument(
						configVals.getValue(VDWExstreamProperties.SHAREPOINT_DOCUMENT_ROUTER_URL),
						configVals.getValue(VDWExstreamProperties.AZURE_SCOPE_GUID), fileName,
						configVals.getValue(VDWExstreamProperties.SAP_ACCOUNT_NUMBER),
						configVals.getValue(VDWExstreamProperties.SAP_COMPANY_CODE),
						configVals.getValue(VDWExstreamProperties.SAP_DOCUMENT_TYPE));
				msg = "Document location: " + location;
				LOG.info(msg);
				Utils.logToExstream(configVals.getStrsService(), StrsServiceable.MSG_INFO, 1, msg);
				msg = "Finished route document in Sharepoint";
				LOG.info(msg);
				Utils.logToExstream(configVals.getStrsService(), StrsServiceable.MSG_INFO, 1, msg);

			} catch (IOException e) {
				LOG.error(e.getMessage(), e);
				Utils.logToExstream(configVals.getStrsService(), StrsServiceable.MSG_ERROR, 1, e.getMessage());
				return false;
			}

		} else {
			msg = "Data stream is empty";
			LOG.error(msg);
			Utils.logToExstream(configVals.getStrsService(), StrsServiceable.MSG_ERROR, 1, msg);
			return false;
		}

		return true;
	}
}
