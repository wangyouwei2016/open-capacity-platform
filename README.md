# open-capaciry-platform云原生改造与开源for Huawei Cloud

[TOC]

## *改造基于开源项目: [open-capacity-platform](https://gitee.com/dromara/open-capacity-platform)

## 一、背景介绍
### 1、open-capacity-platform

​	[open-capacity-platform](https://gitee.com/dromara/open-capacity-platform)(以下简称：OCP)项目是dromara开源组织推出的一个基于Spring Cloud的企业级微服务框架。包含用户权限管理，配置中心管理，运维管理等功能模块。该项目设计目的是分离前后端，帮助企业快速开发部署，提供快速接入核心接口的能力。



### 2、项目分析

​	OCP的定位是基于Spring Cloud的企业级微服务框架，目的是帮助开发者在应用开发过程中集中精力到业务开发上。就针对OCP而言，提供了基本的日志中心微服务，并用到了普罗米修斯和Zipkin等做监控管理，这些运维、日志相关的服务本身还是需要人工运维，同样会消耗开发者精力。其次，OCP项目提供了部署在服务器和容器的方式，这种部署方式在实际企业开发应用中，需要企业去预估业务量去配置资源，但这种预判过于依赖经验，不可避免会出现采购成本过高或者服器资源不足的情况，并且灵活性和可扩展性不高。

#### <img src="https://gitee.com/HuaweiCloudDeveloper/open-capacity-platform-for-huaweicloud/raw/master-dev/images/ocp-架构图.png" alt="ocp-架构图" style="zoom:150%;" />

## 二、方案设计

​	结合对OCP项目的分析，使用云上组件替换原架构的思路进行改造：将**OCP**用的注册中心从Eureka替换成CSE；使用RDS+DCS实现数据库上云；改用OBS实现原应用中文件上传；使用CodeHub+CloudBuild进行编译构建+制作docker镜像；使用k8s+容器基础设施承载应用；使用APM+AOM+LTS替换原有的运维+日志。

 ### 1、方案架构图

![image-20221118111038179](https://gitee.com/HuaweiCloudDeveloper/open-capacity-platform-for-huaweicloud/raw/master-dev/images/330D431A-FB75-427A-8A0B-F12641C6EDA0.jpg)

### 2、方案优势

​	OCP上云后，所有的服务可以在华为云控制台统一进行管理，打通了开发--测试--部署--运维全生命周期管理，并与其它产品预集成，开箱即用，简化应用云上开发，云上部署，简化部署、发布，简化应用上云。运维能力转到了华为云侧，不需要开发者再去维护运维服务，只需要使用运维服务即可.云原生运维提供了自动化运维和告警降噪等能力。全容器化的架构设计，任务调度更灵活，执行效率更高。利用云计算的灵活性、数据安全性、可扩展性，也可以帮助企业节省成本。

### 3、流程介绍

| <div style="width:70px">主流程</div> | <div style="width:300px">子任务</div> | <div style="width:100px">操作指导 </div>                     |
| :--------- | :----------------------------------------------------------- | ------------------------------------------------------------ |
| 1.准备工作 | 熟悉了解开源项目open-capacity-platform；<br/>根据该文档中：三、云服务介绍 学习云服务； | [开源项目](https://gitee.com/owenwangwen/open-capacity-platform/tree/master-dev/)<br>[云服务介绍](# 三、云服务介绍) |
| 2.本地改造 | **开发基于本地代码进行改造，CSE替换Eureka；**<br/>使用华为云OBS替换掉原调其他厂商的对象存储服务； | [OCP接入CSE](# 2、OCP接入CSE)<br>[使用OBS改造file-center](# 特殊服务改造file-center) |
| 3.数据集成 | RDS 数据库初始化数据库表；<br/>使用RDS 地址替换数据库地址；<br/>使用DCS 地址替换数据库地址； | [集成RDS+DCS](# 4、RDS+DCS替换原项目中的Mysql和Redis)        |
| 4.镜像制作 | 代码上传CodeHub，进行代码托管；<br>基于云上的编译构建能力，构建docker镜像并推送到SWR；<br>SWR中存储部署需要用到的docker镜像； | [代码上传CodeHub](# 3、代码上传CodeHub，进行代码托管)<br>[CoudBuild制作镜像](# 5、CloudBuild进行编译构建，构建docker镜像并推送到SWR) |
| 5.云上部署 | **CCE中创建无状态负载**，创建过程中关联APM；<br/>部署完成后查看CSE中服务注册情况；<br/>**CCE 创建service-ELB 方式暴露服务**；<br/>公网域名申请；<br/>创建域名并记录解析到ELB公网IP； | [创建负载](# 创建负载并关联APM)<br>[创建service-ELB类型服务](# 创建service-ELB访问方式暴露服务)<br>[实现域名访问应用](# 8、DNS实现通过域名访问应用) |
| 6.运维集成 | 部署完成后LTS配置容器日志采集；<br/>APM查看性能指标，调用链等；<br>AOM查看容器标准日志，容器性能监控；<br>LTS查看容器业务日志； | [LTS日志采集](# 7、CCE接入LTS云日志)<br>[APM-调用链](https://support.huaweicloud.com/usermanual-apm/apm_02_0010.html)<br>[AOM查看日志文件](https://support.huaweicloud.com/usermanual-aom/aom_02_0010.html)<br>[LTS云日志](# 7、CCE接入LTS云日志) |
| 7.结果验证 | CCE中负载运行正常；<br>用户业务访问成功；                    | [结果验证](# 六、实现效果)                                   |



## 三、云服务介绍

### 1、涉及云服务介绍

**华为云CSE**：微服务引擎（Cloud Service Engine）是用于微服务应用的云中间件，为用户提供注册发现、服务治理、配置管理等高性能和高韧性的企业级云服务能力；CSE可无缝兼容SpringCloud、ServiceComb等开源生态；用户也可结合其他云服务，快速构建云原生微服务体系，实现微服务应用的快速开发和高可用运维。[帮助文档](https://support.huaweicloud.com/productdesc-cse/cse_productdesc_0001.html)

| <div style="width:54px">云服务</div> | <div style="width:80px">功能模块文档</div> |           <div style="width:240px">文档地址 </div>           | <div style="width:100px">建议掌握程度<br>(了解-操作-精读)</div> |
| :----------------------------------: | :----------------------------------------: | :----------------------------------------------------------: | :----------------------------------------------------------: |
| CSE    | 查看服务注册    | https://support.huaweicloud.com/usermanual-cse/cse_usermanual_0019.html | 了解                             |
| CSE    | 本地代码改造    | https://support.huaweicloud.com/qs-cse/cse_qs_0002.html      | 操作                             |
| CSE    | 本地代码改造    | https://support.huaweicloud.com/devg-cse/cse_devg_0037.html#cse_devg_0037__section56161456192411 | 了解                             |
| CSE    | 本地代码改造    | https://support.huaweicloud.com/devg-cse/cse_devg_0010.html  | 操作                             |
| CSE    | 基于本地安装CSE | https://support.huaweicloud.com/devg-cse/cse_devg_0036.html  | 操作                             |

**华为云CodeHub**：代码托管（CodeHub）源自华为千亿级代码管理经验，基于Git，提供企业代码托管的全方位服务,为软件开发者提供基于Git的在线代码托管服务，包括代码克隆/下载/提交/推送/比较/合并/分支/Code Review等功能。[帮助文档](https://support.huaweicloud.com/productdesc-codehub/devcloud_pdtd_20002.html)

| <div style="width:54px">云服务</div> | <div style="width:80px">功能模块文档</div> |           <div style="width:240px">文档地址 </div>           | <div style="width:100px">建议掌握程度<br>(了解-操作-精读)</div> |
| :----------------------------------: | :----------------------------------------: | :----------------------------------------------------------: | :----------------------------------------------------------: |
| CodeHub | 创建代码仓        | https://support.huaweicloud.com/usermanual-codehub/devcloud_hlp_00035.html | 操作                             |
| CodeHub | 创建并配置秘钥    | https://support.huaweicloud.com/usermanual-codehub/devcloud_hlp_00083.html | 了解                             |
| CodeHub | Git的代码托管入门 | https://support.huaweicloud.com/qs-codehub/codehub_qs_1000.html | 了解                             |

**华为云CloudBuild**：编译构建（CloudBuild）基于云端大规模并发加速，为客户提供高速、低成本、配置简单的混合语言构建能力，帮助客户缩短构建时间，提升构建效率。[帮助文档](https://support.huaweicloud.com/productdesc-devcloud/devcloud_pdtd_00001.html)

| <div style="width:54px">云服务</div> | <div style="width:80px">功能模块文档</div> |           <div style="width:230px">文档地址 </div>           | <div style="width:96px">建议掌握程度<br>(了解-操作-精读)</div> |
| :----------------------------------: | :----------------------------------------: | :----------------------------------------------------------: | :----------------------------------------------------------: |
| CloudBuild | 创建编译任务  | https://support.huaweicloud.com/codeci_faq/codeci_faq_1045.html | 操作                             |
| CloudBuild | Maven构建     | https://support.huaweicloud.com/qs-codeci/codeci_qs_2001.html | 操作                             |
| CloudBuild | 镜像推送到SWR | https://support.huaweicloud.com/usermanual-codeci/codeci_ug_0013.html | 操作                             |

**华为云SWR**：容器镜像服务（SoftWare Repository for Container）是一种支持容器镜像全生命周期管理的服务， 提供简单易用、安全可靠的镜像管理功能，帮助用户快速部署容器化服务。[帮助文档](https://support.huaweicloud.com/cloudbuild/index.html)

| <div style="width:54px">云服务</div> | <div style="width:80px">功能模块文档</div> |           <div style="width:240px">文档地址 </div>           | <div style="width:100px">建议掌握程度<br>(了解-操作-精读)</div> |
| :----------------------------------: | :----------------------------------------: | :----------------------------------------------------------: | :----------------------------------------------------------: |
| SWR    | SWR创建镜像组织 | https://support.huaweicloud.com/usermanual-swr/swr_01_0014.html | 了解                             |
| SWR    | 镜像同步        | https://support.huaweicloud.com/usermanual-swr/swr_01_0101.html | 了解                             |
| SWR    | 镜像同步        | https://support.huaweicloud.com/swr_faq/swr_faq_0022.html    | 了解                             |

**华为云CCE**：云容器引擎（Cloud Container Engine，简称CCE）提供高度可扩展的、高性能的企业级Kubernetes集群，支持运行Docker容器。借助云容器引擎，您可以在云上轻松部署、管理和扩展容器化应用程序。[帮助文档](https://support.huaweicloud.com/productdesc-cce/cce_productdesc_0001.html)

| <div style="width:54px">云服务</div> | <div style="width:80px">功能模块文档</div> |           <div style="width:240px">文档地址 </div>           | <div style="width:100px">建议掌握程度<br>(了解-操作-精读)</div> |
| :----------------------------------: | :----------------------------------------: | :----------------------------------------------------------: | :----------------------------------------------------------: |
| CCE    | 创建CCE集群     | https://support.huaweicloud.com/usermanual-cce/cce_10_0028.html | 了解                             |
| CCE    | 创建CCE集群     | https://support.huaweicloud.com/usermanual-cce/cce_10_0363.html | 了解                             |
| CCE    | 负载创建        | https://support.huaweicloud.com/usermanual-cce/cce_10_0006.html | 操作                             |
| CCE    | 负载创建        | https://support.huaweicloud.com/usermanual-cce/cce_10_0047.html | 操作                             |
| CCE    | 创建service     | https://support.huaweicloud.com/usermanual-cce/cce_10_0249.html | 操作                             |
| CCE    | 创建service     | https://support.huaweicloud.com/usermanual-cce/cce_10_0011.html | 操作                             |
| CCE    | service-ELB创建 | https://support.huaweicloud.com/usermanual-cce/cce_10_0014.html | 精读                             |
| CCE    | 监控概述        | https://support.huaweicloud.com/usermanual-cce/cce_10_0182.html | 了解                             |

**华为云RDS**：云数据库RDS（Relational Database Service，简称RDS）是一种基于云计算平台的稳定可靠、弹性伸缩、便捷管理的在线云数据库服务。[帮助文档](https://support.huaweicloud.com/productdesc-rds/zh-cn_topic_dashboard.html)

| <div style="width:54px">云服务</div> | <div style="width:80px">功能模块文档</div> |           <div style="width:240px">文档地址 </div>           | <div style="width:100px">建议掌握程度<br>(了解-操作-精读)</div> |
| :----------------------------------: | :----------------------------------------: | :----------------------------------------------------------: | :----------------------------------------------------------: |
| RDS    | 购买RDS      | https://support.huaweicloud.com/qs-rds/rds_02_0008.html      | 了解                             |
| RDS    | 连接实例     | https://support.huaweicloud.com/usermanual-rds/rds_mysql_connect_02.html | 了解                             |
| RDS    | 创建数据库   | https://support.huaweicloud.com/usermanual-das/das_04_0042.html | 操作                             |

**华为云DCS**：分布式缓存服务（Distributed Cache Service，简称DCS）是华为云提供的一款内存数据库服务，兼容了Redis和Memcached两种内存数据库引擎，为您提供即开即用、安全可靠、弹性扩容、便捷管理的在线分布式缓存能力，满足用户高并发及数据快速访问的业务诉求。[帮助文档](https://support.huaweicloud.com/productdesc-dcs/dcs-pd-200713001.html)

| <div style="width:54px">云服务</div> | <div style="width:80px">功能模块文档</div> |           <div style="width:240px">文档地址 </div>           | <div style="width:100px">建议掌握程度<br>(了解-操作-精读)</div> |
| :----------------------------------: | :----------------------------------------: | :----------------------------------------------------------: | :----------------------------------------------------------: |
| DCS    | 购买DCS          | https://support.huaweicloud.com/usermanual-dcs/dcs-ug-0713002.html | 了解                             |
| DCS    | 修改库安全组规则 | https://support.huaweicloud.com/dcs_faq/dcs-faq-0713002.html | 精读                             |
| DCS    | 连接Redis        | https://support.huaweicloud.com/usermanual-dcs/dcs-ug-0713005.html | 操作                             |

**华为云OBS**： 对象存储服务（Object Storage Service，OBS）是一个基于对象的海量存储服务，为客户提供海量、安全、高可靠、低成本的数据存储能力，使用时无需考虑容量限制，并且提供多种存储类型供选择，满足客户各类业务场景诉求。[帮助文档](https://support.huaweicloud.com/obs/index.html)

| <div style="width:54px">云服务</div> | <div style="width:80px">功能模块文档</div> |           <div style="width:240px">文档地址 </div>           | <div style="width:100px">建议掌握程度<br>(了解-操作-精读)</div> |
| :----------------------------------: | :----------------------------------------: | :----------------------------------------------------------: | :----------------------------------------------------------: |
| OBS    | 创建桶        | https://support.huaweicloud.com/usermanual-obs/zh-cn_topic_0045829088.html | 了解                             |
| OBS    | Java SDK      | https://support.huaweicloud.com/sdk-java-devg-obs/obs_21_0001.html | 操作                             |
| OBS    | 快速入门-Java | https://support.huaweicloud.com/sdk-java-devg-obs/obs_21_0101.html | 操作                             |

**华为云ELB**：华为云弹性负载均衡（ Elastic Load Balance）将访问流量自动分发到多台云服务器，扩展应用系统对外的服务能力，实现更高水平的应用容错。[帮助文档](https://support.huaweicloud.com/elb/index.html)

| <div style="width:54px">云服务</div> | <div style="width:80px">功能模块文档</div> |           <div style="width:240px">文档地址 </div>           | <div style="width:100px">建议掌握程度<br>(了解-操作-精读)</div> |
| :----------------------------------: | :----------------------------------------: | :----------------------------------------------------------: | :----------------------------------------------------------: |
| ELB    | 创建负载     | https://support.huaweicloud.com/usermanual-elb/zh-cn_topic_0015479967.html | 操作                             |
| ELB    | 绑定公网IP   | https://support.huaweicloud.com/usermanual-elb/elb_ug_fz_0009.html | 操作                             |

**华为云DNS**：云解析服务（Domain Name Service）提供高可用，高扩展的权威DNS服务和DNS管理服务，帮助您将域名或应用资源转换成用于计算机连接的IP地址，从而将最终用户路由到相应的应用资源上。[帮助文档](https://support.huaweicloud.com/dns/index.html)

| <div style="width:54px">云服务</div> | <div style="width:80px">功能模块文档</div> |           <div style="width:240px">文档地址 </div>           | <div style="width:100px">建议掌握程度<br>(了解-操作-精读)</div> |
| :----------------------------------: | :----------------------------------------: | :----------------------------------------------------------: | :----------------------------------------------------------: |
| DNS    | 创建公网域名 | https://support.huaweicloud.com/usermanual-dns/zh-cn_topic_0035467702.html | 了解                             |
| DNS    | 添加域名解析 | https://support.huaweicloud.com/usermanual-dns/dns_usermanual_06012.html | 操作                             |

**华为云APM**：应用性能管理（Application Performance Management）是实时监控并管理企业应用性能和故障的云服务，帮助企业快速解决分布式架构下问题定位和性能瓶颈分析难题，改善用户体验。[帮助文档](https://support.huaweicloud.com/productdesc-apm2/apm_01_0001.html)

| <div style="width:54px">云服务</div> | <div style="width:80px">功能模块文档</div> |           <div style="width:240px">文档地址 </div>           | <div style="width:100px">建议掌握程度<br>(了解-操作-精读)</div> |
| :----------------------------------: | :----------------------------------------: | :----------------------------------------------------------: | :----------------------------------------------------------: |
| APM    | CCE接入APM     | https://support.huaweicloud.com/apm_faq/apm_03_0018.html     | 操作                             |
| APM    | 查看应用指标   | https://support.huaweicloud.com/usermanual-apm2/apm_07_0007.html | 了解                             |
| APM    | 查看应用调用链 | https://support.huaweicloud.com/usermanual-apm2/apm_07_0018.html | 了解                             |
| APM    | 查看应用拓扑   | https://support.huaweicloud.com/usermanual-apm2/apm_07_0019.html | 了解                             |

**华为云AOM**：应用运维管理（Application Operations Management）是云上应用的一站式立体化运维管理平台，实时监控应用及云资源，采集各项指标、日志及事件等数据分析应用健康状态，提供告警及数据可视化功能，帮助您及时发现故障，全面掌握应用、资源及业务的实时运行状况。[帮助文档](https://support.huaweicloud.com/aom/index.html)

| <div style="width:54px">云服务</div> | <div style="width:80px">功能模块文档</div> |           <div style="width:240px">文档地址 </div>           | <div style="width:100px">建议掌握程度<br>(了解-操作-精读)</div> |
| :----------------------------------: | :----------------------------------------: | :----------------------------------------------------------: | :----------------------------------------------------------: |
| AOM    | 开通AOM2.0       | https://support.huaweicloud.com/usermanual-aom2/agent_01_0013.html | 了解                             |
| AOM    | 查看容器标准日志 | https://support.huaweicloud.com/usermanual-aom2/mon_01_0045.html | 了解                             |
| AOM    | 查看容器性能指标 | https://support.huaweicloud.com/usermanual-aom2/mon_01_0025.html | 了解                             |

**华为云LTS**：云日志服务（Log Tank Service）提供一站式日志采集、秒级搜索、海量存储、结构化处理、转储和可视化图表等功能，满足应用运维、网络日志可视化分析、等保合规和运营分析等应用场景。[帮助文档](https://support.huaweicloud.com/lts/index.html)
| <div style="width:54px">云服务</div> | <div style="width:80px">功能模块文档</div> |           <div style="width:240px">文档地址 </div>           | <div style="width:100px">建议掌握程度<br>(了解-操作-精读)</div> |
| :----------------------------------: | :----------------------------------------: | :----------------------------------------------------------: | :----------------------------------------------------------: |
| LTS    | 创建容器主机组 | https://support.huaweicloud.com/usermanual-lts/lts_04_0511.html | 精读                             |
| LTS    | 应用日志查看   | https://support.huaweicloud.com/usermanual-lts/lts_05_0005.html | 了解                             |
### 2、云服务学习

**相关实验**

| <div style="width:120px">实验内容</div>          | <div style="width:300px">实验地址</div>                      | <div style="width:50px">建议</div> |
| -------------------------------------- | ------------------------------------------------------------ | ---- |
| 使用ServiceStage托管和治理天气预报应用 | https://education.huaweicloud.com/courses/course-v1:HuaweiX+CBUCNXPX003+Self-paced/about | 必做 |
| 数据库服务实践                         | https://lab.huaweicloud.com/testdetail_520                   | 熟悉 |
| 云容器快速搭建网站                     | https://lab.huaweicloud.com/testdetail_1931                  | 熟悉 |

**视频课程**

| <div style="width:120px">视频课程</div> | <div style="width:300px">课程地址</div>                          | <div style="width:50px">建议</div> |
| --------------------------- | ------------------------------------------------------------ | ---- |
| 持续开发与集成              | https://education.huaweicloud.com/courses/course-v1:HuaweiX+CBUCNXX110+Self-paced/about | 熟悉 |
| 持续部署与发布              | https://education.huaweicloud.com/courses/course-v1:HuaweiX+CBUCNXX113+Self-paced/about | 熟悉 |
| CSE介绍                     | https://education.huaweicloud.com/courses/course-v1:HuaweiX+CBUCNXP044+Self-paced/about | 学习 |
| 云解析服务：由浅入深        | https://education.huaweicloud.com/courses/course-v1:HuaweiX+CBUCNXPX003+Self-paced/about | 熟悉 |
| 云上电商应用如何高效运维    | https://education.huaweicloud.com/courses/course-v1:HuaweiX+CBUCNXP019+Self-paced/about | 熟悉 |
| 两小时玩转华为云日志服务LTS | https://education.huaweicloud.com/courses/course-v1:HuaweiX+CBUCNXCM001+Self-paced/about | 学习 |

## 四、环境准备
- 开发环境：

  |    环境Windows    |    版本2019     |
  | :---------------: | :-------------: |
  |        JDK        |       1.8       |
  |       Maven       |      3.6.3      |
  |    SpringBoot     | 2.1.16.RELEASE  |
  |    SpringCloud    |  Greenwich.SR6  |
  | SpringCloudHuawei | 1.6.1-Greenwich |
  |       IDEA        | 社区版-2022.1.3 |
  |      Windows      |      2019       |

- 华为云环境：

  注册云账号，并完成实名认证，具体服务可在具体用到时再创建，避免资源浪费。

## 五、开发流程
#### 1、OCP本地部署运行

> *如果直接推到远程仓库CodeHub上，再在代码仓里直接做修改也可以，那就不用在本地创建环境了，这样不能在本地进行调试，有利有弊。如果不想在本地部署运行，可以不安装MySQL、Redis，步骤3.中可以暂不修改数据库配置，并跳过4.5.两步

1. ##### 本地环境准备：

   - 安装数据库MySQL、Redis、Git
   - 参考文档：[MySQL8.x安装](https://blog.csdn.net/An0217313/article/details/119245960)、[Redis安装](https://blog.csdn.net/weixin_61594803/article/details/122695446)、[Git安装](https://blog.csdn.net/qq_17685725/article/details/122994058)

2. ##### 克隆开源项目[open-capacity-platform](https://gitee.com/dromara/open-capacity-platform/tree/2.0.1/)到本地，拉取的是2.0.1分支下的代码，可参考以下步骤：

   - 想要保存项目的文件夹下，邮件打开Git Bash Here，输入克隆指令：

     ```tex
     git clone -b 2.0.1 https://gitee.com/dromara/open-capacity-platform.git
     ```

3. ##### 本地部署运行验证项目可行性

   - IDEA导入解压好的项目，执行mvn clean清理一下目标缓存；

   - 模块处理：

     （1）本次改造因为使用云上的运维管理+日志服务，删除监控中心monitor-center；

     （2）有两个网关：api-gateway使用的是zuul网关、new-api-gateway使用的是gateway网关，本次改造选用的是new-api-gateway；

     （3）其中tuning-center属于调测中心，是对单独模块接口进行调测使用的， 也删掉；

     （4）要删掉的模块在总的open-capacity-platform的pom.xml文件中，注释掉对应的<module>，并删除对应模块的文件包，避免后面使用DevCloud进行编译构建时在这些服务上报错。

     ![image-20221118091554410](https://gitee.com/HuaweiCloudDeveloper/open-capacity-platform-for-huaweicloud/raw/master-dev/images/image-20221118091554410.png)

   - 找到sql文件夹，利用文件夹中的sql文件初始化数据库；

   - 删除没有用到的模块后，修改项目中微服务模块中的resources中的配置文件，修改mysql和redis配置参数，本地部署的话，需要修改redis地址，端口没改动的话还是6379；MySQL需修改url地址，端口没改动的话还是3306，修改用户名和密码。

     （**如果mysql安装是8.x版本，需要修改url，添加参数配置时区，如serverTimezone=UTC，也可以设置成其他时区**）

4. ##### 本地运行测试：

   先启动Eureka注册中心register-center/eureka-center，该服务多环境配置指定的是的application-slave0.yml配置，所以默认端口是1111，随后启动最小启动（eureka-center、new-api-gateway、auth-server、user-center、back-center）的其他微服务，浏览器访问本地1111端口，查看服务是否注册成功。

5. ##### 服务使用测试：

   访问前端服务，8066端口，使用管理员账号admin登录，密码admin。如果使用其他账号登录，需要将sys_user表中其他用户对应的enabled字段修改为1，可以将admin的密码复制给其他用户，其他用户可以先用密码admin登录，登录后再在系统中修改。
   
   ![image-20221118103231396](https://gitee.com/HuaweiCloudDeveloper/open-capacity-platform-for-huaweicloud/raw/master-dev/images/image-20221118103231396.png)

#### 2、OCP代码改造

> 使用云上微服务引擎CSE替换原有的注册中心

1. ##### 为华为云账号新增访问密钥AK/AK，记得保存.csv文件，[操作指导](https://support.huaweicloud.com/usermanual-ca/ca_01_0003.html)。

2. ##### 拿到CSE注册中心和配置中心地址：

   - 登录华为云账号，进入控制台；

   - 左侧服务列表中搜索服务CSE，点击进入，弹出授权，点击确认，IAM子账号可能会授权失败，但不影响，切到旧版界面，旧版提供了一个专业版CSE可以免费使用。

     ***注意：**本次改造所用的华为云服务需要都在同一区域下，这里是在广州，也可以选择其他区域，所有服务保持一致即可。

     ![image-20221118111038179](https://gitee.com/HuaweiCloudDeveloper/open-capacity-platform-for-huaweicloud/raw/master-dev/images/image-20221118111038179.png)

   - 在旧版界面拿到注册中心和配置中心地址

     ![image-20221118111250735](https://gitee.com/HuaweiCloudDeveloper/open-capacity-platform-for-huaweicloud/raw/master-dev/images/image-20221118111250735.png)

3. ##### 替换依赖Eureka-->CSE：

   - 修改服务中的pom文件，将Eureka依赖换成spring-cloud-huawei的依赖，版本需要对照，修改如下：

     ```xml
     <!--选用eureka时打开-->
     <!--< dependency >
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
     </dependency >-->
     <dependency>
         <groupId>com.huaweicloud</groupId>
         <artifactId>spring-cloud-starter-huawei-service-engine-gateway</artifactId>
         <version>1.6.1-Greenwich</version>
         <!--项目中已有引入swagger依赖，这里剔除防止依赖冲突-->
         <exclusions>
             <exclusion>
                 <groupId>com.huaweicloud</groupId>
                 <artifactId>spring-cloud-starter-huawei-swagger</artifactId>
             </exclusion>
         </exclusions>
     </dependency>
     ```

   - **支持版本对照表：**

     | Branch    | Spring Cloud Huawei Latest Version | Compiled Spring Cloud Version | Compiled Spring Boot Version |
     | --------- | ---------------------------------- | ----------------------------- | ---------------------------- |
     | master    | 1.10.5-2021.0.x                    | 2021.0.3                      | 2.6.7                        |
     | 2020.0.x  | 1.10.5-2020.0.x                    | 2020.0.5                      | 2.5.12                       |
     | Hoxton    | 1.9.3-Hoxton                       | Hoxton.SR9                    | 2.3.5.RELEASE                |
     | Greenwich | 1.6.1-Greenwich                    | Greenwich.SR6                 | 2.1.6-RELEASE                |
     | Finchley  | 1.6.1-Finchley                     | 2.0.4.RELEASE                 | 2.0.9.RELEASE                |
     | Edgware   | 1.2.0-Edgware                      | 1.3.6.RELEASE                 | 1.5.22.RELEASE               |

4. ##### 修改配置文件bootstrap，配置CSE：

   - 对于涉及注册中心的服务，修改resources目录下的bootstrap或application配置文件，删除Euraka配置，新增CSE配置，CSE配置如下：

     ```yaml
     spring:
         cloud:
             servicecomb:
                 discovery:
                     enabled: true
                     watch: false
                     address: https://xxxxxxx        #替换成第2步拿到的CSE服务注册中心地址
                     appName: OCP-application		#应用名，可自定义，所有服务要保持统一
                     serviceName: ${spring.application.name}
                     version: 0.0.1
                     healthCheckInterval: 30
     
                 config:
                     serverAddr: https://xxxxxxx     #替换成第2步拿到的CSE配置中心地址
                     serverType: config-center
                 credentials:
                     enabled: true
                     accessKey: 你的AK					#替换成第1步拿到的Ak值
                     secretKey: 你的SK					#替换成第1步拿到的Sk值
                     akskCustomCipher: default
                     project: cn-south-1				  #替换成你的CSE服务所在区域，由第1步确定
     ```

     **[*区域和终端节点查询](https://developer.huaweicloud.com/endpoint)**

     ![image-20221118115327027](https://gitee.com/HuaweiCloudDeveloper/open-capacity-platform-for-huaweicloud/raw/master-dev/images/image-20221118115327027.png)

5. ##### CSE接入效果：

   - 如果使用的是CSE旧版操作界面提供的免费的CSE地址，那么可以在本地（走前面本地部署运行流程的话）直接进行服务启动注册，启动后可在CSE控制台查看是否注册成功；

     ![image-20221122151153862](https://gitee.com/HuaweiCloudDeveloper/open-capacity-platform-for-huaweicloud/raw/master-dev/images/image-20221122151153862.png)

   - 如果使用的不是免费的专业版，而是自己购买的CSE，那么需要给CSE绑定公网IP，不建议这么做，因为微服务引擎无法认证鉴权能力，开放到公网面临安全风险，会增加系统的脆弱性。此种情况下如果想要本地调试，可以开通一个[ECS弹性云服务器](https://support.huaweicloud.com/basics-ecs/ecs_01_0102.html)，并需要保证该云服务器和你的CSE在同一个[虚拟私有云VPC](https://support.huaweicloud.com/qs-vpc/zh-cn_topic_0017816228.html)。
   
6. ##### **file-center微服务改造**：

      > 操作流程：
      >
      > 购买OBS--->添加Maven依赖--->配置文件配置OBS参数--->创建OBS客户端bean--->
      >
      > 编写OBS对象操作业务层方法--->文件操作类型枚举中添加华为云--->修改Controller中接口方法中默认的枚举类型为华为云

      - **file-center服务改造——对象存储使用华为云OBS：**

        1. 购买OBS：控制台搜OBS，进入OBS首页界面点击右上购买即可，参数可参考以下配置。
      
           ![image-20221129152536135](https://gitee.com/HuaweiCloudDeveloper/open-capacity-platform-for-huaweicloud/raw/master-dev/images/image-20221129152536135.png)
      
        2. 添加依赖：在file-center服务的pom文件中，集成华为OBS的SDK。
      
           ```xml
           <dependency>
              <groupId>com.huaweicloud</groupId>
              <artifactId>esdk-obs-java-bundle</artifactId>
              <version>3.21.11</version>
           </dependency>
           ```

        3. 修改配置文件：在file-center服务的application.yml文件中增加OBS配置，可参考以下配置。

           - [获取AK/SK](https://support.huaweicloud.com/usermanual-ca/ca_01_0003.html)；

           - [获取obs终端节点endPoint](https://developer.huaweicloud.com/endpoint?OBS)；
      
             ![image-20221122093907117](https://gitee.com/HuaweiCloudDeveloper/open-capacity-platform-for-huaweicloud/raw/master-dev/images/image-20221122093907117.png)
      
           - 具体配置
      
             ```yaml
             obs:
               endPoint: //服务所在区域对应的终端节点
               ak: //你的ak
               sk: //你的sk
               bucketName: //桶名称
               domain: //https://访问域名    访问域名可在OBS桶的“概览”中找到，其值实际就是：桶名称.终端节点
        
        4. 创建OBS配置类
        
           - 按照file-center服务中原有的代码逻辑，为OBS客户端类创建配置类，配置Bean。在config目录下创建HuaweiOSSConfig配置类，配置类中代码如下：
        
             ```java
             package com.open.capacity.oss.config;
             import com.obs.services.ObsClient;
             import org.springframework.beans.factory.annotation.Value;
             import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
             import org.springframework.context.annotation.Bean;
             import org.springframework.context.annotation.Configuration;
             /**
              * 华为云配置
              */
             @Configuration
             public class HuaweiOSSConfig {
             
                 @Value("${obs.endpoint}")
                 private String endpoint;
                 @Value("${obs.ak}")
                 private String accessKeyId;
                 @Value("${obs.sk}")
                 private String accessKeySecret;
             
                 /**
                  * 华为云文件存储client
                  */
                 @Bean
                 @ConditionalOnProperty(name = "obs.ak", matchIfMissing = true)
                 public ObsClient ossClient() {
             		ObsClient obsClient = new ObsClient(accessKeyId, accessKeySecret, endpoint);
                     return obsClient;
                 }
             }
             ```
        
        5. 创建华为与OBS服务层实现类
        
           - 创建文件服务FileService接口的华为云OBS实现类，如HuaweiOssServiceImpl，具体代码如下：
        
             ```java
             package com.open.capacity.oss.service.impl;
             import com.obs.services.ObsClient;
             import com.open.capacity.oss.dao.FileDao;
             import com.open.capacity.oss.model.FileInfo;
             import com.open.capacity.oss.model.FileType;
             import lombok.extern.slf4j.Slf4j;
             import org.springframework.beans.factory.annotation.Autowired;
             import org.springframework.beans.factory.annotation.Value;
             import org.springframework.stereotype.Service;
             import org.springframework.web.multipart.MultipartFile;
             /**
              * 华为云obs存储文件
             */
             @Service("huaweiOssServiceImpl")
             @Slf4j
             public class HuaweiOssServiceImpl extends AbstractFileService {
             
                 @Autowired
                 private FileDao fileDao;
             
                 @Override
                 protected FileDao getFileDao() {
                     return fileDao;
                 }
             
                 @Override
                 protected FileType fileType() {
                     return FileType.HUAWEI;
                 }
             
                 @Autowired
                 private ObsClient obsClient;
             
                 @Value("${obs.bucketName}")
                 private String bucketName;
                 @Value("${obs.domain}")
                 private String domain;
             
                 @Override
                 protected void uploadFile(MultipartFile file, FileInfo fileInfo) throws Exception {
                     obsClient.putObject(bucketName, fileInfo.getName(), file.getInputStream());
                     fileInfo.setUrl(domain + "/" + fileInfo.getName());
                 }
             
                 @Override
                 protected boolean deleteFile(FileInfo fileInfo) {
                     obsClient.deleteObject(bucketName, fileInfo.getName());
                     return true;
                 }
                 /**
                  * 上传大文件，分片上传 每片一个临时文件，待实现
                  */
                 @Override
                 protected void chunkFile(String guid, Integer chunk, MultipartFile file, Integer chunks, String filePath) throws Exception {
             
                 }
                 /**
                  * 合并分片文件，每一个小片合并一个完整文件，待实现
                  */
                 @Override
                 protected FileInfo mergeFile(String guid, String fileName, String filePath) throws Exception {
                     return null;
                 }
             }
             ```
        
        6. 添加枚举、修改OssServiceFactory、修改FileController
        
           - model目录下的枚举FileType中添加华为枚举，HUAWEI
        
           - config目录下的OssServiceFactory类中添加华为云的FileService实现类，添加以下代码：
        
             ```java
             //类中添加属性：
             @Autowored
             private FileService huaweiOssServiceImpl;
             
             //init方法中添加：
             map.put(FileType.HUAWEI,huaweiOssServiceImpl);
             ```
        
           - controller目录下的FileController中，将所有的FileType.XXX枚举都改为FileType.HUAWEI
        
        7. file-center服务中其他云服务厂商的对象存储可以不删，但要想有效使用，需要去其官网开通服务，修改配置。如要删除，可按照以上引入华为云OBS的流程去删除对应内容即可。
        

7. ##### APM链路ID集成到日志：

      > 将APM中的每次请求对应的链路ID打印到OCP日志中，并通过LTS根据链路ID过滤日志，查询具体链路ID相关的日志。

      **代码改造**：创建MdcKey类记录映射关系，并修改内部工具inner-intergration中日志模块log-spring-boot-starter的logback配置文件，即resources下的logback-spring.xml，将链路ID添加到日志格式中。

      - MdcKey类

      ```java
      public class MdcKeyTest {
          public final static String TRACE_ID_KEY = "apm-traceid"; //只有被采样的数据才有apm-traceId
          public final static String GTRACE_ID_KEY = "apm-gtraceid"; //所有包括未采样的调用链都有apm-gtraceId
          public final static String SPAN_ID_KEY = "apm-spanid";
      }
      ```

      - logback-spring.xml文件修改日志格式，添加APM的链路ID：traceID：*%X{apm-traceid}*
        

      ```xml
      <property name="CONSOLE_LOG_PATTERN" value="[${APP_NAME}:${ServerIP}:${ServerPort}] [ traceId: %X{apm-traceid} ] %clr(%d{yyyy-MM-dd HH:mm:ss.SSS}){faint} %clr(%level){blue} %clr(${PID}){magenta} %clr([%thread]){orange} %clr(%logger){cyan} %m%n${LOG_EXCEPTION_CONVERSION_WORD:-%wEx}" />
      <property name="CONSOLE_LOG_PATTERN_NO_COLOR" value="[${APP_NAME}:${ServerIP}:${ServerPort}] [ traceId: %X{apm-traceid} ] %d{yyyy-MM-dd HH:mm:ss.SSS} %level ${PID} [%thread] %logger %m%n${LOG_EXCEPTION_CONVERSION_WORD:-%wEx}" />
      ```

#### 3、代码托管

> 代码上传CodeHub，进行代码托管。代码上云后，可在云端仓库直接修改，也可以先在本地开发调测，成功后再同步到云端仓库

1. ##### 在[CodeHub创建仓库](https://hn.devcloud.huaweicloud.com/codehub/home)。

   - 通过[codehub链接]()进入DevCloud(集成了CodeHub)，点击设置SSH密钥，[操作指导](https://support.huaweicloud.com/usermanual-codehub/codehub_ug_8002.html)；

   - 创建项目&仓库，项目名和仓库名自定义即可；

     ![image-20221118155050922](https://gitee.com/HuaweiCloudDeveloper/open-capacity-platform-for-huaweicloud/raw/master-dev/images/image-20221118155050922.png)

     ![image-20221118160024598](https://gitee.com/HuaweiCloudDeveloper/open-capacity-platform-for-huaweicloud/raw/master-dev/images/image-20221118160024598.png)

   

2. ##### 代码上云：

   - 克隆新建仓库到本地，拿到.git文件夹，并将该文件夹复制到ocp项目父目录下，并在此目录右键打开Git Bash Here

     ![image-20221118160750316](https://gitee.com/HuaweiCloudDeveloper/open-capacity-platform-for-huaweicloud/raw/master-dev/images/image-20221118160750316.png)

   - 使用git将ocp代码推送到CodeHub远程仓库

     ![image-20221118161348912](https://gitee.com/HuaweiCloudDeveloper/open-capacity-platform-for-huaweicloud/raw/master-dev/images/image-20221118161348912.png)



#### 4、数据库配置

> RDS+DCS替换原项目中的Mysql和Redis

1. ##### 购买RDS:

   - 华为云控制台左侧服务列表搜索RDS，进入RDS首页，点击购买，配置可参考[购买RDS fro MySQL](https://support.huaweicloud.com/qs-rds/rds_02_0100.html)（只看步骤1即可）

     ![image-20221118163633815](https://gitee.com/HuaweiCloudDeveloper/open-capacity-platform-for-huaweicloud/raw/master-dev/images/image-20221118163633815.png)

   - 使用云上DAS服务连接登录数据库

     ![image-20221118163852907](https://gitee.com/HuaweiCloudDeveloper/open-capacity-platform-for-huaweicloud/raw/master-dev/images/image-20221118163852907.png)

   - 查看RDS的连接信息

     ![image-20221118174805998](https://gitee.com/HuaweiCloudDeveloper/open-capacity-platform-for-huaweicloud/raw/master-dev/images/image-20221118174805998.png)

   - 通过DAS导入项目目录下的sql文件，初始化RDS数据库

     ![image-20221118173307269](https://gitee.com/HuaweiCloudDeveloper/open-capacity-platform-for-huaweicloud/raw/master-dev/images/image-20221118173307269.png)

     

2. ##### 购买DCS:

   - 华为云控制台左侧服务列表搜索DCS，进入DCS首页，点击购买，配置可参考[购买Redis实例](https://support.huaweicloud.com/usermanual-dcs/dcs-ug-0713002.html)

   - ![image-20230223132502016](https://gitee.com/HuaweiCloudDeveloper/open-capacity-platform-for-huaweicloud/raw/master-dev/images/image-20230223132502016.png)

     ![image-20221118173821875](https://gitee.com/HuaweiCloudDeveloper/open-capacity-platform-for-huaweicloud/raw/master-dev/images/image-20221118173821875.png)

   - 获取redis连接信息

     ![image-20221118174823599](https://gitee.com/HuaweiCloudDeveloper/open-capacity-platform-for-huaweicloud/raw/master-dev/images/image-20221118174823599.png)

3. ##### 修改数据库配置，修改CodeHub代码仓中微服务的MySQL和Redis配置。也可以在本地修改，再推到CodeHub远程仓库，需要注意以下几点：

   - MySQL和Redis配置端口，要根据再购买RDS和DCS配置的端口对照，不一定是3306和6379；
   - 如果RDS购买的是8.0版本的MySQL，再数据库配置中，url中要配置时区参数，如serverTimezone=UTC；



#### 6、CloudBuild编译构建

1. ##### 登录华为云，进入控制台，搜索进入DevCloud首页，点击左侧菜单栏中的“代码托管”，选择自己的仓库。

   ![image-20221121101139487](https://gitee.com/HuaweiCloudDeveloper/open-capacity-platform-for-huaweicloud/raw/master-dev/images/image-20221121101139487.png)

2. ##### 点击“设置构建”进入新建构建任务：代码源—>构建模板

   ![image-20221121102117980](https://gitee.com/HuaweiCloudDeveloper/open-capacity-platform-for-huaweicloud/raw/master-dev/images/image-20221121102117980.png)

   ![image-20221121102128685](https://gitee.com/HuaweiCloudDeveloper/open-capacity-platform-for-huaweicloud/raw/master-dev/images/image-20221121102128685.png)

3. ##### 构建配置+制作镜像并推送到SWR配置

   ![image-20221121113406950](https://gitee.com/HuaweiCloudDeveloper/open-capacity-platform-for-huaweicloud/raw/master-dev/images/image-20221121113406950-16690016506981.png)

   ![image-20221121113416593](https://gitee.com/HuaweiCloudDeveloper/open-capacity-platform-for-huaweicloud/raw/master-dev/images/image-20221121113416593.png)

4. ##### 执行编译构建任务，并查看结果

   ##### ![image-20221121114513285](https://gitee.com/HuaweiCloudDeveloper/open-capacity-platform-for-huaweicloud/raw/master-dev/images/image-20221121114513285.png)

5. ##### 注意：auth-sso、generator-center没有Dockerfile文件，需要自己的添加，如auth-sso中：

   ![image-20221121165753435](https://gitee.com/HuaweiCloudDeveloper/open-capacity-platform-for-huaweicloud/raw/master-dev/images/image-20221121165753435.png)

6. *编译构建和打包镜像报错请仔细阅读报错信息后参考该文档中[**七、问题记录**](# 七、问题记录)

5. **需要制作镜像的服务有：**file-center、generator-center、new-api-gateway、job-admin、auth-server、auth-sso、back-senter、user-center。**其中前端服务back-center可以先不制作镜像，因为只用配置的路由地址需要路由服务在CCE中部署之后才能确定；job-admin也先不制作镜像，后面要改造成华为云OBS。**

#### 7、应用部署

> 使用云容器引擎CCE对项目进行容器化部署+管理

1. ##### 购买CCE集群

   - 控制台搜CCE，进入CCE控制首页，点击购买CCE集群

     ![image-20221121173324211](https://gitee.com/HuaweiCloudDeveloper/open-capacity-platform-for-huaweicloud/raw/master-dev/images/image-20221121173324211.png)

   - 集群配置，可**参考**以下配置参数，区域跟之前服务保持一致。

     ![image-20221121173409360](https://gitee.com/HuaweiCloudDeveloper/open-capacity-platform-for-huaweicloud/raw/master-dev/images/image-20221121173409360.png)

   - 插件配置、规格确认

     ![image-20221121173649985](https://gitee.com/HuaweiCloudDeveloper/open-capacity-platform-for-huaweicloud/raw/master-dev/images/image-20221121173649985.png)

   - 集群创建成功后，回到集群管理，点击进如具体的集群中，点击左侧菜单中节点管理，点击界面右上创建节点

     ![image-20221121174936832](https://gitee.com/HuaweiCloudDeveloper/open-capacity-platform-for-huaweicloud/raw/master-dev/images/image-20221121174936832.png)

   - 配置节点，可参考以下配置

     ![image-20221121175012624](https://gitee.com/HuaweiCloudDeveloper/open-capacity-platform-for-huaweicloud/raw/master-dev/images/image-20221121175012624.png)

     ![image-20221121175039370](https://gitee.com/HuaweiCloudDeveloper/open-capacity-platform-for-huaweicloud/raw/master-dev/images/image-20221121175039370.png)

2. ##### 创建负载并关联APM

   - 点击所建集群进入集群详细操作界面，如下，创建**无状态负载**

     ![image-20221121181212632](https://gitee.com/HuaweiCloudDeveloper/open-capacity-platform-for-huaweicloud/raw/master-dev/images/image-20221121181212632.png)

     

   - 创建负载配置，包含**接入APM**

     ![image-20221121181249965](https://gitee.com/HuaweiCloudDeveloper/open-capacity-platform-for-huaweicloud/raw/master-dev/images/image-20221121181249965.png)

     ![image-20221121181945006](https://gitee.com/HuaweiCloudDeveloper/open-capacity-platform-for-huaweicloud/raw/master-dev/images/image-20221121181945006.png)

   - 接入APM验证，控制台搜索APM，使用新版控制台，点击应用监控—>应用列表

     ![image-20221121182042580](https://gitee.com/HuaweiCloudDeveloper/open-capacity-platform-for-huaweicloud/raw/master-dev/images/image-20221121182042580.png)

   - **根据以上创建负载流程，为以下服务创建负载：**file-center、generator-center、new-api-gateway、job-admin、auth-server、auth-sso、back-senter、user-center、workflow-center。

   - 负载创建完成后，可在CSE控制台查看需要注册的服务是否注册成功。

3. ##### 创建service-ELB访问方式暴露服务

   - 注册中CSE的服务可以通过路由跳转，不需要配置服务发现。**需要配置服务发现的微服务有：new-api-gateway、back-center、job-admin。**

   - 创建负载均衡ELB，控制台搜索ELB，点击右上购买，购买完成后可查看该ELB绑定的公网IP。

     ![image-20221121191258981](https://gitee.com/HuaweiCloudDeveloper/open-capacity-platform-for-huaweicloud/raw/master-dev/images/image-20221121191258981.png)

     ![image-20221121190859744](https://gitee.com/HuaweiCloudDeveloper/open-capacity-platform-for-huaweicloud/raw/master-dev/images/image-20221121190859744.png)

   - **这里可以先跳到8、DNS实现通过域名访问应用**

4. ##### 为new-api-gateway服务创建负载均衡访问。

   - 点击进入具体负载详情界面，点击访问方式，创建serviceELB服务

     ![image-20221121192250042](https://gitee.com/HuaweiCloudDeveloper/open-capacity-platform-for-huaweicloud/raw/master-dev/images/image-20221121192250042.png)

   - 负载均衡的服务发现创建完成后，就可以通过之前ELB绑定的公网IP+9200 在公网环境下访问到new-api-gateway服务。

5. ##### 为back-center服务创建负载均衡访问。

   - back-center镜像重新制作：前端服务back-center中有配置路由的地址，前端服务后面可定是要通过外网访问，所以这里前端服务中的路由地址也要修改为公网地址。可以用上面为new-api-gateway创建服务发现后的外网访问方式；配置位置如下：

     ![image-20221121195644474](https://gitee.com/HuaweiCloudDeveloper/open-capacity-platform-for-huaweicloud/raw/master-dev/images/image-20221121195644474.png)

   - 修改后重新制作back-center镜像，步骤参考**5、CloudBuild进行编译构建，构建docker镜像并推送到SWR**

   - 为back-center创建负载均衡类型的服务发现，步骤同为new-api-gateway创建服务发现一样，端口与服务配置文件端口保持一致：8066。

6. ##### 为job-admin服务创建负载均衡访问。
   
   > 为了支撑运营相关工作,降低业务操作风险,提升生产操作质量,急需建设统一的调度系统，job-admin为调度系统中的执行器，负责接收调度请求并执行任务逻辑，接收“调度中心”的执行请求、终止请求和日志请求等。
   
   - 为job-admin创建负载均衡类型的服务发现，步骤同为new-api-gateway创建服务发现一样，端口与服务配置文件端口保持一致：8888。
   
   - 修改数据库中job-admin的三个接口对应的访问路径：在user-center库中的sys_memu表。
   
     ![image-20221121201002614](https://gitee.com/HuaweiCloudDeveloper/open-capacity-platform-for-huaweicloud/raw/master-dev/images/image-20221121201002614.png)
   
7. 至此，基于CCE+CSE服务部署基本完成。



#### 8、接入LTS云日志

1. ##### 控制台进入LTS首页界面，创建日志组和主机组，使用其自动创建。

   ![image-20221122110146159](https://gitee.com/HuaweiCloudDeveloper/open-capacity-platform-for-huaweicloud/raw/master-dev/images/image-20221122110146159.png)

2. ##### CCE日志接入，LTS首页选择日志接入，点击云容器引擎CCE-应用日志

   ![image-20221122110819347](https://gitee.com/HuaweiCloudDeveloper/open-capacity-platform-for-huaweicloud/raw/master-dev/images/image-20221122110819347.png)

3. ##### 接入日志配置

   - 选择日志流

     ![image-20221122111014921](https://gitee.com/HuaweiCloudDeveloper/open-capacity-platform-for-huaweicloud/raw/master-dev/images/image-20221122111014921.png)

   - 安装日志采集组件：确认安装完毕

   - 选择主机组

     ![image-20221122111259560](https://gitee.com/HuaweiCloudDeveloper/open-capacity-platform-for-huaweicloud/raw/master-dev/images/image-20221122111259560.png)

   - 采集配置

     ![image-20221122111636405](https://gitee.com/HuaweiCloudDeveloper/open-capacity-platform-for-huaweicloud/raw/master-dev/images/image-20221122111636405.png)

   - 接入完成，查看日志流中收集到的日志

     ![image-20221122112306566](https://gitee.com/HuaweiCloudDeveloper/open-capacity-platform-for-huaweicloud/raw/master-dev/images/image-20221122112306566.png)

4. ##### CCE接入LTS完成。

#### 9、配置DNS

1. **域名注册**：[注册地址](https://www.huaweicloud.com/product/domain/search.html?domainName=&domainSuffix=.com&env=)

   ![image-20230223141513823](https://gitee.com/HuaweiCloudDeveloper/open-capacity-platform-for-huaweicloud/raw/master-dev/images/image-20230223141513823.png)

1. ##### 创建公网域名

   ![image-20230223141638440](https://gitee.com/HuaweiCloudDeveloper/open-capacity-platform-for-huaweicloud/raw/master-dev/images/image-20230223141638440.png)

2. ##### 点击刚刚创建的公网域名，进入域名详情页，点击右上“添加记录集”

   ![image-20221122115017727](https://gitee.com/HuaweiCloudDeveloper/open-capacity-platform-for-huaweicloud/raw/master-dev/images/image-20221122115017727.png)

3. ##### 再次点公网域名，查看解析记录，找到对应域名，便可使用该域名去访问OCP系统。

   ![image-20221122141240343](https://gitee.com/HuaweiCloudDeveloper/open-capacity-platform-for-huaweicloud/raw/master-dev/images/image-20221122141240343.png)

#### 10、OCP业务访问

1. ##### 完成以上步骤，OCP开源项目的云原生改造就算是完成了，可以使用去访问前端登录页面，使用OCP业务功能，账号:admin，密码:admin。

   <img src="https://gitee.com/HuaweiCloudDeveloper/open-capacity-platform-for-huaweicloud/raw/master-dev/images/image-20221122141827735.png" alt="image-20221122141827735" style="zoom:150%;" />
   
   
   
2. ##### 运维能力验证

   - CCE集群中的节点默认安装了ICAgent，且默认集成了AOM，可以直接使用，[操作指导](https://support.huaweicloud.com/usermanual-cce/cce_10_0018.html)。
   - CCE接入LTS后，可进行日志搜索、加工、分析、告警等一系列操作，[操作指导](https://support.huaweicloud.com/usermanual-lts/lts_04_0001.html)。
   - CCE接入AOP后，可进查看服务调用链、应用拓扑、事务监控等操作，[操作指导](https://support.huaweicloud.com/usermanual-apm2/apm_07_0001.html)。

## 六、实现效果
### 1、效果

1. 服务注册到CSE，为用户提供注册发现、服务治理、配置管理等高性能和高韧性的企业级云服务能力；CSE可无缝兼容Spring Cloud、ServiceComb等开源生态；用户也可结合其他云服务，快速构建云原生微服务体系，实现微服务应用的快速开发和高可用运维。

   ![image-20221122151625951](https://gitee.com/HuaweiCloudDeveloper/open-capacity-platform-for-huaweicloud/raw/master-dev/images/image-20221122151625951.png)

2. CCE中负载运行正常，CCE基于业界主流的Docker和Kubernetes开源技术构建的容器服务，提供众多契合企业大规模容器集群场景的功能，在系统可靠性、高性能、开源社区兼容性等多个方面具有独特的优势，满足企业在构建容器云方面的各种需求。

   ![image-20221122162351587](https://gitee.com/HuaweiCloudDeveloper/open-capacity-platform-for-huaweicloud/raw/master-dev/images/image-20221122162351587.png)

3. OCP应用基础功能使用正常

   - 使用公网域名访问8066端口，登录admin：admin

     ![image-20221122164954349](https://gitee.com/HuaweiCloudDeveloper/open-capacity-platform-for-huaweicloud/raw/master-dev/images/image-20221122164954349.png)

4. LTS中使用APM链路ID查询次链路链路ID相关的日志。APM提供实时监控并管理企业应用性能和故障的云服务，帮助企业快速解决分布式架构下问题定位和性能瓶颈分析难题，改善用户体验。LTS提供一站式日志采集、秒级搜索、海量存储、结构化处理、转储和可视化图表等功能，满足应用运维、网络日志可视化分析、等保合规和运营分析等应用场景。

   - ![image-20230219132618262](https://gitee.com/HuaweiCloudDeveloper/open-capacity-platform-for-huaweicloud/raw/master-dev/images/image-20230219132618262.png)
   - ![image-20230219132945654](https://gitee.com/HuaweiCloudDeveloper/open-capacity-platform-for-huaweicloud/raw/master-dev/images/image-20230219132945654.png)

5. APM告警使用：告警是指接入APM Agent的服务在触发告警条件时上报的信息，以便您在第一时间获取到服务的异常状况，进而迅速处理故障，避免造成业务损失。

   - 使用SMN（消息通知服务）[创建主题](https://support.huaweicloud.com/usermanual-smn/zh-cn_topic_0043961401.html)并[添加订阅](https://support.huaweicloud.com/usermanual-smn/zh-cn_topic_0043961402.html)。

     ![image-20230219134439935](https://gitee.com/HuaweiCloudDeveloper/open-capacity-platform-for-huaweicloud/raw/master-dev/images/image-20230219134439935.png)

     ![image-20230219135057411](https://gitee.com/HuaweiCloudDeveloper/open-capacity-platform-for-huaweicloud/raw/master-dev/images/image-20230219135057411.png)

   - APM配置告警，使用推荐模板举例

     1. 配置告警模板，并绑定节点

        ![image-20230219135605271](https://gitee.com/HuaweiCloudDeveloper/open-capacity-platform-for-huaweicloud/raw/master-dev/images/image-20230219135605271.png)

        ![image-20230219140420704](https://gitee.com/HuaweiCloudDeveloper/open-capacity-platform-for-huaweicloud/raw/master-dev/images/image-20230219140420704.png)

        ![image-20230219140442053](https://gitee.com/HuaweiCloudDeveloper/open-capacity-platform-for-huaweicloud/raw/master-dev/images/image-20230219140442053.png)

        ![image-20230219140451040](https://gitee.com/HuaweiCloudDeveloper/open-capacity-platform-for-huaweicloud/raw/master-dev/images/image-20230219140451040.png)

        ![image-20230219140500568](https://gitee.com/HuaweiCloudDeveloper/open-capacity-platform-for-huaweicloud/raw/master-dev/images/image-20230219140500568.png)

        ![image-20230219140527966](https://gitee.com/HuaweiCloudDeveloper/open-capacity-platform-for-huaweicloud/raw/master-dev/images/image-20230219140527966.png)

     2. 触发告警测试。CCE中将auth-server负载的实例个数调为0，即停掉auth-server服务，在使用OCP，收到告警邮件/短信。

        ![image-20230219141430296](https://gitee.com/HuaweiCloudDeveloper/open-capacity-platform-for-huaweicloud/raw/master-dev/images/image-20230219141430296.png)

        ![image-20230219141713396](https://gitee.com/HuaweiCloudDeveloper/open-capacity-platform-for-huaweicloud/raw/master-dev/images/image-20230219141713396.png)



### 2、优化方案

#### * 使用环境变量灵活配置

> 由于之前的一些配置都写死在了代码中，如mysql的配置，如后期mysql服务器地址出现变动，还要修改源码后重新编译构建、制作镜像；这里CCE提供的环境变量来配置mysql参数等，后期对应配置参数出现变动，直接在CCE中对应的负载中修改其环境变量中的值即可，不需要再重新编译构建代码。

- 使用CCE创建负载时的**环境变量**替换掉写在代码里的数据库配置，以file-center服务为例：

  1. 修改配置文件

     - 修改application.yml中mysql、redis的相关配置：这里是修改了mysql的主机ip和用户名密码、redis的主机ip

       ![image-20221124153405943](https://gitee.com/HuaweiCloudDeveloper/open-capacity-platform-for-huaweicloud/raw/master-dev/images/image-20221124153405943.png)

  2. 重新制作file-center镜像

     配置文件修改后push到远程仓库CodeHub中，之后编译构建，构建file-center新的镜像即可。流程参考[制作镜像](# 5、CloudBuild进行编译构建，构建docker镜像并推送到SWR)。

  3. 升级负载

     - 负载升级入口

       ![image-20221124154220056](https://gitee.com/HuaweiCloudDeveloper/open-capacity-platform-for-huaweicloud/raw/master-dev/images/image-20221124154220056.png)

     - 升级项1：升级镜像版本

       ![image-20221124155026593](https://gitee.com/HuaweiCloudDeveloper/open-capacity-platform-for-huaweicloud/raw/master-dev/images/image-20221124155026593.png)

     - 升级项2：配置环境变量

       ![image-20221124155031910](https://gitee.com/HuaweiCloudDeveloper/open-capacity-platform-for-huaweicloud/raw/master-dev/images/image-20221124155031910.png)

  4. 业务使用测试-文件上传

     - 使用OCP系统，上传图片

       ![image-20221124162018577](https://gitee.com/HuaweiCloudDeveloper/open-capacity-platform-for-huaweicloud/raw/master-dev/images/image-20221124162018577.png)

     - 查看RDS中是否有上传图片信息
  
       ![image-20221124162025008](https://gitee.com/HuaweiCloudDeveloper/open-capacity-platform-for-huaweicloud/raw/master-dev/images/image-20221124162025008.png)

#### * 使用流水线(CodeArts Pipeline)实现持续交付

> 模块在进行版本更新时，需要再次进行编译构建，创建镜像，手动在CCE进行镜像升级，频繁的版本更新操作比较麻烦、耗时，可以使用流水线CloudPipeline实现可视化、可定制的持续交付服务，实现缩短交付周期和提升交付质量的效果。

- 创建流水线，以user-center微服务为例，操作流程如下：

  1. 在CodeArts首页顶部菜单栏选择“服务”下的“流水线”；在流水线列表点击“新建流水线”；配置OCP项目所在的仓库信息；选择空白模板；

  2. 在流水线配置页面执行：“基本信息”中修改名称；“参数设置”中配置版本参数；“执行计划”中配置触发；“工作流”中配置代码检查、构建、部署任务；

     ![image-20230223104043001](https://gitee.com/HuaweiCloudDeveloper/open-capacity-platform-for-huaweicloud/raw/master-dev/images/image-20230223104043001.png)

     ![image-20230223104353051](https://gitee.com/HuaweiCloudDeveloper/open-capacity-platform-for-huaweicloud/raw/master-dev/images/image-20230223104353051.png)

     - **添加代码检查任务**：新建代码检查；在OCP对应仓库明后，点击创建；流水线配置选择对应代码检查任务；检查模式选择Full。
     - **添加构建任务**：新建构建；选择Maven-容器模板；配置“制作镜像并推送到SWR仓库”。

     ![image-20230223104716771](https://gitee.com/HuaweiCloudDeveloper/open-capacity-platform-for-huaweicloud/raw/master-dev/images/image-20230223104716771.png)

     ![image-20230223110122208](https://gitee.com/HuaweiCloudDeveloper/open-capacity-platform-for-huaweicloud/raw/master-dev/images/image-20230223110122208.png)

     ![image-20230223110303369](https://gitee.com/HuaweiCloudDeveloper/open-capacity-platform-for-huaweicloud/raw/master-dev/images/image-20230223110303369.png)

     - **添加部署任务**：新建部署任务；自定义应用名；部署模板选择“Kubernetes快速部署(CCE集群)”；配置任务；选择“保存”。

     ![image-20230223112128830](https://gitee.com/HuaweiCloudDeveloper/open-capacity-platform-for-huaweicloud/raw/master-dev/images/image-20230223112128830.png)

     ![image-20230223124332800](https://gitee.com/HuaweiCloudDeveloper/open-capacity-platform-for-huaweicloud/raw/master-dev/images/image-20230223124332800.png)

  3. 保存流水线，Push更新内容到user-center模块。可以看到流水线被触发执行。

     ![image-20230223125030249](https://gitee.com/HuaweiCloudDeveloper/open-capacity-platform-for-huaweicloud/raw/master-dev/images/image-20230223125030249.png)



## 七、问题记录

| 序号 | 问题                                     | 主要报错信息                                                 | 解决方法                                                     |
| :--: | :--------------------------------------- | ------------------------------------------------------------ | :----------------------------------------------------------- |
|  1   | 项目启动失败，报数据库链接失败或时区问题 | connection failed或time zone error                           | 将配置文件中的数据库配置参数修改为自己的；MySQL8.x需要指定时区，如serverTimezone=UTC |
|  2   | wrokflow-center启动失败                  | ![image-20221124163647633](https://gitee.com/HuaweiCloudDeveloper/open-capacity-platform-for-huaweicloud/raw/master-dev/images/image-20221124163647633.png) | 该项目中使用的flowable工作流引擎，其中application配置文件中，将database-schema-update参数值修改为true，即会对所有表进行更新，表不存在的话则自动建表，开发时常用该值。首次启动表创建好后可以还该回false，false标识表不存在或版本不匹配则抛出异常。 |
|  3   | 编译构建失败                             | Error: org/springframework/boot/maven/RepackageMojo has been compiled by a more recent version of the Java Runtime (class file version 61.0), this version of the Java Runtime only recognizes class file versions up to 52.0 | pom文件中指定spring-boot-maven-plugin插件的版本，可设置成桶springBoot同一版本：<version>2.1.16.RELEASE</version> |
|  4   | 打包镜像失败：基础镜像找不到             | **执行docker命令失败,错误信息为:Get https://production.cloudflare.docker.com/registry-v2/docker/registry/v2/blobs/sha256/c2/c2274a1a0e2786ee9101b08f76111f9ab8019e368dce1e325d3c284a0ca33397/data?verify=1668599680-s2lXdeatlphUqV3wNPTNXpoTExE%3D: net/http: TLS handshake timeout** | 可以修改基础镜像为java:8                                     |
|  5   | 打包镜像失败：提示xxx.jar包找不到        | **[ERROR] [制作镜像并推送到SWR仓库:external_docker_builder] : 执行docker命令失败,错误信息为:ADD failed: stat /var/lib/docker/tmp/docker-builder507223225/user-center: no such file or directory** | 1、jar包命名是否正确，是否带jar后缀；2、pom文件中<build><finalName>标签中是否配置为${project.artifactId}，该值也该是跟Dockerfile中的jar包名一致 |
|  6   | 验证码生成失败，浏览器不是500            | 当为前端服务创建service_ELB服务发现后用域名或公网ip访问前端时，验证法获取失败。浏览器查看状态码不是500，证明不是后端服务出错 | 前端服务中view.static.module.config.js中的base_server，该值是配的路由服务地址，由于前端是从公网访问，所以路由地址也要配成公网地址 |
|  7   | 验证码生成失败，浏览器是500              | 当为前端服务创建service_ELB服务发现后用域名或公网ip访问前端时，验证法获取失败。浏览器查看状态码是500，证明是后端服务报错。查看日志，发现没有走到auth-server，卡在了路由服务。 | 错误原因：gateway默认使用的是webFlux，而Tomcat使用的是netty，项目中不应引入Tomcat-embed-core依赖。解决办法：在gateway项目pom依赖中使用<exclusion>标签去除Tomcat-embed-core或spring-boot-starter-tomcat依赖。 |
|  8   | 部署到CCE后负载运行报错，链接RDS失败     | 负载创建后，状态为“运行中”但CSE中却没有实例信息，且负载的日志中报错**：The last packet sent successfully to the server was 0 milliseconds ago. The driver has not received any packets from the server.**连接数据库失败。 | RDS使用的安全组没有对容器网段(CCE容器的集群信息页面可以看到容器网段)放开3306端口，添加对应安全组的入口规则，对容器网段放开3306端口。 |
