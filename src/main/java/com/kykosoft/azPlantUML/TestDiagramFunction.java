package com.kykosoft.azPlantUML;

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
 * Genrerate a sample diagram to verify PlantUML integration is working correctly.
 *
 * @author	S. Kyle Korndoerfer
 * @version 1.0
 * @since	1.0
 */
public class TestDiagramFunction {
	/**
	 * Generates a sample 'Bob -> Alice' diagram using hardcoded exmaple content.
	 *
	 * @param	request		The incoming HTTP Request
	 * @param	context		The Azure Function execution context
	 * @return	The generated diagram or an error response
	 * @since	1.0
	 */
	@FunctionName("TestDiagram")
	public HttpResponseMessage run(
			@HttpTrigger(
				name = "req",
				methods = {HttpMethod.GET},
				authLevel = AuthorizationLevel.ANONYMOUS)
				HttpRequestMessage<Optional<String>> request,
			final ExecutionContext context ) {

		Logger logger = context.getLogger();
		String source = "@startuml\nBob -> Alice: Hello!\n@enduml\n";

		byte[] img = DiagramGenerator.generate(source, logger);

		HttpResponseMessage response;
		if (img != null && img.length > 0) {
			response = request
					.createResponseBuilder(HttpStatus.OK)
					.body(img)
					.header("Content-Type", "image/png")
					.build();
		} else {
			response = request
					.createResponseBuilder(HttpStatus.BAD_REQUEST)
					.body("Error generating the diagram from the source content")
					.header("Content-Type", "text/plain")
					.build();
		}

		return response;
	}
}
