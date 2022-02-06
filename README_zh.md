# springboot插件式开发框架

### 介绍
此框架可在`SpringBoot`项目上开发出用于扩展项目的插件，可在插件模块中单独定义接口、静态文件、`Mybatis-xml`等扩展功能。

### 核心功能
- 基于`Pf4j`开发。
- 插件配置式插拔于`SpringBoot`项目。
- 在`SpringBoot`上可以进行插件式开发, 扩展性极强, 可以针对不同项目开发不同插件, 进行不同插件`jar`包的部署。
- 可通过配置文件指定要启用或者禁用插件, 可以指定启动插件顺序。
- 支持上传插件和插件配置文件到服务器, 并且无需重启主程序, 动态部署插件、更新插件。
- 支持查看插件运行状态, 查看插件安装位置。
- 无需重启主程序, 通过接口动态的安装插件、卸载插件、启用插件、停止插件、备份插件、删除插件。
- 在插件应用模块上可以使用`Spring`注解定义组件, 进行依赖注入。
- 支持在插件中开发`Http-Restful`接口。
- 支持在插件中单独定义持久层访问等需求。
- 可以遵循主程序提供的插件接口开发任意扩展功能。
- 支持注解进行任意业务场景扩展, 并使用定义的坐标进行场景命中。
- 插件可以根据生产和开发环境自定义独立的配置文件。目前只支持yml文件。
- 支持自定义扩展开发接口, 使用者可以在预留接口上扩展额外功能。
- 支持插件之间的方法调用。
- 支持插件`Http`接口文档: `Swagger`、`SpringDoc`。
- 插件支持`拦截器`的定制开发。
- 插件对`Spring-Aop`进行支持。

### 扩展包功能
#### `SpringBoot-Mybatis`扩展包

1. 支持在插件中自定义`Mapper`接口、`Mapper xml` 以及对应的实体`bean`
2. 支持集成`Mybatis`、`Mybatis-Plus`、`Tk-Mybatis`
3. 支持可在插件中独立配置数据源

- 详见 [插件SpringBoot Mybatis扩展](https://www.yuque.com/starblues/iuyk4y/rzkxnx)

#### 静态资源访问扩展包

1. 支持通过http访问插件中静态资源
2. 支持`Thymeleaf`模板引擎进行界面插件式开发

- 详见 [插件静态资源访问扩展](https://www.yuque.com/starblues/iuyk4y/dztnst)

#### 插件日志扩展包

1. 插件可自主配置集成`logback`日志
2. 插件可自主配置集成`log4j2`日志

- 详见 [插件静态资源访问扩展](https://www.yuque.com/starblues/iuyk4y/eb8s99)
- 感谢 [sousouki](https://gitee.com/caoshx_sousouki) 的开发提供

### 运行环境
1. jdk1.8+
2. apache maven 3.6+
3. spring-boot 2.0.0+

### maven 仓库地址

[https://mvnrepository.com/artifact/com.gitee.starblues/springboot-plugin-framework](https://mvnrepository.com/artifact/com.gitee.starblues/springboot-plugin-framework)

### 文档地址

- [https://www.yuque.com/starblues/iuyk4y](https://www.yuque.com/starblues/iuyk4y)

### 衍生产品
#### 携带前后端插件功能的后台管理系统
- [Grape](https://gitee.com/starblues/grape)
#### ETL 工具
- [Rope](https://gitee.com/starblues/rope)

### 案例
- [springboot-plugin-framework 功能测试+案例](https://gitee.com/starblues/springboot-plugin-framework-example)

### QQ交流群
859570617(**点赞框架后可进群, 进群前请备注gitee昵称**)

### 框架维护
**欢迎各位开发爱好者参与到框架的维护和扩展开发当中**

扩展开发文档见: - [springboot-plugin-framework 扩展功能](https://www.yuque.com/starblues/iuyk4y/fa01y1)


