package ozosync.job.shop.database;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

public class OrderSaveExecuter extends DataBaseExecuter {

    public OrderSaveExecuter(SessionFactory sessionFactory) {
        super(sessionFactory);
    }

    @Override
    protected Object executeInternal(Session session) {
        return null;
    }
}
