package ozosync.job.order.service;

import com.ebay.soap.eBLBaseComponents.ExternalTransactionType;
import com.ebay.soap.eBLBaseComponents.OrderType;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ozosync.domain.OrderExternalTransactionInfo;
import ozosync.domain.OrderInfo;
import ozosync.domain.OrderStatus;
import ozosync.domain.PaypalPaymentInfo;
import ozosync.domain.schema.OrderExternalTransactionSchema;
import ozosync.domain.schema.PaypalPaymentSchema;

import java.util.Date;

@Service
public class OrderDownloadExternalTransactionService {

    @Autowired
    private SessionFactory sessionFactory;

    public void saveOrderExternalTransaction(OrderInfo orderInfo, OrderType orderDownload) {
        Session session = sessionFactory.getCurrentSession();

        // 只同步已支付及之前的订单内容
        Long statusId = orderInfo.getStatusId();
        if (statusId > OrderStatus.ID_YZF) {
            return;
        }

        // order external transaction
        Long orderId = orderInfo.getId();
        ExternalTransactionType[] externalTransactionList = orderDownload.getExternalTransaction();
        int externalTransactionLength = orderDownload.getExternalTransactionLength();
        if (externalTransactionList != null && externalTransactionLength != 0) {
            for (int index = 0; index < externalTransactionLength; index++) {
                ExternalTransactionType externalTransaction = orderDownload.getExternalTransaction(index);
                String transactionId = externalTransaction.getExternalTransactionID();
                Date transactionTime = externalTransaction.getExternalTransactionTime().getTime();

                // 保存 order external transaction， 更新 paypal payment 中的 order_id 和 order_code

                OrderExternalTransactionInfo orderExternalTransactionInfo = (OrderExternalTransactionInfo) session.createCriteria(OrderExternalTransactionInfo.class)
                        .add(Restrictions.eq(OrderExternalTransactionSchema.orderId, orderId))
                        .add(Restrictions.eq(OrderExternalTransactionSchema.transactionId, transactionId))
                        .setMaxResults(1)
                        .uniqueResult();

                PaypalPaymentInfo paypalPaymentInfo = (PaypalPaymentInfo) session.createCriteria(PaypalPaymentInfo.class)
                        .add(Restrictions.eq(PaypalPaymentSchema.transactionId, transactionId))
                        .setMaxResults(1)
                        .uniqueResult();

                if (orderExternalTransactionInfo == null) {
                    orderExternalTransactionInfo = new OrderExternalTransactionInfo();
                    orderExternalTransactionInfo.setOrderId(orderId);
                    orderExternalTransactionInfo.setTransactionId(transactionId);
                    orderExternalTransactionInfo.setTransactionTime(transactionTime);

                    if (paypalPaymentInfo != null) {
                        orderExternalTransactionInfo.setPaymentId(paypalPaymentInfo.getId());
                    } else {
                        orderExternalTransactionInfo.setPaymentId(0L);
                    }

                    session.save(orderExternalTransactionInfo);

                    session.flush();

                    if (paypalPaymentInfo != null) {
                        paypalPaymentInfo.setOrderId(orderInfo.getId());
                        paypalPaymentInfo.setOrderCode(orderInfo.getCode());
                        paypalPaymentInfo.setUpdateTime(new Date());

                        session.update(paypalPaymentInfo);
                    }

                    session.flush();
                }
            }
        }

    }
}
