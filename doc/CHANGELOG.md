# DBFound 版本变更记录

本文档记录 DBFound 各版本的主要变更、修复内容和升级注意事项。版本记录按发布时间倒序排列，最新版本在前，便于快速了解近期变化和升级影响。

## 维护规则

- 每个版本使用二级标题；同一组变更对应多个版本时，在同一标题中使用 `/` 分隔。版本段落按发布时间倒序维护。
- 修复缺陷使用“修复”，性能或结构调整使用“优化”，新增能力使用“新增”或“支持”。
- 如果变更存在兼容性影响，请同步补充到“升级注意事项”。
- 不确定或待补充的内容请明确标注“待补充”，避免留下空版本。

## 升级注意事项

- 父子项目运行 `main` 方法无法访问 JSP 时，请将 working directory 设置为 `$MODULE_WORKING_DIR$`。
- `3.3.11` 版本调整了 `filter` 判断逻辑：所有 `filter` 会放入 `param`，升级时需检查依赖 `filter` 空值判断的业务逻辑。
- `3.4.1` 版本调整了导出 `exportSize` 的生效逻辑：只要 `context.isExport()` 为 true 即可生效，不再依赖 `autoPager` 判断。
- `3.4.5` 和 `4.1.2` 默认通过 `dbfound_request_handler` 提供 Web API；如需保持旧行为，可配置为 `dbfound_default_controller`。
- `3.5.8` 和 `4.2.5` 优化了 `countSql` 执行逻辑：调用 `beforeCount` 前会先生成 SQL 和对应的 `exeParam`，该调整存在一定兼容性影响。
- `3.6.2` 中，`Context.withBeanParam` 方法和 `JsonUtil.ResponseObjectSerializer` 暂不支持 `@Column`。
- `3.6.4` 之后实现的 `instr`、`locate` 函数区分大小写；如需忽略大小写，建议配合 `upper` 或 `lower` 使用。
- `3.7.1` 和 `4.4.1` 优化了 model 数值类型处理：`Types.DECIMAL`、`Types.NUMERIC` 改为使用 `BigDecimal` 接收数据。升级时需检查原先按 `Double`、`Float`等类型直接强转或比较的代码，必要时改为通过统一的类型转换方法处理。

---

## dbfound 3.7.7 - 2026-06-05 / dbfound 4.4.5 - 2026-06-09

- 优化 Excel 导入功能，自动去掉空白行，增强兼容性，解决空文件或第一行为空白行时空指针异常

## dbfound 3.7.6 - 2026-05-28 / dbfound 4.4.4 - 2026-06-02

- 修复 Excel 导出 format 失效问题

## dbfound 3.7.5 - 2026-05-26 / dbfound 4.4.3 - 2026-05-22

- 优化 modelCache 异常处理，异常 model 不再进行缓存，避免过多异常 model 导致内存溢出
- 优化 modelReader 逻辑，优先使用 URL 方式读取 model，不轻易做 `file.exists()` 判断，提升性能
- 优化 ActionEngine 逻辑，添加 ActionReflect 清空逻辑

## dbfound 3.7.4 - 2026-05-20

- 优化 ModelReader 异常信息
- 优化 Spring Boot init 方式，解决因传入 servlet context 导致 projectRoot 错乱问题

## dbfound 3.7.3 - 2026-05-19

- `DBFoundConfig` 重构配置去静态化，添加 `initToken` 机制进行访问鉴权

## dbfound 3.7.2 - 2026-05-11 / dbfound 4.4.2 - 2026-05-11

- 优化 model 日志输出，begin 和 end 日志增加 query、execute 标识
- 修复 `countSql` 不支持 SQL `WITH` 语法的问题
- 优化`DataUtil`方法，`intValue`，`longValue`，`shortValue` 支持 1.0 转化 1
- 优化 `SqlEntity` 参数 number 类型处理，字符串转 number 时使用 `BigDecimal` 替代 double
- 优化`printContext`相关代码
- context 新增 `currentModelAction` 参数
- 优化 context 数据处理，新增 `ContextData` 独立管理数据
- 优化 DBFound EL

