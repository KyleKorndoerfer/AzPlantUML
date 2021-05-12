package com.kykosoft.azPlantUML;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.sourceforge.plantuml.SourceStringReader;

/**
 * Generates a PlantUML diagram from source text.
 *
 * @author	S. Kyle Korndoerfer
 * @version 1.0
 * @since	1.0
 */
public class DiagramGenerator {

	/**
	 * Generates a PlantUML diagram as a png from source text.
	 *
	 * @param source	text contents of the diagram
	 * @param logger	logger instance to use
	 * @return generated diagram as a png byte array
	 * @since 1.0
	 */
	public static byte[] generate(String source, Logger logger) {
		logger.info("Received a reuqest to generate an image");
		SourceStringReader reader = new SourceStringReader(source);

		try (ByteArrayOutputStream png = new ByteArrayOutputStream()) {
			String desc = reader.generateImage(png);	// desc would contain what?
			byte[] pngBytes = png.toByteArray();

			logger.info("Image successfully generated from the source content");

			return pngBytes;
		} catch (IOException ex) {
			logger.log(
				Level.SEVERE,
				"Unable to generate a diagram image from the supplied data",
				ex);
		}

		return null;
	}
}
