package hudson.plugins.aironapp.utils;

import hudson.Launcher;

import java.io.IOException;
import java.io.PrintStream;

/**
 * Date: 04.10.12
 * Time: 11:48
 *
 * @author MiG35
 */
public class ExecutorHelper
{
	public static void executeLineCommand(String command, Launcher launcher) throws IOException, InterruptedException
	{
		int resultCode = executeLineCommandWithResult(command, launcher);
	}

	public static int executeLineCommandWithResult(String command, Launcher launcher)
			throws IOException, InterruptedException
	{
		Launcher.ProcStarter procCmd = launcher.new ProcStarter();
		procCmd.cmdAsSingleString(command);
		procCmd.stdout(launcher.getListener());

		int resultCode = launcher.launch(procCmd).join();

		PrintStream logger = launcher.getListener().getLogger();
		if (resultCode != 0)
		{
			logger.println(String.format("Script executed. The exit code is %s.", resultCode));
		}
		else
		{
			logger.println("Script executed successfully.");
		}

		return resultCode;
	}
}
