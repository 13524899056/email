package com.jwaoo.tools.sendmail.utils;

import com.jwaoo.common.core.config.Global;
import com.jwaoo.common.core.utils.LogUtils;
import com.sun.net.ssl.internal.ssl.Provider;
import org.apache.commons.lang3.CharEncoding;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;
import java.security.Security;
import java.util.Properties;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * @author Jerry
 * @date 2018/4/8 17:38
 */
public class MailUtil {

    private final Logger log = LoggerFactory.getLogger(MailUtil.class);

    private static final String DEFAULT_HOST = "127.0.0.1";
    private static final String PROP_TLS = "mail.smtp.ssl.enable";
    //    private static final String PROP_HOST = "mail.host";
//    private static final String PROP_PORT = "mail.port";
//    private static final String PROP_USER = "mail.user";
//    private static final String PROP_PASSWORD = "mail.password";
//    private static final String PROP_PROTO = "mail.protocol";
//    private static final String PROP_AUTH = "mail.auth";
    private static final String PROP_SMTP_AUTH = "mail.smtp.auth";
    private static final String PROP_STARTTLS = "mail.smtp.starttls.enable";
    private static final String PROP_TRANSPORT_PROTO = "mail.transport.protocol";
    //    private static final String EMAIL_TEMPLATE_PROTO = "mail.resetPwd.template";
//    private static final String EMAIL_SUBJECT = "mail.resetPwd.subject";
    private static final String AWS_USERNAME = "account@senselovers.com";
    private static final String AWS_PASSWORD = "Jwaoo2020";
    private static final String AWS_SSL_FACTORY = "javax.net.ssl.SSLSocketFactory";
    private static final String LOCALHOST_USERNAME = "apple02@jwaoo.com";
    private static final String LOCALHOST_PASSWORD = "Jwaoo135";
    private static final String LOCALHOST_PROTOCOL = "smtp";
    private static final String LOCALHOST_HOST = "smtp.qiye.163.com";
    private static final int LOCALHOST_PORT = 25;

    private static final MailUtil instance = new MailUtil();
    private static JavaMailSenderImpl javaMailSender;

    private static Boolean isTestMode = Boolean.valueOf(Global.getConfigCfg("cfg:isTestMode", "false"));


    private MailUtil(){
        if (true)
        {
            javaMailSender = init();
        }else
        {
            javaMailSender = initGmail();
        }
    }

    public static MailUtil getInstance(){
        return instance;
    }

    /**
     * System default email address that sends the e-mails.
     */
    private String from;
    private String cc;
    private String bcc;

