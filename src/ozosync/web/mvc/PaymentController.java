package ozosync.web.mvc;

import java.math.BigDecimal;
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
import ozosync.domain.OrderExternalTransactionInfo;
import ozosync.domain.schema.OrderExternalTransactionSchema;

@Controller
@RequestMapping(value = "/payment")
public class PaymentController {

    private Logger log = Logger.getLogger(PaymentController.class);

    @Autowired
    private SessionFactory sessionFactory;

    @SuppressWarnings("unchecked")
    @RequestMapping(value = "/refund", method = RequestMethod.POST)
    public ModelAndView refund(@RequestParam(value = "transactionId") Long transactionId, @RequestParam(value = "amount") BigDecimal amount) throws Exception {

        Session session = sessionFactory.getCurrentSession();

        OrderExternalTransactionInfo orderExternalTransactionInfo = (OrderExternalTransactionInfo) session.createCriteria(OrderExternalTransactionInfo.class)
                .add(Restrictions.eq(OrderExternalTransactionSchema.transactionId, transactionId))
                .setMaxResults(1)
                .uniqueResult();

        if (orderExternalTransactionInfo == null) {
            throw new Exception(String.format("Transaction: %s 不存在对应的订单付款信息", transactionId));
        }

        session.flush();

        return new ModelAndView(new MappingJackson2JsonView(), new HashMap<String, Object>() {{
            put("success", true);
            put("message", "退款成功！");
        }});
    }
}

/*

        String api_username = "support_api1.ozoestore.com.au";
        String api_password = "4VZZNR4CK3GG9GHW";
        String signature = "AB0YTf4hFkAG5QvPtq2A-oOChxRNATZl570inmmXuRssw4HDbkR5w-8i";

        // PayPalAPIInterfaceServiceService
        Map<String, String> configMap = new HashMap<>();
        configMap.put("mode", "live");
        configMap.put("acct1.UserName", api_username);
        configMap.put("acct1.Password", api_password);
        configMap.put("acct1.Signature", signature);

        PayPalAPIInterfaceServiceService service = new PayPalAPIInterfaceServiceService(configMap);


        BasicAmountType amountType = new BasicAmountType();
        amountType.setValue("0.01");
        amountType.setCurrencyID(CurrencyCodeType.AUD);

        RefundTransactionRequestType refundTransactionRequestType = new RefundTransactionRequestType();
        refundTransactionRequestType.setAmount(amountType);
        refundTransactionRequestType.setTransactionID("TEST_19J52487X0420101R");
        refundTransactionRequestType.setMemo("退款描述");
        refundTransactionRequestType.setRefundType(RefundType.PARTIAL);

        RefundTransactionReq refundTransactionReq = new RefundTransactionReq();
        refundTransactionReq.setRefundTransactionRequest(refundTransactionRequestType);
        RefundTransactionResponseType refundTransactionResponseType = service.refundTransaction(refundTransactionReq);

        if (refundTransactionResponseType.getAck() == AckCodeType.SUCCESS) {
            String refundTransactionId = refundTransactionResponseType.getRefundTransactionID();
            refundTransactionId = refundTransactionId.replace("'", "");
        } else {
            List<ErrorType> errorTypeList = refundTransactionResponseType.getErrors();
            for (ErrorType errorType : errorTypeList) {
                System.out.println(errorType.getLongMessage());
            }
        }

 */