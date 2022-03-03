# 基于Gitee实现的轻量化配置中心组件

## 配置说明
| 配置项  | 说明  | 示例 |
| ------------ | ------------ | ------------ |
| codest.config.gitee.url  | Gitee仓库Open API配置文件资源地址  | https://gitee.com/api/v5/repos/codest-c/项目名/contents/模块名/server.properties |
| codest.config.gitee.token  | Gitee Access Token  | 设置 - 私人令牌中生成 |
| codest.config.provider  | 自定义远程配置读取类，实现RemoteConfigProvider接口，优先级最高，配置后不再自动执行Gitee资源读取  | xxx.xxx.xxx.xxxProvider |

## 配置重载
```
// 注入RefreshConfigExecutor执行器
private final RefreshConfigExecutor executor;
// 调用配置刷新方法
executor.execute();
```
可以自行决定刷新配置的触发方式，比如web接口手动触发或者通过定时任务刷新的方式。