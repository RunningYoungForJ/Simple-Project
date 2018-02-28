# Simple-Project

> 该项目是一个论坛登录模块，实现登录模块常见的功能，通过该项目进一步熟悉Spring开发的流程，以及各功能设计的思路。

## 主要技术支持

- Maven构建工具
- Spring JDBC
- Spring声明式事务
- Spring MVC
- IDEA

## 功能描述

1. 登录页面提供form表单接收用户名/密码信息。（login.jsp）
2. 用户输入用户名/密码信息，并提交表单到对应的controller。
3. 服务器端检查接收的用户信息是否匹配，No-4，Yes-5。
4. 信息不匹配，返回登录页面，并给出提示。
5. 信息匹配，更新用户登录日志（增加一条登录记录）（LoginLogDAO），给用户增加5个积分（UserDAO）更新用户的最后登录IP、时间，最后重定向到欢迎页面。（main.jsp）

## 数据库搭建

> 所有的表结构都在sampledb数据库下。

1. 创建数据库,默认字符集采用utf8。

   ```mysql
   drop database if exists sampledb;

   create database sampledb default character set utf8;

   use sampledb;
   ```

2. 创建数据表

   ```mysql
   create table t_user(
       -> user_id int auto_increment primary key,
       -> user_name varchar(30),
       -> credits int,
       -> password varchar(32),
       -> last_visit datetime,
       -> last_ip varchar(32)
       -> )engine=innodb;

   create table t_login_log(
       -> login_log_id int auto_increment primary key,
       -> user_id int,
       -> ip varchar(32),
       -> login_datetime datetime
       -> )engine=innodb;

   ```

   其中，t_user是用户信息表；t_login_log是用户登录日志表。engine=innodb设定了表的引擎是InnoDB，该类型的表支持事务。MySQL默认采用MyISAM引擎，该类型的表不支持事务，仅存储数据，优点在于读写速度快。

3. 初始化数据，用户名/密码是admin/123456

   ```mysql
   insert into t_user (user_name,password) values ('admin','123456');
   ```

## 项目POM文件

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>com.smart</groupId>
    <artifactId>chapter2</artifactId>
    <version>1.0-SNAPSHOT</version>

    <properties>
        <file.encoding>UTF-8</file.encoding>
        <spring.version>4.2.1.RELEASE</spring.version>
        <mysql.version>5.1.45</mysql.version>
        <servlet.version>2.5</servlet.version>
        <commons-dbcp.version>1.4</commons-dbcp.version>
        <servlet-api.version>3.1.0</servlet-api.version>
        <jstl.version>1.2</jstl.version>
    </properties>

    <dependencies>
        <!--依赖的Spring模块类库-->
        <dependency>
            <groupId>org.springframework</groupId>
            <artifactId>spring-core</artifactId>
            <version>${spring.version}</version>
        </dependency>
        <dependency>
            <groupId>org.springframework</groupId>
            <artifactId>spring-beans</artifactId>
            <version>${spring.version}</version>
        </dependency>

        <!--依赖的数据库驱动类库-->
        <dependency>
            <groupId>mysql</groupId>
            <artifactId>mysql-connector-java</artifactId>
            <version>${mysql.version}</version>
        </dependency>

        <!--依赖的连接池类库-->
        <dependency>
            <groupId>commons-dbcp</groupId>
            <artifactId>commons-dbcp</artifactId>
            <version>${commons-dbcp.version}</version>
        </dependency>

        <!--依赖的Web类库-->
        <dependency>
            <groupId>javax.servlet</groupId>
            <artifactId>javax.servlet-api</artifactId>
            <version>${servlet-api.version}</version>
        </dependency>
        <dependency>
            <groupId>javax.servlet</groupId>
            <artifactId>jstl</artifactId>
            <version>${jstl.version}</version>
        </dependency>

    </dependencies>
