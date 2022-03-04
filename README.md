# SpringBoot Plugin Development Framework

- A new `3.0.0' version is available, a framework for dynamically extending the system.

### Introduce
The framework can be developed in the `SpringBoot` project plugin, plugin can be used in exactly the same way as `SpringBoot`. Using this framework you can achieve the following requirements:

- In the plugin, you can develop as a mini `spring-boot` project, which is easy to use.
- The extension of various function points in the system in plugin, for flexible system expansion, no longer need to use branches to deliver different requirements of the project.
- Various frameworks and their various `spring-boot-XXX-starter` can be integrated in plugin.
- Independent dependencies can now be defined in plugin, not in the main program.
- Can perfectly solve the plugin package and plugin package, plugin package and the main program because of the same framework of different versions of the conflict problem. Individual plugin can define different versions of the framework for the same dependency.
- Without the need to restart the main program, you can freely realize the dynamic installation and deployment of the plugin package, to dynamically expand the function of the system.
- Plugin can also integrate microservice modules independently of the main program.
- And so on, you can imagine what pressing needs and extensions this framework brings to you.
### Features
1. It simplifies the integration steps of the framework and makes it easier to get started.
2. Plugin development is closer to `spring-boot` native development.
3. Package plugin with `Maven` to support independent package compilation of plugins. Current support:
- Dev packaging: Package the plugin as a development environment plugin (packaged only once).
- Prod packaging: Package plug-ins into a `jar`, `zip`, `folder`, etc.
4. Plugin support two modes of operation: 
- Plugin mode: As a plugin, it is bootloaded by the main program.
- Autonomous boot mode: Starts as a separate `SpringBoot` project。
5. Self-developed class loader, support plugin definition of a variety of dependent 'jar' packages.
6. Various frameworks and their various `spring-boot-XXX-starter` can be integrated in plugins, such as integration: `mybatis`、`mybatis-plus`、`spring-jpa` etc.

### Environment
1. jdk1.8+d
2. apache maven 3.6+
3. spring-boot 2.0.0+

### Maven repository

[https://mvnrepository.com/artifact/com.gitee.starblues/springboot-plugin-framework](https://mvnrepository.com/artifact/com.gitee.starblues/springboot-plugin-framework)

### Doc

- [https://www.yuque.com/starblues/iuyk4y/ypurcw](https://www.yuque.com/starblues/iuyk4y/ypurcw)

### Derivative products
#### Background management system with front and rear plugin function
- [Grape](https://gitee.com/starblues/grape)
#### ETL Tool
- [Rope](https://gitee.com/starblues/rope)

### Example
- [springboot-plugin-framework-example](https://gitee.com/starblues/springboot-plugin-framework-example)

### Contact
QQ: 859570617(**After you like the framework, you can enter the group. Please note the Gitee/GitHub nickname before entering the group**)


