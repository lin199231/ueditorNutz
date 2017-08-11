package com.baidu.ueditor;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.nutz.ioc.loader.annotation.Inject;
import com.baidu.ueditor.define.ActionMap;
import com.baidu.ueditor.define.AppInfo;
import com.baidu.ueditor.define.BaseState;
import com.baidu.ueditor.define.State;
import com.baidu.ueditor.hunter.FileManager;
import com.baidu.ueditor.hunter.ImageHunter;
import com.baidu.ueditor.upload.StorageManager;
import com.baidu.ueditor.upload.Uploader;
import org.nutz.log.Log;
import org.nutz.log.Logs;

public class ActionEnter {
	private static final Log log = Logs.get();

	private ConfigManager configManager = null;
	private HttpServletRequest request = null;
	private String actionType = null;

	private FileManager fileManager;
	private StorageManager storageManager;

	public ActionEnter(ConfigManager configManager){
		this.configManager = configManager;
	}

	public String exec (HttpServletRequest request) {

		this.request = request;
		this.actionType = request.getParameter( "action" );
		String callbackName = this.request.getParameter("callback");
		if ( callbackName != null ) {

			if ( !validCallbackName( callbackName ) ) {
				return new BaseState( false, AppInfo.ILLEGAL ).toJSONString();
			}

			return callbackName+"("+this.invoke()+");";

		} else {
			return this.invoke();
		}

	}

	public String invoke() {

		if ( actionType == null || !ActionMap.mapping.containsKey( actionType ) ) {
			return new BaseState( false, AppInfo.INVALID_ACTION ).toJSONString();
		}

		if ( this.configManager == null || !this.configManager.valid() ) {
			return new BaseState( false, AppInfo.CONFIG_ERROR ).toJSONString();
		}

		State state = null;

		int actionCode = ActionMap.getType( this.actionType );

		Map<String, Object> conf = null;

		switch ( actionCode ) {

			case ActionMap.CONFIG:
				return this.configManager.getAllConfig().toString();

			case ActionMap.UPLOAD_IMAGE:
			case ActionMap.UPLOAD_SCRAWL:
			case ActionMap.UPLOAD_VIDEO:
			case ActionMap.UPLOAD_FILE:
				conf = this.configManager.getConfig( actionCode );
				state = new Uploader( request, conf, storageManager).doExec();
				break;

			case ActionMap.CATCH_IMAGE:
				conf = configManager.getConfig( actionCode );
				String[] list = this.request.getParameterValues( (String)conf.get( "fieldName" ) );
				state = new ImageHunter( conf ).capture( list, storageManager );
				break;

			case ActionMap.LIST_IMAGE:
			case ActionMap.LIST_FILE:
				conf = configManager.getConfig( actionCode );
				int start = this.getStartIndex();
				String marker = this.getMarker();
				fileManager.setConf(conf);
				state = fileManager.listFile( start,marker );
				break;
//			case ActionMap.DELETE_FILE:
//				conf = configManager.getConfig(actionCode);
//				String fileKey = this.getFileKey();
//				state = new FileManager(conf).deleteFile(fileKey);
		}

		return state.toJSONString();
	}

	public String getFileKey () {
		String fileKey = this.request.getParameter( "marker" );
		return fileKey;
	}
	public int getStartIndex () {
		String start = this.request.getParameter( "start" );
		try {
			return Integer.parseInt( start );
		} catch ( Exception e ) {
			return 0;
		}
	}
	public String getMarker () {
		String marker = this.request.getParameter( "marker" );
		return marker;
	}

	/**
	 * callback参数验证
	 */
	public boolean validCallbackName ( String name ) {
		if ( name.matches( "^[a-zA-Z_]+[\\w0-9_]*$" ) ) {
			return true;
		}

		return false;
	}

	public FileManager getFileManager() {
		return fileManager;
	}

	public void setFileManager(FileManager fileManager) {
		this.fileManager = fileManager;
	}

	public StorageManager getStorageManager() {
		return storageManager;
	}

	public void setStorageManager(StorageManager storageManager) {
		this.storageManager = storageManager;
	}
}
