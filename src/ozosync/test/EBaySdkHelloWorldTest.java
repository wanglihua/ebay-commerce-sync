package ozosync.test;

import com.ebay.sdk.ApiContext;
import com.ebay.sdk.call.GeteBayOfficialTimeCall;
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

            System.out.print("\n");
            System.out.print("+++++++++++++++++++++++++++++++++++++++\n");
            System.out.print("+ Welcome to eBay SDK for Java Sample +\n");
            System.out.print("+  - HelloWorld                   +\n");
            System.out.print("+++++++++++++++++++++++++++++++++++++++\n");
            System.out.print("\n");

            ApiContext apiContext = TestUtil.getApiContext();

            // [Step 2] Create call object and execute the call
            GeteBayOfficialTimeCall apiCall = new GeteBayOfficialTimeCall(apiContext);
            System.out.println("Begin to call eBay API, please wait ... ");
            Calendar cal = apiCall.geteBayOfficialTime();
            System.out.println("End to call eBay API, show call result ...");

            // [Setp 3] Handle the result returned
            System.out.println("Official eBay Time : " + cal.getTime().toString());
        } catch (Exception e) {
            System.out.println("Fail to get eBay official time.");
            e.printStackTrace();
        }

    }
}
