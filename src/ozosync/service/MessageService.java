package ozosync.service;

import com.ebay.sdk.ApiContext;
import com.ebay.sdk.call.AddMemberMessageAAQToPartnerCall;
import com.ebay.sdk.call.AddMemberMessageRTQCall;
import com.ebay.sdk.call.GetMemberMessagesCall;
import com.ebay.soap.eBLBaseComponents.MemberMessageExchangeType;
import com.ebay.soap.eBLBaseComponents.MemberMessageType;
import com.ebay.soap.eBLBaseComponents.MessageStatusTypeCodeType;
import com.ebay.soap.eBLBaseComponents.MessageTypeCodeType;
import com.ebay.soap.eBLBaseComponents.PaginationType;
import com.ebay.soap.eBLBaseComponents.QuestionTypeCodeType;
import java.util.ArrayList;
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
import ozosync.domain.ECommercePatformInfo;
import ozosync.domain.MessageInfo;
import ozosync.domain.MessageStatus;
import ozosync.domain.MessageType;
import ozosync.domain.MessageTypeInfo;
import ozosync.domain.OrderInfo;
import ozosync.domain.OrderItemMessageViewInfo;
import ozosync.domain.ProductInfo;
import ozosync.domain.ProductStoreItemInfo;
import ozosync.domain.ShopAccountInfo;
import ozosync.domain.StatusInfo;
import ozosync.domain.UserInfo;
import ozosync.domain.schema.MessageSchema;
import ozosync.domain.schema.OrderItemMessageViewSchema;
import ozosync.domain.schema.OrderSchema;
import ozosync.domain.schema.ProductStoreItemSchema;
import ozosync.job.service.EbayApiContextService;

@Service
public class MessageService {

    private Logger log = Logger.getLogger(MessageService.class);

    @Autowired
    private EBayApiConfigService eBayApiConfigService;

    @Autowired
    private SessionFactory sessionFactory;

    @Autowired
    private EbayApiContextService ebayApiContextService;

    public void reply(Long messageId, String messageBody) throws Exception {
        Session session = sessionFactory.getCurrentSession();

        MessageInfo messageInfo = (MessageInfo) session.get(MessageInfo.class, messageId);
        ApiContext apiContext = ebayApiContextService.getApiContextByShopId(messageInfo.getShopId());

        AddMemberMessageRTQCall addMemberMessageRTQCall = new AddMemberMessageRTQCall(apiContext);
        addMemberMessageRTQCall.setItemID(messageInfo.getItemId());

        MemberMessageType memberMessage = new MemberMessageType();
        memberMessage.setParentMessageID(messageInfo.getMessageId());
        memberMessage.setRecipientID(new String[]{messageInfo.getSenderId()});
        memberMessage.setSenderID(messageInfo.getRecipientId());
        memberMessage.setDisplayToPublic(messageInfo.getDisplayToPublic());
        memberMessage.setBody(messageBody);
        addMemberMessageRTQCall.setMemberMessage(memberMessage);

        addMemberMessageRTQCall.addMemberMessageRTQ();

        downloadMemberMessagesInNumberOfMinutes(messageInfo.getShopId(), 10);
    }

    @SuppressWarnings("unchecked")
    public List<String> sendMessageToCustomers(List<Long> orderIdList, String subject, String body) throws Exception {
        Session session = sessionFactory.getCurrentSession();

        List<OrderItemMessageViewInfo> orderItemList = session.createCriteria(OrderItemMessageViewInfo.class)
                .add(Restrictions.in(OrderItemMessageViewSchema.orderId, orderIdList))
                .list();

        List<Long> successOrderIdList = new ArrayList<>();

        for (OrderItemMessageViewInfo orderItem : orderItemList) {
            try {
                ApiContext apiContext = ebayApiContextService.getApiContextByShopId(orderItem.getShopId());
                AddMemberMessageAAQToPartnerCall addMemberMessageAAQToPartnerCall = new AddMemberMessageAAQToPartnerCall(apiContext);
                addMemberMessageAAQToPartnerCall.setItemID(orderItem.getItemId());

                MemberMessageType memberMessageType = new MemberMessageType();
                memberMessageType.setMessageType(MessageTypeCodeType.CONTACT_EBAY_MEMBER);
                memberMessageType.setSubject(subject);
                memberMessageType.setBody(body);
                memberMessageType.setEmailCopyToSender(true);
                memberMessageType.setQuestionType(QuestionTypeCodeType.CUSTOMIZED_SUBJECT);
                memberMessageType.setRecipientID(new String[]{orderItem.getBuyerUserId()});

                addMemberMessageAAQToPartnerCall.setMemberMessage(memberMessageType);

                addMemberMessageAAQToPartnerCall.addMemberMessageAAQToPartner();

                if (successOrderIdList.contains(orderItem.getOrderId()) == false) {
                    successOrderIdList.add(orderItem.getOrderId());
                }
            } catch (Exception exp) {
                log.fatal(exp.getMessage(), exp);
            }
        }

        List<String> failOrderCodeList = new ArrayList<>();

        if (successOrderIdList == null || successOrderIdList.size() == 0 || successOrderIdList.size() != orderIdList.size()) {

            List<Long> failOrderIdList = new ArrayList<>();

            for (Long orderId : orderIdList) {
                if (successOrderIdList.contains(orderId) == false) {
                    failOrderIdList.add(orderId);
                }
            }

            List<OrderInfo> failOrderList = session.createCriteria(OrderInfo.class)
                    .add(Restrictions.in(OrderSchema.id, failOrderIdList))
                    .list();

            for (OrderInfo failOrder : failOrderList) {
                failOrderCodeList.add(failOrder.getCode());
            }
        }

        return failOrderCodeList;
    }

