package fairy.easy.httpcanary;

import net.lightbody.bmp.core.har.HarEntry;

public interface AbstractParam {
    void getParam(HarEntry harEntry);
}
