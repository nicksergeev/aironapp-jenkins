package hudson.plugins.aironapp;

import hudson.Launcher;
import hudson.model.AbstractBuild;
import hudson.model.BuildListener;
import hudson.plugins.aironapp.data.AfterBuildStepApplicationInfo;
import hudson.plugins.aironapp.data.PreBuildStepApplicationInfo;

/**
 * Date: 04.10.12
 * Time: 12:08
 *
 * @author MiG35
 */
public interface IntegrationExecutor
{
	void performPreBuildActions(AbstractBuild build, Launcher launcher, BuildListener listener,
			PreBuildStepApplicationInfo preBuildStepApplicationInfo) throws Exception;

	void performAfterBuildActions(AbstractBuild build, Launcher launcher, BuildListener listener,
			AfterBuildStepApplicationInfo afterBuildStepApplicationInfo) throws Exception;
}
