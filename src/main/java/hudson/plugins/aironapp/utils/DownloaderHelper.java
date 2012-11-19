package hudson.plugins.aironapp.utils;

import hudson.Launcher;

import java.io.IOException;
import java.io.PrintStream;

/**
 * Date: 03.10.12
 * Time: 18:50
 *
 * @author MiG35
 */
public class DownloaderHelper
{
	public static void executeDownload(String urlString, String pathString, Launcher launcher, PrintStream logger)
			throws IOException, InterruptedException
	{
		logger.println("Downloading...");

		ExecutorHelper.executeLineCommand("pwd", launcher);

		ExecutorHelper.executeLineCommand("rm -rf " + pathString, launcher);
		ExecutorHelper.executeLineCommand("wget " + urlString, launcher);

		ExecutorHelper.executeLineCommand("mkdir -p " + getDirName(pathString), launcher);
		ExecutorHelper.executeLineCommand("mv ./" + getFileName(pathString) + " " +
				pathString, launcher);

		logger.println("Downloading... Success");
	}

	private static String getDirName(String pathString)
	{
		int index = pathString.lastIndexOf("/");

		if (-1 != index)
		{
			return pathString.substring(0, index - 1);
		}
		return pathString;
	}

	private static String getFileName(String urlString)
	{
		int index = urlString.lastIndexOf("/");

		if (-1 != index)
		{
			return urlString.substring(index + 1);
		}
		return urlString;
	}
}
