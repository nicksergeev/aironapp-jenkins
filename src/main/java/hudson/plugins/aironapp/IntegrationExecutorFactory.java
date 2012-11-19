package hudson.plugins.aironapp;

import hudson.plugins.aironapp.integration.android.AndroidIntegrationExecutor;
import hudson.plugins.aironapp.integration.iphone.IOSIntegrationExecutor;

import java.io.PrintStream;
import java.util.*;

/**
 * Date: 04.10.12
 * Time: 12:08
 *
 * @author MiG35
 */
public class IntegrationExecutorFactory
{
	public final static Map<String, Class<? extends IntegrationExecutor>> sExecutors;

	public static String getAndroidExecutorName()
	{
		return "Android";
	}

	public static String getiOSExecutorName()
	{
		return "iOS";
	}

	static
	{
		HashMap<String, Class<? extends IntegrationExecutor>> executors =
				new HashMap<String, Class<? extends IntegrationExecutor>>();

		executors.put(getAndroidExecutorName(), AndroidIntegrationExecutor.class);
		executors.put(getiOSExecutorName(), IOSIntegrationExecutor.class);

		sExecutors = Collections.unmodifiableMap(executors);
	}

	public static Collection<String> getExecutors()
	{
		return sExecutors.keySet();
	}

	public static IntegrationExecutor getIntegrationExecutor(String executorName, PrintStream logger)
			throws IllegalAccessException, InstantiationException
	{
		IntegrationExecutor executor = null;

		Set<String> keys = sExecutors.keySet();

		for (String key : keys)
		{
			if (key.equalsIgnoreCase(executorName))
			{
				logger.println(String.format("This is %s project", key));

				executor = sExecutors.get(key).newInstance();
				break;
			}
		}

		if (null == executor)
		{
			throw new RuntimeException("Can't detect AirOnApp executor for this kind of project");
		}

		return executor;
	}
}
