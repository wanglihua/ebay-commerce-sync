package ozosync.web.mvc;

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
import ozosync.domain.OrderInfo;
import ozosync.domain.OrderRefundInfo;
import ozosync.domain.PaypalAccountInfo;
import ozosync.domain.PaypalPaymentInfo;
import ozosync.domain.RefundStatus;
import ozosync.domain.StatusInfo;
import ozosync.domain.schema.PaypalPaymentSchema;
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

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    public ModelAndView refund(@RequestParam(value = "order_refund_id") Long orderRefundId) throws Exception {

        Session session = sessionFactory.getCurrentSession();

        OrderRefundInfo orderRefundInfo = (OrderRefundInfo) session.get(OrderRefundInfo.class, orderRefundId);
        if (orderRefundInfo == null) {
            throw new Exception(String.format("不存在对应的订单退款申请信息！ %d", orderRefundId));
        }

        if (orderRefundInfo.getStatusId() != RefundStatus.REFUND_STATUS_YSH && orderRefundInfo.getStatusId() != RefundStatus.REFUND_STATUS_YSB) {
            throw new Exception(String.format("订单退款当前不是已审核或已失败状态！ %d", orderRefundId));
        }

        PaypalPaymentInfo paypalPaymentInfo = (PaypalPaymentInfo) session.createCriteria(PaypalPaymentInfo.class)
                .add(Restrictions.eq(PaypalPaymentSchema.transactionId, orderRefundInfo.getPayTranId()))
                .setMaxResults(1)
                .uniqueResult();

        if (paypalPaymentInfo == null) {
            throw new Exception(String.format("不存在对应的订单付款信息！ %s", orderRefundInfo.getPayTranId()));
        }

        PaypalAccountInfo paypalAccountInfo = (PaypalAccountInfo) session.get(PaypalAccountInfo.class, paypalPaymentInfo.getAccountId());

        // PayPalAPIInterfaceServiceService
        Map<String, String> configMap = new HashMap<>();
        configMap.put("mode", "live");
        configMap.put("acct1.UserName", paypalAccountInfo.getApiUserName());
        configMap.put("acct1.Password", paypalAccountInfo.getApiPassword());
        configMap.put("acct1.Signature", paypalAccountInfo.getSignature());

        PayPalAPIInterfaceServiceService service = new PayPalAPIInterfaceServiceService(configMap);


        BasicAmountType amountType = new BasicAmountType();
        amountType.setValue(new DecimalFormat("0.00").format(orderRefundInfo.getRefundAmount()));
        amountType.setCurrencyID(CurrencyCodeType.AUD);

        RefundTransactionRequestType refundTransactionRequestType = new RefundTransactionRequestType();
        refundTransactionRequestType.setAmount(amountType);
        refundTransactionRequestType.setTransactionID(paypalPaymentInfo.getTransactionId());
        refundTransactionRequestType.setMemo(String.format("ebay order %s refund", orderRefundInfo.getOrderCode()));
        if (paypalPaymentInfo.getGrossAmount().compareTo(orderRefundInfo.getRefundAmount()) > 0) {
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

            orderRefundInfo.setTransactionId(refundTransactionId);

            StatusInfo refundSuccessStatus = (StatusInfo) session.get(StatusInfo.class, RefundStatus.REFUND_STATUS_YCG);
            orderRefundInfo.setStatusId(RefundStatus.REFUND_STATUS_YCG);
            orderRefundInfo.setStatusName(refundSuccessStatus.getName());

            session.update(orderRefundInfo);
            session.flush();

            // 记录订单历史信息
            OrderInfo orderInfo = (OrderInfo) session.get(OrderInfo.class, orderRefundInfo.getOrderId());
            orderHistoryService.insertOrderHistory(orderInfo, "订单退款, 金额:" + new DecimalFormat("0.00").format(orderRefundInfo.getRefundAmount()));

            session.flush();

            return new ModelAndView(new MappingJackson2JsonView(), new HashMap<String, Object>() {{
                put("success", true);
                put("message", "退款成功！");
            }});

        } else {

            StatusInfo refundFailedStatus = (StatusInfo) session.get(StatusInfo.class, RefundStatus.REFUND_STATUS_YSB);
            orderRefundInfo.setStatusId(RefundStatus.REFUND_STATUS_YSB);
            orderRefundInfo.setStatusName(refundFailedStatus.getName());

            session.update(orderRefundInfo);
            session.flush();

            final StringBuilder sb = new StringBuilder();
            List<ErrorType> errorTypeList = refundTransactionResponseType.getErrors();
            for (ErrorType errorType : errorTypeList) {
                log.error("退款错误！");
                log.error(errorType.getShortMessage());
                log.error(errorType.getLongMessage());

                sb.append(errorType.getShortMessage());
                sb.append(errorType.getLongMessage());
            }

            return new ModelAndView(new MappingJackson2JsonView(), new HashMap<String, Object>() {{
                put("success", false);
                put("message", "退款失败！" + sb.toString());
            }});
        }
    }
}
