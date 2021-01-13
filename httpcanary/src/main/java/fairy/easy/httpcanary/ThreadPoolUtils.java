package fairy.easy.httpcanary;



import java.util.concurrent.Executor;
import java.util.concurrent.Executors;


/**
 * 线程工具类
 *
 * @date 2019/3/13
 */
public class ThreadPoolUtils {

    /**
     * 单利类
     **/
    private static ThreadPoolUtils singleton;
    /**
     * android自带封装线程池
     **/
    private static Executor mExecutor;
    /**
     * 线程数
     */
    private static final int GTM_THREAD_LENGTH = 5;

    /**
     * 初始化线程池
     *
     * @return
     */
    public static ThreadPoolUtils getInstance() {
        if (singleton == null) {
            synchronized (ThreadPoolUtils.class) {
                if (singleton == null) {
                    singleton = new ThreadPoolUtils();
                    mExecutor = Executors.newFixedThreadPool(GTM_THREAD_LENGTH);
                }
            }
        }
        return singleton;
    }

    /**
     * 开启runnable
     *
     * @param runnable
     */
    public void execute(Runnable runnable) {
        try {
            if (runnable != null) {
                mExecutor.execute(runnable);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
