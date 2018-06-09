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
import java.util.Date;
import java.util.List;
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
import ozosync.domain.UserInfo;
import ozosync.domain.schema.MessageSchema;
import ozosync.domain.schema.ProductStoreItemSchema;
import ozosync.job.service.EbayApiContextService;
import ozosync.service.EBayApiConfigService;

@Service
public class MemberMessageDownloader {

    private static final int NUMBER_OF_DAY = 1;

    private Logger log = Logger.getLogger(MemberMessageDownloader.class);

    @Autowired
    private EbayApiContextService ebayApiContextService;

    @Autowired
    private EBayApiConfigService eBayApiConfigService;

    @Autowired
    private SessionFactory sessionFactory;

    @SuppressWarnings("unchecked")
    public void download() {
        try {
            Session session = sessionFactory.getCurrentSession();

            List<EBayApiConfigInfo> ebayApiConfigList = eBayApiConfigService.getConfiglist();
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

                                messageInfo.setMemberDownloadTime(new Date());

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

                                        ProductInfo productInfo = (ProductInfo) session.get(ProductInfo.class, productId);
                                        if (productInfo == null) {
                                            // throw new Exception(String.format("指定的商品 id:%d 不存在！", productId));
                                            messageInfo.setProductId(0L);
                                            messageInfo.setSku("");
                                        } else {
                                            messageInfo.setProductId(productId);
                                            messageInfo.setSku(productInfo.getSku());

                                            if (productInfo.getSaleUserId() != 0L) {
                                                UserInfo userInfo = (UserInfo) session.get(UserInfo.class, productInfo.getSaleUserId());
                                                if (userInfo != null) {
                                                    messageInfo.setSaleUserId(userInfo.getId());
                                                    messageInfo.setSaleUser(userInfo.getLoginName() + "(" + userInfo.getNickName() + ")");
                                                }
                                            }

                                            if (productInfo.getServiceUserId() != 0L) {
                                                UserInfo userInfo = (UserInfo) session.get(UserInfo.class, productInfo.getServiceUserId());
                                                if (userInfo != null) {
                                                    messageInfo.setServiceUserId(userInfo.getId());
                                                    messageInfo.setServiceUser(userInfo.getLoginName() + "(" + userInfo.getNickName() + ")");
                                                }
                                            }

                                            if (productInfo.getDevUserId() != 0L) {
                                                UserInfo userInfo = (UserInfo) session.get(UserInfo.class, productInfo.getDevUserId());
                                                if (userInfo != null) {
                                                    messageInfo.setDevUserId(userInfo.getId());
                                                    messageInfo.setDevUser(userInfo.getLoginName() + "(" + userInfo.getNickName() + ")");
                                                }
                                            }
                                        }
                                    }
                                }

                                if (memberMessageExchangeType.getCreationDate() != null) {
                                    messageInfo.setCreateTime(memberMessageExchangeType.getCreationDate().getTime());
                                }

                                if (memberMessageExchangeType.getLastModifiedDate() != null) {
                                    messageInfo.setLastModifiedDate(memberMessageExchangeType.getLastModifiedDate().getTime());
                                }

                                if (memberMessageType.getParentMessageID() != null) {
                                    messageInfo.setParentMessageId(memberMessageType.getParentMessageID());
                                }

                                messageInfo.setMessageStatus(memberMessageExchangeType.getMessageStatus().value());

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
        ApiContext apiContext = ebayApiContextService.getApiContextByConfig(ebayApiConfigInfo);

        GetMemberMessagesCall getMemberMessagesCall = new GetMemberMessagesCall(apiContext);
        getMemberMessagesCall.setMailMessageType(MessageTypeCodeType.ALL);

        Calendar startCreateTime = Calendar.getInstance();
        startCreateTime.add(Calendar.DATE, -NUMBER_OF_DAY);
        getMemberMessagesCall.setStartCreationTime(startCreateTime);

        Calendar endCreateTime = Calendar.getInstance();
        getMemberMessagesCall.setEndCreationTime(endCreateTime);

        PaginationType pagination = new PaginationType();
        pagination.setEntriesPerPage(200);
        getMemberMessagesCall.setPagination(pagination);

        return getMemberMessagesCall;
    }
}
