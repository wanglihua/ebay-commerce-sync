package ozosync.job.message;

import java.util.Arrays;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import ozosync.job.SyncJob;

public class MessageJob extends SyncJob {

    private Logger log = Logger.getLogger(MessageJob.class);

    @Autowired
    private MyMessageDownloader myMessageDownloader;

    @Autowired
    private MemberMessageDownloader memberMessageDownloader;

    @Override
    protected void executeInternal() {
        try {
            memberMessageDownloader.download();
            myMessageDownloader.download();
        } catch (Exception exp) {
            log.fatal(exp);
            log.fatal(Arrays.toString(exp.getStackTrace()).replace(",", "\r\n"));
        }
    }
}
