package ozosync.service;

import com.ebay.sdk.ApiContext;
import com.ebay.sdk.call.AddMemberMessageRTQCall;
import com.ebay.soap.eBLBaseComponents.MemberMessageType;
import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ozosync.domain.MessageInfo;
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
}

/*
AddMemberMessageAAQToPartner ???
 */