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
5. 信息匹配，更新用户登录日志（最后登录时间、Ip地址）（LoginLogDAO），给用户增加5个积分（UserDAO），最后重定向到欢迎页面。（main.jsp）

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

​                               

