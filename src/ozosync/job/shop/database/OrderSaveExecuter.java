package ozosync.job.shop.database;

import com.ebay.soap.eBLBaseComponents.OrderType;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

public class OrderSaveExecuter extends DataBaseExecuter {

    private OrderType order;

    public void setOrder(OrderType order) {
        this.order = order;
    }

    public OrderSaveExecuter(SessionFactory sessionFactory) {
        super(sessionFactory);
    }

    @Override
    protected Object executeInternal(Session session) {
        return null;
    }
}
