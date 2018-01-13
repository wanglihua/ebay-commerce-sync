package ozosync.job.shop.database;

import com.ebay.soap.eBLBaseComponents.OrderType;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import ozosync.domain.EBayApiConfigInfo;
import ozosync.domain.OrderInfo;
import ozosync.domain.schema.OrderSchema;

public class OrderSaveExecuter extends DataBaseExecuter {

    private OrderType orderDownload;

    public void setOrderDownload(OrderType orderDownload) {
        this.orderDownload = orderDownload;
    }

    private EBayApiConfigInfo apiConfigInfo;

    public void setApiConfigInfo(EBayApiConfigInfo apiConfigInfo) {
        this.apiConfigInfo = apiConfigInfo;
    }

    public OrderSaveExecuter(SessionFactory sessionFactory) {
        super(sessionFactory);
    }

    @Override
    protected Object executeInternal(Session session) {
        String orderCode = orderDownload.getOrderID();
        String[] orderCodeSplitArray = orderCode.split("-");
        if (orderCodeSplitArray.length > 1 && orderCodeSplitArray[1].length() > 13) {
            orderCode = orderCodeSplitArray[1];
        }

        OrderInfo orderInDb = (OrderInfo) session.createCriteria(OrderInfo.class)
                .add(Restrictions.eq(OrderSchema.code, orderCode))
                .uniqueResult();

        boolean orderNotExist = orderInDb == null;
        if (orderNotExist) {
            orderInDb = new OrderInfo();
        }

        orderInDb.setCode(orderCode);
        orderInDb.setShopAccount(apiConfigInfo.getShopAccount());
        orderInDb.setTranId(orderDownload.getShippingDetails().getSellingManagerSalesRecordNumber().toString());
        orderInDb.setFullTranId(apiConfigInfo.getShopAccount().getCode() + orderDownload.getShippingDetails().getSellingManagerSalesRecordNumber());

        if (orderNotExist) {
            session.save(orderInDb);
        } else {
            session.update(orderInDb);
        }

        session.flush();

        return null;
    }
}
