package ozosync.job.payment;

import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ozosync.domain.PaypalAccountInfo;
import ozosync.service.PaypalApiConfigService;
import urn.ebay.api.PayPalAPI.PayPalAPIInterfaceServiceService;
import urn.ebay.api.PayPalAPI.TransactionSearchReq;
import urn.ebay.api.PayPalAPI.TransactionSearchRequestType;
import urn.ebay.api.PayPalAPI.TransactionSearchResponseType;
import urn.ebay.apis.eBLBaseComponents.ErrorType;
import urn.ebay.apis.eBLBaseComponents.PaymentTransactionSearchResultType;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

/*
import urn.ebay.api.PayPalAPI.GetTransactionDetailsReq;
import urn.ebay.api.PayPalAPI.GetTransactionDetailsRequestType;
import urn.ebay.api.PayPalAPI.GetTransactionDetailsResponseType;
import urn.ebay.api.PayPalAPI.TransactionSearchReq;
import urn.ebay.api.PayPalAPI.TransactionSearchRequestType;
import urn.ebay.api.PayPalAPI.TransactionSearchResponseType;
*/

@Service
public class PaymentDownloader {

    private static final int NUMBER_OF_DAY = 1;

    private Logger log = Logger.getLogger(PaymentDownloader.class);

    @Autowired
    private PaypalApiConfigService paypalApiConfigService;

    @Autowired
    private SessionFactory sessionFactory;

    @SuppressWarnings("unchecked")
    public void download() {
        try {
            Session session = sessionFactory.getCurrentSession();

            List<PaypalAccountInfo> paypalAccountInfoList = paypalApiConfigService.getConfiglist();
            for (PaypalAccountInfo paypalAccountInfo : paypalAccountInfoList) {
                try {
                    downloadAccountPaymentList(paypalAccountInfo.getClientId(), paypalAccountInfo.getSecret());
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

    public void downloadAccountPaymentList(String clientId, String secret) throws Exception {

        // TransactionSearchRequestType
        TransactionSearchRequestType requestType = new TransactionSearchRequestType();

        Calendar startTime = Calendar.getInstance();
        startTime.add(Calendar.DATE, -NUMBER_OF_DAY);

        Calendar endTime = Calendar.getInstance();

        requestType.setStartDate(getTimeString(startTime.getTime()));
        requestType.setEndDate(getTimeString(endTime.getTime()));

        // PayPalAPIInterfaceServiceService
        Map<String, String> configMap = new HashMap<>();
        configMap.put("mode", "live");
        configMap.put("acct1.UserName", "support_api1.ozoestore.com.au");
        configMap.put("acct1.Password", "4VZZNR4CK3GG9GHW");
        configMap.put("acct1.Signature", "AB0YTf4hFkAG5QvPtq2A-oOChxRNATZl570inmmXuRssw4HDbkR5w-8i");

        /*
API username：support_api1.ozoestore.com.au
API password：4VZZNR4CK3GG9GHW
Signature：AB0YTf4hFkAG5QvPtq2A-oOChxRNATZl570inmmXuRssw4HDbkR5w-8i
         */

        PayPalAPIInterfaceServiceService service = new PayPalAPIInterfaceServiceService(configMap);

        // TransactionSearchReq
        TransactionSearchReq transactionSearchReq = new TransactionSearchReq();
        transactionSearchReq.setTransactionSearchRequest(requestType);

        // TransactionSearchResponseType
        TransactionSearchResponseType response = service.transactionSearch(transactionSearchReq);

        List<PaymentTransactionSearchResultType> paymentTransactionSearchResultTypeList = response.getPaymentTransactions();

        System.out.println("Payment Transaction Search Result Count: ");
        System.out.println(paymentTransactionSearchResultTypeList.size());

        List<ErrorType> errorList = response.getErrors();
        for (ErrorType errorType : errorList) {
            System.out.println(errorType.getLongMessage());
        }


        int i = 1 + 3;

        /*
        GetTransactionDetailsReq getDetailsReq = new GetTransactionDetailsReq();
        GetTransactionDetailsRequestType getDetailsReqType = new GetTransactionDetailsRequestType();
        getDetailsReqType.setTransactionID(id);
        getDetailsReq.setGetTransactionDetailsRequest(getDetailsReqType);

        GetTransactionDetailsResponseType getDetailsResp = null;
        getDetailsResp = service.getTransactionDetails(getDetailsReq);
        */
    }

    private String getTimeString(Date time) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
        timeFormat.setTimeZone(TimeZone.getTimeZone("GMT"));

        return String.format("%sT%s.000Z", dateFormat.format(time), timeFormat.format(time));
    }
}