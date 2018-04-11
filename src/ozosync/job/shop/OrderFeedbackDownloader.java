package ozosync.job.shop;

import com.ebay.sdk.ApiAccount;
import com.ebay.sdk.ApiContext;
import com.ebay.sdk.ApiCredential;
import com.ebay.soap.eBLBaseComponents.SiteCodeType;
import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import ozosync.domain.EBayApiConfigInfo;

public class OrderFeedbackDownloader {

    private Logger log = Logger.getLogger(OrderFeedbackDownloader.class);

    private SessionFactory sessionFactory = null;

    public OrderFeedbackDownloader(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @SuppressWarnings("unchecked")
    public void download() {
        Session session = sessionFactory.getCurrentSession();


    }

    private ApiContext getApiContextByConfig(EBayApiConfigInfo ebayApiConfigInfo) {
        ApiContext apiContext = new ApiContext();

        ApiCredential apiCredential = apiContext.getApiCredential();

        ApiAccount apiAccount = new ApiAccount();
        apiAccount.setApplication(ebayApiConfigInfo.getAppId());
        apiAccount.setCertificate(ebayApiConfigInfo.getCertId());
        apiAccount.setDeveloper(ebayApiConfigInfo.getDevId());

        apiCredential.setApiAccount(apiAccount);

        apiCredential.seteBayToken(ebayApiConfigInfo.getToken());
        apiContext.setApiServerUrl("https://api.ebay.com/wsapi");

        apiContext.getApiLogging().setLogSOAPMessages(false);// This will log SOAP requests and responses

        apiContext.setSite(SiteCodeType.AUSTRALIA);

        return apiContext;
    }
}
