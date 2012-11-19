package hudson.plugins.aironapp.integration.android;

/**
 * Date: 30.10.12
 * Time: 15:23
 *
 * @author MiG35
 */
public class Helper
{
	private static final String ANDROID_BASE_DIR = "androidBaseDir";

	public static String getBaseDir(String baseDir)
	{
		if (null == baseDir || baseDir.length() == 0)
		{
			return "";
		}

		baseDir = removeSlashInStartAndAddAtTheEndOfBaseDir(baseDir);

		checkBaseDirValue(baseDir);

		return baseDir;
	}

	private static String removeSlashInStartAndAddAtTheEndOfBaseDir(String baseDir)
	{
		baseDir = baseDir.endsWith("/") ? baseDir : baseDir + "/";

		while (baseDir.startsWith("/") || baseDir.startsWith("./"))
		{
			baseDir = baseDir.startsWith("/") ? baseDir.substring(1) : baseDir;
			baseDir = baseDir.startsWith("./") ? baseDir.substring(2) : baseDir;
		}

		return baseDir;
	}

	private static void checkBaseDirValue(String baseDir)
	{
		if (baseDir.contains("/../"))
		{
			throw new IllegalArgumentException(ANDROID_BASE_DIR + " can't have \"..\" symbols");
		}

		if (baseDir.startsWith("../"))
		{
			throw new IllegalArgumentException(ANDROID_BASE_DIR + " can't starts with ..\" symbol");
		}

		if (baseDir.contains("/./"))
		{
			throw new IllegalArgumentException(ANDROID_BASE_DIR + " can't have \".\" symbols");
		}

		if (baseDir.contains("/./"))
		{
			throw new IllegalArgumentException(ANDROID_BASE_DIR + " can't have \".\" symbols");
		}
	}

	public static String generateUpDirectoriesFromPath(String baseDir)
	{
		baseDir = baseDir.replaceAll("[^/]*[^./][^/]*|[.]+[^/]", "..");
		return baseDir;
	}
}
