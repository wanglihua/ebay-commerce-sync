package ozosync.job.order;

import java.util.Arrays;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import ozosync.job.SyncJob;

public class OrderJob extends SyncJob {

    private Logger log = Logger.getLogger(OrderJob.class);

    @Autowired
    private OrderDownloader orderDownloader;

    @Override
    protected void executeInternal() {
        try {
            orderDownloader.download();

        } catch (Exception exp) {
            log.fatal(exp);
            log.fatal(Arrays.toString(exp.getStackTrace()).replace(",", "\r\n"));
        }
    }
}
