package hudson.plugins.aironapp.integration;

import hudson.FilePath;
import hudson.Launcher;
import hudson.model.AbstractBuild;
import hudson.model.BuildListener;
import hudson.model.Result;
import hudson.model.Run;
import hudson.plugins.aironapp.IntegrationExecutor;
import hudson.plugins.aironapp.data.AfterBuildStepApplicationInfo;
import hudson.plugins.aironapp.utils.AirOnAppRequest;
import hudson.plugins.aironapp.utils.ExecutorHelper;

import java.io.File;
import java.io.FileInputStream;
import java.util.List;

/**
 * Date: 05.10.12
 * Time: 16:16
 *
 * @author MiG35
 */
public abstract class BaseIntegrationExecutor implements IntegrationExecutor
{
	public void performAfterBuildActions(AbstractBuild build, Launcher launcher, BuildListener listener,
			AfterBuildStepApplicationInfo afterBuildStepApplicationInfo) throws Exception
	{
		if (build.getResult() == Result.SUCCESS)
		{
			sentArtifact(build, launcher, listener, afterBuildStepApplicationInfo);
		}
	}

	protected void sentArtifact(AbstractBuild build, Launcher launcher, BuildListener listener,
			AfterBuildStepApplicationInfo afterBuildStepApplicationInfo) throws Exception
	{
		final String exportFileExtension = getExportFileExtension();

		String artifactName = build.getProject() != null ? build.getProject().getName() : null;
		if (null == artifactName || artifactName.length() == 0)
		{
			artifactName = "exportArtifactFile";
		}

		FilePath filePath = new FilePath(build.getWorkspace(), artifactName + exportFileExtension);

		for (Run.Artifact artifact : (List<Run.Artifact>) build.getArtifacts())
		{

			File file = artifact.getFile();
            listener.getLogger().println("Artifacts file: " + file.getName());
			if (file.getName().endsWith(exportFileExtension))
			{
				ExecutorHelper.executeLineCommand("echo found", launcher);
				filePath.copyFrom(new FileInputStream(file));

				AirOnAppRequest airOnAppRequest = buildAirOnAppRequest(build, afterBuildStepApplicationInfo, filePath);

				try
				{
					airOnAppRequest.send(launcher);
				}
				finally
				{
					filePath.delete();
				}
				return;
			}
		}

		// nothing found

		ExecutorHelper.executeLineCommand("echo \"Nothing found!\"", launcher);
		throw new IllegalStateException("Artifacts NOT found!!! Abort.");
	}

	protected AirOnAppRequest buildAirOnAppRequest(AbstractBuild build,
			AfterBuildStepApplicationInfo afterBuildStepApplicationInfo, FilePath fileToSend) throws Exception
	{
		AirOnAppRequest airOnAppRequest = new AirOnAppRequest(afterBuildStepApplicationInfo.getBaseUrl());

		airOnAppRequest.addParameter("data[email]", afterBuildStepApplicationInfo.getEmail());
		airOnAppRequest.addParameter("data[password]", afterBuildStepApplicationInfo.getPassword());
		airOnAppRequest.addParameter("data[application]", afterBuildStepApplicationInfo.getApplication());
		if (null != build.getDescription())
		{
			airOnAppRequest.addParameter("data[mark]", build.getDescription());
		}

		airOnAppRequest.setArtifact("data[ipa_file]", fileToSend.absolutize().getRemote());

		return airOnAppRequest;
	}

	protected abstract String getExportFileExtension();
}
