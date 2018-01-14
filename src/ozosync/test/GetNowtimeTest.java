package ozosync.test;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;
import ozosync.common.SpringConfigLocation;
import ozosync.domain.ExchangeRateInfo;
import ozosync.domain.NowTimeInfo;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(locations = {SpringConfigLocation.configLocation})
public class GetNowtimeTest {

    @Autowired
    private SessionFactory sessionFactory;

    @Test
    @Transactional
    public void getNowtime() {
        Session session = sessionFactory.getCurrentSession();
        NowTimeInfo nowTimeInfo = (NowTimeInfo) session.createCriteria(NowTimeInfo.class)
                .uniqueResult();
        System.out.println(nowTimeInfo.getNow());
    }
}

/*
package ozosync.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;
import ozosync.common.SpringConfigLocation;
import ozosync.domain.ExchangeRateInfo;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(locations = {SpringConfigLocation.configLocation})
public class ExchangeRateServiceTest {

    private ExchangeRateService exchangeRateService;

    @Test
    @Transactional
    public void getAudExchangeRate() {
        ExchangeRateInfo exchangeRateInfo = exchangeRateService.getAudExchangeRate();
        System.out.println(exchangeRateInfo.getRate());
    }
}

 */