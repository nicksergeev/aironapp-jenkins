package hudson.plugins.aironapp;

import hudson.AbortException;
import hudson.Extension;
import hudson.Launcher;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.BuildListener;
import hudson.plugins.aironapp.data.AfterBuildStepApplicationInfo;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.BuildStepMonitor;
import hudson.tasks.Publisher;
import hudson.tasks.Recorder;
import net.sf.json.JSONObject;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.StaplerRequest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


/**
 * Date: 05.10.12
 * Time: 15:37
 *
 * @author MiG35
 */
@SuppressWarnings("unused")
public class AfterBuildStepTask extends Recorder
{
	private static final String sTag = "AirOnApp. AfterBuild step. ";

	private final AfterBuildStepApplicationInfo mAfterBuildStepApplicationInfo;

	@DataBoundConstructor
	public AfterBuildStepTask(final String email, final String password, final String application, final String baseUrl,
			final String integrationExecutor)
	{
		mAfterBuildStepApplicationInfo =
				new AfterBuildStepApplicationInfo(email, password, application, baseUrl, integrationExecutor);
	}

	public String getEmail()
	{
		return mAfterBuildStepApplicationInfo.getEmail();
	}

	public String getPassword()
	{
		return mAfterBuildStepApplicationInfo.getPassword();
	}

	public String getApplication()
	{
		return mAfterBuildStepApplicationInfo.getApplication();
	}

	public String getBaseUrl()
	{
		return mAfterBuildStepApplicationInfo.getBaseUrl();
	}

	public String getIntegrationExecutor()
	{
		return mAfterBuildStepApplicationInfo.getIntegrationExecutor();
	}

	public BuildStepMonitor getRequiredMonitorService()
	{
		return BuildStepMonitor.NONE;
	}

	public List<String> getIntegrationExecutors()
	{
		return new ArrayList<String>(IntegrationExecutorFactory.sExecutors.keySet());
	}

	@Override
	public boolean perform(AbstractBuild build, Launcher launcher, BuildListener listener)
			throws InterruptedException, IOException
	{
		listener.getLogger().println(sTag + "Starts");

		if (launcher.isUnix())
		{
			listener.getLogger().println("Build node is compatible");

			if (AirOnAppHelper.airOnAppBuild(build))
			{
				listener.getLogger().println("This build will be with AirOnApp integration");

				try
				{
					IntegrationExecutor executor = IntegrationExecutorFactory
							.getIntegrationExecutor(mAfterBuildStepApplicationInfo.getIntegrationExecutor(),
									listener.getLogger());

					executor.performAfterBuildActions(build, launcher, listener, mAfterBuildStepApplicationInfo);
				}
				catch (Exception e)
				{
					listener.fatalError("Can't execute AirOnApp integration. See log message.");
					e.printStackTrace(listener.getLogger());

					throw new AbortException(e.getMessage());
				}
			}
			else
			{
				listener.getLogger().println("This is regular build");
			}
		}
		else
		{
			listener.error("Compatible only with Unix");
		}

		listener.getLogger().println(sTag + "Ends");

		return true;
	}


	@Extension
	public static final class DescriptorImpl extends BuildStepDescriptor<Publisher>
	{
		public DescriptorImpl()
		{
			super(AfterBuildStepTask.class);

			load();
		}

		public boolean isApplicable(Class<? extends AbstractProject> jobType)
		{
			return true;
		}

		@Override
		public String getDisplayName()
		{
			return "Post build AirOnApp task";
		}

		@Override
		public boolean configure(StaplerRequest req, JSONObject formData) throws FormException
		{
			save();
			return super.configure(req, formData);
		}
	}
}
