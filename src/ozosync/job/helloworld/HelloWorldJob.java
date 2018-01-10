package ozosync.job.helloworld;

import java.text.SimpleDateFormat;
import java.util.Date;
import org.apache.log4j.Logger;
import org.hibernate.FlushMode;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.jetbrains.annotations.NotNull;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.orm.hibernate4.SessionFactoryUtils;
import org.springframework.orm.hibernate4.SessionHolder;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import ozosync.domain.NowTimeInfo;

public class HelloWorldJob extends QuartzJobBean {

    private Logger log = Logger.getLogger(HelloWorldJob.class);

    private SessionFactory sessionFactory;

    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {

        Date nowTime = getNowTime();

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        log.warn(simpleDateFormat.format(nowTime) + " Hello World!");
    }

    @NotNull
    private Date getNowTime() {
        Session session;
        SessionHolder sessionHolder = (SessionHolder) TransactionSynchronizationManager.getResource(getSessionFactory());
        boolean existingTransaction = sessionHolder != null;
        if (existingTransaction) {
            log.error("Found thread-bound Session for HibernateInterceptor");
            session = sessionHolder.getSession();
        } else {
            try {
                session = getSessionFactory().openSession();
                session.setFlushMode(FlushMode.MANUAL);
            } catch (HibernateException ex) {
                throw new DataAccessResourceFailureException("Could not open Hibernate Session", ex);
            }
            TransactionSynchronizationManager.bindResource(getSessionFactory(), new SessionHolder(session));
        }
        try {
            // 这里写数据库访问业务逻辑代码
            NowTimeInfo nowTimeInfo = (NowTimeInfo) session.createCriteria(NowTimeInfo.class)
                    .uniqueResult();
            return nowTimeInfo.getNow();
        } catch (Exception ex) {
            ex.printStackTrace();
            throw ex;
        } finally {
            if (existingTransaction) {
                log.debug("Not closing pre-bound Hibernate Session after TransactionalQuartzTask");
            } else {
                SessionFactoryUtils.closeSession(session);
                TransactionSynchronizationManager.unbindResource(getSessionFactory());
            }
        }
    }
}
