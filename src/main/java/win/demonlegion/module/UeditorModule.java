package win.demonlegion.module;

import com.baidu.ueditor.ActionEnter;
import com.baidu.ueditor.config.UeditorProperties;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.Ok;

import javax.servlet.http.HttpServletRequest;

@IocBean
@At("/ueditor")
public class UeditorModule {
    private static final Log log = Logs.get();
    @Inject
    protected ActionEnter actionEnter;
    @Inject
    protected UeditorProperties ueditorProperties;


    @At("/upload")
    @Ok("raw:json")
    public String exe(HttpServletRequest request){
        return actionEnter.exec(request);
    }

    @At("/test")
    public String test(HttpServletRequest request){
        log.debug("ueditorProperties: " + ueditorProperties.getBucket());
        return "ueditorProperties: " + ueditorProperties.getPageSize();
    }
}
