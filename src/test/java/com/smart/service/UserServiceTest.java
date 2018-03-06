package com.smart.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTransactionalTestNGSpringContextTests;
import org.testng.annotations.Test;

import static org.testng.Assert.*;

/**
 * Created by yangkun on 2018/2/28.
 */
@Test
public class UserServiceTest extends AbstractTransactionalTestNGSpringContextTests{
    @Autowired
    private UserService userService;

   /* @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }*/

    @Test
    public void hasMatchUser(){
        boolean b1=userService.hasMatchUser("admin","123456");
        boolean b2=userService.hasMatchUser("admin","111111");
        assertTrue(b1);
        assertTrue(!b2);
    }


}