## dbfound 3.7.1 - 2026-05-08 / dbfound 4.4.1 - 2026-05-08

- DBFound 项目参数支持从 JVM 参数获取，datasource 参数可使用 `dbfound.datasource.{provideName}.password`
- dbfound异常处理模块优化
- `basePath`设置默认值为`${@contextPath}`
- `DBFoundConfig`逻辑优化 `${@projectRoot}`推算逻辑调整
- 优化context相关代码与Transaction事务管理相关逻辑
- model相关代码优化，`Types.DECIMAL`、`Types.NUMERIC` 采用`BigDecimal`来接收数据
- 优化`StringUtil`中sql的处理
- 优化query代码并补充相应的测试用例
- 优化adapter功能添加handle功能支持
- 优化各个版本数据方言并添加测试用例

## dbfound 3.7.0 - 2026-05-01 / dbfound 4.4.0 - 2026-05-01

- 新增 `api-allow-urls` 参数，配置需要暴露的 model HTTP API 接口地址
- `CollisionException` HTTP 状态码改为 422
- 优化`modelCache`逻辑，在正式环境`modelModifyCheck`关闭后，错误的model文件，不再检查更新
- 升级MySQL 9.5.0和POI 5.4.1版本
- DBFound 3.7 之后，JDK 要求调整为 Java 11

## dbfound 3.6.8 - 2026-04-27 / dbfound 4.3.6 - 2026-04-28

- 新增 `FIFOCache`，DSql 缓存改为 FIFO cache 方式，提升性能

## dbfound 3.6.7 - 2026-04-24 / dbfound 4.3.5 - 2026-04-24

- query 新增 `printContext` 属性，支持在 query 执行前将 Context 数据打印到控制台

## dbfound 3.6.6 - 2025-12-17 / dbfound 4.3.4 - 2025-12-17

- 优化 Excel 导出时 title 或 `name` 为空的报错信息

## dbfound 3.6.5 - 2025-09-23

- 修复`modelName`兼容问题（以反斜杠开头）

## dbfound 4.3.3 - 2025-07-17

- 修复`StringUtil`在下划线转驼峰时，因连续下划线(或以下划线结尾)出现异常问题

## dbfound 3.6.4 - 2025-06-23

- DSql function模块重构
- 新增`substring`、`substring_index`、`find_in_set`、`instr`、`locate`、`upper`、`lower`函数支持

## dbfound 4.3.2 - 2025-06-12

- context复制`session`数据时不再主动创建`session`

## dbfound 3.6.3 - 2025-06-05

- Excel导入支持定义`columns`
- 调整默认导出大小为20w

## dbfound 3.6.2 - 2025-05-22

- 重构`ModelEngine`方法，支持用户设置自定义的`ModelOperator`
- 移除`StartListener`，web模块的Listener支持`ServletContext`参数
- `BaseControl`改名为 `BaseController`
- 新增`NoServletResponseException`异常类，在`WebWriter`没有Response对象的时候抛出
- 新增`isBatchExecuteRequest`方法，优化batchExecute判断逻辑
- 新增responseObject.getOutParam(paramName)方法
- 新增context.withParam（`name`，value）、withBeanParam（bean）、`withMapParam(map)`、`withPageStart(0)`、`withPageLimit(10)`方法
- 优化`modelExecutor`、`modelEngine`简化缩减方法

## dbfound 3.6.1 - 2025-05-07 / dbfound 4.3.1 - 2025-05-09

- `param`标签在没有声明类型的情况下，默认改为`UNKNOWN`
- export默认导出大小控制在30万
- 修复`emptyAsNull`逻辑缺陷

## dbfound 3.6.0 - 2025-02-11 / dbfound 4.3.0 - 2025-02-14

- `sqlPart`添加`elseif`和`else`类型
- Excel导入加入double自动转long
- 修复文件上传，因`filter`过早context构建，导致文件上传识别异常，`request`对象问题

## dbfound 3.5.9 - 2025-01-13

- 修复匿名枚举类处理异常问题

## dbfound 3.5.8 - 2024-10-09 / dbfound 4.2.5 - 2024-11-05

