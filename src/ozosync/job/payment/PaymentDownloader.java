package ozosync.job.payment;

import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ozosync.domain.OrderExternalTransactionInfo;
import ozosync.domain.PaypalAccountInfo;
import ozosync.domain.PaypalPaymentInfo;
import ozosync.domain.schema.OrderExternalTransactionSchema;
import ozosync.domain.schema.PaypalPaymentSchema;
import ozosync.service.PaypalApiConfigService;
import urn.ebay.api.PayPalAPI.PayPalAPIInterfaceServiceService;
import urn.ebay.api.PayPalAPI.TransactionSearchReq;
import urn.ebay.api.PayPalAPI.TransactionSearchRequestType;
import urn.ebay.api.PayPalAPI.TransactionSearchResponseType;
import urn.ebay.apis.eBLBaseComponents.AckCodeType;
import urn.ebay.apis.eBLBaseComponents.ErrorType;
import urn.ebay.apis.eBLBaseComponents.PaymentTransactionSearchResultType;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;


@Service
public class PaymentDownloader {

    private static final int NUMBER_OF_DAY = 1;
    private static final int MAX_FETCH_COUNT = 100;
    private static final int MAX_CALL_COUNT = 30;

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
                    downloadAccountPaymentList(paypalAccountInfo);
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

    public void downloadAccountPaymentList(PaypalAccountInfo paypalAccountInfo) throws Exception {

        Session session = sessionFactory.getCurrentSession();

        // PayPalAPIInterfaceServiceService
        Map<String, String> configMap = new HashMap<>();
        configMap.put("mode", "live");
        configMap.put("acct1.UserName", paypalAccountInfo.getApiUserName());
        configMap.put("acct1.Password", paypalAccountInfo.getApiPassword());
        configMap.put("acct1.Signature", paypalAccountInfo.getSignature());

        PayPalAPIInterfaceServiceService service = new PayPalAPIInterfaceServiceService(configMap);

        // TransactionSearchRequestType
        TransactionSearchRequestType requestType = new TransactionSearchRequestType();

        Calendar startTime = Calendar.getInstance();
        startTime.add(Calendar.DATE, -NUMBER_OF_DAY);
        requestType.setStartDate(getTimeString(startTime.getTime()));

        Calendar endTime = Calendar.getInstance();
        String endTimeStr = getTimeString(endTime.getTime());

        int resultCount = 0;

        int totalCallCount = 0;

        do {
            requestType.setEndDate(endTimeStr);

            // System.out.println("startTime: " + getTimeString(startTime.getTime()));
            // System.out.println("endTime: " + endTimeStr);


            // TransactionSearchReq
            TransactionSearchReq transactionSearchReq = new TransactionSearchReq();
            transactionSearchReq.setTransactionSearchRequest(requestType);

            // TransactionSearchResponseType
            TransactionSearchResponseType response = service.transactionSearch(transactionSearchReq);

            List<PaymentTransactionSearchResultType> paymentTransactionSearchResultTypeList = response.getPaymentTransactions();

            resultCount = paymentTransactionSearchResultTypeList.size();

            if (resultCount > 0) {

                for (PaymentTransactionSearchResultType searchResultType : paymentTransactionSearchResultTypeList) {
                    try {
                        String transactionId = searchResultType.getTransactionID();

                        PaypalPaymentInfo paypalPaymentInfo = (PaypalPaymentInfo) session.createCriteria(PaypalPaymentInfo.class)
                                .add(Restrictions.eq(PaypalPaymentSchema.transactionId, transactionId))
                                .setMaxResults(1)
                                .uniqueResult();

                        if (paypalPaymentInfo == null) {
                            paypalPaymentInfo = new PaypalPaymentInfo();

                            paypalPaymentInfo.setAccountId(paypalAccountInfo.getId());
                            paypalPaymentInfo.setPaypalAccount(paypalAccountInfo.getPaypalAccount());

                            paypalPaymentInfo.setTransactionId(transactionId);
                            paypalPaymentInfo.setType(searchResultType.getType());

                            String payer = searchResultType.getPayer();
                            if (payer == null) {
                                payer = "";
                            }
                            paypalPaymentInfo.setPayer(payer);

                            String payerDisplayName = searchResultType.getPayerDisplayName();
                            if (payerDisplayName == null) {
                                payerDisplayName = "";
                            }
                            paypalPaymentInfo.setPayerDisplayName(payerDisplayName);

                            paypalPaymentInfo.setCurrency(searchResultType.getGrossAmount().getCurrencyID().getValue());
                            paypalPaymentInfo.setGrossAmount(new BigDecimal(searchResultType.getGrossAmount().getValue()));
                            paypalPaymentInfo.setFeeAmount(new BigDecimal(searchResultType.getFeeAmount().getValue()));
                            paypalPaymentInfo.setNetAmount(new BigDecimal(searchResultType.getNetAmount().getValue()));
                            paypalPaymentInfo.setStatus(searchResultType.getStatus());

                            SimpleDateFormat payTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            String payTimeStr = searchResultType.getTimestamp().replace("T", " ").replace("Z", "");
                            paypalPaymentInfo.setPayTime(payTimeFormat.parse(payTimeStr));

                            paypalPaymentInfo.setCreateTime(new Date());
                            paypalPaymentInfo.setUpdateTime(new Date());

                            session.save(paypalPaymentInfo);

                            session.flush();

                            OrderExternalTransactionInfo orderExternalTransactionInfo = (OrderExternalTransactionInfo) session.createCriteria(OrderExternalTransactionInfo.class)
                                    .add(Restrictions.eq(OrderExternalTransactionSchema.transactionId, transactionId))
                                    .setMaxResults(1)
                                    .uniqueResult();

                            if (orderExternalTransactionInfo != null) {
                                orderExternalTransactionInfo.setPaymentId(paypalPaymentInfo.getId());
                                orderExternalTransactionInfo.setUpdateTime(new Date());
                                session.update(orderExternalTransactionInfo);
                            }

                            session.flush();

                            if (endTimeStr.compareTo(searchResultType.getTimestamp()) > 0) {
                                endTimeStr = searchResultType.getTimestamp();
                            }
                        }
                    } catch (Exception exp) {
                        log.fatal(exp);
                        log.fatal(Arrays.toString(exp.getStackTrace()).replace(",", "\r\n"));
                    }
                }
            }

            if (response.getAck() == AckCodeType.FAILURE) {
                List<ErrorType> errorList = response.getErrors();
                for (ErrorType errorType : errorList) {
                    log.fatal(errorType.getShortMessage());
                    log.fatal(errorType.getLongMessage());
                }
            }
            totalCallCount++;
        } while (resultCount > 0 && resultCount >= MAX_FETCH_COUNT && totalCallCount <= MAX_CALL_COUNT); // >= 中的 > 号是不成立的

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