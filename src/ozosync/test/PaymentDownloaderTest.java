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
ebay.au168@gmail.com
ASQYgv1ltlY5F5B1DxBqD4uoRplytCXNrmYj9GS7AFX0zxTIwGwh9E3NMu3jGFptVPzWjVKkVLylqk6g
EE4Vf1Xq-oD3_0IhayJ2n5mMhOlf-fdmAFdWiR6kb5Xyci1YgAq2YdkAxUhRaQVPR7lYhLO1b-Y07_HT

support@ozoestore.com.au
AYCCYjJ50pKm27CNgQCsNHEYpoiNS9FW6gYpHKF7KR0PmXKyMvV127ld0t0hUlvJtXCMhwKsDiW0x6rw
EDw7YOVHVzvMjxxR3UgFCYPyChKE3LVh69CnsqtsI07bPsIOtltvsBeAkolvDRTFP4_zOZuEGaU5VWtY
         */

        String clientId = "AYCCYjJ50pKm27CNgQCsNHEYpoiNS9FW6gYpHKF7KR0PmXKyMvV127ld0t0hUlvJtXCMhwKsDiW0x6rw";
        String secret = "EDw7YOVHVzvMjxxR3UgFCYPyChKE3LVh69CnsqtsI07bPsIOtltvsBeAkolvDRTFP4_zOZuEGaU5VWtY";

        paymentDownloader.downloadAccountPaymentList(clientId, secret);
    }
}