- 优化静态参数赋值逻辑，执行sql不再调用staticParamParse
- 优化`countSql`执行逻辑；在调用`beforeCount`前 就将sql生成好，并生成相应的exeParam，该逻辑与原逻辑存在一定兼容问题
- 优化sqlEntity、query、sqlEntity正则sql生成相关逻辑（去掉Match相关方法如Matcher.quoteReplacement），使用Iterator替换for
- 集合参数允许空集合，null也当成空集合处理，不再对空集合报错

## dbfound 3.5.7 - 2024-08-26 / dbfound 4.2.4 - 2024-09-04

- 优化`param`赋值逻辑，提升执行性能
- 修复3.5.6中`sqlPart`执行异常
- 修复在循环逻辑下`param`默认值不生效问题
- 优化cache性能，map的key不再使用class对象，改为string
- DSql支持自定义函数
- el语法优化支撑多维数组 a[1][2]
- 新增`ModelCompileException`对重名的query和execute进行检查报错
- 优化`batchExecuteSql`执行逻辑，分页时复用`param`对象
- model语法xsd文件添加unique约束对`param`、`filter`、query、execute要求`name`唯一

## dbfound 3.5.6 - 2024-08-20 / dbfound 4.2.3 - 2024-08-21

- adapter配置分隔符添加逗号、分号支持，controlle包名配置统一分割逻辑
- `batchSql`、`batchExecuteSql`添加`item`、`index`支持
- 优化`sqlPart`对应for类型的逻辑优化，只有在有for子节点的情况，params才new一个新的出来

## dbfound 3.5.5 - 2024-08-08

- 修复`sqlPart`在for循环下面`sourcePath`取值异常问题
- query 新增 Verifier，支持参数校验
- dSql支持`between and`语法
- dSql修复`not like`不生效问题
- adapter逻辑升级，支持配置多个

## dbfound 3.5.4 - 2024-07-29

- 新增系统参数logWithParamSql，sql日志打印支持结合`param`参数一起打印
- 优化异常处理逻辑，sql异常不再打印sql到前端
- `sqlPart`自动补全特性添加是否已经带有`and`逻辑

## dbfound 4.2.2 - 2024-07-24

- 优化dSql or表达式、`and`表达式、in表达式 执行逻辑
- 修复for类型的`sqlPart`因if过滤导致为空的情况下异常问题
- 修复`sqlTrim`前置逗号因空格不生效问题

## dbfound 3.5.3 - 2024-07-17

- 修复batchExecute日志打印`modelName`问题
- 优化model执行相关代码，减少不必要参数
- `batchExecuteSql`改为根据`paramNameSet`进行参数`batchAssign`处理，与`sqlPart`一致
- 优化`sqlPart`在for循环下的 if判断逻辑，提升性能
- IF类型的`sqlPart`支持`sourcePath`下是否有值判断是否生效

## dbfound 3.5.2 - 2024-06-05 / dbfound 4.2.1 - 2024-06-18

- 修复Form表单 multipart方式下，未处理JSON字符串转化对象问题
- `param`添加`getMapValue`和`getListValue`泛型方法
- 修复BatchExecuteSql赋值问题

## dbfound 3.5.1 - 2024-05-27 / dbfound 4.2.0 - 2024-05-30

- 拦截器支持处理option跨域请求
- 文件下载和Excel导出，expose 改为addHeader方式，解决header覆盖问题
- 新增启动监听Listener支持
- 异常处理优化，dbfound web支持自定义`exceptionHandler`

## dbfound 3.5.0 - 2024-05-15

- 实现`sqlPart`嵌套逻辑（暂不支持for循环嵌套）
- `sqlPart`支持`item`、`index`设置
- 优化query语法文件，`param`、sql、`filter`校验顺序
- 新增in表达式支持，优化DSql在sql语法不支持的情况下异常处理
- 重构model模块代码
- 重构mvc模块代码
- mvc支持注解方式`@ActionMapping` `@ActionTransactional`
- 重构UI部分代码
- 优化Adapter从springContext获取逻辑，解决父子类依赖找不到bean的问题

