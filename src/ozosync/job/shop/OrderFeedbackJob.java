package ozosync.job.shop;

import java.util.Arrays;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import ozosync.job.SyncJob;

public class OrderFeedbackJob extends SyncJob {

    private Logger log = Logger.getLogger(ShopJob.class);

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
