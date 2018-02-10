package ozosync.web.mvc;

import com.ebay.sdk.ApiAccount;
import com.ebay.sdk.ApiContext;
import com.ebay.sdk.ApiCredential;
import com.ebay.sdk.call.AddMemberMessageRTQCall;
import com.ebay.soap.eBLBaseComponents.MemberMessageType;
import com.ebay.soap.eBLBaseComponents.SiteCodeType;
import java.util.HashMap;
import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;
import ozosync.domain.EBayApiConfigInfo;
import ozosync.domain.MessageInfo;
import ozosync.domain.StatusInfo;

import java.util.List;
import ozosync.domain.schema.EBayApiConfigSchema;

@Controller
@RequestMapping(value = "/message")
public class MessageController {

    private Logger log = Logger.getLogger(MessageController.class);

    @Autowired
    private SessionFactory sessionFactory;

    @RequestMapping(value = "/", method = RequestMethod.GET)
    @SuppressWarnings("unchecked")
    public String index() {

        Session session = sessionFactory.getCurrentSession();

        List<StatusInfo> statusList = session.createCriteria(StatusInfo.class).list();

        return "home";
    }

    @SuppressWarnings("unchecked")
    @RequestMapping(value = "/reply", method = RequestMethod.POST)
    public ModelAndView reply(@RequestParam(value = "messageId") String messageId, @RequestParam(value = "messageBody") String messageBody) throws Exception {
        Session session = sessionFactory.getCurrentSession();


        MessageInfo messageInfo = (MessageInfo) session.get(MessageInfo.class, messageId);
        ApiContext apiContext = getApiContextByShopId(messageInfo.getShopId());

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

    @SuppressWarnings("unchecked")
    @RequestMapping(value = "/test", method = RequestMethod.GET)
    public ModelAndView test() throws Exception {
        Session session = sessionFactory.getCurrentSession();

        return new ModelAndView(new MappingJackson2JsonView(), new HashMap<String, Object>() {{
            put("success", true);
            put("message", "消息回复成功！");
        }});
    }

    private ApiContext getApiContextByShopId(Long shopId) {
        Session session = sessionFactory.getCurrentSession();

        EBayApiConfigInfo ebayApiConfigInfo = (EBayApiConfigInfo) session.createCriteria(EBayApiConfigInfo.class)
                .add(Restrictions.eq(EBayApiConfigSchema.shopId, shopId))
                .setMaxResults(1)
                .uniqueResult();

        ApiContext apiContext = new ApiContext();

        ApiCredential apiCredential = apiContext.getApiCredential();

        ApiAccount apiAccount = new ApiAccount();
        apiAccount.setApplication(ebayApiConfigInfo.getAppId());
        apiAccount.setCertificate(ebayApiConfigInfo.getCertId());
        apiAccount.setDeveloper(ebayApiConfigInfo.getDevId());

        apiCredential.setApiAccount(apiAccount);

        apiCredential.seteBayToken(ebayApiConfigInfo.getToken());
        apiContext.setApiServerUrl("https://api.ebay.com/wsapi");

        apiContext.getApiLogging().setLogSOAPMessages(true);

        apiContext.setSite(SiteCodeType.AUSTRALIA);

        return apiContext;
    }
}
