package ozosync.job;

import java.text.SimpleDateFormat;
import java.util.Date;
import org.apache.log4j.Logger;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;

@Component
public class HelloWorldJob extends QuartzJobBean {

    private Logger log = Logger.getLogger(HelloWorldJob.class);

    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        log.warn(simpleDateFormat.format(date) + "Hello World!");
    }
}
