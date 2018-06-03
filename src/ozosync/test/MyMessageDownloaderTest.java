package ozosync.test;

import com.ebay.sdk.ApiContext;
import com.ebay.sdk.call.GetMyMessagesCall;
import com.ebay.soap.eBLBaseComponents.DetailLevelCodeType;
import com.ebay.soap.eBLBaseComponents.MyMessagesMessageType;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;
import ozosync.common.SpringConfigLocation;
import ozosync.domain.EBayApiConfigInfo;
import ozosync.domain.schema.EBayApiConfigSchema;
import ozosync.job.message.MyMessageDownloader;
import ozosync.job.service.EbayApiContextService;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(locations = {SpringConfigLocation.configLocation})
public class MyMessageDownloaderTest {

    @Autowired
    private MyMessageDownloader myMessageDownloader;

    @Autowired
    private EbayApiContextService ebayApiContextService;

    @Autowired
    private SessionFactory sessionFactory;

    @Test
    @Transactional
    public void myMessageDownload() {
        myMessageDownloader.download();
    }

    @Test
    @Transactional
    public void testDownloadSingleMessage() throws Exception {

        Session session = sessionFactory.getCurrentSession();

        // 7--99556875124

        // 2--99288054587

        Long shopId = 2L;

        String messageId = "99288356857";

        EBayApiConfigInfo ebayApiConfigInfo = (EBayApiConfigInfo) session.createCriteria(EBayApiConfigInfo.class)
                .add(Restrictions.eq(EBayApiConfigSchema.shopId, shopId))
                .setMaxResults(1)
                .uniqueResult();

        GetMyMessagesCall getMyMessagesCall = createGetMyMessagesCall(ebayApiConfigInfo);

        DetailLevelCodeType[] detailLevel = new DetailLevelCodeType[]{
                DetailLevelCodeType.RETURN_MESSAGES,
        };

        getMyMessagesCall.setDetailLevel(detailLevel);
        getMyMessagesCall.setMessageIDs(new String[]{messageId});
        getMyMessagesCall.getMyMessages();

        MyMessagesMessageType[] messages = getMyMessagesCall.getReturnedMyMessages();
        if (messages != null) {
            for (MyMessagesMessageType message : messages) {
                String messageText = message.getText();
                System.out.println(messageText);
            }
        }
    }

    private GetMyMessagesCall createGetMyMessagesCall(EBayApiConfigInfo ebayApiConfigInfo) {
        ApiContext apiContext = ebayApiContextService.getApiContextByConfig(ebayApiConfigInfo);
        return new GetMyMessagesCall(apiContext);
    }
}
