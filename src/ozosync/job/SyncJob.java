package ozosync.job;

import org.apache.log4j.Logger;
import org.hibernate.FlushMode;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.orm.hibernate4.SessionFactoryUtils;
import org.springframework.orm.hibernate4.SessionHolder;
import org.springframework.transaction.support.TransactionSynchronizationManager;

public class SyncJob implements Job {

    private Logger log = Logger.getLogger(SyncJob.class);

    @Autowired
    protected SessionFactory sessionFactory;

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
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
            executeInternal();
        } catch (Exception ex) {
            ex.printStackTrace();
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

    protected void executeInternal() {

    }
}
