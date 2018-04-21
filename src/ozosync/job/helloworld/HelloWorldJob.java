package ozosync.job.helloworld;

import java.text.SimpleDateFormat;
import java.util.Date;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import ozosync.job.SyncJob;
import ozosync.service.HelloWorldService;

public class HelloWorldJob extends SyncJob {

    private Logger log = Logger.getLogger(HelloWorldJob.class);

    @Autowired
    private HelloWorldService helloWorldService;

    @Override
    public void executeInternal() {
        Date nowTime = helloWorldService.getNowTime();

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        log.warn(simpleDateFormat.format(nowTime) + " Hello World!");
    }
}
