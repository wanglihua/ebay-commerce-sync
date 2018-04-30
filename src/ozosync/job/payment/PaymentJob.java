package ozosync.job.payment;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import ozosync.job.SyncJob;

import java.util.Arrays;

public class PaymentJob extends SyncJob {

    private Logger log = Logger.getLogger(PaymentJob.class);

    @Autowired
    private PaymentDownloader paymentDownloader;

    @Override
    protected void executeInternal() {
        try {
            paymentDownloader.download();
        } catch (Exception exp) {
            log.fatal(exp);
            log.fatal(Arrays.toString(exp.getStackTrace()).replace(",", "\r\n"));
        }
    }
}
