package hudson.plugins.aironapp.utils;

import hudson.Launcher;
import org.apache.commons.httpclient.NameValuePair;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: exeshneg
 * Date: 08.11.12
 * Time: 16:37
 * To change this template use File | Settings | File Templates.
 */
public class BaseRequest {
    protected String mUrl;

    protected String mArtifactName;
    protected String mArtifactPath;
    protected List<NameValuePair> mParameters;


    public BaseRequest(String url)
    {
        mUrl = url;
        mParameters = new ArrayList<NameValuePair>();
    }

    public void addParameter(String name, String value)
    {
        mParameters.add(new NameValuePair(name, value));
    }

    public void setArtifact(String fileName, String artifactPath)
    {
        mArtifactName = fileName;
        mArtifactPath = artifactPath;
    }
    /**
     * this method opens socket and send data there. Then read answer.
     *
     * @param launcher
     * @throws java.io.IOException
     */
    public void send(Launcher launcher) throws IOException, InterruptedException
    {
        ExecutorHelper.executeLineCommand("chmod +x /home/build/workspace/AirOnAppTest-An/bin", launcher);

        StringBuilder parametersAsString = new StringBuilder();

        parametersAsString.append("curl -f ");
        for (NameValuePair param : mParameters)
        {
            parametersAsString.append(" --form \"");
            parametersAsString.append(param.getName());
            parametersAsString.append("=");
            parametersAsString.append(param.getValue());
            parametersAsString.append("\" ");
        }
        parametersAsString.append(" --form \"");
        parametersAsString.append(mArtifactName);
        parametersAsString.append("=@");
        parametersAsString.append(mArtifactPath);
        parametersAsString.append("\" ");

        parametersAsString.append(mUrl);

        int resultCode = ExecutorHelper.executeLineCommandWithResult(parametersAsString.toString().trim(), launcher);
        if (0 != resultCode)
        {
            throw new IOException("Can't execute");
        }
    }
}
