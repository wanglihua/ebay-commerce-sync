package ozosync.job.payment;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import ozosync.job.SyncJob;

public class PaymentJob extends SyncJob {

    private Logger log = Logger.getLogger(PaymentJob.class);

    @Autowired
    private PaymentDownloader paymentDownloader;

    @Override
    protected void executeInternal() {
        try {
            paymentDownloader.download();
        } catch (Exception exp) {
            log.fatal(exp.getMessage(), exp);
        }
    }
}
