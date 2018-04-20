package ozosync.job.shop;

import java.util.Arrays;
import org.apache.log4j.Logger;
import org.hibernate.SessionFactory;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.quartz.QuartzJobBean;

public class OrderFeedbackJob extends QuartzJobBean {

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
            OrderFeedbackDownloader orderFeedbackDownloader = new OrderFeedbackDownloader(sessionFactory);
            orderFeedbackDownloader.download();
        } catch (Exception exp) {
            log.fatal(exp);
            log.fatal(Arrays.toString(exp.getStackTrace()).replace(",", "\r\n"));
        }
    }
}
