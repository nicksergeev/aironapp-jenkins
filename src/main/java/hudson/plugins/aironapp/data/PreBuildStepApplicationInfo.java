package hudson.plugins.aironapp.data;

import org.jfree.text.TextUtilities;

/**
 * Date: 09.10.12
 * Time: 16:52
 *
 * @author MiG35
 */
public class PreBuildStepApplicationInfo
{
	private final String mApplication;
	private final String mBaseUrl;
	private final String mLibUrl;
	private final String mIntegrationExecutor;
	private String mBaseDir;

	public PreBuildStepApplicationInfo(final String application, final String baseUrlParam, final String libUrl,
			final String integrationExecutor, final String baseDir)
	{
		mApplication = application;
		String baseUrl = baseUrlParam;
		if(null == baseUrl)
		{
			baseUrl = "";
		}
		String fixedBaseUrl = baseUrl.trim();
		if (fixedBaseUrl.length() > 0)
		{
			mBaseUrl = fixedBaseUrl.endsWith("/") ? fixedBaseUrl : fixedBaseUrl + "/";
		}
		else
		{
			mBaseUrl = fixedBaseUrl;
		}
		mLibUrl = libUrl;
		mIntegrationExecutor = integrationExecutor;
		mBaseDir = baseDir;
	}

	public String getApplication()
	{
		return mApplication;
	}

	public String getBaseUrl()
	{
		return mBaseUrl;
	}

	public String getLibUrl()
	{
		return mLibUrl;
	}

	public String getIntegrationExecutor()
	{
		return mIntegrationExecutor;
	}

	public String getBaseDir()
	{
		return mBaseDir;
	}
}
