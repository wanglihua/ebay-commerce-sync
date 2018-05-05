package ozosync.test;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;
import ozosync.common.SpringConfigLocation;
import ozosync.job.payment.PaymentDownloader;

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

        paymentDownloader.downloadAccountPaymentList(api_username, api_password, signature);
    }
}
