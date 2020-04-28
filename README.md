silktrader-platform-v2 为星客后端架构升级版本，silktrader-platform版本存在着架构的缺陷。

## [接口文档]

### 应用端口 ###
```
ucenter-server      6201    /uc2        service-ucenter
account-server      6028    /acct       service-account

lock-v2-server      6226    /lock2      service-lock-v2
lock-slp-server     6701    /lock-slp   service-lock-slp


gateway-server      6610    /           service-gateway
gateway-unsafe-server 6620 /          service-gateway2-unsafe
exchange-api-v2-server 6203 /exchange-api2 service-exchange-api-v2
otc-open-server     6202                otc-open-server
service-exchange-v2-freeze_release 6230 /exchange2fr   service-exchange-v2-freeze_release
会员体系 6231
幸运宝 6232
出入金风险监控 6235
```

## 接口规范 ##
### 请求头的规范 ###
```
***请求头是公共信息，请不要用于当作接口的参数使用***

  1、请求头格式： Content-Type=application/x-www-form-urlencoded
  2、appId=应用ID，由系统指定
  3、apiKey=用户token（如果不需要签名验证的话，token可以是任意字符串）
  4、apiTime=客户端系统时间戳（注意不能与服务器时间相差太大）
  5、apiSign=数据签名，如无apiKey则无效签名
  
备注：
  1、详见“gateway-server”模块中的“模块说明.md”。
  2、存在着部分接口因为不需要登录，无法进行签名和加密的处理
```
### 请求数据的规范 ###
```
1、请求数据格式
    data=base64(ECB(请求数据))

加密说明：
    DES加密模式:ECB
    填充:pkcs7padding/pkcs5padding
    输出:base64

备注：详见“gateway-server”模块中的“模块说明.md”
```

## 测试环境 ##

### 应用程序 ###
```
  部署路径：/home/deploy/app
  启停命令：sbml 应用程序名称  [start 1024|stop] 
  
IP:172.16.0.79  
  应用程序：exchange-api、exchange-vip-api、guess-api、otc-api、
           ucenter-api、silktrader-pay、cmarket-api、txlcn-tm
           
IP:172.16.0.80
  应用程序：chat、market、scheduled-job、open-api、collect-supervisor
  
IP:172.16.0.85
  应用程序：config-server、admin-api、exchange、lock-api、wallet、eureka-server、
    txlcn-tm
```