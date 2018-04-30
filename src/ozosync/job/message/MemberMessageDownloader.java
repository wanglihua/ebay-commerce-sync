package ozosync.job.message;

import com.ebay.sdk.ApiContext;
import com.ebay.sdk.call.GetMemberMessagesCall;
import com.ebay.soap.eBLBaseComponents.MemberMessageExchangeType;
import com.ebay.soap.eBLBaseComponents.MemberMessageType;
import com.ebay.soap.eBLBaseComponents.MessageStatusTypeCodeType;
import com.ebay.soap.eBLBaseComponents.MessageTypeCodeType;
import com.ebay.soap.eBLBaseComponents.PaginationType;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;
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
import ozosync.job.service.ApiContextService;
import ozosync.service.EBayApiConfigService;

@Service
public class MemberMessageDownloader {

    private static final int number_of_day = 1;

    private Logger log = Logger.getLogger(MemberMessageDownloader.class);

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
                    GetMemberMessagesCall getMemberMessagesCall = createGetMemberMessagesCall(ebayApiConfigInfo);

                    int pageNumber = 0;

                    do {
                        getMemberMessagesCall.getPagination().setPageNumber(++pageNumber);
                        MemberMessageExchangeType[] memberMessageExchangeTypes = getMemberMessagesCall.getMemberMessages();

                        if (memberMessageExchangeTypes != null && memberMessageExchangeTypes.length != 0) {
                            for (MemberMessageExchangeType memberMessageExchangeType : memberMessageExchangeTypes) {

                                MemberMessageType memberMessageType = memberMessageExchangeType.getQuestion();

                                String messageId = memberMessageType.getMessageID();

                                MessageInfo messageInfo = (MessageInfo) session.createCriteria(MessageInfo.class)
                                        .add(Restrictions.eq(MessageSchema.messageId, messageId))
                                        .setMaxResults(1)
                                        .uniqueResult();

                                if (messageInfo == null) {
                                    messageInfo = new MessageInfo();
                                }

                                messageInfo.setMessageId(messageId);
                                messageInfo.setShopId(ebayApiConfigInfo.getShopId());

                                if (memberMessageExchangeType.getItem() == null) {
                                    messageInfo.setItemId("");
                                    messageInfo.setProductId(0L);
                                    messageInfo.setSku("");
                                } else {
                                    String itemId = memberMessageExchangeType.getItem().getItemID();

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

                                messageInfo.setCreateTime(memberMessageExchangeType.getCreationDate().getTime());
                                messageInfo.setLastModifiedDate(memberMessageExchangeType.getLastModifiedDate().getTime());

                                if (memberMessageExchangeType.getMessageStatus() == MessageStatusTypeCodeType.ANSWERED) {
                                    messageInfo.setStatusId(MessageStatus.ANSWERED);
                                } else if (memberMessageExchangeType.getMessageStatus() == MessageStatusTypeCodeType.UNANSWERED) {
                                    messageInfo.setStatusId(MessageStatus.UNANSWERED);
                                }

                                messageInfo.setDisplayToPublic(memberMessageType.isDisplayToPublic());
                                messageInfo.setMessageType(memberMessageType.getMessageType().value());
                                messageInfo.setQuestionType(memberMessageType.getQuestionType().value());
                                messageInfo.setRecipientId(memberMessageType.getRecipientID(0));
                                messageInfo.setSenderEmail(memberMessageType.getSenderEmail());
                                messageInfo.setSenderId(memberMessageType.getSenderID());
                                messageInfo.setSubject(memberMessageType.getSubject());
                                messageInfo.setBody(memberMessageType.getBody());

                                session.saveOrUpdate(messageInfo);
                                session.flush();
                            }
                        }
                    } while (getMemberMessagesCall.getHasMoreItems());

                    session.flush();

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

    private GetMemberMessagesCall createGetMemberMessagesCall(EBayApiConfigInfo ebayApiConfigInfo) {
        ApiContext apiContext = apiContextService.getApiContextByConfig(ebayApiConfigInfo);

        GetMemberMessagesCall getMemberMessagesCall = new GetMemberMessagesCall(apiContext);
        getMemberMessagesCall.setMailMessageType(MessageTypeCodeType.ALL);

        Calendar startCreateTime = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
        startCreateTime.add(Calendar.DATE, -number_of_day);
        getMemberMessagesCall.setStartCreationTime(startCreateTime);

        Calendar endCreateTime = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
        getMemberMessagesCall.setEndCreationTime(endCreateTime);

        PaginationType pagination = new PaginationType();
        pagination.setEntriesPerPage(200);
        getMemberMessagesCall.setPagination(pagination);

        return getMemberMessagesCall;
    }
}
