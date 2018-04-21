package ozosync.job.message;

import org.springframework.beans.factory.annotation.Autowired;
import ozosync.job.SyncJob;

public class MessageJob extends SyncJob {

    @Autowired
    private MessageDownloader messageDownloader;

    @Override
    protected void executeInternal() {

    }
}
