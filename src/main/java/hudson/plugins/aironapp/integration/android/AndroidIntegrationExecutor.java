package hudson.plugins.aironapp.integration.android;

import hudson.FilePath;
import hudson.Launcher;
import hudson.model.AbstractBuild;
import hudson.model.BuildListener;
import hudson.plugins.aironapp.data.PreBuildStepApplicationInfo;
import hudson.plugins.aironapp.integration.BaseIntegrationExecutor;
import hudson.plugins.aironapp.utils.ExecutorHelper;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;

/**
 * Date: 04.10.12
 * Time: 12:16
 *
 * @author MiG35
 */
public class AndroidIntegrationExecutor extends BaseIntegrationExecutor
{
	private static final String sTag = "AirOnApp. Android. ";

	private static final String sAirOnAppLibName = "AirOnAppAndroidLib";
	private static final String sAirOnAppLibPath = "./" + sAirOnAppLibName + "/";
	private static final String sAirOnAppConfFileName = "aironapp_conf.xml";

	private String mBaseDir;

	public void performPreBuildActions(AbstractBuild build, Launcher launcher, BuildListener listener,
			PreBuildStepApplicationInfo preBuildStepApplicationInfo) throws Exception
	{
		mBaseDir = Helper.getBaseDir(preBuildStepApplicationInfo.getBaseDir());

		listener.getLogger().println(sTag + "BaseDir is " + mBaseDir);

		checkoutFreshCopyOfAirOnAppBuild(build, launcher, listener, preBuildStepApplicationInfo);
		prebuildProjectChanges(build, launcher, listener, preBuildStepApplicationInfo);
	}

	private void checkoutFreshCopyOfAirOnAppBuild(AbstractBuild build, Launcher launcher, BuildListener listener,
			PreBuildStepApplicationInfo preBuildStepApplicationInfo) throws Exception
	{
		listener.getLogger().println(sTag + "Check Out step...");

		executeDownload(build, preBuildStepApplicationInfo);

		addLocalPropertiesToAirOnAppLibrary(build, launcher, listener);

		listener.getLogger().println(sTag + "Check Out step... Success");
	}

	private void executeDownload(AbstractBuild build, PreBuildStepApplicationInfo preBuildStepApplicationInfo)
			throws IOException, InterruptedException
	{
		URL url = new URL(preBuildStepApplicationInfo.getLibUrl());
		URLConnection connection = url.openConnection();
		connection.connect();

		build.getWorkspace().unzipFrom(connection.getInputStream());
	}

	private void addLocalPropertiesToAirOnAppLibrary(AbstractBuild build, Launcher launcher, BuildListener listener)
			throws Exception
	{
		String localPropertiesName = "local.properties";

		FilePath airOnAppLibFilePath = new FilePath(build.getWorkspace(), sAirOnAppLibPath + localPropertiesName);
		if (airOnAppLibFilePath.exists())
		{
			airOnAppLibFilePath.delete();
		}

		String hundsonHomePath = build.getEnvironment(launcher.getListener()).get("HUDSON_HOME");

		FilePath localPropertiesFilePath =
				new FilePath(airOnAppLibFilePath, hundsonHomePath + "/android/local_1.8.properties");
		airOnAppLibFilePath.copyFrom(localPropertiesFilePath);
	}

	private void prebuildProjectChanges(final AbstractBuild build, Launcher launcher, final BuildListener listener,
			PreBuildStepApplicationInfo preBuildStepApplicationInfo) throws IOException, InterruptedException
	{
		listener.getLogger().println(sTag + "PreBuild step starts");

		changeApplicationClass(build, launcher, listener);

		addAirOnAppConfFile(build, listener, preBuildStepApplicationInfo);

		listener.getLogger().println(sTag + "PreBuild step ends");
	}

	private void changeApplicationClass(AbstractBuild build, Launcher launcher, BuildListener listener)
			throws IOException, InterruptedException
	{
		listener.getLogger().println(sTag + "try to change application class...");

		String workspace = build.getWorkspace().getRemote() + "/";

		String shFile = workspace + sAirOnAppLibName + "/application_extender.sh ";
		FilePath shFilePath = new FilePath(build.getWorkspace(), shFile.trim());
		shFilePath.chmod(777);
		shFilePath = new FilePath(build.getWorkspace(), workspace + sAirOnAppLibName + "/build_properties_changer.sh");
		shFilePath.chmod(777);

		ExecutorHelper.executeLineCommand(shFile + workspace + mBaseDir + " " + workspace + sAirOnAppLibName + " " +
				sAirOnAppLibName + " " + workspace, launcher);

		listener.getLogger().println(sTag + "try to change application class... Success");
	}

	private void addAirOnAppConfFile(AbstractBuild build, BuildListener listener,
			PreBuildStepApplicationInfo preBuildStepApplicationInfo) throws IOException, InterruptedException
	{
		listener.getLogger().println(sTag + "try add conf file...");

		FilePath confFilePath = generateConfFile(build);
		FilePath backFilePath = new FilePath(build.getWorkspace(), sAirOnAppLibPath + sAirOnAppConfFileName);

		final String configuration = backFilePath.readToString();
		listener.getLogger().println("Configuration: " + configuration);

		String newConfiguration = String.format(configuration, preBuildStepApplicationInfo.getApplication(),
				preBuildStepApplicationInfo.getBaseUrl());
		listener.getLogger().println("New configuration: " + newConfiguration);

		confFilePath.write(newConfiguration, "UTF-8");

		listener.getLogger().println(sTag + "try add conf file... Success");
	}

	private FilePath generateConfFile(AbstractBuild build) throws IOException, InterruptedException
	{
		FilePath valuesFilePath = new FilePath(build.getWorkspace(), mBaseDir + "res/values").absolutize();
		String confFileName = "aironapp_inetegration_configuration_file";

		return new FilePath(valuesFilePath, confFileName + ".xml");
	}

	@Override
	protected String getExportFileExtension()
	{
		return ".apk";
	}
}
