package ozosync.test;

import java.util.HashMap;
import java.util.Map;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;
import ozosync.common.SpringConfigLocation;
import ozosync.domain.PaypalAccountInfo;
import ozosync.job.payment.PaymentDownloader;
import urn.ebay.api.PayPalAPI.GetTransactionDetailsReq;
import urn.ebay.api.PayPalAPI.GetTransactionDetailsRequestType;
import urn.ebay.api.PayPalAPI.GetTransactionDetailsResponseType;
import urn.ebay.api.PayPalAPI.PayPalAPIInterfaceServiceService;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(locations = {SpringConfigLocation.configLocation})
public class PaymentDownloaderTest {

    @Autowired
    private PaymentDownloader paymentDownloader;

    @Test
    @Transactional
    public void paymentListDownload() {
        paymentDownloader.download();
    }

    @Test
    @Transactional
    public void accountPaymentListDownload() throws Exception {
        /*
API username：support_api1.ozoestore.com.au
API password：4VZZNR4CK3GG9GHW
Signature：AB0YTf4hFkAG5QvPtq2A-oOChxRNATZl570inmmXuRssw4HDbkR5w-8i
         */

        String api_username = "support_api1.ozoestore.com.au";
        String api_password = "4VZZNR4CK3GG9GHW";
        String signature = "AB0YTf4hFkAG5QvPtq2A-oOChxRNATZl570inmmXuRssw4HDbkR5w-8i";

        PaypalAccountInfo paypalAccountInfo = new PaypalAccountInfo();
        paypalAccountInfo.setApiUserName(api_username);
        paypalAccountInfo.setApiPassword(api_password);
        paypalAccountInfo.setSignature(signature);
        paypalAccountInfo.setId(1L);
        paypalAccountInfo.setPaypalAccount("support@ozoestore.com.au");

        paymentDownloader.downloadAccountPaymentList(paypalAccountInfo);
    }

    @Test
    @Transactional
    public void getTransactionDetail() throws Exception {
        String api_username = "support_api1.ozoestore.com.au";
        String api_password = "4VZZNR4CK3GG9GHW";
        String signature = "AB0YTf4hFkAG5QvPtq2A-oOChxRNATZl570inmmXuRssw4HDbkR5w-8i";

        // PayPalAPIInterfaceServiceService
        Map<String, String> configMap = new HashMap<>();
        configMap.put("mode", "live");
        configMap.put("acct1.UserName", api_username);
        configMap.put("acct1.Password", api_password);
        configMap.put("acct1.Signature", signature);

        PayPalAPIInterfaceServiceService service = new PayPalAPIInterfaceServiceService(configMap);

        GetTransactionDetailsReq getTransactionDetailsReq = new GetTransactionDetailsReq();
        GetTransactionDetailsRequestType requestType = new GetTransactionDetailsRequestType();
        requestType.setTransactionID("19J52487X0420101R");
        getTransactionDetailsReq.setGetTransactionDetailsRequest(requestType);
        GetTransactionDetailsResponseType responseType = service.getTransactionDetails(getTransactionDetailsReq);

        int i = 1 + 1;

    }
}
