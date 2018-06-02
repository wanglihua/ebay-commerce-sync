package ozosync.test;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import ozosync.common.SpringConfigLocation;
import ozosync.job.message.service.MyMessageService;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(locations = {SpringConfigLocation.configLocation})
public class MyMessageServiceTest {

    @Autowired
    private MyMessageService myMessageService;

    @Test
    public void testReadCData() {
        String s = "<![CDATA[要获取的内容]]>";

        System.out.println(myMessageService.getCData1(s));
    }


}
