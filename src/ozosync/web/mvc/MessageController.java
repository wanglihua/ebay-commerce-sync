package ozosync.web.mvc;

import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import ozosync.domain.StatusInfo;

import java.util.List;

@Controller
public class MessageController {

    private Logger log = Logger.getLogger(MessageController.class);

    @Autowired
    private SessionFactory sessionFactory;

    @RequestMapping(value = "/", method = RequestMethod.GET)
    @SuppressWarnings("unchecked")
    public String index() throws Exception {

        Session session = sessionFactory.getCurrentSession();

        List<StatusInfo> statusList = session.createCriteria(StatusInfo.class).list();

        return "home";


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

        apiContext.getApiLogging().setLogSOAPMessages(true);

        apiContext.setSite(SiteCodeType.AUSTRALIA);

        return apiContext;
    }
}
