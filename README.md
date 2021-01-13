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
debugImplementation  'fairy.easy.httpcanary:httpcanary-androidx:{latestVersion}'
```

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