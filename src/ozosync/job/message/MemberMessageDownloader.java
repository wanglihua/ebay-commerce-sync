package ozosync.job.message;

import com.ebay.sdk.ApiContext;
import com.ebay.sdk.call.GetMemberMessagesCall;
import com.ebay.soap.eBLBaseComponents.MemberMessageExchangeType;
import com.ebay.soap.eBLBaseComponents.MessageTypeCodeType;
import com.ebay.soap.eBLBaseComponents.PaginationType;
import java.util.Calendar;
import java.util.List;
import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ozosync.domain.EBayApiConfigInfo;
import ozosync.job.service.EbayApiContextService;
import ozosync.service.EBayApiConfigService;
import ozosync.service.MessageService;

@Service
public class MemberMessageDownloader {

    private static final int NUMBER_OF_DAY = 1;

    private Logger log = Logger.getLogger(MemberMessageDownloader.class);

    @Autowired
    private EbayApiContextService ebayApiContextService;

    @Autowired
    private EBayApiConfigService eBayApiConfigService;

    @Autowired
    private MessageService messageService;

    @Autowired
    private SessionFactory sessionFactory;

    @SuppressWarnings("unchecked")
    public void download() {
        try {
            Session session = sessionFactory.getCurrentSession();

            List<EBayApiConfigInfo> ebayApiConfigList = eBayApiConfigService.getConfiglist();
            for (EBayApiConfigInfo ebayApiConfigInfo : ebayApiConfigList) {
                try {
                    GetMemberMessagesCall getMemberMessagesCall = createGetMemberMessagesCall(ebayApiConfigInfo);

                    int pageNumber = 0;

                    do {
                        getMemberMessagesCall.getPagination().setPageNumber(++pageNumber);
                        MemberMessageExchangeType[] memberMessageExchangeTypes = getMemberMessagesCall.getMemberMessages();

                        if (memberMessageExchangeTypes != null && memberMessageExchangeTypes.length != 0) {
                            for (MemberMessageExchangeType memberMessageExchangeType : memberMessageExchangeTypes) {
                                messageService.saveDownloadMessage(ebayApiConfigInfo, memberMessageExchangeType);
                            }
                        }
                    } while (getMemberMessagesCall.getHasMoreItems());

                    session.flush();

                } catch (Exception exp) {
                    log.fatal(exp.getMessage(), exp);
                }
            }
        } catch (Exception exp) {
            log.fatal(exp.getMessage(), exp);
        }
    }

    private GetMemberMessagesCall createGetMemberMessagesCall(EBayApiConfigInfo ebayApiConfigInfo) {
        ApiContext apiContext = ebayApiContextService.getApiContextByConfig(ebayApiConfigInfo);

        GetMemberMessagesCall getMemberMessagesCall = new GetMemberMessagesCall(apiContext);
        getMemberMessagesCall.setMailMessageType(MessageTypeCodeType.ALL);

        Calendar startCreateTime = Calendar.getInstance();
        startCreateTime.add(Calendar.DATE, -NUMBER_OF_DAY);
        getMemberMessagesCall.setStartCreationTime(startCreateTime);

        Calendar endCreateTime = Calendar.getInstance();
        getMemberMessagesCall.setEndCreationTime(endCreateTime);

        PaginationType pagination = new PaginationType();
        pagination.setEntriesPerPage(200);
        getMemberMessagesCall.setPagination(pagination);

        return getMemberMessagesCall;
    }
}
