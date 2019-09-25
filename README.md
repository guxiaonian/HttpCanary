<div align="center">

## HttpCanary

**`Android`Crash日志打印输出**

[![Download](https://api.bintray.com/packages/guxiaonian/httpcanary/httpcanary/images/download.svg) ](https://bintray.com/guxiaonian/httpcanary/httpcanary/_latestVersion)
[![GitHub issues](https://img.shields.io/github/issues/guxiaonian/HttpCanary.svg)](https://github.com/guxiaonian/HttpCanary/issues)
[![GitHub forks](https://img.shields.io/github/forks/guxiaonian/HttpCanary.svg)](https://github.com/guxiaonian/HttpCanary/network)
[![GitHub stars](https://img.shields.io/github/stars/guxiaonian/HttpCanary.svg)](https://github.com/guxiaonian/HttpCanary/stargazers)
[![GitHub license](https://img.shields.io/github/license/guxiaonian/HttpCanary.svg)](http://www.apache.org/licenses/LICENSE-2.0)

</div>
<br>

# 效果展示

![http_logo](./img/img1.jpg)

![http_logo](./img/img2.jpg)


# 依赖

```gradle
debugImplementation  'fairy.easy.httpcanary:httpcanary:{latestVersion}'
releaseImplementation  'fairy.easy.httpcanary:httpcanary-no-op:{latestVersion}'
//androidX使用
//debugImplementation  'fairy.easy.httpcanary:httpcanary-androidx:{latestVersion}'

```
      
# 调用方式

```java
public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        HttpCanary.install(this);
    }
}

```

# 注意事项

1. targetSdkVersion设置为28以下
2. 添加1.8版本

```gradle
 compileOptions {
        targetCompatibility 1.8
        sourceCompatibility 1.8
    }
```