## dbfound 4.1.7 - 2024-04-16 / dbfound 3.4.8 - 2024-04-18

- 移除`fileSaveType`相关逻辑； 去掉`FileUploadFolder`目录设置
- 集合参数支持逗号分割的字符串

## dbfound 4.1.6 - 2024-04-12

- `param`为File类型时，支持访问本机文件
- 文件下载逻辑优化，使用fileDownloadResponseObject进行处理
- file处理逻辑优化，不再将`param`的value设置为InputStream等需要使用时再转化，只有`executeSql`支持file类型的`param`，`querySql`只支持一个文件下载

## dbfound 3.4.7 - 2024-01-17 / dbfound 4.1.5 - 2024-02-05

- `InitProcedure`不再开启事务；解决spring事务管理冲突导致连接不回收问题

## dbfound 3.4.6 - 2023-12-22 / dbfound 4.1.4 - 2024-01-03

- 优化`param`相关逻辑，新增`IOType`、`FileSaveType`枚举类
- 优化context将`request`和Response属性设置为final
- 对`OPTIONS`请求不进行处理

## dbfound 3.4.5 - 2023-12-08

- 新增`sqlTrim`功能，支持去除sql前后多余的逗号
- `param`添加`emptyAsNull`属性，解决部分业务需要赋值空字符串情况
- `sqlPart`支持`andClause`和`whereClause`

## dbfound 4.1.3 - 2023-11-29

- 异常处理添加对error基本的处理
- 重构`dbfound_request_handler`处理逻辑，采用`HandlerMethod`实现；解决advice不生效问题

## dbfound 4.1.2 - 2023-11-24

- 新增`dbfoundRequestHandleMapping`处理请求
- 移除 `openDefaultController` 参数，改为 `api-expose-strategy: dbfound_request_handler`，兼容 `path_pattern_parser`
- `webWriter` 响应类型改为 `application/json`
- 优化`Reflector`方法多态处理逻辑

## dbfound 3.4.4 - 2023-11-14

- 修复Excel导入缺失一行问题

## dbfound 4.1.1 - 2023-11-07 / dbfound 3.4.3 - 2023-11-02

- 优化 SQL 执行异常处理，错误信息包含 `modelName` 和 `queryName`
- 优化 `JsonUtil` 处理逻辑，避免直接调用 `writerObject`，提升处理性能

## dbfound 4.1.0 - 2023-10-17 / dbfound 3.4.2 - 2023-10-19

- 优化 `DataUtil.convertMapToBean` 方法，支持下划线转驼峰
- context 新增 `getMap`、`getList`、`getInt`、`getBoolean` 等泛型方法
- 优化 DBFound EL 逻辑，提升 bean 反射性能
- 优化 model 和 enum 的泛型实现逻辑
- adapter 添加 `MapQueryAdapter` 和 `ObjectQueryAdapter`

## dbfound 3.4.1 - 2023-10-13

- 数据库连接池交由 Spring 管理
- 优化 Excel Java API，添加相应的导出方法
- 统一 bean 命名规范，DBFound bean 统一小写

## dbfound 3.4.0 - 2023-10-11

- csv导出对日期字段 添加格式化处理； 修复导出时没有null判断
- 重构Excel导出导入逻辑，支持自定义 解析器，支持xlsx导出
- 移除自带JSON，改为使用`Jackson`
- 移除`BeanUtil`包，改用reflect
- connection 事务执行完成后设置 `autoCommit=true`
- 移除 JXL，全部使用 POI；支持合并单元格
- 使用`LinkedHashMap`保存参数，维持打印参数的顺序
- 升级使用 Hikari 连接池
- 升级使用 SLF4J 日志
- query 导出与最大分页限制设置默认值 50w 和 1w

## dbfound 4.0.8 - 2023-09-26

- 新增csv导入导出支持； sys/accessLog.export?`export_type=csv`
- `batchExecuteSql` 优化日志打印，只打印每次执行第一行的参数记录

## dbfound 3.3.12 - 2023-09-15

- `sqlPart`添加`autoClearComma`功能

## dbfound 4.0.7 - 2023-09-07

- 修复单行注释兼容性问题

