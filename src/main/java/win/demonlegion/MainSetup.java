package win.demonlegion;

import java.sql.Driver;
import java.sql.DriverManager;
import java.util.Enumeration;

import com.baidu.ueditor.config.UeditorProperties;
import org.nutz.ioc.Ioc;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.NutConfig;
import org.nutz.mvc.Setup;

public class MainSetup implements Setup {

    private static final Log log = Logs.get();

    // 特别留意一下,是init方法,不是destroy方法!!!!!

    public void init(NutConfig conf) {
    }

    public void destroy(NutConfig conf) {
        // 解决com.alibaba.druid.proxy.DruidDriver和com.mysql.jdbc.Driver在reload时报warning的问题
        // 多webapp共享mysql驱动的话,以下语句删掉
        Enumeration<Driver> en = DriverManager.getDrivers();
        while (en.hasMoreElements()) {
            try {
                Driver driver = en.nextElement();
                String className = driver.getClass().getName();
                if ("com.alibaba.druid.proxy.DruidDriver".equals(className)
                        || "com.mysql.jdbc.Driver".equals(className)) {
                    log.debug("deregisterDriver: " + className);
                    DriverManager.deregisterDriver(driver);
                }
            }
            catch (Exception e) {
                log.error(e.getMessage());
            }
        }
        // 关闭OSSClient
        Ioc ioc = conf.getIoc();
        UeditorProperties ueditorProperties = ioc.get(UeditorProperties.class);
        ueditorProperties.ossClient.shutdown();
    }
}
