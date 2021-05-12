package com.kykosoft.azPlantUML;

import java.io.IOException;
import java.util.Optional;
import java.util.logging.Level;
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
 * This function processes a diagram generation request against an Azure DevOps
 * git repo.
 *
 * @author	S. Kyle Korndoerfer
 * @version	1.0
 * @since	1.0
 */
public class DevOpsFunction {

	/**
	 * Generates an image using the contents of a file in an Azure DevOps repository.
	 * <p>
	 * Accessed at: /api/DevOps?path=%2Fpath%2Fto%2Ffile.puml
	 *
	 * @param request	incoming HTTP request
	 * @param context	Azure Function execution context
	 * @return	HTTP Response
	 * @since	1.0
	 */
	@FunctionName("DevOps")
	public HttpResponseMessage run(
			@HttpTrigger(
				name = "req",
				methods = {HttpMethod.GET},
				authLevel = AuthorizationLevel.ANONYMOUS)
				HttpRequestMessage<Optional<String>> request,
			final ExecutionContext context ) {

		Logger logger = context.getLogger();
		HttpResponseMessage response = null;

		// check request parameters
		if (!request.getQueryParameters().containsKey("path")) {
			String errMessage = "'path' is a required parameter";
			logger.log(Level.SEVERE, errMessage);

			return request
					.createResponseBuilder(HttpStatus.BAD_REQUEST)
					.body(errMessage)
					.header("Content-Type", "text/plain")
					.build();
		}

		DevOpsDiagram generator = new DevOpsDiagram(logger);

		try {
			byte[] diagramImage = generator.generate(request.getQueryParameters().get("path"));

			response = request.createResponseBuilder(HttpStatus.OK)
				.body(diagramImage)
				.header("Content-Type", "image/png")
				.build();
		} catch (IOException ex) {
			response = request.createResponseBuilder(HttpStatus.INTERNAL_SERVER_ERROR)
				.body(ex.toString())
				.header("Content-Type", "text/plain")
				.build();
		}

		return response;
	}
}
