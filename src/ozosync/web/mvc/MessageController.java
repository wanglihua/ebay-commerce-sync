package ozosync.web.mvc;

import com.ebay.sdk.ApiContext;
import com.ebay.sdk.call.AddMemberMessageRTQCall;
import com.ebay.soap.eBLBaseComponents.MemberMessageType;
import java.util.HashMap;
import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;
import ozosync.domain.MessageInfo;
import ozosync.service.ApiContextService;

@Controller
@RequestMapping(value = "/message")
public class MessageController {

    private Logger log = Logger.getLogger(MessageController.class);

    @Autowired
    private SessionFactory sessionFactory;

    @Autowired
    private ApiContextService apiContextService;

    @SuppressWarnings("unchecked")
    @RequestMapping(value = "/reply", method = RequestMethod.POST)
    public ModelAndView reply(@RequestParam(value = "messageId") Long messageId, @RequestParam(value = "messageBody") String messageBody) throws Exception {
        Session session = sessionFactory.getCurrentSession();


        MessageInfo messageInfo = (MessageInfo) session.get(MessageInfo.class, messageId);
        ApiContext apiContext = apiContextService.getApiContextByShopId(messageInfo.getShopId());

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

        return new ModelAndView(new MappingJackson2JsonView(), new HashMap<String, Object>() {{
            put("success", true);
            put("message", "消息回复成功！");
        }});
    }
}
