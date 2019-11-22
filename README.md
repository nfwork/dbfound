## 前言

    dbfound是笔者在2011年开始起草的一个持久层框架，2012年发布1.0版本，在googlecode开源；2014年迁移到github；目前最新版本2.4.5。

    框架最初的想法是提供一个容器，把sql以xml载体，部署运行在容器中，就可以对外提供curd等接口服务；这就是dbfound的起源；dbfound为快捷而生。

    期初的dbfound只提供了rest http接口来操控数据库，在1.3版本后出现了一个新的灵感，如果有一套UI框架 也像配置sql一样 来配置界面，并且能与保存sql的xml绑定，而进行自动的增删改查，那会是一个怎么样的东西；于是有了dbfoundui。dbfoundui为款速ui开发而生，极大的简化了web前端编程的难度。

    最初的dbfound结合后面的dbfoundui，组成了现在的dbfound；dbfound为快捷而生。dbfound注重后端的数据库编程，简化编程技术难点、缩减编程时间成本，对于管理后台类的数据接口非常实用；dbfoundui注重前端界面编码，以标签可配置的方式 提供简介的界面开发，在管理管理领域得到广泛认可。

## dbfound环境搭建

    dbfound提供了两种开发模式，一种是纯dbfound框架搭建，一种是dbfound+springboot架构；

    详细的环境搭建步骤，请参考：[https://my.oschina.net/nfwork/blog/3132793](https://my.oschina.net/nfwork/blog/3132793)

    我们也提供了两种架构的demo：

    纯dbfound架构demo：**[https://github.com/nfwork/dbfound-demo](https://github.com/nfwork/dbfound-demo) **

    dbfound+springboot架构demo：**[https://github.com/nfwork/dbfound-springboot-demo](https://github.com/nfwork/dbfound-springboot-demo) **

## dbfound 开发api文档

    dbfound api文档请参考：[https://my.oschina.net/nfwork/blog/322892](https://my.oschina.net/nfwork/blog/322892)
    
    dbfound api github wiki：[https://github.com/nfwork/dbfound/wiki](https://github.com/nfwork/dbfound/wiki)

## 项目开源地址

    dbfound开源github地址：[https://github.com/nfwork/dbfound](https://github.com/nfwork/dbfound)

    dbfound-spring-boot-starter开源地址：[https://github.com/nfwork/dbfound-spring-boot-starter](https://github.com/nfwork/dbfound-spring-boot-starter)

## dbfound互联网后台案例介绍

    互联网管理后台快速解决方案dbfound；[https://my.oschina.net/nfwork/blog/351012](https://my.oschina.net/nfwork/blog/351012)

## dbfound与springboot集成介绍

    dbfound与springboot集成文档：[https://my.oschina.net/nfwork/blog/3130250](https://my.oschina.net/nfwork/blog/3130250)

## dbfound三层架构介绍

    dbfound三层架构使用，dbfound自身的mvc 和 dbfound+springboot架构下的 springmvc

    [https://my.oschina.net/nfwork/blog/323182](https://my.oschina.net/nfwork/blog/323182)