    private JavaMailSenderImpl initGmail(){
        this.from = AWS_USERNAME;
        this.cc = "";//settings.get("mail.cc");
        this.bcc = "";//settings.get("mail.bcc");

        log.debug("Configuring gmail server");
        Provider p = new Provider();
        Security.addProvider(p);
        //String SSL_FACTORY = "javax.net.ssl.SSLSocketFactory";
        // Get a Properties object
        Properties props = System.getProperties();
        props.setProperty("mail.smtp.host","smtp.gmail.com");
        props.setProperty("mail.smtp.socketFactory.class", AWS_SSL_FACTORY);
        props.setProperty("mail.smtp.socketFactory.fallback", "false");
        props.setProperty("mail.smtp.port", "465");
        props.setProperty("mail.smtp.socketFactory.port", "465");
        props.setProperty("mail.smtp.user", AWS_USERNAME);
        props.setProperty("mail.smtp.password", AWS_PASSWORD);
        props.setProperty("mail.smtp.auth", "true");

        JavaMailSenderImpl sender = new JavaMailSenderImpl();
        Session session = Session.getDefaultInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(AWS_USERNAME, AWS_PASSWORD);
            }
        });
        sender.setSession(session);
        sender.setUsername(AWS_USERNAME);
        sender.setPassword(AWS_PASSWORD);
        sender.setJavaMailProperties(props);
        return sender;
    }


    private JavaMailSenderImpl init() {
        this.from = LOCALHOST_USERNAME;
        this.cc = "";
        this.bcc = "";
        log.debug("Configuring mail server");
        Boolean tls = false;
        Boolean auth = true;

        JavaMailSenderImpl sender = new JavaMailSenderImpl();
        if (LOCALHOST_HOST != null && !LOCALHOST_HOST.isEmpty()) {
            sender.setHost(LOCALHOST_HOST);
        } else {
            log.warn("Warning! Your SMTP server is not configured. We will try to use one on localhost.");
            log.debug("Did you configure your SMTP settings in your application.yml?");
            sender.setHost(DEFAULT_HOST);
        }
        sender.setPort(LOCALHOST_PORT);
        sender.setUsername(LOCALHOST_USERNAME);
        sender.setPassword(LOCALHOST_PASSWORD);

        Properties sendProperties = new Properties();
        sendProperties.setProperty(PROP_SMTP_AUTH, auth.toString());
        sendProperties.setProperty(PROP_TLS, tls.toString());
        sendProperties.setProperty(PROP_TRANSPORT_PROTO, LOCALHOST_PROTOCOL);
        sendProperties.setProperty(PROP_STARTTLS, tls.toString());
        sender.setJavaMailProperties(sendProperties);
        return sender;
    }

    public boolean sendEmail(String to, String content, String subject) {

        log.debug("Send e-mail to '{}' with subject '{}' and content={}", to, subject, content);
        // Prepare message using a Spring helper
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        try {
            MimeMessageHelper message = new MimeMessageHelper(mimeMessage, true, CharEncoding.UTF_8);
            message.setTo(to);
            message.setFrom(from);
            if (StringUtils.isNotBlank(cc)) {
                message.setCc(cc.split(","));
            }
            if (StringUtils.isNotBlank(bcc)) {
                message.setBcc(bcc.split(","));
            }

            message.setSubject(subject);
            message.setText(content, true);
            javaMailSender.send(mimeMessage);
            log.info("Sent e-mail to User '{}'", to);
            return true;
        } catch (Exception e) {
            log.error("E-mail could not be sent to user '{}', exception is: {}", to, e.getMessage());
            return false;
        }
    }

    @Async
    public Future<Boolean> sendVerificationCode(String email) {
        String subject = "Email verification";
        String content = "Just a Test Email!!!!!!!";

        try {
            sendEmail(email, content, subject);
            return new AsyncResult<>(true);
        }catch (Exception e){
            log.error("exception: " + e.getMessage());
        }
        return new AsyncResult<>(false);
    }


    public Boolean sendNotice(String email)
    {
        String subject = "Sense Lovers(IOS) maintenance has stopped and is replaced by Sense Player.";
//        String HefURL2 = "Hello Friends,\n" +
//                "To create a better and safer experience for users, we decide to use Sense Player instead of Sense Lovers. (Android users are not included. If you are , you can ignore the email.) \n" +
//                "Please excuse us for any inconvenience we may have caused you. Here is the instruction that can help you download and install Sense Player .If there is any question, please feel free to contact us at customerservice@senselovers.com. Thanks!\n" +
//                "1．Please search “SensePlayer”in your App Store to download it\n" +
//                "2. Once Sense Player is installed, launch the app and enter “https://www.senselovers.com/resource”or “senselovers”for the login page.\n" +
//                "<img src=\"http://s3-us-west-1.amazonaws.com/sensepublic/upload/Commons/spl_bt97.png\" alt=\"\">\n" +
//                "3.Please enter your Sense Lovers account and password, and then you will see many recently updated videos are waiting for you.\n" +
//                "4.If you would like to know more details, you can check the user guide by clicking “三” on the top left corner. \n" +
//                "<img src=\"http://s3-us-west-1.amazonaws.com/sensepublic/upload/Commons/spl_bt98.png\" alt=\"\">\n" +
//                "\n" +
//                "Warm Regards\n" +
//                "Sense Lovers Technical Support";

        String HefURL2 ="<div >\n" +
                "  <div style=\"margin-top:20px;\">\n" +
                "    Hello Friends,\n" +
                "  </div>\n" +
                "  <div style=\"margin-top:20px;\">\n" +
                "    To create a better and safer experience for users, we decide to use Sense Player instead of Sense Lovers. (Android users are not included. If you are, you can ignore the email. ) \n" +
                "    Please excuse us for any inconvenience we may have caused you. Here is the instruction that can help you download and install Sense Player. If there is any question, please feel free to contact us at <a href=\"mailto:customerservice@senselovers.com\"><u>customerservice@senselovers.com</u></a> Thanks!\n" +
                "  </div>\n" +
                "  <div style=\"margin-top:10px;\">\n" +
                "    <span >1. Please search “SensePlayer”in your App Store to download it.</span>\n" +
                "  </div>    \n" +
                "  <div  style=\"margin-top:10px;\">\n" +
                "    <div >\n" +
                "      <span >2. Once Sense Player is installed, launch the app and enter “https://www.senselovers.com/resource” or “senselovers” for the login page.</span>\n" +
                "    </div>\n" +
                "    <div >\n" +
                "      <img src=\"http://s3-us-west-1.amazonaws.com/sensepublic/upload/Commons/spl_bt97.png\" alt=\"\">\n" +
                "    </div>\n" +
                "  </div>\n" +
                "   <div style=\"margin-top:10px;\">\n" +
                "  <span class=\"\">3. Please enter your Sense Lovers account and password, and then you will see many recently updated videos are waiting for you.</span>\n" +
                "   </div>\n" +
                "  <div  style=\"margin-top:10px;\">\n" +
                "    <div >\n" +
                "      <span class=\"\">4. If you would like to know more details, you can check the user guide by clicking “三” on the top left corner. </span>\n" +
                "    </div>\n" +
                "    <div >\n" +
                "      <img src=\"http://s3-us-west-1.amazonaws.com/sensepublic/upload/Commons/spl_bt98.png\" alt=\"\">\n" +
                "    </div>\n" +
                "  </div>\n" +
                "  <div id=\"\" style=\"display:block;margin-top:80px;\">\n" +
                "    <div>\n" +
                "      Warm Regards,\n" +
                "    </div>\n" +
                "    <div style=\"margin-top:10px;margin-bottom:30px;\">\n" +
                "      Sense Lovers Technical Support\n" +
                "    </div>\n" +
                "  </div>\n" +
                " </div>\n";

        String content = "<html><head><meta http-equiv='Content-Type' content='text/html; charset=gb2312'/></head><body>" + HefURL2 + "<br></body></html>";
        try {
            CompletableFuture<Boolean> res = CompletableFuture.supplyAsync(() -> sendEmail(email, content, subject));
            return res.get(3, TimeUnit.SECONDS);
//               return new AsyncResult<>(true);
        } catch (Exception e){
            LogUtils.log4Error("send mail error ", e);
            return false;
        }
    }

}
