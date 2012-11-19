package hudson.plugins.aironapp.data;

/**
 * Date: 10.10.12
 * Time: 9:19
 *
 * @author MiG35
 */
public class AfterBuildStepApplicationInfo
{
	private final String mEmail;
	private final String mPassword;
	private final String mApplication;
	private final String mBaseUrl;
	private final String mIntegrationExecutor;

	public AfterBuildStepApplicationInfo(final String email, final String password, final String application,
			final String baseUrl, final String integrationExecutor)
	{
		mEmail = email;
		mPassword = password;
		mApplication = application;
		String fixedBaseUrl = baseUrl.trim();
		if (fixedBaseUrl.length() > 0)
		{
			mBaseUrl = fixedBaseUrl.endsWith("/") ? fixedBaseUrl : fixedBaseUrl + "/";
		}
		else
		{
			mBaseUrl = fixedBaseUrl;
		}
		mIntegrationExecutor = integrationExecutor;
	}

	public String getEmail()
	{
		return mEmail;
	}

	public String getPassword()
	{
		return mPassword;
	}

	public String getApplication()
	{
		return mApplication;
	}

	public String getBaseUrl()
	{
		return mBaseUrl;
	}

	public String getIntegrationExecutor()
	{
		return mIntegrationExecutor;
	}
}
