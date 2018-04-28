package ozosync.job.message;

import com.ebay.sdk.ApiContext;
import com.ebay.sdk.call.GetMemberMessagesCall;
import com.ebay.soap.eBLBaseComponents.MemberMessageExchangeType;
import java.util.Arrays;
import java.util.List;
import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ozosync.domain.EBayApiConfigInfo;
import ozosync.job.service.ApiContextService;
import ozosync.service.EBayApiConfigService;

@Service
public class MemberMessageDownloader {

    private Logger log = Logger.getLogger(MemberMessageDownloader.class);

    @Autowired
    private ApiContextService apiContextService;

    @Autowired
    private EBayApiConfigService eBayApiConfigService;

    @Autowired
    private SessionFactory sessionFactory;

    @SuppressWarnings("unchecked")
    public void download() {
        try {
            Session session = sessionFactory.getCurrentSession();

            List<EBayApiConfigInfo> ebayApiConfigList = eBayApiConfigService.getConifglist();
            for (EBayApiConfigInfo ebayApiConfigInfo : ebayApiConfigList) {
                try {
                    GetMemberMessagesCall getMemberMessagesCall = createGetMemberMessagesCall(ebayApiConfigInfo);

                    int pageNumber = 0;

                    do {
                        getMemberMessagesCall.getPagination().setPageNumber(++pageNumber);
                        MemberMessageExchangeType[] memberMessageExchangeTypes = getMemberMessagesCall.getMemberMessages();

                        if (memberMessageExchangeTypes != null && memberMessageExchangeTypes.length != 0) {
                            for (MemberMessageExchangeType memberMessageExchangeType : memberMessageExchangeTypes) {
                                //
                            }
                        }
                    } while (getMemberMessagesCall.getHasMoreItems());

                } catch (Exception exp) {
                    log.fatal(exp);
                    log.fatal(Arrays.toString(exp.getStackTrace()).replace(",", "\r\n"));
                }
            }
        } catch (Exception exp) {
            log.fatal(exp);
            log.fatal(Arrays.toString(exp.getStackTrace()).replace(",", "\r\n"));
        }
    }

    private GetMemberMessagesCall createGetMemberMessagesCall(EBayApiConfigInfo ebayApiConfigInfo) {
        ApiContext apiContext = apiContextService.getApiContextByConfig(ebayApiConfigInfo);
        GetMemberMessagesCall getMemberMessagesCall = new GetMemberMessagesCall(apiContext);

        return getMemberMessagesCall;
    }

}
