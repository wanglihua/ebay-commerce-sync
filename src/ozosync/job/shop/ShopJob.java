package ozosync.job.shop;

import org.apache.log4j.Logger;
import org.hibernate.SessionFactory;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.quartz.QuartzJobBean;

public class ShopJob extends QuartzJobBean {

    private Logger log = Logger.getLogger(ShopJob.class);

    private SessionFactory sessionFactory;

    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        try {
            OrderDownloader orderDownloader = new OrderDownloader(sessionFactory);
            orderDownloader.download();
        } catch (Exception exp) {
            log.fatal(exp.getMessage());
            log.fatal(exp);
        }
    }
}
