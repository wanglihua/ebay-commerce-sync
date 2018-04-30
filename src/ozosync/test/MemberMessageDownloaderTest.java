package ozosync.test;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;
import ozosync.common.SpringConfigLocation;
import ozosync.job.message.MemberMessageDownloader;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(locations = {SpringConfigLocation.configLocation})
public class MemberMessageDownloaderTest {

    @Autowired
    private MemberMessageDownloader memberMessageDownloader;

    @Test
    @Transactional
    public void memberMessageDownload() {
        memberMessageDownloader.download();
    }
}
