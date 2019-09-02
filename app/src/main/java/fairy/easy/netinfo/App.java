package fairy.easy.netinfo;

import android.app.Application;


import fairy.easy.httpcanary.HttpCanary;

public class App extends Application {


    @Override
    public void onCreate() {
        super.onCreate();
        HttpCanary.install(this);
    }


}
