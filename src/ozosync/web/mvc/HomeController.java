package ozosync.web.mvc;

import java.util.List;
import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import ozosync.domain.StatusInfo;

@Controller
public class HomeController {

    private Logger log = Logger.getLogger(HomeController.class);

    @Autowired
    private SessionFactory sessionFactory;

    @RequestMapping(value = "/", method = RequestMethod.GET)
    @SuppressWarnings("unchecked")
    public String index() throws Exception {

        Session session = sessionFactory.getCurrentSession();

        List<StatusInfo> statusList = session.createCriteria(StatusInfo.class).list();

        return "home";
    }
}
