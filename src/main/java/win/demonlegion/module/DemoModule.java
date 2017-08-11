package win.demonlegion.module;

import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.annotation.At;

@IocBean
@At("/demo")
public class DemoModule {
	private static final Log log = Logs.get();

	@At
	public String hello(){
		return "Hello world";
	}
}

