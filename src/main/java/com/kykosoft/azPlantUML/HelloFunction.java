package com.kykosoft.azPlantUML;

import java.util.Optional;

import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.HttpMethod;
import com.microsoft.azure.functions.HttpRequestMessage;
import com.microsoft.azure.functions.HttpResponseMessage;
import com.microsoft.azure.functions.HttpStatus;
import com.microsoft.azure.functions.annotation.AuthorizationLevel;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.azure.functions.annotation.HttpTrigger;

/**
 * Azure Function with HTTP Trigger the returns a greeting
 *
 * @author	S. Kyle Korndoerfer
 * @version	1.0
 * @since	1.0
 */
public class HelloFunction {

	/**
	 * Returns a 'hello' response with the name given.
	 * <p>
	 * This function listens at endpoint "/api/HttpExample". Two ways to invoke it using "curl" command in bash:
	 * <ol>
	 * <li><code>curl -d "HTTP Body" {your host}/api/HttpExample</code>
	 * <li><code>curl "{your host}/api/HttpExample?name=HTTP%20Query"</code>
	 * </ol>
	 * @param request	incoming http request
	 * @param context	Azure Function execution context
	 * @return	HTTP Response with the content
	 * @since	1.0
	 */
    @FunctionName("Hello")
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
            return request
					.createResponseBuilder(HttpStatus.BAD_REQUEST)
					.body("Please pass a name on the query string or in the request body")
					.build();
        } else {
            return request
					.createResponseBuilder(HttpStatus.OK)
					.body("Hello, " + name)
					.build();
        }
    }
}
