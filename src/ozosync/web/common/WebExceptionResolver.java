package ozosync.web.common;

import java.text.SimpleDateFormat;
import java.util.Date;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.log4j.Logger;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

public class WebExceptionResolver implements HandlerExceptionResolver {

    private Logger log = Logger.getLogger(WebExceptionResolver.class);

    //系统抛出的异常
    @Override
    public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, Object handler, Exception exception) {
        //handler就是处理器适配器要执行的Handler对象(只有method)

        //错误信息
        String message = exception.getMessage();

        Date curDateTime = new Date();
        log.fatal(message, exception);

        ModelAndView modelAndView = new ModelAndView();

        //将错误信息传到页面
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        modelAndView.addObject("message", String.format("%s，系统出现错误，请联系管理员！详情：%s", simpleDateFormat.format(curDateTime), message));

        /*
        if (isAjaxRequest(request)) {
            modelAndView.addObject("success", false);
            modelAndView.setView(new MappingJackson2JsonView());
        } else {
            //指向到错误界面
            modelAndView.setViewName("error");
        }
        */

        modelAndView.addObject("success", false);
        modelAndView.setView(new MappingJackson2JsonView());

        return modelAndView;
    }

    private boolean isAjaxRequest(HttpServletRequest request) {
        String requestedWith = request.getHeader("X-Requested-With");
        return requestedWith != null && requestedWith.equals("XMLHttpRequest");
    }
}
