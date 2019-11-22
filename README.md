## 前言

    dbfound是笔者在2011年开始起草的一个持久层框架，2012年发布1.0版本，在googlecode开源；2014年迁移到github；目前最新版本2.4.5。

    框架最初的想法是提供一个容器，把sql以xml载体，部署运行在容器中，就可以对外提供curd等接口服务；这就是dbfound的起源；dbfound为快捷而生。

    期初的dbfound只提供了rest http接口来操控数据库，在1.3版本后出现了一个新的灵感，如果有一套UI框架 也像配置sql一样 来配置界面，并且能与保存sql的xml绑定，而进行自动的增删改查，那会是一个怎么样的东西；于是有了dbfoundui。dbfoundui为款速ui开发而生，极大的简化了web前端编程的难度。

    最初的dbfound结合后面的dbfoundui，组成了现在的dbfound；dbfound为快捷而生。dbfound注重后端的数据库编程，简化编程技术难点、缩减编程时间成本，对于管理后台类的数据接口非常实用；dbfoundui注重前端界面编码，以标签可配置的方式 提供简介的界面开发，在管理后台领域得到广泛认可。

## dbfound环境搭建

    dbfound提供了两种开发模式，一种是纯dbfound框架搭建，一种是dbfound+springboot架构；

    详细的环境搭建步骤，请参考：[https://github.com/nfwork/dbfound/wiki/dbfound%E7%8E%AF%E5%A2%83%E6%90%AD%E5%BB%BA%E4%B8%8Edemo%E4%B8%8B%E8%BD%BD](https://github.com/nfwork/dbfound/wiki/dbfound%E7%8E%AF%E5%A2%83%E6%90%AD%E5%BB%BA%E4%B8%8Edemo%E4%B8%8B%E8%BD%BD)

    我们也提供了两种架构的demo：

    纯dbfound架构demo：**[https://github.com/nfwork/dbfound-demo](https://github.com/nfwork/dbfound-demo) **

    dbfound+springboot架构demo：**[https://github.com/nfwork/dbfound-springboot-demo](https://github.com/nfwork/dbfound-springboot-demo) **

## dbfound 开发api文档

    dbfound api文档请参考：[https://github.com/nfwork/dbfound/wiki/dbfound-api-%E5%BC%80%E5%8F%91%E6%96%87%E6%A1%A3](https://github.com/nfwork/dbfound/wiki/dbfound-api-%E5%BC%80%E5%8F%91%E6%96%87%E6%A1%A3)

## 项目开源地址

    dbfound开源github地址：[https://github.com/nfwork/dbfound](https://github.com/nfwork/dbfound)

    dbfound-spring-boot-starter开源地址：[https://github.com/nfwork/dbfound-spring-boot-starter](https://github.com/nfwork/dbfound-spring-boot-starter)

## dbfound与springboot集成介绍

    dbfound与springboot集成文档：[https://github.com/nfwork/dbfound/wiki/dbfound%E4%B8%8Espringboot%E9%9B%86%E6%88%90%E8%AF%B4%E6%98%8E%E6%96%87%E6%A1%A3](https://github.com/nfwork/dbfound/wiki/dbfound%E4%B8%8Espringboot%E9%9B%86%E6%88%90%E8%AF%B4%E6%98%8E%E6%96%87%E6%A1%A3)

## dbfound三层架构介绍

    dbfound三层架构使用，dbfound自身的mvc 和 dbfound+springboot架构下的 springmvc：[https://github.com/nfwork/dbfound/wiki/dbfound%E7%9A%84%E4%B8%89%E5%B1%82%E6%9E%B6%E6%9E%84%E4%B9%8Bdbfound-mvc](https://github.com/nfwork/dbfound/wiki/dbfound%E7%9A%84%E4%B8%89%E5%B1%82%E6%9E%B6%E6%9E%84%E4%B9%8Bdbfound-mvc)