package hudson.plugins.aironapp.utils;

import hudson.Launcher;
import org.apache.commons.httpclient.NameValuePair;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author MiG35
 */
public class AirOnAppRequest extends BaseRequest
{
	private static final String sSubUrl = "api/1.0/http/uploadIPA";

    public AirOnAppRequest(String baseUrl)
    {
        super(baseUrl + sSubUrl);
    }
}
