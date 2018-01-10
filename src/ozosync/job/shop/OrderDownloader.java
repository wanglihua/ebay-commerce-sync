package ozosync.job.shop;

import java.util.Date;
import org.apache.log4j.Logger;
import org.hibernate.FlushMode;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.orm.hibernate4.SessionFactoryUtils;
import org.springframework.orm.hibernate4.SessionHolder;
import org.springframework.transaction.support.TransactionSynchronizationManager;

public class OrderDownloader {

    private Logger log = Logger.getLogger(OrderDownloader.class);

    private SessionFactory sessionFactory;

    public OrderDownloader(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public void execute() {

    }

    private void saveOrUpdateOrder() {
        Session session;
        SessionHolder sessionHolder = (SessionHolder) TransactionSynchronizationManager.getResource(sessionFactory);
        boolean existingTransaction = sessionHolder != null;
        if (existingTransaction) {
            log.error("Found thread-bound Session for HibernateInterceptor");
            session = sessionHolder.getSession();
        } else {
            try {
                session = sessionFactory.openSession();
                session.setFlushMode(FlushMode.MANUAL);
            } catch (HibernateException ex) {
                throw new DataAccessResourceFailureException("Could not open Hibernate Session", ex);
            }
            TransactionSynchronizationManager.bindResource(sessionFactory, new SessionHolder(session));
        }
        try {
            // 这里写数据库访问业务逻辑代码

            session.flush();
        } catch (Exception ex) {
            //错误信息
            log.fatal(ex.getMessage(), ex);
            //ex.printStackTrace();
            throw ex;
        } finally {
            if (existingTransaction) {
                log.debug("Not closing pre-bound Hibernate Session after TransactionalQuartzTask");
            } else {
                SessionFactoryUtils.closeSession(session);
                TransactionSynchronizationManager.unbindResource(sessionFactory);
            }
        }
    }
}
