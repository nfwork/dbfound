# dbfound

`dbfound` 是一个以 `Model XML + Context + HTTP API / Java API` 为核心的数据接口开发框架，适合快速构建后台管理、报表查询、批量处理、导入导出等数据密集型场景。

它的目标不是替代所有应用开发方式，而是尽量用更少的样板代码，把常见的数据接口开发收敛为：

- 一个 `model.xml`
- 一套统一的参数上下文 `Context`
- 一组统一的 query / execute 调用方式

对于管理后台、运营平台、内部系统、数据报表等场景，这种方式通常可以显著降低接口开发成本。

## 为什么是 dbfound

传统方式下，一个简单的数据接口往往需要拆成：

- Controller
- Service
- DAO / Mapper
- SQL 映射文件
- 参数对象 / 返回对象

而在 `dbfound` 中，很多场景可以直接用一个 model 文件完成主要的数据访问逻辑，再按需用 Java 做业务编排和扩展。

`dbfound` 适合解决的问题包括：

- 列表查询、详情查询、增删改查
- 带动态过滤条件的 SQL 查询
- 批量插入、批量更新
- Excel / CSV 导入导出
- 通过 HTTP 快速暴露数据接口
- 在 Java 中统一调用数据模型
- 用 adapter 对 query / execute 做前后增强

## 核心概念

如果你第一次接触 `dbfound`，先记住下面几个概念：

### 1. model

`dbfound` 以 XML model 为核心载体，一个 model 文件通常对应一组相关的数据接口。

例如：

- 用户管理
- 角色管理
- 报表统计
- 导入任务

### 2. query

`query` 用于查询数据，对应读操作。

### 3. execute

`execute` 用于写操作，对应新增、更新、删除、批量处理等。

### 4. Context

`Context` 是参数与上下文数据的统一载体，常见数据区包括：

- `param`
- `request`
- `session`
- `header`
- `cookie`
- `outParam`

### 5. modelName

`modelName` 由 model 文件路径决定。

例如：

- `classpath:model/sys/user.xml` -> `sys/user`
- `classpath:model/report/daily.xml` -> `report/daily`

## 一个最小示例

下面是一个最简单的查询 model：

```xml
<?xml version="1.0" encoding="UTF-8"?>
<model xmlns="http://dbfound.googlecode.com/model" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xsi:schemaLocation="http://dbfound.googlecode.com/model https://raw.githubusercontent.com/nfwork/dbfound/master/tags/model.xsd">

    <query>
        <sql>
            select user_id, username, nick_name
            from sys_user
            #WHERE_CLAUSE#
            order by user_id desc
        </sql>
        <filter name="username" dataType="varchar" express="username like concat('%', ${@username}, '%')"/>
    </query>

</model>
```

调用方式有两种：

### HTTP 调用

```text
/sys/user.query
```

### Java 调用

```java
Context context = new Context().withParam("username", "admin");
List<Map<String, Object>> users = modelExecutor.queryList(context, "sys/user", null);
```

## 主要能力

`dbfound` 的核心能力包括：

- `query / execute`
- `filter`
- `verifier`
- `collisionSql`
- `sqlPart`
- `batchExecuteSql`
- `batchSql`
- `querySql`
- `excelReader`
- `QueryAdapter / ExecuteAdapter`

这些能力可以组合起来覆盖从简单 CRUD 到中等复杂度报表接口的大多数场景。

## 使用方式

`dbfound` 主要支持两种调用模式。

### 1. HTTP API

适合快速对外提供数据接口：

- `{modelName}.query`
- `{modelName}.query!{queryName}`
- `{modelName}.execute`
- `{modelName}.execute!{executeName}`

### 2. Java API

适合在应用内部调用：

- `queryList`
- `queryOne`
- `query`
- `execute`
- `batchExecute`

## 推荐使用场景

推荐在以下场景优先考虑 `dbfound`：

- 管理后台
- 运营后台
- 内部业务平台
- 数据管理系统
- 报表系统
- 导入导出任务

如果你的系统主要是复杂领域建模、强事务业务编排、重 DDD 聚合设计，`dbfound` 也可以作为数据接口层的一种实现方式，但通常不建议把所有领域逻辑都压进 model。

## 文档导航

建议按下面顺序阅读：

### 入门

- [环境搭建](https://github.com/nfwork/dbfound/wiki/dbfound-environment-setup)
- [官方示例](https://github.com/nfwork/dbfound/wiki/dbfound-example)

### 核心文档

- [dbfound model api](https://github.com/nfwork/dbfound/wiki/dbfound-model-api)
- [dbfound java api](https://github.com/nfwork/dbfound/wiki/dbfound-java-api)
- [dbfound model adapter](https://github.com/nfwork/dbfound/wiki/dbfound-model-adapter)

### 其他文档

- [dbfound mvc api](https://github.com/nfwork/dbfound/wiki/dbfound-mvc-api)
- [dbfound ui api](https://github.com/nfwork/dbfound/wiki/dbfound-ui-api)

如果你维护的是旧版 JSP / MVC 场景，请重点阅读 MVC、UI 相关文档；如果你是在现代 Spring Boot 项目中使用，建议优先关注 `model api` 与 Spring Boot starter。

## Spring Boot 用户入口

如果你是在 Spring Boot 项目中使用 `dbfound`，建议直接从下面这个项目开始：

- [dbfound-spring-boot-starter](https://github.com/nfwork/dbfound-spring-boot-starter)

它提供了更符合现代 Spring Boot 项目的集成方式，以及 `ModelExecutor` 等常用调用能力。

## 版本说明

`dbfound` 当前同时存在不同技术路线的版本系列：

- `3.x`：基于 `javax.servlet`
- `4.x`：基于 `jakarta.servlet`

选择版本时请根据你的运行环境决定：

- Spring Boot 2.x / 旧版 servlet 体系，优先关注 `3.x`
- Spring Boot 3.x / Jakarta EE 体系，优先关注 `4.x`

具体最新版本请以仓库 release 或实际发布版本为准。

## 项目定位变化

`dbfound` 早期除了后端数据接口能力，还包含 UI / JSP 标签式开发能力。随着前后端分离逐渐成为主流，当前更建议将 `dbfound` 主要理解为：

- 面向数据接口开发的核心能力
- 基于 model 的数据访问表达方式
- 面向 Spring Boot / HTTP API / Java API 的集成能力

旧版 UI / MVC 能力仍可继续使用，但不再建议作为大多数新项目的首选入口。

## 适合怎样的团队

`dbfound` 通常更适合：

- 管理后台较多的团队
- 数据接口需求频繁变化的团队
- 需要快速搭建内部系统的团队
- 希望降低重复 CRUD 成本的团队

如果你的团队经常遇到“一个简单后台接口要写很多层文件”的问题，那么 `dbfound` 会比较有价值。

## 快速开始建议

如果你想快速感受 `dbfound`，建议这样开始：

1. 跑通官方示例
2. 阅读 `dbfound model api`
3. 手写一个最简单的 `query`
4. 再写一个 `execute`
5. 最后再学习 `filter / sqlPart / adapter / batchExecuteSql`

这样比一开始就阅读全量 API 更容易建立正确心智模型。

## 贡献与反馈

欢迎通过以下方式帮助改进 `dbfound`：

- 提交 issue 反馈文档问题或使用问题
- 提交 pull request 改进示例或文档
- 补充现代 Spring Boot 场景下的最佳实践

如果你在真实项目中使用了 `dbfound`，也欢迎分享案例和经验，帮助文档变得更实用。
