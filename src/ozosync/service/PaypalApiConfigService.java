package ozosync.service;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ozosync.domain.PaypalAccountInfo;

import java.util.List;

@Service
public class PaypalApiConfigService {

    @Autowired
    private SessionFactory sessionFactory;

    @SuppressWarnings("unchecked")
    public List<PaypalAccountInfo> getConfiglist() {
        Session session = sessionFactory.getCurrentSession();
        return session.createCriteria(PaypalAccountInfo.class).list();
    }
}
