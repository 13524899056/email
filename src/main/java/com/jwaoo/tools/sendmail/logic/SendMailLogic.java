package com.jwaoo.tools.sendmail.logic;

import com.jwaoo.common.core.utils.LogUtils;
import com.jwaoo.tools.sendmail.utils.MailUtil;
import com.jwaoo.tools.sendmail.utils.SpringContextUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Jerry
 * @date 2018/4/8 17:39
 */
@Component
public class SendMailLogic {

    @Autowired
    private JdbcTemplate jdbcTemplate;


    public List<Map<String,Object>> findUsers(int pageNo,int pageSize){
        String sql = "select email from user where (os_type is null or os_type='ios' or os_type='') and status = 0  order by id  limit ?,? ";
        return jdbcTemplate.queryForList(sql,new Integer[]{(pageNo-1) * pageSize ,pageSize});
    }


    public static Object sendMail(){
        SendMailLogic sendMailLogic = SpringContextUtils.getBean(SendMailLogic.class);
        boolean hasToSend = true;
        boolean success = false;
        int pageNo = 1 ;
        int pageSize = 500;
        List<Map<String,Object>> users = null;
        int count = 0;
        List<String> ls = new ArrayList<>();
        List<String> fails = new ArrayList<>();
        while(hasToSend){
            users = sendMailLogic.findUsers(pageNo, pageSize);
            if(users == null || users.size() == 0){
                hasToSend = false;
            }else{
                pageNo++;
                for(Map<String,Object> user:users){
                    String email = user.get("email").toString();
                    success = MailUtil.getInstance().sendNotice(email);
                    count++;
                    if(success) {
                        ls.add(email);
                    }else{
                        fails.add(email);
                        LogUtils.log4Info("send fail " + email);
                    }
                }
            }
        }
        Map result = new HashMap<>();
        result.put("sendNum",count);
        result.put("success",ls);
        result.put("fail",fails);
        return result;
    }

}
