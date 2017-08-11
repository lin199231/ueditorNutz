package com.baidu.ueditor.upload;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.aliyun.oss.ClientException;
import com.aliyun.oss.OSSException;
import com.aliyun.oss.model.PutObjectResult;
import com.baidu.ueditor.config.UeditorProperties;
import org.apache.commons.io.FileUtils;

import com.baidu.ueditor.define.AppInfo;
import com.baidu.ueditor.define.BaseState;
import com.baidu.ueditor.define.FileType;
import com.baidu.ueditor.define.State;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;

public class StorageManager {
	public static final int BUFFER_SIZE = 8192;

	private UeditorProperties ueditorProperties;

	public State saveBinaryFile(byte[] data, String path) {
		State state;
		String key = ueditorProperties.getUploadDirPrefix() + getFileName(path);
		try {
			PutObjectResult putObjectResult = ueditorProperties.ossClient.putObject(ueditorProperties.getBucket(), key, new ByteArrayInputStream(data));
			state = new BaseState(true);
			state.putInfo("size", data.length);
			state.putInfo("title", path);
			state.putInfo("url", ueditorProperties.getBaseUrl() + "/" + key);
		}catch (OSSException oe) {
			state = new BaseState(false, AppInfo.IO_ERROR);
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

	public State saveFileByInputStream(InputStream is, String path, long maxSize) {
		State state;
		File tmpFile = getTmpFile();
		
		byte[] dataBuf = new byte[ 2048 ];
		BufferedInputStream bis = new BufferedInputStream(is, StorageManager.BUFFER_SIZE);

		try {
			BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(tmpFile), StorageManager.BUFFER_SIZE);
			int count = 0;
			while ((count = bis.read(dataBuf)) != -1) {
				bos.write(dataBuf, 0, count);
			}
			bos.flush();
			bos.close();

			if (tmpFile.length() > maxSize) {
				tmpFile.delete();
				return new BaseState(false, AppInfo.MAX_SIZE);
			}

			state = saveTmpFile(tmpFile, path);

			if (!state.isSuccess()) {
				tmpFile.delete();
			}

			return state;
			
		} catch (IOException e) {
		}
		return new BaseState(false, AppInfo.IO_ERROR);
	}

	public State saveFileByInputStream(InputStream is, String path) {
		State state;

		File tmpFile = getTmpFile();

		byte[] dataBuf = new byte[ 2048 ];
		BufferedInputStream bis = new BufferedInputStream(is, StorageManager.BUFFER_SIZE);

		try {
			BufferedOutputStream bos = new BufferedOutputStream( new FileOutputStream(tmpFile), StorageManager.BUFFER_SIZE);

			int count = 0;
			while ((count = bis.read(dataBuf)) != -1) {
				bos.write(dataBuf, 0, count);
			}
			bos.flush();
			bos.close();

			state = saveTmpFile(tmpFile, path);

			if (!state.isSuccess()) {
				tmpFile.delete();
			}

			return state;
		} catch (IOException e) {
		}
		return new BaseState(false, AppInfo.IO_ERROR);
	}
	
	private File getTmpFile() {
		File tmpDir = FileUtils.getTempDirectory();
		String tmpFileName = (Math.random() * 10000 + "").replace(".", "");
		return new File(tmpDir, tmpFileName);
	}

	private State saveTmpFile(File tmpFile, String path) {
		State state;
		String key = ueditorProperties.getUploadDirPrefix() + getFileName(path);
		try {
			PutObjectResult putObjectResult = ueditorProperties.ossClient.putObject(ueditorProperties.getBucket(), key, tmpFile);
			state = new BaseState(true);
			state.putInfo("size", tmpFile.length());
			state.putInfo("title", path);
			state.putInfo("url", ueditorProperties.getBaseUrl() + "/" + key);
		}catch (OSSException oe) {
			state = new BaseState(false, AppInfo.IO_ERROR);
			state.putInfo("code", oe.getErrorCode());
			state.putInfo("message", oe.getMessage());
		} catch (ClientException ce) {
			state = new BaseState(false, AppInfo.IO_ERROR);
			state.putInfo("code", ce.getErrorCode());
			state.putInfo("message", ce.getMessage());
		}
		return state;
	}
	
	private String getFileName(String fileName) {
		String suffix =  FileType.getSuffixByFilename(fileName);
		return new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date())+ (int)(Math.random()*9000 +1000) + suffix;
	}

	public UeditorProperties getUeditorProperties() {
		return ueditorProperties;
	}

	public void setUeditorProperties(UeditorProperties ueditorProperties) {
		this.ueditorProperties = ueditorProperties;
	}
}
