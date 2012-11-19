package hudson.plugins.aironapp;

import hudson.model.AbstractBuild;
import hudson.scm.ChangeLogSet;

/**
 * Date: 05.10.12
 * Time: 15:45
 *
 * @author MiG35
 */
public class AirOnAppHelper
{
	private static final String sAirOnAppIntegrationCommitMessage = "READY";


	public static boolean airOnAppBuild(AbstractBuild build)
	{
		ChangeLogSet changes = build.getChangeSet();
		if (!changes.isEmptySet())
		{
			for (Object changeObject : changes.getItems())
			{
				ChangeLogSet.Entry change = (ChangeLogSet.Entry) changeObject;

				String commitMessage = change.getMsg();

				if (null != commitMessage)
				{
					if (commitMessage.startsWith(sAirOnAppIntegrationCommitMessage))
					{
						return true;
					}
				}
			}
		}

		return true; // todo fix
	}

}
