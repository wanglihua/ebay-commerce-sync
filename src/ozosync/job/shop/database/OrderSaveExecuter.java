package ozosync.job.shop.database;

import com.ebay.soap.eBLBaseComponents.OrderType;
import java.util.Arrays;
import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import ozosync.domain.EBayApiConfigInfo;
import ozosync.domain.OrderInfo;

public class OrderSaveExecuter extends DataBaseExecuter {

    private Logger log = Logger.getLogger(OrderSaveExecuter.class);

    private OrderType orderDownload;

    public void setOrderDownload(OrderType orderDownload) {
        this.orderDownload = orderDownload;
    }

    private EBayApiConfigInfo apiConfigInfo;

    public void setApiConfigInfo(EBayApiConfigInfo apiConfigInfo) {
        this.apiConfigInfo = apiConfigInfo;
    }

    private OrderRepository orderRepository = new OrderRepository();
    private OrderEntryRepository orderEntryRepository = new OrderEntryRepository();
    private OrderShippingAddressRepository orderShippingAddressRepository = new OrderShippingAddressRepository();

    public OrderSaveExecuter(SessionFactory sessionFactory) {
        super(sessionFactory);
    }

    @Override
    protected Object executeInternal(Session session) {
        OrderInfo orderInfo = null;

        try {
            try {
                // order
                orderInfo = orderRepository.saveOrder(session, orderDownload, apiConfigInfo);

                // order entry
                orderEntryRepository.saveOrderEntries(session, orderInfo, orderDownload);
            } catch (Exception exp) {
                log.fatal(exp);
                log.fatal(Arrays.toString(exp.getStackTrace()).replace(",", "\r\n"));
            }

            // 这样可以在保存明细出错时，继续保存收货地址
            try {
                // order shipping address
                orderShippingAddressRepository.saveOrderShippingAddress(session, orderDownload, orderInfo);
            } catch (Exception exp) {
                log.fatal(exp);
                log.fatal(Arrays.toString(exp.getStackTrace()).replace(",", "\r\n"));
            }

            return orderInfo;
        } catch (Exception exp) {
            log.fatal(exp);
            log.fatal(Arrays.toString(exp.getStackTrace()).replace(",", "\r\n"));
        }

        return orderInfo;
    }
}
