package ozosync.job.shop.database;

import com.ebay.soap.eBLBaseComponents.AddressType;
import com.ebay.soap.eBLBaseComponents.CompleteStatusCodeType;
import com.ebay.soap.eBLBaseComponents.OrderStatusCodeType;
import com.ebay.soap.eBLBaseComponents.OrderType;
import com.ebay.soap.eBLBaseComponents.ShippingServiceOptionsType;
import com.ebay.soap.eBLBaseComponents.TransactionArrayType;
import com.ebay.soap.eBLBaseComponents.TransactionType;
import java.math.BigDecimal;
import java.math.MathContext;
import java.util.Date;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Property;
import org.hibernate.criterion.Restrictions;
import ozosync.domain.EBayApiConfigInfo;
import ozosync.domain.ExchangeRateInfo;
import ozosync.domain.OrderHistoryInfo;
import ozosync.domain.OrderInfo;
import ozosync.domain.OrderShippingAddressInfo;
import ozosync.domain.OrderStatus;
import ozosync.domain.StatusInfo;
import ozosync.domain.schema.OrderSchema;
import ozosync.domain.schema.OrderShippingAddressSchema;
import ozosync.service.ExchangeRateService;

public class OrderSaveExecuter extends DataBaseExecuter {

    private OrderType orderDownload;

    public void setOrderDownload(OrderType orderDownload) {
        this.orderDownload = orderDownload;
    }

    private EBayApiConfigInfo apiConfigInfo;

    public void setApiConfigInfo(EBayApiConfigInfo apiConfigInfo) {
        this.apiConfigInfo = apiConfigInfo;
    }

    private OrderRepository orderRepository = new OrderRepository();
    private OrderShippingAddressRepository orderShippingAddressRepository = new OrderShippingAddressRepository();

    public OrderSaveExecuter(SessionFactory sessionFactory) {
        super(sessionFactory);
    }

    @Override
    protected Object executeInternal(Session session) {
        // order
        OrderInfo orderInfo = orderRepository.saveOrder(session, orderDownload, apiConfigInfo);

        // order shipping address
        orderShippingAddressRepository.saveOrderShippingAddress(session, orderDownload, orderInfo);

        return orderInfo;
    }

    private void saveOrderEntries(Session session, OrderType orderDownload) {
        // 保存订单明细
        TransactionArrayType transactionArray = orderDownload.getTransactionArray();

        if (transactionArray != null && transactionArray.getTransactionLength() > 0) {
            int transactionLength = transactionArray.getTransactionLength();
            for (int index = 0; index < transactionLength; index++) {
                TransactionType transaction = transactionArray.getTransaction(index);
                String itemSku = transaction.getItem().getSKU();


                System.out.println("transaction id: " + transaction.getTransactionID());
            }
        }
    }
}
