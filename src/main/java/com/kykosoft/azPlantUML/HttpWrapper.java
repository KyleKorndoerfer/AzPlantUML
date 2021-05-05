package com.kykosoft.azPlantUML;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

/**
 * Wrapper class for making HTTP requests using Apache HTTP Components library.
 *
 * @author	S. Kyle Korndoerfer
 * @version	1.0
 * @since	1.0
 */
public class HttpWrapper {

	/** The base url address for all HTTP requests made from this instance. */
	private String baseUrl = null;

	/** Logging instance to use. */
	private Logger logger;

	/**
	 * Instantiates a new instance.
	 *
	 * @param	logger	The logging instance to use.
	 * @param	baseUrl	The base URL to use for all requests.
	 * @since	1.0
	 */
	public HttpWrapper(Logger logger, String baseUrl) {
		this.logger = logger;
		this.baseUrl = baseUrl;
	}

	/**
	 * Retrieves string content from the specified endpoint.
	 *
	 * @param	path
	 * @param	headers
	 * @param	queryParams
	 * @return	The result content of the http request as a string.
	 * @throws	URISyntaxException	if the provided path results in an invalid URI
	 * @since	1.0
	 */
	public String GetStringContent(
			final String path,
			final Map<String, String> headers,
			final Map<String, String> queryParams)
	throws URISyntaxException, IOException {

		String url = String.format("%1$s/%2$s", baseUrl, path);
		logger.fine("Making request to: " + url);
		String content = null;

		try (final CloseableHttpClient httpClient = HttpClients.createDefault()) {
			final HttpGet httpGet = buildGetRequest( url, queryParams, headers);

			try (final CloseableHttpResponse response = httpClient.execute(httpGet))			 {
				logger.info("Received a response with status: " + response.getStatusLine().getStatusCode());

				int statusCode = response.getStatusLine().getStatusCode();
				if (statusCode == 200) {
					content = EntityUtils.toString(response.getEntity());
				}
			}
		} catch (IOException ex) {
			logger.log(
					Level.SEVERE,
					"Error while retrieving content from server",
					ex);
			throw ex;
		}

		return content;
	}


	/**
	 * Builds an HTTP Get request using the provided parameters ready to execute.
	 *
	 * @param	uri			The full URI of the request
	 * @param	queryParams	Collection of query parameters to add to the request
	 * @param	headers		Collection of headers to add to the request
	 * @return	Composed HTP GET request ready to execute
	 * @throws	URISyntaxException	An invalid URI is supplied
	 * @since	1.0
	 */
	private HttpGet buildGetRequest(
			String uri,
			Map<String, String> queryParams,
			Map<String, String> headers)
	throws URISyntaxException {
		URIBuilder uriBuilder = new URIBuilder(uri);

		if (queryParams != null && queryParams.size() > 0) {
			queryParams.forEach( (header, value) -> uriBuilder.addParameter(header, value) );
		}

		HttpGet httpGet = new HttpGet( uriBuilder.build() );

		if (headers != null && headers.size() > 0) {
			headers.forEach( (header, value) -> httpGet.addHeader(header, value) );
		}

		return httpGet;
	}
}
