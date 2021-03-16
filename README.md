<div align="center">

## HttpCanary

**`Android`Http请求打印输出**

[![Download](https://api.bintray.com/packages/guxiaonian/httpcanary/httpcanary/images/download.svg) ](https://bintray.com/guxiaonian/httpcanary/httpcanary/_latestVersion)
[![GitHub issues](https://img.shields.io/github/issues/guxiaonian/HttpCanary.svg)](https://github.com/guxiaonian/HttpCanary/issues)
[![GitHub forks](https://img.shields.io/github/forks/guxiaonian/HttpCanary.svg)](https://github.com/guxiaonian/HttpCanary/network)
[![GitHub stars](https://img.shields.io/github/stars/guxiaonian/HttpCanary.svg)](https://github.com/guxiaonian/HttpCanary/stargazers)
[![GitHub license](https://img.shields.io/github/license/guxiaonian/HttpCanary.svg)](http://www.apache.org/licenses/LICENSE-2.0)

</div>
<br>

# 依赖

```gradle
debugImplementation  'fairy.easy.httpcanary:httpcanary:{latestVersion}'
```

# 说明
> 本产品以SDK的形式来获取本应用所有网络请求的详细信息。

## 步骤说明

1. 给予读写权限，生成证书
2. 安装证书
3. 开启全局代理，如果不开启，默认抓取当前应用的网络请求
4. 给予本应用root权限，获取流量发送方的详细信息(手机需root)
5. 证书迁移至系统目录下(手机需root)
6. 开始抓包

## 界面展示

![](./img/img1.jpg)
![](./img/img2.jpg)

# 注意事项

1. targetSdkVersion设置为27及以下
2. 添加1.8版本

```gradle
 compileOptions {
        targetCompatibility 1.8
        sourceCompatibility 1.8
    }
```
3. 添加`multiDexEnabled true`。
