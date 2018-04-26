package ozosync.job.message;

import com.ebay.sdk.ApiContext;
import com.ebay.sdk.call.GetMyMessagesCall;
import com.ebay.soap.eBLBaseComponents.DetailLevelCodeType;
import com.ebay.soap.eBLBaseComponents.MyMessagesMessageType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ozosync.domain.EBayApiConfigInfo;
import ozosync.domain.MessageInfo;
import ozosync.domain.schema.MessageSchema;
import ozosync.job.service.ApiContextService;
import ozosync.service.EBayApiConfigService;

@Service
public class MyMessageDownloader {

    private Logger log = Logger.getLogger(MyMessageDownloader.class);

    @Autowired
    private ApiContextService apiContextService;

    @Autowired
    private EBayApiConfigService eBayApiConfigService;

    @Autowired
    private SessionFactory sessionFactory;

    @SuppressWarnings("unchecked")
    public void download() {
        try {
            Session session = sessionFactory.getCurrentSession();

            List<EBayApiConfigInfo> ebayApiConfigList = eBayApiConfigService.getConifglist();
            for (EBayApiConfigInfo ebayApiConfigInfo : ebayApiConfigList) {
                try {
                    // headers
                    MyMessagesMessageType[] messageHeaders = retrieveMessageHeaders(ebayApiConfigInfo);

                    // 过滤掉 message id 数据库中已存在的
                    List<MyMessagesMessageType> newMessageHeaderList = new ArrayList<>();
                    for (MyMessagesMessageType messageHeader : messageHeaders) {
                        String messageId = messageHeader.getMessageID();
                        Long count = (Long) session.createCriteria(MessageInfo.class)
                                .add(Restrictions.eq(MessageSchema.messageId, messageId))
                                .setProjection(Projections.rowCount())
                                .uniqueResult();

                        if (count == 0) {
                            newMessageHeaderList.add(messageHeader);
                        }
                    }

                    // If there are Messages
                    if (newMessageHeaderList.size() != 0) {
                        int page = 1, totalPages = 0;
                        //  retrieve ten messages at a time ( a page)
                        if (messageHeaders.length % 10 != 0) {
                            totalPages = messageHeaders.length / 10 + 1;
                        } else {
                            totalPages = messageHeaders.length;
                        }
                        // make second GetMyMessages call
                        retrieveMessages(ebayApiConfigInfo, page, totalPages, newMessageHeaderList);
                    }

                } catch (Exception exp) {
                    log.fatal(exp);
                    log.fatal(Arrays.toString(exp.getStackTrace()).replace(",", "\r\n"));
                }
            }
        } catch (Exception exp) {
            log.fatal(exp);
            log.fatal(Arrays.toString(exp.getStackTrace()).replace(",", "\r\n"));
        }
    }

    private GetMyMessagesCall createGetMyMessagesCall(EBayApiConfigInfo ebayApiConfigInfo) {
        ApiContext apiContext = apiContextService.getApiContextByConfig(ebayApiConfigInfo);
        return new GetMyMessagesCall(apiContext);
    }

    private MyMessagesMessageType[] retrieveMessageHeaders(EBayApiConfigInfo ebayApiConfigInfo) throws Exception {
        GetMyMessagesCall getMyMessagesCall = createGetMyMessagesCall(ebayApiConfigInfo);

        DetailLevelCodeType[] detailLevels = new DetailLevelCodeType[]{
                DetailLevelCodeType.RETURN_HEADERS
        };

        getMyMessagesCall.setDetailLevel(detailLevels);
        getMyMessagesCall.getMyMessages();
        return getMyMessagesCall.getReturnedMyMessages();
    }

    private void retrieveMessages(EBayApiConfigInfo ebayApiConfigInfo, int page, int totalPages, List<MyMessagesMessageType> messageHeaderList) throws Exception {
        GetMyMessagesCall getMyMessagesCall = createGetMyMessagesCall(ebayApiConfigInfo);

        String[] messageIds = null;
        int start = 0, end = 0;

        start = (page - 1) * 10;

        if (totalPages == 1 || page == totalPages) {
            end = messageHeaderList.size();
        } else {
            end = (page * 10);
        }

        messageIds = new String[end - start];

        for (int i = start; i < end; i++) {
            if (messageHeaderList.get(i) != null) {
                for (int j = 0; j < (end - start); j++) {
                    messageIds[j] = messageHeaderList.get(i).getMessageID();
                }
            }
        }

        DetailLevelCodeType[] detailLevel = new DetailLevelCodeType[]{
                DetailLevelCodeType.RETURN_MESSAGES,
        };

        getMyMessagesCall.setDetailLevel(detailLevel);
        getMyMessagesCall.setMessageIDs(messageIds);
        getMyMessagesCall.getMyMessages();

        // todo 处理消息
        MyMessagesMessageType[] messages = getMyMessagesCall.getReturnedMyMessages();
        if (messages != null) {
            for (MyMessagesMessageType message : messages) {
                System.out.println(message.getText());
            }
        }

        // make another GetMyMessage call when there are more than ten messages
        if (page < totalPages) {
            retrieveMessages(ebayApiConfigInfo, ++page, totalPages, messageHeaderList);
        }
    }
}
