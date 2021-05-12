package com.kykosoft.azPlantUML;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.sourceforge.plantuml.SourceStringReader;

/**
 * Generates a PlantUML diagram from source text.
 *
 * @author	S. Kyle Korndoerfer
 * @version	1.0
 * @since	1.0
 */
public class DevOpsDiagram {

	/** Name of environment variable that holds the DevOps organization name */
	private static final String ENV_ORGANIZATION = "azuml_devopsOrg";

	/** Name of the environment variable that holds the DevOps project name */
	private static final String ENV_PROJECT = "azuml_devopsProject";

	/** Name of the environment variable that holds the DevOps Repository Guid/Uuid */
	private static final String ENV_REPOSITORY_ID = "azuml_devopsRepoId";

	/** Name of the environment variable that holds the Personal Access Toekn for accessing DevOps */
	private static final String ENV_PAT = "azuml_devopsPAT";

	/** Format string for error message when looking for environment variables */
	private static final String ENV_ERROR_FORMAT = "Unable to retrieve '%1$s' environment variable";

	/** format string for generating an error diagram */
	private static final String ERROR_DIAGRAM_FORMAT = "@startuml\nparticipant Error #red\nnote over Error\n\t%1$s\nend note\n@enduml";

	/** The base URI for the DevOps service */
	private static final String DevOpsBaseUri = "https://dev.azure.com";

	/**
	 * Format string for building the DevOps REST API request for the contents
	 * of a file in the GIT repository.
	 * <p>
	 * The positional parameters in the format string are as follows:
	 * <ul>
	 * <li>'%1$s' = DevOps Organization
	 * <li>'%2$s' = DevOps Project
	 * <li>'%3$s' = DevOps GIT Repository Id (GUID)
	 * </ul>
	 * <p>
	 * The following query parameters must also be included:
	 * <ul>
	 * <li>path = Base64 encoded path to the file in the GIT repository; https://www.base64encoder.io/java/
	 * <li>api-version = DevOps API version to use
	 * </ul>
	 */
	private static final String PathFormat = "%1$s/%2$s/_apis/git/repositories/%3$s/items";

	/**
	 * Name of the parameter used to identify the path to the diagram source
	 * file in the DevOps GIT repository.
	 */
	private static final String FilePathParam = "path";

	/** Name of the DevOps REST API version parameter. */
	private static final String ApiVersionParam = "api-version";

	/** DevOps REST API version to use. */
	private static final String ApiVersion = "6.1-preview.1";


	/** The name of the DevOps organization to access */
	private final String _organization;

	/** The name of the DevOps project to access */
	private final String _project;

	/** The GUID/UUID of the source DevOps repository */
	private final String _repositoryId;

	/** The Personal Access Token (PAT) used to access DevOps */
	private final String _pat;

	/** The authorization header to send to DevOps REST API */
	private final String _authHeader;

	/** Logging instance to use */
	private final Logger _logger;

	/** Used to make http requests against the DevOps API */
	private final HttpWrapper _httpClient;


	/**
	 * Initializes a new instance.
	 *
	 * @param logger	logging instance to use
	 * @since 1.0
	 */
	public DevOpsDiagram(Logger logger)
	{
		_logger = logger;

		// read environment variables
		_organization = System.getenv(ENV_ORGANIZATION);
		if (_organization == null || _organization.isEmpty()) {
			throw new ExceptionInInitializerError(String.format(ENV_ERROR_FORMAT, ENV_ORGANIZATION));
		}

		_project = System.getenv(ENV_PROJECT);
		if (_project == null || _project.isEmpty()) {
			throw new ExceptionInInitializerError(String.format(ENV_ERROR_FORMAT, ENV_PROJECT));
		}

		_repositoryId = System.getenv(ENV_REPOSITORY_ID);
		if (_repositoryId == null || _repositoryId.isEmpty()) {
			throw new ExceptionInInitializerError(String.format(ENV_ERROR_FORMAT, ENV_REPOSITORY_ID));
		}

		_pat = System.getenv(ENV_PAT);
		if (_pat == null || _pat.isEmpty()) {
			throw new ExceptionInInitializerError(String.format(ENV_ERROR_FORMAT, ENV_PAT));
		}

		// encode the Basic Authorization header value
		_authHeader = generateAuthHeaderValue();

		// create the HTTP Client
		_httpClient = new HttpWrapper(logger, DevOpsBaseUri);
	}


	/**
	 * Retrieves the diagram source text from the DevOps git repo and generates
	 * a PNG image from it.
	 *
	 * @param path path to the source file in DevOps repo.
	 * @return PNG image as a byte[]
	 * @since 1.0
	 */
	public byte[] generate(String path) throws IOException {
		String diagramSource = retrieveSourceText(path);

		byte[] image = generateDiagram(diagramSource);

		return image;
	}

	/**
	 * Creates the basic auth header value.
	 *
	 * @return	Base64 encoded Basic auth token value
	 * @since	1.0
	 */
	private String generateAuthHeaderValue() {
		String plainText = ":" + _pat;
		String header = null;

		try {
			header = "Basic " + Base64.getEncoder().encodeToString(
					plainText.getBytes(StandardCharsets.UTF_8.toString()));
		} catch (UnsupportedEncodingException ex) {
			// what to do here?
		}

		return header;
	}

	/**
	 * Retrieves the diagram source text from the DevOps repo.
	 *
	 * @param path	path to source file in the git repository
	 * @return source text used to generate the diagram
	 * @since 1.0
	 */
	private String retrieveSourceText(String path) {
		// build query params list
		Map<String, String> params = new HashMap<String, String>();
		// TODO: Use java.net.URI type to validate the path??
		params.put(FilePathParam, path);
		params.put(ApiVersionParam, ApiVersion);

		String formattedPath = String.format(PathFormat, _organization, _project, _repositoryId);

		// configure headers
		Map<String, String> headers = new HashMap<String, String>();
		headers.put("Authorization", _authHeader);

		String diagramContent = null;
		try {
			diagramContent = _httpClient.GetStringContent(formattedPath, headers, params);
		} catch (Exception ex) {
			_logger.log(Level.SEVERE, "Execpected error occurred", ex);
			diagramContent = String.format(ERROR_DIAGRAM_FORMAT, ex.toString());
		}

		return diagramContent;
	}

	/**
	 * Generates a PlantUML diagram image from source text.
	 *
	 * @param source	source text for the diagram
	 * @return a PNG byte array of the generated diagram.
	 * @since 1.0
	 */
	private byte[] generateDiagram(String source) throws IOException {
		ByteArrayOutputStream png = null;
		byte[] pngBytes = null;

		try {
			SourceStringReader reader = new SourceStringReader(source);
			png = new ByteArrayOutputStream();
			String desc = reader.generateImage(png);	// desc would contain what?
			pngBytes = png.toByteArray();
		} catch (IOException ex) {
			_logger.log(Level.SEVERE, "Unexpected error generating the diagram", ex);
			throw ex;
		} finally {
			png.close();
		}

		return pngBytes;
	}
}