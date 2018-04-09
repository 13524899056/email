package com.jwaoo.tools.sendmail.web;

import com.jwaoo.tools.sendmail.logic.SendMailLogic;
import com.jwaoo.tools.sendmail.utils.MailUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2018/4/9.
 */
@RestController
@RequestMapping("/")
public class EmailController {

    @Autowired
    private SendMailLogic sendMailLogic;

    @RequestMapping("/sendMail")
    public Object sendMail(){
        boolean hasToSend = true;
        int pageNo = 1 ;
        int pageSize = 50;
        List<Map<String,Object>> users = null;
        int count = 0;
        List<String> ls = new ArrayList<>();
        while(hasToSend){
            users = sendMailLogic.findUsers(pageNo,pageSize);
            if(users == null || users.size() == 0){
                hasToSend = false;
            }else{
                pageNo++;
                for(Map<String,Object> user:users){
                    String email = user.get("email").toString();
                    MailUtil.getInstance().sendNotice(email);
                    System.out.println(email);
                    count++;
                    ls.add(email);
                }
            }
        }
        Map result = new HashMap<>();
        result.put("sendNum",count);
        result.put("emails",ls);
        return result;
    }
}
