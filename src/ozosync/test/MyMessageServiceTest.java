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
import ozosync.common.SpringConfigLocation;
import ozosync.domain.EBayApiConfigInfo;
import ozosync.domain.schema.EBayApiConfigSchema;
import ozosync.job.message.service.MyMessageService;
import ozosync.job.service.EbayApiContextService;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(locations = {SpringConfigLocation.configLocation})
public class MyMessageServiceTest {

    @Autowired
    private MyMessageService myMessageService;

    @Autowired
    private EbayApiContextService ebayApiContextService;

    @Autowired
    private SessionFactory sessionFactory;

    @Test
    public void testReadCData() {
        String s = "<![CDATA[要获取的内容]]>";

        System.out.println(myMessageService.getCData1(s));
    }

    private GetMyMessagesCall createGetMyMessagesCall(EBayApiConfigInfo ebayApiConfigInfo) {
        ApiContext apiContext = ebayApiContextService.getApiContextByConfig(ebayApiConfigInfo);
        return new GetMyMessagesCall(apiContext);
    }

    public void testDownloadSingleMessage() throws Exception {

        Session session = sessionFactory.getCurrentSession();

        Long shopId = 1L;
        String messageId = "";

        EBayApiConfigInfo ebayApiConfigInfo = (EBayApiConfigInfo) session.createCriteria(EBayApiConfigInfo.class)
                .add(Restrictions.eq(EBayApiConfigSchema.shopId, shopId))
                .setMaxResults(1)
                .uniqueResult();

        GetMyMessagesCall getMyMessagesCall = createGetMyMessagesCall(ebayApiConfigInfo);

        DetailLevelCodeType[] detailLevel = new DetailLevelCodeType[]{
                DetailLevelCodeType.RETURN_MESSAGES,
        };

        getMyMessagesCall.setDetailLevel(detailLevel);
        getMyMessagesCall.setMessageIDs(new String[]{});
        getMyMessagesCall.getMyMessages();

        MyMessagesMessageType[] messages = getMyMessagesCall.getReturnedMyMessages();
        if (messages != null) {
            for (MyMessagesMessageType message : messages) {
                String messageText = message.getText();
                System.out.println(messageText);
            }
        }
    }
}