    public void saveDownloadMessage(EBayApiConfigInfo ebayApiConfigInfo, MemberMessageExchangeType memberMessageExchangeType) {
        Session session = sessionFactory.getCurrentSession();

        MemberMessageType memberMessageType = memberMessageExchangeType.getQuestion();

        String messageId = memberMessageType.getMessageID();

        MessageInfo messageInfo = (MessageInfo) session.createCriteria(MessageInfo.class)
                .add(Restrictions.eq(MessageSchema.messageId, messageId))
                .setMaxResults(1)
                .uniqueResult();

        if (messageInfo == null) {
            messageInfo = new MessageInfo();
        }

        messageInfo.setTypeId(MessageType.MESSAGE_TYPE_SALE);

        MessageTypeInfo messageTypeInfo = (MessageTypeInfo) session.get(MessageTypeInfo.class, MessageType.MESSAGE_TYPE_SALE);
        if (messageTypeInfo != null) {
            messageInfo.setTypeName(messageTypeInfo.getTypeName());
        }

        messageInfo.setMemberDownloadTime(new Date());

        messageInfo.setMessageId(messageId);
        messageInfo.setShopId(ebayApiConfigInfo.getShopId());

        ShopAccountInfo shopAccountInfo = (ShopAccountInfo) session.get(ShopAccountInfo.class, ebayApiConfigInfo.getShopId());
        if (shopAccountInfo != null) {
            ECommercePatformInfo eCommercePatformInfo = (ECommercePatformInfo) session.get(ECommercePatformInfo.class, shopAccountInfo.getPlatformId());
            if (eCommercePatformInfo != null) {
                messageInfo.setShopName(eCommercePatformInfo.getName() + " " + shopAccountInfo.getName());
            }
        }

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
                messageInfo.setViewItemUrl(productStoreItemInfo.getViewItemUrl());

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

                    if (productInfo.getServiceUserId() != 0L && messageInfo.getServiceUserId() == 0L) { // 客服人员后面会被修改，修改过的不能被覆盖
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
            StatusInfo statusInfo = (StatusInfo) session.get(StatusInfo.class, MessageStatus.ANSWERED);
            if (statusInfo != null) {
                messageInfo.setStatusName(statusInfo.getName());
            }

        } else if (memberMessageExchangeType.getMessageStatus() == MessageStatusTypeCodeType.UNANSWERED) {
            messageInfo.setStatusId(MessageStatus.UNANSWERED);
            StatusInfo statusInfo = (StatusInfo) session.get(StatusInfo.class, MessageStatus.UNANSWERED);
            if (statusInfo != null) {
                messageInfo.setStatusName(statusInfo.getName());
            }
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

    public void downloadMemberMessagesInNumberOfDays(EBayApiConfigInfo ebayApiConfigInfo, int numberOfDays) throws Exception {
        ApiContext apiContext = ebayApiContextService.getApiContextByConfig(ebayApiConfigInfo);

        GetMemberMessagesCall getMemberMessagesCall = new GetMemberMessagesCall(apiContext);
        getMemberMessagesCall.setMailMessageType(MessageTypeCodeType.ALL);

        Calendar startCreateTime = Calendar.getInstance();
        startCreateTime.add(Calendar.DATE, -numberOfDays);
        getMemberMessagesCall.setStartCreationTime(startCreateTime);

        Calendar endCreateTime = Calendar.getInstance();
        getMemberMessagesCall.setEndCreationTime(endCreateTime);

        PaginationType pagination = new PaginationType();
        pagination.setEntriesPerPage(200);
        getMemberMessagesCall.setPagination(pagination);

        int pageNumber = 0;

        do {
            getMemberMessagesCall.getPagination().setPageNumber(++pageNumber);
            MemberMessageExchangeType[] memberMessageExchangeTypes = getMemberMessagesCall.getMemberMessages();

            if (memberMessageExchangeTypes != null && memberMessageExchangeTypes.length != 0) {
                for (MemberMessageExchangeType memberMessageExchangeType : memberMessageExchangeTypes) {
                    saveDownloadMessage(ebayApiConfigInfo, memberMessageExchangeType);
                }
            }
        } while (getMemberMessagesCall.getHasMoreItems());
    }

    public void downloadMemberMessagesInNumberOfMinutes(Long shopId, int numberOfMinutes) throws Exception {
        EBayApiConfigInfo ebayApiConfigInfo = eBayApiConfigService.getConfig(shopId);
        downloadMemberMessagesInNumberOfMinutes(ebayApiConfigInfo, numberOfMinutes);
    }

    public void downloadMemberMessagesInNumberOfMinutes(EBayApiConfigInfo ebayApiConfigInfo, int numberOfMinutes) throws Exception {
        ApiContext apiContext = ebayApiContextService.getApiContextByConfig(ebayApiConfigInfo);

        GetMemberMessagesCall getMemberMessagesCall = new GetMemberMessagesCall(apiContext);
        getMemberMessagesCall.setMailMessageType(MessageTypeCodeType.ALL);

        Calendar startCreateTime = Calendar.getInstance();
        startCreateTime.add(Calendar.MINUTE, -numberOfMinutes);
        getMemberMessagesCall.setStartCreationTime(startCreateTime);

        Calendar endCreateTime = Calendar.getInstance();
        getMemberMessagesCall.setEndCreationTime(endCreateTime);

        PaginationType pagination = new PaginationType();
        pagination.setEntriesPerPage(200);
        getMemberMessagesCall.setPagination(pagination);

        int pageNumber = 0;

        do {
            getMemberMessagesCall.getPagination().setPageNumber(++pageNumber);
            MemberMessageExchangeType[] memberMessageExchangeTypes = getMemberMessagesCall.getMemberMessages();

            if (memberMessageExchangeTypes != null && memberMessageExchangeTypes.length != 0) {
                for (MemberMessageExchangeType memberMessageExchangeType : memberMessageExchangeTypes) {
                    saveDownloadMessage(ebayApiConfigInfo, memberMessageExchangeType);
                }
            }
        } while (getMemberMessagesCall.getHasMoreItems());
    }

    public void downloadMemberMessagesByItemId(Long shopId, String itemId) throws Exception {

        EBayApiConfigInfo ebayApiConfigInfo = eBayApiConfigService.getConfig(shopId);

        ApiContext apiContext = ebayApiContextService.getApiContextByConfig(ebayApiConfigInfo);

        GetMemberMessagesCall getMemberMessagesCall = new GetMemberMessagesCall(apiContext);
        getMemberMessagesCall.setMailMessageType(MessageTypeCodeType.ALL);

        getMemberMessagesCall.setItemID(itemId);

        PaginationType pagination = new PaginationType();
        pagination.setEntriesPerPage(200);
        getMemberMessagesCall.setPagination(pagination);

        int pageNumber = 0;

        do {
            getMemberMessagesCall.getPagination().setPageNumber(++pageNumber);
            MemberMessageExchangeType[] memberMessageExchangeTypes = getMemberMessagesCall.getMemberMessages();

            if (memberMessageExchangeTypes != null && memberMessageExchangeTypes.length != 0) {
                for (MemberMessageExchangeType memberMessageExchangeType : memberMessageExchangeTypes) {
                    saveDownloadMessage(ebayApiConfigInfo, memberMessageExchangeType);
                }
            }
        } while (getMemberMessagesCall.getHasMoreItems());
    }

    public void downloadMemberMessagesByMessageId(Long shopId, String messageId) throws Exception {

        EBayApiConfigInfo ebayApiConfigInfo = eBayApiConfigService.getConfig(shopId);

        ApiContext apiContext = ebayApiContextService.getApiContextByConfig(ebayApiConfigInfo);

        GetMemberMessagesCall getMemberMessagesCall = new GetMemberMessagesCall(apiContext);
        getMemberMessagesCall.setMailMessageType(MessageTypeCodeType.ALL);

        getMemberMessagesCall.setMemberMessageID(messageId);

        PaginationType pagination = new PaginationType();
        pagination.setEntriesPerPage(200);
        getMemberMessagesCall.setPagination(pagination);

        int pageNumber = 0;

        do {
            getMemberMessagesCall.getPagination().setPageNumber(++pageNumber);
            MemberMessageExchangeType[] memberMessageExchangeTypes = getMemberMessagesCall.getMemberMessages();

            if (memberMessageExchangeTypes != null && memberMessageExchangeTypes.length != 0) {
                for (MemberMessageExchangeType memberMessageExchangeType : memberMessageExchangeTypes) {
                    saveDownloadMessage(ebayApiConfigInfo, memberMessageExchangeType);
                }
            }
        } while (getMemberMessagesCall.getHasMoreItems());
    }
}