## dbfound 3.3.11 - 2023-08-29

- 修复dbfound3 `printContext`时，JsonUtil如果data中有fileItem导致error问题
- 修复dbfound4 文件上传中 如果有普通文本字段，会乱码问题
- 文件上传优化，Spring Boot项目支持dbfound文件上传处理逻辑
- `filter`执行逻辑调整，判断生效逻辑后置，`filter`可在adapter设值
- JsonUtil功能调整，支持`int[]`数组JSON转化，对`transient`字段不进行JSON序列化
- 文件上传逻辑升级，支持多文件上传，同名多文件使用list处理
- Excel导入，对日期类型进行完善，支持time和 datetime
- el `setData`方法支持设置 数组格式，在不存在的情况下 new 一个arrayList
- 优化sql异常处理，适配Spring jdbc的sql异常处理
> 升级提示：该版本调整了 `param` 处理逻辑，执行 adapter 前不再进行有效性过滤，会全部放入 params。升级时需重点检查 `param` 空值判断逻辑和 `beforeQuery` 方法。

## dbfound 4.0.6 - 2023-08-18

- dbfound4 集成common-fileupload2，实现文件上传功能

## dbfound 3.3.10 - 2023-08-15

- Sql注释解析支持
- Model相关bean继承关系，init逻辑优化
- `sqlPart`在`where`子句中添加`autoCompletion`自动填充`where`或者`and`功能
- 修复导出时`currentPath`为null问题
- sql生成规则优化，避免出现连续多个空格
- 日志逻辑优化，对敏感参数进行脱敏打印

## dbfound 3.3.9 / dbfound 4.0.5 - 2023-08-08

- 修复query `getSimpleList`当class为Integer等包装类型时，null值返回0问题

## dbfound 3.3.8 - 2023-08-07

- 查询返回类型处理，不再对 .0 的 float、double类型 转化为long、int
- 为空判断改为 `getObject`；修复`querySql`没有null判断，而导致int类型null变为0的问题
- 优化query分页逻辑，新建count参数判断是否需要执行

## dbfound 4.0.4 - 2023-08-03

- 调整starter初始化逻辑，条件加载web mvc相关bean，以便支持`WebFlux`或其他项目

## dbfound 3.3.7 - 2023-07-31

- 修复`sqlPart`创建`param`时，如果`param`已经存在则进行告警

## dbfound 4.0.3 - 2023-07-28

- 修复`querySql`使用`sqlPart`时没有自动创建`param`问题

## dbfound 3.3.6 - 2023-07-27

- adapter异常处理优化
- 数据库方言扩展 优化
- 新增功能`sqlPart`
- `d_p_rm` 改为 `d_rm`
- model root path 拼写错误
- enum映射添加code属性支持

## dbfound 3.3.5 / dbfound 4.0.2 - 2023-07-20

- query支持简单类型查询
- Excel导出mapper支持数组数据

## dbfound 3.3.4 - 2023-07-18

- 修复启动依赖查询时，dbfound还没初始化问题
- 事务管理器支持事务传播特性
- MySQL驱动默认使用 CJ 版本
- 事务管理支持只读事务
- 修复事务结束后conn没有重置问题
- 修复query没有进入事务问题
- 连接池添加maxWait属性，默认5000，避免出现等待死循环问题

## dbfound 3.3.3 - 2023-07-01 / dbfound 4.0.1 - 2023-07-11

- 修复Excel导出不携带请求column参数问题
- `executeSql`、`querySql`执行输出参数进行日志打印
- `DispatcherFilter`不再对options请求进行处理

## dbfound 3.3.2 - 2023-06-29

- query和execute层面支持设置`connectionProvide`

## dbfound 3.3.1 + 4.0.0 - 2023-06-15

- DSql cache 函数写法优化
- 优化 datasource provider 注册逻辑
- 新增 datasource extension 区域

## dbfound 3.3.0 - 2023-06-12

