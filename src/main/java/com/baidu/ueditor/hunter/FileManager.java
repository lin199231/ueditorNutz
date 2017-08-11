package com.baidu.ueditor.hunter;

import java.util.*;

import com.aliyun.oss.ClientException;
import com.aliyun.oss.OSSException;
import com.aliyun.oss.model.ListObjectsRequest;
import com.aliyun.oss.model.OSSObjectSummary;
import com.aliyun.oss.model.ObjectListing;
import com.baidu.ueditor.config.UeditorProperties;
import com.baidu.ueditor.define.AppInfo;
import com.baidu.ueditor.define.BaseState;
import com.baidu.ueditor.define.MultiState;
import com.baidu.ueditor.define.State;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.log.Log;
import org.nutz.log.Logs;

public class FileManager {
	private static final Log log = Logs.getLog(FileManager.class);

	private String dir = null;
	private String rootPath = null;
	private String[] allowFiles = null;
	private int count = 0;

	private UeditorProperties ueditorProperties;

	public void setConf(Map<String, Object> conf) {
		this.rootPath = (String)conf.get( "rootPath" );
		this.dir = this.rootPath + (String)conf.get( "dir" );
		this.allowFiles = this.getAllowFiles( conf.get("allowFiles") );
		this.count = (Integer)conf.get( "count" );
	}

	public State listFile ( int index,String marker ) {
		State state;
		try {
			// 列举Object
			ObjectListing objectListing = ueditorProperties.ossClient.listObjects(new ListObjectsRequest(ueditorProperties.getBucket())
					.withMarker(marker).withMaxKeys(ueditorProperties.getPageSize()));
			List<OSSObjectSummary> sums = objectListing.getObjectSummaries();
			List<String> fileList = new ArrayList<String>();
			for (OSSObjectSummary s : sums) {
				fileList.add(s.getKey());
			}
			log.debug(fileList.toString());
			state = this.getState( fileList.toArray(new String[fileList.size()]) );
			state.putInfo( "start", index );
			state.putInfo( "isLast", objectListing.isTruncated()+"" );
			state.putInfo( "marker", objectListing.getNextMarker() );
			state.putInfo( "total", Integer.MAX_VALUE );
		} catch (OSSException oe) {
			state = new BaseState( false, AppInfo.NOT_EXIST );
			state.putInfo("code", oe.getErrorCode());
			state.putInfo("message", oe.getMessage());
		} catch (ClientException ce) {
			state = new BaseState(false, AppInfo.IO_ERROR);
			state.putInfo("code", ce.getErrorCode());
			state.putInfo("message", ce.getMessage());
		} finally {
			// 关闭client
//			if(ueditorProperties.ossClient != null) ueditorProperties.ossClient.shutdown();
		}
		return state;
	}

	public State deleteFile(String key) {
		State state;
		try {
			// 删除单个Object
			ueditorProperties.ossClient.deleteObject(ueditorProperties.getBucket(), key);
			state = new BaseState(true);
		} catch (OSSException oe) {
			state = new BaseState( false, AppInfo.IO_ERROR );
			state.putInfo("code", oe.getErrorCode());
			state.putInfo("message", oe.getMessage());
		} catch (ClientException ce) {
			state = new BaseState(false, AppInfo.IO_ERROR);
			state.putInfo("code", ce.getErrorCode());
			state.putInfo("message", ce.getMessage());
		} finally {
			// 关闭client
			if(ueditorProperties.ossClient != null) ueditorProperties.ossClient.shutdown();
		}
		return state;
	}

	private State getState ( String[] files ) {
		MultiState state = new MultiState( true );
		BaseState fileState = null;


		for ( String url : files ) {
			if ( url == null ) {
				break;
			}
			fileState = new BaseState( true );
			fileState.putInfo( "url", ueditorProperties.getBaseUrl() + "/" + url );
			state.addState( fileState );
		}

		return state;

	}
	
	private String[] getAllowFiles ( Object fileExt ) {
		String[] exts = null;
		String ext = null;
		
		if ( fileExt == null ) {
			return new String[ 0 ];
		}
		
		exts = (String[])fileExt;
		
		for ( int i = 0, len = exts.length; i < len; i++ ) {
			
			ext = exts[ i ];
			exts[ i ] = ext.replace( ".", "" );
			
		}
		
		return exts;
	}

	public UeditorProperties getUeditorProperties() {
		return ueditorProperties;
	}

	public void setUeditorProperties(UeditorProperties ueditorProperties) {
		this.ueditorProperties = ueditorProperties;
	}
}
