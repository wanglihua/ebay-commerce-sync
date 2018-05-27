package ozosync.job.message;

import com.ebay.sdk.ApiContext;
import com.ebay.sdk.call.GetMyMessagesCall;
import com.ebay.soap.eBLBaseComponents.DetailLevelCodeType;
import com.ebay.soap.eBLBaseComponents.MyMessagesMessageType;
import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ozosync.domain.EBayApiConfigInfo;
import ozosync.domain.MessageInfo;
import ozosync.domain.MessageStatus;
import ozosync.domain.ProductInfo;
import ozosync.domain.ProductStoreItemInfo;
import ozosync.domain.schema.MessageSchema;
import ozosync.domain.schema.ProductStoreItemSchema;
import ozosync.job.message.service.MyMessageService;
import ozosync.job.service.EbayApiContextService;
import ozosync.service.EBayApiConfigService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Service
public class MyMessageDownloader {

    private static final int NUMBER_OF_DAY = 1;

    private Logger log = Logger.getLogger(MyMessageDownloader.class);

    @Autowired
    private EbayApiContextService ebayApiContextService;

    @Autowired
    private EBayApiConfigService eBayApiConfigService;

    @Autowired
    private SessionFactory sessionFactory;

    @Autowired
    private MyMessageService myMessageService;

    @SuppressWarnings("unchecked")
    public void download() {
        try {
            Session session = sessionFactory.getCurrentSession();

            List<EBayApiConfigInfo> ebayApiConfigList = eBayApiConfigService.getConfiglist();
            for (EBayApiConfigInfo ebayApiConfigInfo : ebayApiConfigList) {
                try {
                    // headers
                    MyMessagesMessageType[] messageHeaders = retrieveMessageHeaders(ebayApiConfigInfo);

                    // 过滤掉 message id 数据库中已存在的
                    List<MyMessagesMessageType> newMessageHeaderList = new ArrayList<>();

                    for (MyMessagesMessageType messageHeader : messageHeaders) {
                        /*
                        String messageId = messageHeader.getMessageID();
                        Long count = (Long) session.createCriteria(MessageInfo.class)
                                .add(Restrictions.eq(MessageSchema.messageId, messageId))
                                .setProjection(Projections.rowCount())
                                .uniqueResult();

                        if (count == 0) {
                            newMessageHeaderList.add(messageHeader);
                        }
                        */

                        // 先不过滤了
                        newMessageHeaderList.add(messageHeader);
                    }


                    // If there are Messages
                    if (newMessageHeaderList.size() != 0) {
                        int page = 1, totalPages = 0;
                        //  retrieve ten messages at a time ( a page)
                        if (messageHeaders.length % 10 != 0) {
                            totalPages = messageHeaders.length / 10 + 1;
                        } else {
                            totalPages = messageHeaders.length / 10;
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
        ApiContext apiContext = ebayApiContextService.getApiContextByConfig(ebayApiConfigInfo);
        return new GetMyMessagesCall(apiContext);
    }

    private MyMessagesMessageType[] retrieveMessageHeaders(EBayApiConfigInfo ebayApiConfigInfo) throws Exception {
        GetMyMessagesCall getMyMessagesCall = createGetMyMessagesCall(ebayApiConfigInfo);

        DetailLevelCodeType[] detailLevels = new DetailLevelCodeType[]{
                DetailLevelCodeType.RETURN_HEADERS
        };

        getMyMessagesCall.setDetailLevel(detailLevels);

        Calendar startTime = Calendar.getInstance();
        startTime.add(Calendar.DATE, -NUMBER_OF_DAY);
        getMyMessagesCall.setStartTime(startTime);

        Calendar endTime = Calendar.getInstance();
        getMyMessagesCall.setEndTime(endTime);

        getMyMessagesCall.getMyMessages();

        return getMyMessagesCall.getReturnedMyMessages();
    }

    private void retrieveMessages(EBayApiConfigInfo ebayApiConfigInfo, int page, int totalPages, List<MyMessagesMessageType> messageHeaderList) throws Exception {

        Session session = sessionFactory.getCurrentSession();

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

        MyMessagesMessageType[] messages = getMyMessagesCall.getReturnedMyMessages();
        if (messages != null) {
            for (MyMessagesMessageType message : messages) {
                String messageId = message.getMessageID();

                MessageInfo messageInfo = (MessageInfo) session.createCriteria(MessageInfo.class)
                        .add(Restrictions.eq(MessageSchema.messageId, messageId))
                        .setMaxResults(1)
                        .uniqueResult();

                if (messageInfo == null) {
                    messageInfo = new MessageInfo();
                }

                messageInfo.setMyDownloadTime(new Date());

                messageInfo.setMessageId(messageId);
                messageInfo.setShopId(ebayApiConfigInfo.getShopId());

                String itemId = message.getItemID();

                if (itemId == null || itemId.equals("")) {
                    messageInfo.setItemId("");
                    messageInfo.setProductId(0L);
                    messageInfo.setSku("");
                } else {
                    messageInfo.setItemId(itemId);

                    ProductStoreItemInfo productStoreItemInfo = (ProductStoreItemInfo) session.createCriteria(ProductStoreItemInfo.class)
                            .add(Restrictions.eq(ProductStoreItemSchema.itemId, itemId))
                            .setMaxResults(1)
                            .uniqueResult();

                    if (productStoreItemInfo == null) {
                        // throw new Exception("商品店铺 item id 不存在！");
                        messageInfo.setProductId(0L);
                        messageInfo.setSku("");
                    } else {
                        Long productId = productStoreItemInfo.getProductId();
                        messageInfo.setProductId(productId);

                        ProductInfo productInfo = (ProductInfo) session.get(ProductInfo.class, productId);
                        if (productInfo == null) {
                            // throw new Exception(String.format("指定的商品 id:%d 不存在！", productId));
                            messageInfo.setSku("");
                        } else {
                            messageInfo.setSku(productInfo.getSku());
                        }
                    }
                }

                // messageInfo.setDisplayToPublic(memberMessageType.isDisplayToPublic());
                messageInfo.setDisplayToPublic(false);

                if (message.getMessageType() != null) {
                    messageInfo.setMessageType(message.getMessageType().value());
                }
                if (message.getQuestionType() != null) {
                    messageInfo.setQuestionType(message.getQuestionType().value());
                }

                messageInfo.setRecipientId(message.getRecipientUserID());

                // messageInfo.setSenderEmail(message.getSender());
                messageInfo.setSenderEmail("");

                messageInfo.setSenderId(message.getSender());
                messageInfo.setSubject(message.getSubject());

                String messageBody = myMessageService.getMessageBody(message.getText());

                if (messageBody.equals("")) {
                    messageBody = "missing: " + message.getMessageID();
                }

                messageInfo.setBody(messageBody);

                if (message.getCreationDate() != null) {
                    messageInfo.setCreationDate(message.getCreationDate().getTime());
                }

                if (message.isReplied()) {
                    messageInfo.setStatusId(MessageStatus.ANSWERED);
                } else {
                    messageInfo.setStatusId(MessageStatus.UNANSWERED);
                }

                session.saveOrUpdate(messageInfo);
                session.flush();
            }
        }

        // make another GetMyMessage call when there are more than ten messages
        if (page < totalPages) {
            retrieveMessages(ebayApiConfigInfo, ++page, totalPages, messageHeaderList);
        }
    }
}
