package ozosync.test;

import com.ebay.sdk.ApiContext;
import com.ebay.sdk.ApiCredential;
import com.ebay.soap.eBLBaseComponents.SiteCodeType;

public class TestUtil {

    public static ApiContext getApiContext() {
        ApiContext apiContext = new ApiContext();

        //set Api Token to access eBay Api Server
        ApiCredential cred = apiContext.getApiCredential();

        cred.seteBayToken("***REMOVED***");
        apiContext.setApiServerUrl("https://api.ebay.com/wsapi");

        apiContext.getApiLogging().setLogSOAPMessages(true);// This will log SOAP requests and responses

        apiContext.setSite(SiteCodeType.AUSTRALIA); // Set site to UK

        return apiContext;
    }
}
