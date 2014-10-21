cloudsdk
===========

此项目为一个Android程序，可作为CloudBaaS的SDK。

此项目和CloudService配合使用，其中CloudService作为服务端，本项目作为Client端的SDK。

此项目有两个依赖包gson.jar(用于处理json <-> java object相互转换) httpmime.jar（用于处理发送带附件的HTTP请求）

用户使用CloudBaaS来作为Android程序后端时可基于此项目开发。

需要注意的是在使用SDK接口时候，需要先调用CloudClient.init(context,appname,ak,sk)来初始化。

详细文档请参见：http://cloudbaas.sinaapp.com

有任何问题可与我联系：zhiyun.cloud@gmail.com @智云同学
