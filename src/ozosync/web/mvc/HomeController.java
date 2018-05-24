package ozosync.web.mvc;

import org.apache.log4j.Logger;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import java.util.HashMap;

@Controller
public class HomeController {

    private Logger log = Logger.getLogger(HomeController.class);

    @Autowired
    private SessionFactory sessionFactory;

    @RequestMapping(value = "/", method = RequestMethod.GET)
    @SuppressWarnings("unchecked")
    public ModelAndView index() throws Exception {
        return new ModelAndView("home", new HashMap<String, Object>() {{
        }});
    }
}
