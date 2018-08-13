package ozosync.job.message;

import java.util.List;
import org.apache.log4j.Logger;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ozosync.domain.EBayApiConfigInfo;
import ozosync.service.EBayApiConfigService;
import ozosync.service.MessageService;

@Service
public class MemberMessageDownloader {

    private static final int NUMBER_OF_DAY = 1;

    private Logger log = Logger.getLogger(MemberMessageDownloader.class);

    @Autowired
    private EBayApiConfigService eBayApiConfigService;

    @Autowired
    private MessageService messageService;

    @Autowired
    private SessionFactory sessionFactory;

    @SuppressWarnings("unchecked")
    public void download() {
        try {
            List<EBayApiConfigInfo> ebayApiConfigList = eBayApiConfigService.getConfiglist();
            for (EBayApiConfigInfo ebayApiConfigInfo : ebayApiConfigList) {
                try {
                    messageService.downloadMemberMessagesInNumberOfDays(ebayApiConfigInfo, NUMBER_OF_DAY);
                } catch (Exception exp) {
                    log.fatal(exp.getMessage(), exp);
                }
            }
        } catch (Exception exp) {
            log.fatal(exp.getMessage(), exp);
        }
    }
}
