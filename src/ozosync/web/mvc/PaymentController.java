package ozosync.web.mvc;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
import ozosync.domain.OrderExternalTransactionInfo;
import ozosync.domain.OrderInfo;
import ozosync.domain.OrderRefundInfo;
import ozosync.domain.PaypalAccountInfo;
import ozosync.domain.PaypalPaymentInfo;
import ozosync.domain.schema.OrderExternalTransactionSchema;
import ozosync.service.OrderHistoryService;
import urn.ebay.api.PayPalAPI.PayPalAPIInterfaceServiceService;
import urn.ebay.api.PayPalAPI.RefundTransactionReq;
import urn.ebay.api.PayPalAPI.RefundTransactionRequestType;
import urn.ebay.api.PayPalAPI.RefundTransactionResponseType;
import urn.ebay.apis.CoreComponentTypes.BasicAmountType;
import urn.ebay.apis.eBLBaseComponents.AckCodeType;
import urn.ebay.apis.eBLBaseComponents.CurrencyCodeType;
import urn.ebay.apis.eBLBaseComponents.ErrorType;
import urn.ebay.apis.eBLBaseComponents.RefundType;

@Controller
@RequestMapping(value = "/payment")
public class PaymentController {

    private Logger log = Logger.getLogger(PaymentController.class);

    @Autowired
    private SessionFactory sessionFactory;

    @Autowired
    private OrderHistoryService orderHistoryService;

    @SuppressWarnings("unchecked")
    @RequestMapping(value = "/refund", method = RequestMethod.POST)
    public ModelAndView refund(@RequestParam(value = "transactionId") Long transactionId,
                               @RequestParam(value = "amount") BigDecimal amount,
                               @RequestParam(value = "memo") String memo) throws Exception {

        Session session = sessionFactory.getCurrentSession();

        OrderExternalTransactionInfo orderExternalTransactionInfo = (OrderExternalTransactionInfo) session.createCriteria(OrderExternalTransactionInfo.class)
                .add(Restrictions.eq(OrderExternalTransactionSchema.transactionId, transactionId))
                .setMaxResults(1)
                .uniqueResult();

        if (orderExternalTransactionInfo == null || orderExternalTransactionInfo.getPaymentId() != 0L) {
            throw new Exception(String.format("Transaction: %s 不存在对应的订单付款信息", transactionId));
        }

        PaypalPaymentInfo paypalPaymentInfo = (PaypalPaymentInfo) session.get(PaypalPaymentInfo.class, orderExternalTransactionInfo.getPaymentId());
        PaypalAccountInfo paypalAccountInfo = (PaypalAccountInfo) session.get(PaypalAccountInfo.class, paypalPaymentInfo.getAccountId());

        // PayPalAPIInterfaceServiceService
        Map<String, String> configMap = new HashMap<>();
        configMap.put("mode", "live");
        configMap.put("acct1.UserName", paypalAccountInfo.getApiUserName());
        configMap.put("acct1.Password", paypalAccountInfo.getApiPassword());
        configMap.put("acct1.Signature", paypalAccountInfo.getSignature());

        PayPalAPIInterfaceServiceService service = new PayPalAPIInterfaceServiceService(configMap);


        BasicAmountType amountType = new BasicAmountType();
        amountType.setValue(amount.toString());
        amountType.setCurrencyID(CurrencyCodeType.AUD);

        RefundTransactionRequestType refundTransactionRequestType = new RefundTransactionRequestType();
        refundTransactionRequestType.setAmount(amountType);
        refundTransactionRequestType.setTransactionID(paypalPaymentInfo.getTransactionId());
        refundTransactionRequestType.setMemo(memo);
        if (paypalPaymentInfo.getGrossAmount().compareTo(amount) > 0) {
            refundTransactionRequestType.setRefundType(RefundType.PARTIAL);
        } else {
            refundTransactionRequestType.setRefundType(RefundType.FULL);
        }

        RefundTransactionReq refundTransactionReq = new RefundTransactionReq();
        refundTransactionReq.setRefundTransactionRequest(refundTransactionRequestType);
        RefundTransactionResponseType refundTransactionResponseType = service.refundTransaction(refundTransactionReq);

        if (refundTransactionResponseType.getAck() == AckCodeType.SUCCESS) {
            String refundTransactionId = refundTransactionResponseType.getRefundTransactionID();
            refundTransactionId = refundTransactionId.replace("'", "");

            // 记录订单退款信息
            OrderRefundInfo orderRefundInfo = new OrderRefundInfo();
            orderRefundInfo.setOrderId(orderExternalTransactionInfo.getOrderId());
            orderRefundInfo.setTransactionId(refundTransactionId);
            orderRefundInfo.setAmount(amount);
            orderRefundInfo.setDescription(memo);

            session.save(orderRefundInfo);

            // 记录订单历史信息
            OrderInfo orderInfo = (OrderInfo) session.get(OrderInfo.class, orderExternalTransactionInfo.getOrderId());
            orderHistoryService.insertOrderHistory(orderInfo, "订单退款, 金额:" + amount.toString());

            session.flush();

            return new ModelAndView(new MappingJackson2JsonView(), new HashMap<String, Object>() {{
                put("success", true);
                put("message", "退款成功！");
            }});

        } else {
            List<ErrorType> errorTypeList = refundTransactionResponseType.getErrors();
            for (ErrorType errorType : errorTypeList) {
                log.error("退款错误！");
                log.error(errorType.getShortMessage());
                log.error(errorType.getLongMessage());
            }

            return new ModelAndView(new MappingJackson2JsonView(), new HashMap<String, Object>() {{
                put("success", false);
                put("message", "退款失败！");
            }});
        }
    }
}
