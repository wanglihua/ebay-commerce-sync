package ozosync.service;

import org.apache.log4j.Logger;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;
import ozosync.common.SpringConfigLocation;
import ozosync.domain.EBayApiConfigInfo;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(locations = {SpringConfigLocation.configLocation})
public class MessageServiceTest {

    private Logger log = Logger.getLogger(MessageServiceTest.class);

    @Autowired
    private MessageService messageService;

    @Autowired
    private EBayApiConfigService eBayApiConfigService;

    @Test
    @Transactional
    public void reply() {
        try {
            messageService.reply(15945L, "OK,OK");
        } catch (Exception exp) {
            log.fatal(exp.getMessage(), exp);
        }
    }

    @Test
    @Transactional
    public void downloadMessageInFiveMinutes() throws Exception {
        EBayApiConfigInfo eBayApiConfigInfo = eBayApiConfigService.getConfig(7L);
        messageService.downloadMemberMessagesInNumberOfMinutes(eBayApiConfigInfo, 45);
    }
}
