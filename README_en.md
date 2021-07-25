# SpringBoot Plugin Development Framework

### Introduce
This framework can develop plugins for extension projects on the `SpringBoot` project, 
and can define extension functions such as interface, static file and `Mybatis-xml` separately in the plugin module.

### Core
- Plugin configuration plug in SpringBoot project.
- Plugin development can be carried out on springboot with strong expansibility. 
Different plug-ins can be developed for different projects and different plugin jar packages can be deployed.
- You can specify whether to enable or disable plug-ins through the configuration file, and you can specify the order in which plug-ins are started.
- It supports uploading plug-ins and plugin configuration files to the server, and dynamically deploying and updating plug-ins without restarting the main program.
- It supports viewing plugin operation status and plug-in installation location.
- Without restarting the main program, dynamically install, uninstall, enable, stop, backup and delete plug-ins through the interface.
- On the plugin application module, `spring annotations` can be used to define components for dependency injection.
- Supports the development of `Http-Restful` interfaces in plugins
- Support to define persistence layer access and other requirements separately in the plugin.
- You can follow the plug-in interface provided by the main program to develop any extended functions.
- It supports annotation for any business scenario extension, and uses defined coordinates for scenario hit.
- Plugin can customize independent configuration files according to the production and development environment. Currently only `YML files` are supported.
- It supports user-defined extension development interface, and users can extend additional functions on the reserved interface.
- Supports method calls between plugins.
- Supporting plugin http interface documentation: `Swagger`、`SpringDoc`。
- The plugin supports custom development of the 'Spring-MVC interceptor`.

### Expansion pack
1. `SpringBoot-Mybatis` Expansion

- Supports customizing the `mapper` interface, `mapper XML` and the corresponding entity `Bean` in the plugin

- Support integration `Mybatis`、`Mybatis-Plus`、`Tk-Mybatis`

- Supports independent configuration of `DataSources` in plugin

See details: [SpringBoot Mybatis Expansion](http://www.starblues.cn/extension-doc/Mybatis%E6%89%A9%E5%B1%95.html)

2. `Static resource access` Extension

- Supports accessing static resources in plug-ins through HTTP
- Support `Thymeleaf` template engine for interface plug-in development

See details: [Static Resource Access Extension](http://www.starblues.cn/extension-doc/%E9%9D%99%E6%80%81%E8%B5%84%E6%BA%90%E8%AE%BF%E9%97%AE%E6%89%A9%E5%B1%95.html)

3. `Plugin Log` Extension

- The plug-in can independently configure the integrated `logback` log
- The plug-in can independently configure the integrated `log4j2` log

See details: [Plugin Log Extension](http://www.starblues.cn/extension-doc/Log%E6%89%A9%E5%B1%95.html)

Thank for [sousouki](https://gitee.com/caoshx_sousouki) development

### Environment
1. jdk1.8+d
2. apache maven 3.6+
3. spring-boot 2.0.0+

### Maven repository

[https://mvnrepository.com/artifact/com.gitee.starblues/springboot-plugin-framework](https://mvnrepository.com/artifact/com.gitee.starblues/springboot-plugin-framework)

### Doc

- [http://www.starblues.cn/](http://www.starblues.cn/)

### Derivative products
#### Background management system with front and rear plugin function
- [Grape](https://gitee.com/starblues/grape)
#### ETL Tool
- [Rope](https://gitee.com/starblues/rope)

### Example
- [springboot-plugin-framework-example](https://gitee.com/starblues/springboot-plugin-framework-example)

### Contact
859570617(**After you like the framework, you can enter the group. Please note the Gitee/GitHub nickname before entering the group**)

### Framework maintenance
**Welcome all development enthusiasts to participate in the maintenance and extension development of the framework**

See for extended development documents: - [springboot-plugin-framework Extended](http://www.starblues.cn/extension-doc/%E8%87%AA%E5%AE%9A%E4%B9%89%E6%89%A9%E5%B1%95.html)


