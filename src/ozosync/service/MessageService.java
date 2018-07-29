package ozosync.service;

import com.ebay.sdk.ApiContext;
import com.ebay.sdk.call.AddMemberMessageAAQToPartnerCall;
import com.ebay.sdk.call.AddMemberMessageRTQCall;
import com.ebay.soap.eBLBaseComponents.MemberMessageType;
import com.ebay.soap.eBLBaseComponents.MessageTypeCodeType;
import com.ebay.soap.eBLBaseComponents.QuestionTypeCodeType;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ozosync.domain.MessageInfo;
import ozosync.domain.OrderInfo;
import ozosync.domain.OrderItemMessageViewInfo;
import ozosync.domain.schema.OrderItemMessageViewSchema;
import ozosync.domain.schema.OrderSchema;
import ozosync.job.service.EbayApiContextService;

@Service
public class MessageService {

    private Logger log = Logger.getLogger(MessageService.class);

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
}