</project>
```

## Domain层-领域对象

> 领域对象也被称为实体类，它代表了业务的状态，且贯穿于展现层、业务层和持久层，并最终被持久化到数据库中。领域对象使数据库表操作以面向对象的方式进行。

在登录模块中，涉及两个领域对象：User和LoginLog。

1. User.java

   ```java
   package com.smart.domain;

   import java.io.Serializable;
   import java.util.Date;

   /**
    * Created by yangkun on 2018/2/25.
    * 领域对象一般要实现Serializable接口，以便可以序列化
    */
   public class User implements Serializable {
       private int userId;
       private String userName;
       private String password;
       //积分
       private int credits;
       private String lastIp;
       private Date lastVisit;

       public int getUserId() {
           return userId;
       }

       public void setUserId(int userId) {
           this.userId = userId;
       }

       public String getUserName() {
           return userName;
       }

       public void setUserName(String userName) {
           this.userName = userName;
       }

       public String getPassword() {
           return password;
       }

       public void setPassword(String password) {
           this.password = password;
       }

       public int getCredits() {
           return credits;
       }

       public void setCredits(int credits) {
           this.credits = credits;
       }

       public String getLastIp() {
           return lastIp;
       }

       public void setLastIp(String lastIp) {
           this.lastIp = lastIp;
       }

       public Date getLastVisit() {
           return lastVisit;
       }

       public void setLastVisit(Date lastVisit) {
           this.lastVisit = lastVisit;
       }
   }
   ```

2. LoginLog.java

   ```java
   package com.smart.domain;

   import java.io.Serializable;
   import java.util.Date;

   /**
    * Created by yangkun on 2018/2/25.
    */
   public class LoginLog implements Serializable {
       private int loginLogId;
       private int userId;
       private String ip;
       private Date loginDate;

       public int getLoginLogId() {
           return loginLogId;
       }

       public void setLoginLogId(int loginLogId) {
           this.loginLogId = loginLogId;
       }

       public int getUserId() {
           return userId;
       }

       public void setUserId(int userId) {
           this.userId = userId;
       }

       public String getIp() {
           return ip;
       }

       public void setIp(String ip) {
           this.ip = ip;
       }

       public Date getLoginDate() {
           return loginDate;
       }

       public void setLoginDate(Date loginDate) {
           this.loginDate = loginDate;
       }
   }
   ```

## DAO层-持久层

> 持久层的主要工作是从数据库表中加载数据并实例化为对应的领域对象，或将领域对象持久化到数据库表中，即负责数据的访问和操作。
>
> 在DAO中编写SQL语句时，通常将SQL语句写在类静态变量中，这样会使代码更具有可读性。如果编写的SQL语句比较长，那么一般会采用多行字符串的方式进行构造。
>
> 注意：在构造多行SQL语句时，会容易产生拼接错误，规避这种错误的方式是在每行SQL语句的前后都加一个空格，这样就可以有效避免分行SQL语句组合后的错误。

1. UserDao

   ```java
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
   ```

2. LoginLogDao

   ```java
   package com.smart.dao;

   import com.smart.domain.LoginLog;
   import org.springframework.beans.factory.annotation.Autowired;
   import org.springframework.jdbc.core.JdbcTemplate;
   import org.springframework.stereotype.Repository;

   /**
    * Created by yangkun on 2018/2/28.
    * 负责记录用户的登录日志
    */

   @Repository
   public class LoginLogDao {
       private JdbcTemplate jdbcTemplate;

       private final static String INSERT_LOGIN_LOG_SQL=" insert into t_login_log "+
               " (user_id,ip,login_datetime) values(?,?,?)";
       
       @Autowired
       public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
           this.jdbcTemplate = jdbcTemplate;
       }
       
       public void insertLoginLog(LoginLog loginLog){
           Object[] args={loginLog.getUserId(),loginLog.getIp(),loginLog.getLoginDate()};
           jdbcTemplate.update(INSERT_LOGIN_LOG_SQL,args);
       }
   }
   ```

3. 在Spring中装配DAO

   > 在DAO类的实现中，需要用到JdbcTemplate作为访问数据库的入口。但JdbcTemplate需要针对数据库创建连接。
   >
   > JdbcTemplate封装了所有针对数据创建连接、获取数据的模版操作。它需要一个DataSource，从DataSource中获取和创建连接。
   >
   > 所以必须事先声明一个数据源dataSource，用来声明数据库配置信息，然后使用JdbcTemplate的Bean去调用该dataSource。

   具体的配置信息在src/main/resources下的smart-context.xml中。

   ```xml
   <?xml version="1.0" encoding="UTF-8"?>
   <beans xmlns="http://www.springframework.org/schema/beans"
          xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
          xmlns:p="http://www.springframework.org/schema/p"
          xmlns:context="http://www.springframework.org/schema/context"
          xsi:schemaLocation="http://www.springframework.org/schema/beans
            http://www.springframework.org/schema/beans/spring-beans-4.0.xsd
            http://www.springframework.org/schema/context
            http://www.springframework.org/schema/context/spring-context-4.0.xsd">
       <!--开启自动扫描，扫描类包：将标注Spring注解的类自动转化为Bean，同时完成Bean的注入-->
       <context:component-scan base-package="com.smart.dao"></context:component-scan>

       <!--定义一个使用DBCP实现的数据源-->
       <bean id="dataSource" class="org.apache.commons.dbcp.BasicDataSource"
             destroy-method="close"
             p:driverClassName="com.mysql.jdbc.Driver"
             p:url="jdbc:myslq://localhost:3306/sampledb"
             p:username="root"
             p:password="123456">
       </bean>

       <!--定义JDBC模版Bean-->
       <bean id="jdbcTemplate" class="org.springframework.jdbc.core.JdbcTemplate"
             p:dataSource-ref="dataSource">
       </bean>

   </beans>
   ```

## Service业务层

> 登录模块仅有一个业务层，即UserService。UserService层负责将持久层的UserDao和LoginLogDao组织起来，完成用户/密码认证，登录日志的操作。

1. UserService

   ```java
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
   ```

2. 在loginSuccess方法中添加了事务操作。但需要通过配置文件来告诉哪些类的哪些方法需要工作在事务环境下，以及事务的规则和内容。具体的事务配置在smart-context.xml中。(这里只贴出事务配置部分)

   ```xml
   <?xml version="1.0" encoding="UTF-8"?>
   <beans xmlns="http://www.springframework.org/schema/beans"
          xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
          xmlns:p="http://www.springframework.org/schema/p"
          xmlns:context="http://www.springframework.org/schema/context"
          xmlns:aop="http://www.springframework.org/schema/aop"
          xmlns:tx="http://www.springframework.org/schema/tx"
          xsi:schemaLocation="http://www.springframework.org/schema/beans
            http://www.springframework.org/schema/beans/spring-beans-4.0.xsd
            http://www.springframework.org/schema/context
            http://www.springframework.org/schema/context/spring-context-4.0.xsd
            http://www.springframework.org/schema/aop
            http://www.springframework.org/schema/aop/spring-aop-4.0.xsd">
       <!--开启自动扫描，扫描类包：将标注Spring注解的类自动转化为Bean，同时完成Bean的注入-->
       <context:component-scan base-package="com.smart.dao"></context:component-scan>

       <context:component-scan base-package="com.smart.service"></context:component-scan>

       <!--定义一个基于数据源的DataSourceTransactionManager事务管理器，该事务管理器负责声明式事务的管理，该管理器需要引入dataSource Bean-->
       <bean id="transactionManager" class="org.springframework.jdbc.datasource.DataSourceTransactionManager"
             p:dataSource-ref="dataSource">
       </bean>

       <!--通过aop和tx命名空间的语法，以aop的方式为com.smart.service包下所有类的所有标注@Transactional注解的方法都添加事务增强，即它们都工作在事务环境下-->
       <aop:config proxy-target-class="true">
           <aop:pointcut id="serviceMethod"
                         expression="(execution(* com.smart.service..*(..))) and (@annotation(org.springframework.transaction.annotation.Transactional))">
           </aop:pointcut>
           <aop:advisor pointcut-ref="serviceMethod" advice-ref="txAdvice"></aop:advisor>
       </aop:config>

       <tx:advice id="txAdvice" transaction-manager="transactionManager">
           <tx:attributes>
               <tx:method name="*"/>
           </tx:attributes>
       </tx:advice>
   </beans>
   ```

   ​

​                               

