package ozosync.job.service;

import com.ebay.sdk.ApiAccount;
import com.ebay.sdk.ApiContext;
import com.ebay.sdk.ApiCredential;
import com.ebay.soap.eBLBaseComponents.SiteCodeType;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ozosync.domain.EBayApiConfigInfo;
import ozosync.domain.schema.EBayApiConfigSchema;

@Service
public class EbayApiContextService {

    @Autowired
    private SessionFactory sessionFactory;

    public ApiContext getApiContextByShopId(Long shopId) throws Exception {
        Session session = sessionFactory.getCurrentSession();

        EBayApiConfigInfo ebayApiConfigInfo = (EBayApiConfigInfo) session.createCriteria(EBayApiConfigInfo.class)
                .add(Restrictions.eq(EBayApiConfigSchema.shopId, shopId))
                .setMaxResults(1)
                .uniqueResult();

        if (ebayApiConfigInfo == null) {
            throw new Exception(String.format("指定的 shop 不存在, shopId: %d", shopId));
        }

        return getApiContextByConfig(ebayApiConfigInfo);
    }

    public ApiContext getApiContextByConfig(EBayApiConfigInfo ebayApiConfigInfo) {
        return getApiContextByConfig(ebayApiConfigInfo.getAppId(), ebayApiConfigInfo.getCertId(), ebayApiConfigInfo.getDevId(), ebayApiConfigInfo.getToken());
    }

    public ApiContext getApiContextByConfig(String appId, String certId, String devId, String token) {
        ApiContext apiContext = new ApiContext();

        ApiCredential apiCredential = apiContext.getApiCredential();

        ApiAccount apiAccount = new ApiAccount();
        apiAccount.setApplication(appId);
        apiAccount.setCertificate(certId);
        apiAccount.setDeveloper(devId);

        apiCredential.setApiAccount(apiAccount);

        apiCredential.seteBayToken(token);
        apiContext.setApiServerUrl("https://api.ebay.com/wsapi");

        apiContext.getApiLogging().setLogSOAPMessages(false);// This will log SOAP requests and responses

        apiContext.setSite(SiteCodeType.AUSTRALIA);

        return apiContext;
    }
}
