package ozosync.test;

import com.ebay.sdk.ApiContext;
import com.ebay.sdk.call.GetTokenStatusCall;
import com.ebay.sdk.call.GeteBayOfficialTimeCall;
import com.ebay.soap.eBLBaseComponents.TokenStatusType;
import java.util.Calendar;
import org.junit.Test;

public class EBaySdkHelloWorldTest {

    /*
You used the wrong URL: https://api.sandbox.ebay.com/ws/api.dll which is for eBay Trading XML call.
When making Trading SOAP call or using Trading SDKs,  you need to use https://api.sandbox.ebay.com/wsapi for sandbox environment or  https://api.ebay.com/wsapi for production.
     */

    @Test
    public void getEBayTime() {
        try {
            ApiContext apiContext = TestUtil.getApiContext();

            GeteBayOfficialTimeCall apiCall = new GeteBayOfficialTimeCall(apiContext);
            System.out.println("Begin to call eBay API, please wait ... ");
            Calendar cal = apiCall.geteBayOfficialTime();
            System.out.println("End to call eBay API, show call result ...");

            System.out.println("Official eBay Time : " + cal.getTime().toString());
        } catch (Exception e) {
            System.out.println("Fail to get eBay official time.");
            e.printStackTrace();
        }
    }

    @Test
    public void getTokenStatus() {
        try {
            ApiContext apiContext = TestUtil.getApiContext();

            GetTokenStatusCall apiCall = new GetTokenStatusCall(apiContext);
            System.out.println("Begin to call eBay API, please wait ... ");
            TokenStatusType tokenStatus = apiCall.getTokenStatus();
            System.out.println("End to call eBay API, show call result ...");

            System.out.println("token expiration time: " + tokenStatus.getExpirationTime().getTime().toString());
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.out.println(e.toString());
            //e.printStackTrace();
        }
    }
}
