package com.smart.service;

import com.smart.dao.LoginLogDao;
import com.smart.dao.UserDao;
import com.smart.domain.LoginLog;
import com.smart.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by yangkun on 2018/2/28.
 */
@Service
public class UserService {
    private UserDao userDao;
    private LoginLogDao loginLogDao;

    @Autowired
    public void setUserDao(UserDao userDao) {
        this.userDao = userDao;
    }

    @Autowired
    public void setLoginLogDao(LoginLogDao loginLogDao) {
        this.loginLogDao = loginLogDao;
    }

    public boolean hasMatchUser(String userName,String password){
        int matchCount=userDao.getMatchCount(userName,password);
        return matchCount>0;
    }

    public User findUserByUserName(String userName){
        return userDao.findUserByUserName(userName);
    }

    /*
    * 标注该方法运行在事务环境中
    * 因为在Spring事务管理器拦截切入表达式上加入了@Transactional过滤
    * loginSuccess完成一个事务性的数据操作：更新t_user表记录并添加t_login_log表记录
    * 需要使用配置文件来告诉Spring哪些业务类需要工作在事务环境下，以及事务的规则和内容。在smart-context.xml中配置事务信息
    * */
    @Transactional
    public void loginSuccess(User user){
        user.setCredits(5+user.getCredits());
        LoginLog loginLog=new LoginLog();
        loginLog.setUserId(user.getUserId());
        loginLog.setIp(user.getLastIp());
        loginLog.setLoginDate(user.getLastVisit());
        userDao.setUpdateLoginInfo(user);
        loginLogDao.insertLoginLog(loginLog);
    }
}
