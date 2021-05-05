package com.kykosoft.azPlantUML;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Logger;

import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.HttpMethod;
import com.microsoft.azure.functions.HttpRequestMessage;
import com.microsoft.azure.functions.HttpResponseMessage;
import com.microsoft.azure.functions.HttpStatus;
import com.microsoft.azure.functions.annotation.AuthorizationLevel;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.azure.functions.annotation.HttpTrigger;

/**
 * Performs a simple get request against http://httpstatus.io/200
 *
 * @author	S. Kyle Korndoerfer
 * @version	1.0
 * @since	1.0
 */
public class TestHttpFunction {

	/** Test URI to call to validate connectivity */
	private static String url = "http://httpstat.us";

	/**
	 *
	 * @param request	Incoming HTTP request
	 * @param context	Azure Function execution context
	 * @return The result of the call to the test endpoint
	 * @throws URISyntaxException	If an invalid URI was supplied
	 * @throws IOException			If an error occurred while making the HTTP request
	 * @since	1.0
	 */
	@FunctionName("TestHttp")
	public HttpResponseMessage run(
			@HttpTrigger(
				name = "req",
				methods = {HttpMethod.GET},
				authLevel = AuthorizationLevel.ANONYMOUS)
				HttpRequestMessage<Optional<String>> request,
			final ExecutionContext context )
	throws URISyntaxException, IOException {

		Logger logger = context.getLogger();
		HttpResponseMessage response;

		logger.info("Request received");

		Map<String, String> headers = new HashMap<String, String>();
		headers.put("accept", "text/plain");

		HttpWrapper httpWrapper = new HttpWrapper(logger, url);
		//try {
			String content = httpWrapper.GetStringContent("200", headers, null);

			if (content != null || content.length() > 0) {
				response = request
					.createResponseBuilder(HttpStatus.OK)
					.body(content)
					.header("Content-Type", "text/plain")
					.build();
			} else {
				response = request
						.createResponseBuilder(HttpStatus.BAD_REQUEST)
						.body("Request could not be proccessed")
						.header("Content-Type", "text/plain")
						.build();
			}
		// } catch (URISyntaxException ex) {
		// 	logger.log(Level.SEVERE, "Invalid URI", ex);
		// 	response = request
		// 			.createResponseBuilder(HttpStatus.BAD_REQUEST)
		// 			.body("Invalid URI specified; Exception: " + ex.getReason())
		// 			.header("Content-Type", "text/plain")
		// 			.build();
		// } catch (IOException ex) {
		// 	logger.log(Level.SEVERE, "Error while retrieving content", ex);
		// 	response = request
		// 			.createResponseBuilder(HttpStatus.INTERNAL_SERVER_ERROR)
		// 			.body("Error retreiving content; Exception: " + ex.getMessage())
		// 			.header("Content-Type", "text/plain")
		// 			.build();
		// }

		return response;
	}
}
