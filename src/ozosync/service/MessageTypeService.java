package ozosync.service;

import org.springframework.stereotype.Service;
import ozosync.domain.MessageInfo;
import ozosync.domain.MessageTypeInfo;

@Service
public class MessageTypeService {

    public MessageTypeInfo parseMessageType(MessageInfo messageInfo) {
        return null;
    }

}


/*

售前消息    售前消息    paid之前的消息
售后消息    售后消息    paid之后的消息
平台消息    通过eBay渠道发来的消息，发件人为：ebay@ebay.com.au
平台消息    offer    主题包含：New offer received;Counteroffer submitted to buyer;Counter offer pending reminder;Offer expired
平台消息    request    主题包含：Return requested;Respond to your open request;A request is still open for;Your buyer opened a request;Feedback revision request;
平台消息    return    主题包含：Case #;Unpaid item case

 */