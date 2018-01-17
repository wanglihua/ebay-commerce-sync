package ozosync.test;

import org.hibernate.SessionFactory;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;
import ozosync.common.SpringConfigLocation;
import ozosync.job.shop.OrderDownloader;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(locations = {SpringConfigLocation.configLocation})
public class OrderDownloadTest {

    @Autowired
    private SessionFactory sessionFactory;

    @Test
    @Transactional
    public void download() {
        OrderDownloader orderDownloader = new OrderDownloader(sessionFactory);
        orderDownloader.download();
    }
}
