# springboot插件式开发框架

- 全新`3.0.0`版本上线啦，为动态扩展系统而生的框架。

**当前alpha测试版本暂未将依赖包上传到远程仓库, 使用时, 暂可下载源码, 在本地编译后进行尝鲜！！！**

### 介绍
该框架可以在`SpringBoot`项目上开发出插件功能，在插件中可以和`SpringBoot`使用方式一模一样。使用了本框架您可以实现如下需求：

- 在插件中，您可以当成一个微型的`Spring-Boot`项目来开发，简单易用。
- 在插件中扩展出系统各种功能点，用于系统灵活扩展，再也不用使用分支来交付不同需求的项目了。
- 在插件中可以集成各种框架及其各种`spring-boot-xxx-starter`。
- 在插件中可以定义独立依赖包了，再也不用在主程序中定义依赖包了。
- 可以完美解决插件包与插件包、插件包与主程序因为同一框架的不同版本冲突问题了。各个插件可以定义同一依赖的不同版本框架。
- 无需重启主程序，可以自由实现插件包的动态安装部署，来动态扩展系统的功能。
- 插件也可以不依赖主程序独立集成微服务模块。
- 您可以丰富想象该框架给您带来哪些迫切的需求和扩展。

### 特性
1. 简化了框架的集成步骤，更容易上手。
2. 插件开发更加贴近`spring-boot`原生开发。
3. 使用`Maven`打包插件，支持对插件的自主打包编译。目前支持: 

   开发打包：将插件打包成开发环境下的插件(仅需打包一次)。
   
   生产打包：将插件打包成一个`jar`、`zip`、`文件夹`等。
4. 插件支持两种运行模式

   插件模式： 作为一个插件，由主程序引导加载。
   
   自主启动模式：单独作为一个`SpringBoot`项目来启动。
5. 自主的开发的类加载器，支持插件定义各种的依赖`jar`包。
6. 在插件中可以集成各种框架及其各种`spring-boot-xxx-starter`，比如集成`mybatis`、`mybatis-plus`、`spring-jpa`等。

### 运行环境
1. jdk1.8+
2. apache maven 3.6+
3. spring-boot 2.0.0+

### maven 仓库地址

[https://mvnrepository.com/artifact/com.gitee.starblues/springboot-plugin-framework](https://mvnrepository.com/artifact/com.gitee.starblues/springboot-plugin-framework)

### 文档地址

- [https://www.yuque.com/starblues/iuyk4y/ypurcw](https://www.yuque.com/starblues/iuyk4y/ypurcw)

### 衍生产品
#### 携带前后端插件功能的后台管理系统
- [Grape](https://gitee.com/starblues/grape)
#### ETL 工具
- [Rope](https://gitee.com/starblues/rope)

### 案例
- [springboot-plugin-framework 功能测试+案例](https://gitee.com/starblues/springboot-plugin-framework-example)

### QQ交流群
859570617(**点赞框架后可进群, 进群前请备注gitee昵称**)