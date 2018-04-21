package ozosync.job.helloworld;

import java.text.SimpleDateFormat;
import java.util.Date;
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
import ozosync.domain.NowTimeInfo;

public class HelloWorldJob implements Job {

    private Logger log = Logger.getLogger(HelloWorldJob.class);

    @Autowired
    private SessionFactory sessionFactory;

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
            // 这里写数据库访问业务逻辑代码
            Session currentSession = sessionFactory.getCurrentSession();
            NowTimeInfo nowTimeInfo = (NowTimeInfo) currentSession.createCriteria(NowTimeInfo.class)
                    .uniqueResult();

            Date nowTime = nowTimeInfo.getNow();

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            log.warn(simpleDateFormat.format(nowTime) + " Hello World!");
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
}
