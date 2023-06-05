# 动态表单系统
 
该系统通过json配置文件实现基本表单的CRUD，以及流程的处理等等。
通过配置可以自动模式来创建视图


## 系统框架

1. Spring MVC + Spring Cache + Activiti
2. 数据层图通过类似于MongoDB driver来实现动态修改数据
3. 前端使用bootstrap-table, select2, jquery-ui, elfinder, ckeditor等组件


## 运行方式
新建一个Spring Boot的应用

Maven 增加快照地址

    <repositories>
       <repository>
           <id>oss-snapshot</id>
           <url>https://oss.sonatype.org/content/repositories/snapshots</url>
       </repository>
    </repositories>
    
Maven 依赖

    <dependency>
        <groupId>com.github.yuebo</groupId>
        <artifactId>boot-dyna-starter</artifactId>
        <version>1.0.2-SNAPSHOT</version>
    </dependency>
    
修改application.properties

    #数据源
    spring.datasource.url=jdbc:mysql://host:port/dyna?characterEncoding=UTF8&useSSL=false
    spring.datasource.username=root
    spring.datasource.password=password1
    spring.datasource.driver-class-name=com.mysql.jdbc.Driver
    #应用路径
    server.context-path=/dyna
    dyna.init.data=true
    #上传文件路径
    upload.defaultPath=d:/upload
        
启动类

    package com.ifreelight.demo;
    
    import com.github.yuebo.dynaFormApplication;
    import org.springframework.boot.SpringApplication;
    
    @DynaFormApplication
    public class DemoApplication {
    
        public static void main(String[] args) {
            SpringApplication.run(DemoApplication.class, args);
        }
    }

在resources文件夹新建
    
    {
      "name": "boot",
      "type": "create",
      "data": "boot",
      "fields": [
        {
          "name": "message",
          "label": "提示信息",
          "type": "textarea",
          "update": "false",
          "attributes": {
            "class": "form-control",
            "value": "这是一个测试的Boot的View",
            "disabled": "true"
          }
        }
      ],
      "readOnly": "true",
      "title": "Boot测试",
      "permission": []
    }


## 注意事项
1. json文件请使用utf-8编码


## 更新日志
#### 1.0.2-SNAPSHOT
解决子视图问题
#### 1.0.3-SNAPSHOT
增加自定义组件API，用户可以通过扩展UIComponent接口自定义组件
#### 1.0.4-SNAPSHOT
增加自定义视图的支持
#### 1.0.5-SNAPSHOT
1.增加CrudService，可以通过CrudService实现类似JPA的功能
2.优化系统内置视图，删除冗余视图
3.优化初始化数据
4.修复导出失败的问题
5.增加配置属性，可以禁用自动更新数据库表的模式
6.注意此版本和之前版本的数据库不兼容,修改了一下字段以便兼容项目的jpa
* _id->id
* createdBy->created_by
* createdDate->created_time
* updatedBy->updated_by
* updatedDate->updated_time
#### 1.0.6-SNAPSHOT
1. 修复上次组件无法上传文件的错误
2. 修复查询视图在丢失登录状态的时候不重定向到首页的错误
3. 修改id字段变为使用常量
#### 1.0.7-SNAPSHOT
1. 修复表单编辑器的问题
2. 增加配置，使得可以自定义应用名称

#### 2.0.0-SNAPSHOT
1. 更新至spring boot 2.x
2. 适配mysql linux版本(支持区分表名大小写)
3. 

## 详细资料
[官方教程](https://dyna.eappcat.com/2017/12/01/%E5%8A%A8%E6%80%81%E8%A1%A8%E5%8D%95%E6%A6%82%E8%BF%B0/)