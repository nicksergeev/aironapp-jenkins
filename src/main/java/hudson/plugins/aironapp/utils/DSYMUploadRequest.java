package hudson.plugins.aironapp.utils;

/**
 * Created with IntelliJ IDEA.
 * User: exeshneg
 * Date: 08.11.12
 * Time: 16:40
 * To change this template use File | Settings | File Templates.
 */
public class DSYMUploadRequest extends BaseRequest
{
    private static final String sSubUrl = "api/1.0/http/uploadDsym";

    public DSYMUploadRequest(String baseUrl)
    {
        super(baseUrl + sSubUrl);
    }
}
