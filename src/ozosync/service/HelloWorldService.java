package ozosync.service;

import java.util.Date;
import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ozosync.domain.NowTimeInfo;

@Service
public class HelloWorldService {

    private Logger log = Logger.getLogger(HelloWorldService.class);

    @Autowired
    private SessionFactory sessionFactory;

    public Date getNowTime() {
        Session session = sessionFactory.getCurrentSession();
        NowTimeInfo nowTimeInfo = (NowTimeInfo) session.createCriteria(NowTimeInfo.class)
                .uniqueResult();

        return nowTimeInfo.getNow();
    }
}
