package net.bati.guilib;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CommonInitializer implements ModInitializer {
	public static final String ID = "bati-guilib";
	public static final Logger LOGGER = LoggerFactory.getLogger(ID);

	private static boolean loadExamples = true;
	@Override
	public void onInitialize() {
		initializeExamples();

		LOGGER.info("[COMMON] Initialized.");
	}


	private void initializeExamples() {
		if(!loadExamples) return;
	}

	public void shouldLoadExamples(boolean load) {
		loadExamples = load;
	}
}
