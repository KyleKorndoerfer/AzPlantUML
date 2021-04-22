package com.kykosoft.azPlantUML;

import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.HttpMethod;
import com.microsoft.azure.functions.HttpRequestMessage;
import com.microsoft.azure.functions.HttpResponseMessage;
import com.microsoft.azure.functions.HttpStatus;
import com.microsoft.azure.functions.annotation.AuthorizationLevel;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.azure.functions.annotation.HttpTrigger;

import net.sourceforge.plantuml.SourceStringReader;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Optional;

/**
 * Azure Functions with HTTP Trigger.
 */
public class Function {
    /**
     * This function listens at endpoint "/api/HttpExample". Two ways to invoke it using "curl" command in bash:
     * 1. curl -d "HTTP Body" {your host}/api/HttpExample
     * 2. curl "{your host}/api/HttpExample?name=HTTP%20Query"
     */
    @FunctionName("HttpExample")
    public HttpResponseMessage run(
            @HttpTrigger(
                name = "req",
                methods = {HttpMethod.GET},
                authLevel = AuthorizationLevel.ANONYMOUS)
                HttpRequestMessage<Optional<String>> request,
            final ExecutionContext context) {
        context.getLogger().info("Java HTTP trigger processed a request.");

        // Parse query parameter
        final String query = request.getQueryParameters().get("name");
        final String name = request.getBody().orElse(query);

        if (name == null) {
            return request.createResponseBuilder(HttpStatus.BAD_REQUEST).body("Please pass a name on the query string or in the request body").build();
        } else {
            return request.createResponseBuilder(HttpStatus.OK).body("Hello, " + name).build();
        }
    }

	/**
	 * This function returns a sample image for testing purposes.
	 */
	@FunctionName("SampleDiagram")
	public HttpResponseMessage getImage(
			@HttpTrigger(
				name = "req",
				methods = {HttpMethod.GET},
				authLevel = AuthorizationLevel.ANONYMOUS)
				HttpRequestMessage<Optional<String>> request,
			final ExecutionContext context ) {

		byte[] img = generateDiagram();
		HttpResponseMessage response = request
			.createResponseBuilder(HttpStatus.OK)
			.body(img)
			.header("Content-Type", "image/png")
			.build();

		return response;
	}

	/**
	 * Generates a sample diagram for testing purposes.
	 *
	 * @return sample PNG as an OutputStream.
	 */
	private byte[] generateDiagram() {
		ByteArrayOutputStream png = new ByteArrayOutputStream();

		String source = "@startuml\n";
		source += "Bob -> Alice: Hello!\n";
		source += "@enduml\n";

		SourceStringReader reader = new SourceStringReader(source);
		try {
			String desc = reader.generateImage(png);

			return png.toByteArray();
		} catch (IOException ex) {
			// error processing the diagram into a stream
		}

		return null;
	}
}
