package ozosync.test;

import com.ebay.sdk.ApiContext;
import com.ebay.sdk.ApiCredential;

public class TestUtil {

    public static ApiContext getApiContext() {
        ApiContext apiContext = new ApiContext();

        //set Api Token to access eBay Api Server
        ApiCredential cred = apiContext.getApiCredential();

        cred.seteBayToken("***REMOVED***");
        apiContext.setApiServerUrl("https://api.ebay.com/wsapi");

        return apiContext;
    }
}