- 新增`LocalDate`支持
- 优化sql查询类型封装逻辑，不再使用if `else`，改为resolver方式
- Excel导出支持`LocalDate`
- time和localtime支持
- `QueryResponseObject`添加`join`方法
- 移除context的dateformat
- JsonUtil去掉context引用，stringUtil添加缓存
- 优化array处理逻辑，使用Array.getClass().isArray 和 Array.get(`index`)优化性能
- 优化el cache处理逻辑，支持set等集合类似转为array处理
- 对于exception处理和默认service提供接口，去掉链式事务管理，只支持默认的dbfound事务管理

## dbfound 3.2.4

- El set属性时 判断错误不应该使用hasgetter
- 优化reflector是否access判断，仅public可行； 解决3.2.3 public 属性不能`get`和set问题

## dbfound 3.2.3

- 优化`Reflector`代码,去掉private方法反射调用，去掉无用逻辑 ; 解决JDK11 告警问题

## dbfound 3.2.2

- 优化`QueryResponseObject`代码，采用`DataUtil`进行类型转化
- `Param`添加`getInt` `getLong` `getDouble` `getFloat`方法
- `QueryResponseObject` `getMap` 对于冲突的情况，采用后值覆盖

## dbfound 3.2.1

- 优化model调用层次逻辑，Context添加`modelDeep`监听参数，对非top的调用不进行开始结束日志输出，不进行连接关闭
- `QueryResponseObject`添加`getMap`方法

## dbfound 3.2.0

- 支持JSON序列化驼峰转下划线
- 新增`openSession`开关，调整`openSession`设置逻辑
- 调整全局配置选项，统一交由`DBFoundConfig`管理
- 移除`queryLimit`全局配置
- query对象添加`exportSize`属性，限制导出大小
- 默认不开启model文件修改检查
- query对象添加`maxPagerSize`属性，限制分页大小

## dbfound 3.1.3

- 优化JsonUtil 序列化性能

## dbfound 3.1.2

- 对responseObject添加`getList`方法，方便获取简单list对象
- 新增`modelExecute`无context方法，丰富查询

## dbfound 3.1.1

- 对responseObject 添加 `get`方法，方便获取单个属性或对象

## dbfound 3.1.0

- 修复 `.do`请求 ，对于error级别的异常处理情况
- 修复 inner调用逻辑，避免连接池频繁关闭
- 事务的隔离级别
- `filter`添加条件判断`condition`

## dbfound 3.0.5

- 优化sqlserver方言，添加SqlServer方言V2版本
- 修复`.do`请求异常问题
- 增强entity属性映射，解决 大小写 `as`后，找不到属性问题

## dbfound 3.0.3

- `filter` 的`express` `fullTrim`格式化
- 优化JSON性能，去掉null.*匹配，改为用startwith，stringbuffer改为stringBuilder
- model缓存优化，添加`pkgModel`属性 判断是否需要重新加载

## dbfound 3.0.2

- el 支持length获取字符串长度
- Excel导出下载添加`Content-Length`
- 新增 `case when otherwise`逻辑
- dSql支持`like`，修复dSql一些bug

## dbfound 3.0.1

- 新增DSql引擎，提升执行性能
- `whenSql`去除多余的空格 换行 退格
- 优化MySQL分页，使用占位符方式
- el支持size获取大小
- 新增响应code

## dbfound 2.6.8 + 2.3.3

- 修复Excel导出mapper找不到情况下空指针异常问题

## dbfound 2.6.7 + 2.3.2

- 修复Excel导出parameter为空时空指针异常问题
- 调整依赖版本 freemarker、MySQL驱动、common-upload等
- 移除log4j依赖
- 修复Excel导出 不支持对象问题

## dbfound 2.6.6 + 2.3.1

- 优化DBFound EL针对于集合取值性能
- 升级 Excel导出，5万一sheet，支持大于5w数据导出
- Excel日期设置格式
- 优化`batchExecuteSql` 性能

## dbfound 2.6.5 + 2.3.0

- 新增`byte[]`类型支持，datatype归为file
- context改为根据thread id 进行线程判断
- 优化`dataType`设置错误时的异常处理
- 修复`collection`参数二次取值bug

## dbfound 2.6.4 + 2.2.9

- ModelExecutor batchExecute 方法 list泛型修改
- 修复多个query分页相互影响问题

