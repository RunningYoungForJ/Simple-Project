package com.smart.dao;

import com.smart.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * Created by yangkun on 2018/2/28.
 */
/*
* 使用@Repository定义一个Dao的Bean，使用@Autowired将spring容器中的jdbcTemplate注入到该Dao中
* */
@Repository
public class UserDao {
    /*
    * 在org.springframework.jdbc.core.JdbcTemplate中封装了样板式代码
    * */
    private JdbcTemplate jdbcTemplate;

    private final static String MATCH_COUNT_SQL="select count(*) from t_user where user_name=? and password=?";

    private final static String UPDATE_LOGIN_INFO_SQL="update t_user set last_visit=?,last_id=?,credits=? where id=?";

    @Autowired
    public void setJdbcTemplate(JdbcTemplate jdbcTemplate){
        this.jdbcTemplate=jdbcTemplate;
    }

    public int getMatchCount(String userName,String password){
        String sqlStr="select * from t_user where user_name=? and password=?";
        /*
        * spring 3.2.2之后，jdbctemplate中的queryForInt已经被取消了！
        * 全部用queryForObject代替
        * */
        return jdbcTemplate.queryForObject(sqlStr,new Object[]{userName,password},Integer.class);
    }

    public User findUserByUserName(final String userName){
        final User user=new User();
        jdbcTemplate.query(MATCH_COUNT_SQL, new Object[]{userName},
                /*查询结果的处理回调接口*/
                new RowCallbackHandler() {
                    public void processRow(ResultSet rs) throws SQLException {
                        user.setUserId(rs.getInt("user_id"));
                        user.setUserName(userName);
                        user.setCredits(rs.getInt("credits"));
                    }
                }
        );
        return user;
    }

    public void setUpdateLoginInfo(User user){
        jdbcTemplate.update(UPDATE_LOGIN_INFO_SQL,new Object[]{user.getLastVisit(),user.getLastIp(),user.getCredits(),user.getUserId()});
    }


}
