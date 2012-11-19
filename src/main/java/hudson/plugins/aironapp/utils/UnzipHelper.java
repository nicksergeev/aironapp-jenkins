package hudson.plugins.aironapp.utils;

import hudson.Launcher;

import java.io.IOException;

/**
 * Date: 03.10.12
 * Time: 19:40
 *
 * @author MiG35
 */
public class UnzipHelper
{
	public static void unzipFile(Launcher launcher, String zipFileName, String outDir, boolean deleteZipFileOnExit)
			throws IOException, InterruptedException
	{
		ExecutorHelper.executeLineCommand("unzip " + zipFileName + " -d " + outDir, launcher);

		if (deleteZipFileOnExit)
		{
			ExecutorHelper.executeLineCommand("rm -f " + zipFileName, launcher);
		}
	}
}
