package ozosync.service;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ozosync.domain.MessageInfo;
import ozosync.domain.MessageType;
import ozosync.domain.MessageTypeInfo;

@Service
public class MessageTypeService {

    @Autowired
    private SessionFactory sessionFactory;

    public MessageTypeInfo parseMessageType(MessageInfo messageInfo) {
        Session session = sessionFactory.getCurrentSession();

        if ("ebay@ebay.com.au".equals(messageInfo.getSenderEmail())) {
            return (MessageTypeInfo) session.get(MessageTypeInfo.class, MessageType.MESSAGE_TYPE_FROM_EBAY);
        }

        String[] offerTypeStrs = new String[]{"New offer received", "Counter offer submitted to buyer", "Counter offer pending reminder", "Offer expired"};
        for (String offerTypeStr : offerTypeStrs) {
            if (messageInfo.getSubject().contains(offerTypeStr)) {
                return (MessageTypeInfo) session.get(MessageTypeInfo.class, MessageType.MESSAGE_TYPE_OFFER);
            }
        }

        String[] requestTypeStrs = new String[]{"Return requested", "Respond to your open request", "A request is still open for", "Your buyer opened a request", "Feedback revision request"};
        for (String requestTypeStr : requestTypeStrs) {
            if (messageInfo.getSubject().contains(requestTypeStr)) {
                return (MessageTypeInfo) session.get(MessageTypeInfo.class, MessageType.MESSAGE_TYPE_REQUEST);
            }
        }

        String[] returnTypeStrs = new String[]{"Case #", "Unpaid item case"};
        for (String returnTypeStr : returnTypeStrs) {
            if (messageInfo.getSubject().contains(returnTypeStr)) {
                return (MessageTypeInfo) session.get(MessageTypeInfo.class, MessageType.MESSAGE_TYPE_RETURN);
            }
        }

        return (MessageTypeInfo) session.get(MessageTypeInfo.class, MessageType.MESSAGE_TYPE_BEFORE_SALE);
    }

}
