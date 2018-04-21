package ozosync.job.orderfeedback;

import java.util.Arrays;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import ozosync.job.SyncJob;
import ozosync.job.order.OrderJob;

public class OrderFeedbackJob extends SyncJob {

    private Logger log = Logger.getLogger(OrderJob.class);

    @Autowired
    private OrderFeedbackDownloader orderFeedbackDownloader;

    @Override
    protected void executeInternal() {
        try {
            orderFeedbackDownloader.download();
        } catch (Exception exp) {
            log.fatal(exp);
            log.fatal(Arrays.toString(exp.getStackTrace()).replace(",", "\r\n"));
        }
    }
}