## dbfound 2.6.3 + 2.2.8

- 优化正则（改为全局static）、提升DBFound EL性能，el支持entity驼峰转化取值
- 新增elcache 提升el性能
- dateformat统一放入到context，避免重复获取

## dbfound 2.6.1 + 2.2.6

- 调整el简单类型数组获取不到问题
- 新增`collection`参数类型，支持集合取值
- 日志打印逻辑升级，`param`参数按需打印

## dbfound 2.6.0 + 2.2.5

- 修复file InputStream 关闭问题，多个文件时 可能存在问题
- context 处理requestBody数据，添加finally关闭流
- `InitProcedure`添加catch和finally逻辑
- 优化file相关util类，去除无用代码

## dbfound 2.5.9 + 2.2.4

- 修复boolean类型 数字0 应该处理为false
- 修复`filter` 未添加boolean类型

## dbfound 2.5.8 + 2.2.3

- 新增boolean类型支持，当类型为number时 boolean转化为 1和0，其他 true和false的字符串
- 新增枚举类型支持
- 修复2.2.2版本中`BeanUtil`获取属性 都是string问题
- 适配器添加`beforeCount`功能

## dbfound 2.5.7 + 2.2.2

- SimpleDateFormat 线程不安全，每次重复定义 伤性能问题
- `setContextData`标签添加`sourcePath`、`valueTemplate`属性
- `param`和`outParam`支持多层级数据设置，如果不是绝对路径，都使用相对路径赋值
- context root只包含`param`、`outParam`、`request`和`session`；其他不允许设置； 该调整存在旧版本兼容性影响
- 新增`printContext`标签，用于打印上下文数据

## dbfound 2.5.6 + 2.2.1

- `batchSql` 未自动创建`param`
- `querySql` 支持自动创建 `param`，`querySql`赋值数据类型问题，将query、`querySql`、`batchSql` 数据逻辑进行抽离
- 优化 EL 简单类型赋值性能
- 新增`setContextData`标签
- query和execute支持设置`currentPath`

## dbfound 2.5.5 + 2.2.0

> 升级提示：存在 `queryOne` `queryAdapter` Java 代码兼容性问题，需要小的改动

- model文件 query配置支持配置 entity属性配置 泛型
- DBFound EL 支持简单类型 value值获取
- adapter添加泛型支持，可以配置entity属性
- JDK依赖版本改为1.8
- 优化异常处理逻辑，只有CollisionSql不打印堆栈

## dbfound 2.5.4 + 2.1.9

- adapter适配 Spring ioc，支持从容器中获取
- 异常回滚机制改为`rollback=exception`
- 新增`join-chained-transaction`属性，决定是否添加到链条事务，默认true
- 新增`provideName`重复定义校验
- 新增`dbfoundTransactionManager`，并设置为默认值

## dbfound 2.5.3 + 2.1.8

- 修复字段为空时 Excel导出mapper空指针异常问题
- adapter 支持，声明 `queryAdapter` 和 `executeAdapter`
- Excel导出方法，移到service里面

## dbfound 2.5.2 + 2.1.7

- 修复count方法在union和distinct语法下的错误
- 支持单双引号以及嵌套
- `batchSql`添加`cursorRootPath`属性，来设置数据存储路径
- batch对于空字符串设置为null与外部情况保持一致
- 修复model配置`pagerSize`获取count错误
- 新增 `open-default-controller`开关，决定是否开启默认controller

## dbfound 2.5.1 + 2.1.6

- Excel导出mapper 不支持逗号分隔值
- `batchSql` 和 `batchExecuteSql` 不支持相对路径的问题
- `batchSql` 子标签`executeSql`、`whenSql`、`batchExecuteSql`等 如果有 静态赋值，存在赋值异常
- `batchSql`执行中 如果调用query和 execute 路径没有设置为 当前路径问题
- post请求 `filter` 数字类型 0不生效问题
- 嵌套query、execute执行后 `currentPath`和`currentModel`变化问题
- `outMessage`控制是否输出问题
- 移除`entityClone`逻辑，只保留必要的`param`和`filter`
