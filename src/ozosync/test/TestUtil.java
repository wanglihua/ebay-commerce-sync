package ozosync.test;

import com.ebay.sdk.ApiAccount;
import com.ebay.sdk.ApiContext;
import com.ebay.sdk.ApiCredential;
import com.ebay.soap.eBLBaseComponents.SiteCodeType;

public class TestUtil {

    public static ApiContext getApiContext() {
        ApiContext apiContext = new ApiContext();

        //set Api Token to access eBay Api Server
        ApiCredential cred = apiContext.getApiCredential();

        ApiAccount apiAccount = new ApiAccount();
        apiAccount.setApplication("***REMOVED***");
        apiAccount.setCertificate("***REMOVED***");
        apiAccount.setDeveloper("***REMOVED***");

        cred.setApiAccount(apiAccount);
        cred.seteBayToken("***REMOVED***");
        apiContext.setApiServerUrl("https://api.ebay.com/wsapi");

        apiContext.getApiLogging().setLogSOAPMessages(true);// This will log SOAP requests and responses

        apiContext.setSite(SiteCodeType.AUSTRALIA); // Set site to UK

        return apiContext;
    }
}

/*

AppID
***REMOVED***

CertID
***REMOVED***

DevID
***REMOVED***

Token
***REMOVED***


 */