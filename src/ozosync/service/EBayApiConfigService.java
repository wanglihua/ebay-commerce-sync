package ozosync.service;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ozosync.domain.EBayApiConfigInfo;

import java.util.List;

@Service
public class EBayApiConfigService {

    @Autowired
    private SessionFactory sessionFactory;

    @SuppressWarnings("unchecked")
    public List<EBayApiConfigInfo> getConfiglist() {
        Session session = sessionFactory.getCurrentSession();
        return session.createCriteria(EBayApiConfigInfo.class).list();
    }
}
