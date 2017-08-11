package com.baidu.ueditor.upload;

import com.baidu.ueditor.define.State;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;

public class Uploader {
	private HttpServletRequest request;
	private Map<String, Object> conf;
	private StorageManager storageManager;

	public Uploader(HttpServletRequest request, Map<String, Object> conf, StorageManager storageManager) {
		this.request = request;
		this.conf = conf;
		this.storageManager = storageManager;
	}

	public final State doExec() {
		State state;

		if ("true".equals(this.conf.get("isBase64"))) {
			state = new Base64Uploader().save(this.request, this.conf, storageManager);
		} else {
			state = new BinaryUploader().save(this.request, this.conf, storageManager);
		}

		return state;
	}
}
