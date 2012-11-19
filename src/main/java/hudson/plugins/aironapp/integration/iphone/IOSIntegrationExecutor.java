package hudson.plugins.aironapp.integration.iphone;

import hudson.FilePath;
import hudson.Launcher;
import hudson.model.AbstractBuild;
import hudson.model.BuildListener;
import hudson.model.Run;
import hudson.plugins.aironapp.data.AfterBuildStepApplicationInfo;
import hudson.plugins.aironapp.data.PreBuildStepApplicationInfo;
import hudson.plugins.aironapp.integration.BaseIntegrationExecutor;
import hudson.plugins.aironapp.utils.DSYMUploadRequest;
import hudson.plugins.aironapp.utils.ExecutorHelper;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

/**
 * Date: 04.10.12
 * Time: 12:22
 *
 * @author MiG35
 */
public class IOSIntegrationExecutor extends BaseIntegrationExecutor
{
	private static final String sTag = "AirOnApp builder: iPhone ";

	private static final String buildFolder = "aironappbuild";


	public void performPreBuildActions(AbstractBuild build, Launcher launcher, BuildListener listener,
			PreBuildStepApplicationInfo preBuildStepApplicationInfo) throws Exception
	{
		checkoutFreshCopyOfAirOnAppBuild(build, launcher, listener, preBuildStepApplicationInfo);
		addAironappToProject(build, launcher, listener, preBuildStepApplicationInfo.getBaseDir());
	}

	private void addAironappToProject(AbstractBuild build, Launcher launcher, BuildListener listener, String baseDir)
			throws IOException, InterruptedException
	{
		listener.getLogger().println(sTag + "try to change application class...");

		String workspace = build.getWorkspace().getRemote() + "/";

		String shFile = workspace + buildFolder + "/addlib.sh ";
		FilePath shFilePath = new FilePath(build.getWorkspace(), shFile.trim());
		listener.getLogger().println(sTag + "trying to modify permissions on file at path: " + shFilePath);
		shFilePath.chmod(0777);

		listener.getLogger().println(sTag + "Executing command: " + "sh " + shFile + workspace + " " +  baseDir);

		ExecutorHelper.executeLineCommand("sh " + shFile + "\"" + workspace + "\"" +  " " + baseDir, launcher);

		listener.getLogger().println(sTag + "try to change project... Success");
	}

	private void checkoutFreshCopyOfAirOnAppBuild(AbstractBuild build, Launcher launcher, BuildListener listener,
			PreBuildStepApplicationInfo preBuildStepApplicationInfo) throws Exception
	{
		listener.getLogger().println(sTag + "Check Out step...");

		executeDownload(build, preBuildStepApplicationInfo);

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

	@Override
	protected void sentArtifact(AbstractBuild build, Launcher launcher, BuildListener listener,
			AfterBuildStepApplicationInfo afterBuildStepApplicationInfo) throws Exception
	{
		super.sentArtifact(build, launcher, listener, afterBuildStepApplicationInfo);

		listener.getLogger().println("Sending dsym..");
		String dsymExtension = ".dSYM.zip";
		String artifactName = "build" + dsymExtension;

		FilePath filePath = new FilePath(build.getWorkspace(), artifactName);

		for (Run.Artifact artifact : (List<Run.Artifact>) build.getArtifacts())
		{

			File file = artifact.getFile();
			listener.getLogger().println("Artifacts file: " + file.getName());
			if (file.getName().endsWith(dsymExtension))
			{
				listener.getLogger().print("Dsym found!");

				filePath.copyFrom(new FileInputStream(file));

				DSYMUploadRequest dsymUploadRequest =
						sendUploadDsymRequest(build, afterBuildStepApplicationInfo, filePath);

				try
				{
					dsymUploadRequest.send(launcher);
				}
				finally
				{
					filePath.delete();
				}
				return;
			}
		}

		// nothing found
		listener.getLogger().println("Dsym upload failed: file `not found");

	}

	protected DSYMUploadRequest sendUploadDsymRequest(AbstractBuild build,
			AfterBuildStepApplicationInfo afterBuildStepApplicationInfo, FilePath fileToSend) throws Exception
	{
		DSYMUploadRequest dsymUploadRequest = new DSYMUploadRequest(afterBuildStepApplicationInfo.getBaseUrl());

		dsymUploadRequest.addParameter("data[email]", afterBuildStepApplicationInfo.getEmail());
		dsymUploadRequest.addParameter("data[password]", afterBuildStepApplicationInfo.getPassword());
		dsymUploadRequest.addParameter("data[application]", afterBuildStepApplicationInfo.getApplication());
		if (null != build.getDescription())
		{
			dsymUploadRequest.addParameter("data[mark]", build.getDescription());
		}

		dsymUploadRequest.setArtifact("data[dsym_file]", fileToSend.absolutize().getRemote());

		return dsymUploadRequest;
	}

	@Override
	protected String getExportFileExtension()
	{
		return ".ipa";
	}
}
