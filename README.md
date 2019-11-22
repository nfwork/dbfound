# dbfound
dbfound快速应用开发平台
<p>
  DBFound 开发环境搭建： <a href='https://my.oschina.net/nfwork/blog/3132793'>https://my.oschina.net/nfwork/blog/3132793</a><br>
  DBFound API开发文档： <a href='https://my.oschina.net/nfwork/blog/322892'>https://my.oschina.net/nfwork/blog/322892</a><br>
  DBfound + SpringBoot 集成文档 <a href='https://my.oschina.net/nfwork/blog/3130250'>https://my.oschina.net/nfwork/blog/3130250</a><br>
  dbfound ui组件grid 实现后端sql排序： <a href='https://my.oschina.net/nfwork/blog/3131708'>https://my.oschina.net/nfwork/blog/3131708</a><br>
</p>
<p>
DBFound宗旨：解决开发技术复杂、难度高、开发速度慢等问题。
              提供快速、便利、高效率的开发平台。
</p>
<p>
DBFound特性：
</p>
<p>
简叙：
    在如移动互联网行业，管理后台具有一些明显的特点，1、系统多；2、系统小；3、开发周期短，上线快，4、需求变化快。为了满足这些个性化的要求，我们需要一套针对性的快速应用开发平台，来满足这个需求。
    同时由于开发人员流动大，所以开发平台的简易程度也直接影响到了系统的交接与运维；
    面对如此场景，一款新的快速应用开发平台应运而生--dbfound快递应用开发平台。
</p>
<p>
无缝的数据交互：
   dbfound实现了前后端无缝的数据交互。只需要指定后端的响应地址。系统自动将数据传送到后端java中进行处理。
   中间通过数据容器Context，js与java之间直接进行Object对象交互。无须额外的格式转化。
</p>
<p>
智能存储：
   dbfound model实现与数据库进行交互的，程序中无须管理数据库连接、事务、资源的释放、数据格式转化等繁琐问题。
   将sql配置在model（一个xml文件）中，调用指定model实现智能的数据库交互。 
   dbfound model实现了热部署，我们可以实时改变sql语句，大大的缩短调试而重启服务器的次数。
   框架提供了浏览器远程ajax调用model的接口，model将二维表结构转化为List-Map格式，然后转化为json格式传递给前台。
   整个过程对程序员来说是透明的。程序员要做的只是配一个xml文件。
</p>
