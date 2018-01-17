package ozosync.job.shop.database;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import ozosync.domain.EBayApiConfigInfo;

public class GetEBayApiConfigListExecuter extends DataBaseExecuter {

    public GetEBayApiConfigListExecuter(SessionFactory sessionFactory) {
        super(sessionFactory);
    }

    /**
     * @param session hibernate session
     * @return List<EBayApiConfigInfo>
     */
    @Override
    protected Object executeInternal(Session session) {
        return session.createCriteria(EBayApiConfigInfo.class).list();
    }
}
