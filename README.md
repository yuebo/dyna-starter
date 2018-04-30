# 动态表单系统

该系统通过json配置文件实现基本表单的CRUD，已经流程的处理等等。
通过配置可以自动模式来创建视图


## 系统框架

1. Spring MVC + Spring Cache + Activiti
2. 数据层图通过类似于MongoDB driver来实现动态修改数据
3. 前端使用bootstrap-table, select2, jquery-ui, elfinder, ckeditor等组件


## 运行方式
新建一个Spring Boot的应用

Maven

    <dependency>
        <groupId>com.github.yuebo.dyna</groupId>
        <artifactId>boot-starter</artifactId>
        <version>1.0-SNAPSHOT</version>
    </dependency>
    
修改application.properties

    #数据源
    spring.datasource.url=jdbc:mysql://host:port/dyna?characterEncoding=UTF8&useSSL=false
    spring.datasource.username=root
    spring.datasource.password=password1
    spring.datasource.driver-class-name=com.mysql.jdbc.Driver
    #应用路径
    server.context-path=/dyna
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


## 详细资料
[官方教程](https://dyna.eappcat.com/2017/12/01/%E5%8A%A8%E6%80%81%E8%A1%A8%E5%8D%95%E6%A6%82%E8%BF%B0/)