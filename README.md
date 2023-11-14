# CAKE FRAMEWORK

DDD + CQRS + TRACE

```
cake-fast
 -- cake-boot-starter-bizflow      bizflow starter(基于CQRS实现)
 -- cake-boot-starter-cqrs         cqrs starter
 -- cake-boot-starter-log          log starter
 -- cake-boot-starter-mybatis      mybatis starter
cake-fast-examples                 DEMO
 -- cake-fast-example-cqrs         DEMO CQRS
 -- cake-fast-example-log          DEMO - LOG
cake-framework                     框架层
 -- cake-framework-common          通用封装
 -- cake-framework-cqrs            CQRS
 -- cake-framework-ddd             DDD领域设计基础
 -- cake-framework-log             日志(支持链路日志，暂未支持RPC, 后续计划支持，不过未找到类似鹰眼那种开箱即用，后补有机会补充)
```

## 分层思考

实践一段实践后，越加发现DDD是封包的艺术。
![cake-ddd](https://github.com/WXzhongwang/cake/blob/main/DDD.png)




```text

实体和值对象的目的都是抽象聚合若干属性以简化设计和沟通，有了这一层抽象，我们在使用人员实体时，不会产生歧义，在引用地址值对象时，不用列举其全部属性，在同一个限界上下文中，大幅降低误解、缩小偏差，两者的区别如下：
①两者都经过属性聚类形成，实体有唯一性，值对象没有。在本文案例的限界上下文中，人员有唯一性，一旦某个人员被系统纳入管理，它就被赋予了在事件、流程和操作中被唯一识别的能力，而值对象没有也不必具备唯一性。
②实体着重唯一性和延续性，不在意属性的变化，属性全变了，它还是原来那个它；值对象着重描述性，对属性的变化很敏感，属性变了，它就不是那个它了。
③战略上的思考框架稳定不变，战术上的模型设计却灵活多变，实体和值对象也有可能随着系统业务关注点的不同而更换位置。

```