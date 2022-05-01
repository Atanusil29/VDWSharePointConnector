package pro.delaware.eim.exstream.javaconnectors.custom.vandewiele;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VDWSharepointDocumentRouter {

	private static final Logger LOG = LoggerFactory.getLogger(VDWSharepointDocumentRouter.class);

	private VDWSharepointDocumentRouter() {}

	@SuppressWarnings("unchecked")
	public static String routeDocument(String spDocumentRouterUrl, String authBearer, String fileName, String sapAccountNumber,
			String sapCompanyCode, String sapDocumentType) throws IOException, UnsupportedOperationException {
		
		LOG.debug("VDWSharepointDocumentRouter.routeDocument()");
		LOG.trace("PARAM spDocumentRouterUrl = " + spDocumentRouterUrl);
		LOG.trace("PARAM authBearer = " + authBearer);
		LOG.trace("PARAM fileName = " + fileName);
		LOG.trace("PARAM sapAccountNumber = " + sapAccountNumber);
		LOG.trace("PARAM sapCompanyCode = " + sapCompanyCode);
		LOG.trace("PARAM sapDocumentType = " + sapDocumentType);

		JSONObject jsonProperty = new JSONObject();
		jsonProperty.put("key", "vdwlCustomerAccountNumber");
		jsonProperty.put("value", sapAccountNumber);

		JSONArray propertiesList = new JSONArray();
		propertiesList.add(jsonProperty);

		JSONObject jsonDocObj = new JSONObject();
		jsonDocObj.put("DocumentName", fileName);
		jsonDocObj.put("CompanyCode", sapCompanyCode);
		jsonDocObj.put("Type", sapDocumentType);
		jsonDocObj.put("Properties", propertiesList);
		
		LOG.trace("JSONObject jsonDocObj = " + jsonDocObj.toJSONString());

		HttpPost httpPost = new HttpPost(spDocumentRouterUrl);
		StringEntity entity = new StringEntity(jsonDocObj.toJSONString());
		httpPost.setEntity(entity);
		httpPost.setHeader("Accept", "application/json");
		httpPost.setHeader("Content-Type", "application/json");
		httpPost.setHeader("Authorization", "Bearer " + authBearer);
		LOG.trace("httpPost = " + httpPost.toString());

		// execute the request
		InputStream is = null;
		try (CloseableHttpClient httpClient = HttpClients.createDefault();
				CloseableHttpResponse response = httpClient.execute(httpPost)) {

			HttpEntity responseEntity = response.getEntity();

			if (responseEntity == null) {
				// no response
				throw new IOException("Could not retrieve metadata update response: no response entity");

			} else if (response.getStatusLine().getStatusCode() != 200) {
				// failed response
				throw new IOException("Could not retrieve metadata update response: status "
						+ response.getStatusLine().getStatusCode() + ", " + response.getStatusLine().getReasonPhrase());

			} else {
				is = responseEntity.getContent();

				String location = new String(is.readAllBytes(), StandardCharsets.UTF_8);
				LOG.trace("location = " + location);

				return location;
			}
		}
	}
}
