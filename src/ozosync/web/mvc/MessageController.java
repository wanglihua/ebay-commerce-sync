package ozosync.web.mvc;

import java.util.HashMap;
import java.util.List;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;
import ozosync.service.MessageService;

@Controller
@RequestMapping(value = "/message")
public class MessageController {

    private Logger log = Logger.getLogger(MessageController.class);

    @Autowired
    private MessageService messageService;

    @SuppressWarnings("unchecked")
    @RequestMapping(value = "/reply", method = RequestMethod.POST)
    public ModelAndView reply(@RequestParam(value = "messageId") Long messageId, @RequestParam(value = "messageBody") String messageBody) {
        try {
            messageService.reply(messageId, messageBody);
        } catch (final Exception exp) {
            log.fatal(exp.getMessage(), exp);

            return new ModelAndView(new MappingJackson2JsonView(), new HashMap<String, Object>() {{
                put("success", false);
                put("message", exp.getMessage());
            }});
        }

        return new ModelAndView(new MappingJackson2JsonView(), new HashMap<String, Object>() {{
            put("success", true);
            put("message", "消息回复成功！");
        }});
    }

    @RequestMapping(value = "/send/message/to/customers", method = RequestMethod.POST)
    public ModelAndView sendMessageToCustomers(@RequestParam(value = "orderIdList") List<Long> orderIdList,
                                               @RequestParam(value = "subject") String subject,
                                               @RequestParam(value = "body") String body) throws Exception {

        if (orderIdList == null || orderIdList.size() == 0) {
            return new ModelAndView(new MappingJackson2JsonView(), new HashMap<String, Object>() {{
                put("success", false);
                put("message", "订单列表不能为空！");
            }});
        }

        final List<String> failOrderCodeList = messageService.sendMessageToCustomers(orderIdList, subject, body);

        if (failOrderCodeList != null && failOrderCodeList.size() != 0) {
            return new ModelAndView(new MappingJackson2JsonView(), new HashMap<String, Object>() {{
                put("success", false);
                put("message", String.format("订单 %s 消息发送失败！", String.join(",", failOrderCodeList)));
            }});
        }

        return new ModelAndView(new MappingJackson2JsonView(), new HashMap<String, Object>() {{
            put("success", true);
            put("message", "消息发送成功！");
        }});
    }
}
