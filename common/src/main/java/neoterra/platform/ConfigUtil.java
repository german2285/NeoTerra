package neoterra.platform;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import dev.architectury.injectables.annotations.ExpectPlatform;
import neoterra.NTCommon;

public class ConfigUtil {
	public static final Path RTF_CONFIG_PATH = getConfigPath().resolve(NTCommon.MOD_ID);
	public static final Path LEGACY_CONFIG_PATH = getConfigPath().resolve(NTCommon.LEGACY_MOD_ID);
	
	public static Path rtf(String path) {
		return RTF_CONFIG_PATH.resolve(path);
	}
	
	public static Path legacy(String path) {
		return LEGACY_CONFIG_PATH.resolve(path);
	}
	
	@ExpectPlatform
	public static Path getConfigPath() {
		throw new IllegalStateException();
	}
	
	static {
		if(!Files.exists(RTF_CONFIG_PATH)) {
			try {
				Files.createDirectory(RTF_CONFIG_PATH);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
