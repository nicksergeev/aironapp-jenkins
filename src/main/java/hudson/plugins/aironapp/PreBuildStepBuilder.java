package hudson.plugins.aironapp;

import hudson.AbortException;
import hudson.Extension;
import hudson.Launcher;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.BuildListener;
import hudson.plugins.aironapp.data.PreBuildStepApplicationInfo;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.BuildStepMonitor;
import hudson.tasks.Builder;
import net.sf.json.JSONObject;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.StaplerRequest;

import java.util.ArrayList;
import java.util.List;

/**
 * @author MiG35
 */
@SuppressWarnings("unused")
public class PreBuildStepBuilder extends Builder
{
	private static final String sTag = "AirOnApp. PreBuild step. ";

	private final PreBuildStepApplicationInfo mPreBuildStepApplicationInfo;

	@DataBoundConstructor
	public PreBuildStepBuilder(final String application, final String baseUrl, final String libUrl,
			final String baseDir, final String integrationExecutor)
	{
		mPreBuildStepApplicationInfo =
				new PreBuildStepApplicationInfo(application, baseUrl, libUrl, integrationExecutor, baseDir);
	}

	public String getApplication()
	{
		return mPreBuildStepApplicationInfo.getApplication();
	}

	public String getBaseUrl()
	{
		return mPreBuildStepApplicationInfo.getBaseUrl();
	}

	public String getLibUrl()
	{
		return mPreBuildStepApplicationInfo.getLibUrl();
	}

	public String getIntegrationExecutor()
	{
		return mPreBuildStepApplicationInfo.getIntegrationExecutor();
	}

	public List<String> getIntegrationExecutors()
	{
		return new ArrayList<String>(IntegrationExecutorFactory.sExecutors.keySet());
	}

	public String getBaseDir()
	{
		return mPreBuildStepApplicationInfo.getBaseDir();
	}

	public BuildStepMonitor getRequiredMonitorService()
	{
		return BuildStepMonitor.NONE;
	}

	@Override
	public boolean perform(AbstractBuild build, Launcher launcher, BuildListener listener) throws AbortException
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
							.getIntegrationExecutor(mPreBuildStepApplicationInfo.getIntegrationExecutor(),
									listener.getLogger());

					executor.performPreBuildActions(build, launcher, listener, mPreBuildStepApplicationInfo);
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

	// Overridden for better type safety.
	// If your plugin doesn't really define any property on Descriptor,
	// you don't have to do this.
	@Override
	public DescriptorImpl getDescriptor()
	{
		return (DescriptorImpl) super.getDescriptor();
	}

	/**
	 * Descriptor for {@link PreBuildStepBuilder}. Used as a singleton.
	 * The class is marked as public so that it can be accessed from views.
	 * <p/>
	 * <p/>
	 * See <tt>src/main/resources/hudson/plugins/hello_world/PreBuildStepBuilder/*.jelly</tt>
	 * for the actual HTML fragment for the configuration screen.
	 */
	@Extension // This indicates to Jenkins that this is an implementation of an extension point.
	public static final class DescriptorImpl extends BuildStepDescriptor<Builder>
	{
		public boolean isApplicable(Class<? extends AbstractProject> aClass)
		{
			// Indicates that this builder can be used with all kinds of project types
			return true;
		}

		/**
		 * This human readable name is used in the configuration screen.
		 */
		public String getDisplayName()
		{
			return "Add AirOnApp integration";
		}

		@Override
		public boolean configure(StaplerRequest req, JSONObject formData) throws FormException
		{
			save();
			return super.configure(req, formData);
		}
	}
}

