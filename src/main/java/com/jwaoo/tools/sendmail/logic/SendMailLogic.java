package com.jwaoo.tools.sendmail.logic;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

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

}
