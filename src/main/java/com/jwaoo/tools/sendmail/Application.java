package com.jwaoo.tools.sendmail;

import com.jwaoo.common.core.utils.LogUtils;
import com.jwaoo.tools.sendmail.logic.SendMailLogic;
import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import java.net.UnknownHostException;

@SpringBootApplication
public class Application extends SpringBootServletInitializer {



    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(Application.class).bannerMode(Banner.Mode.OFF);
    }

    /**
     * Main method, used to run the application.
     * @throws java.net.UnknownHostException
     */
    public static void main(String[] args) throws UnknownHostException {
        SpringApplication app = new SpringApplication(Application.class);
        app.setBannerMode(Banner.Mode.OFF);
        app.run(args);
        SendMailLogic.sendMail();
    }

    @Override
    public void onStartup(ServletContext servletContext) throws ServletException {
        super.onStartup(servletContext);
        LogUtils.log4Info("start134");
        ///SendMailLogic.sendMail();
    }
}
