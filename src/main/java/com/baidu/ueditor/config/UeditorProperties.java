package com.baidu.ueditor.config;

import com.aliyun.oss.OSSClient;

public class UeditorProperties {

	// 阿里云OSS配置
	public OSSClient ossClient;
	private String bucket = "";
	private boolean autoCreateBucket = false;

	private String baseUrl = "";
	private String uploadDirPrefix = "";
	private int pageSize = 20;

	public UeditorProperties(String endPoint, String accessKeyId, String accessKeySecret) {
		ossClient = new OSSClient(endPoint, accessKeyId, accessKeySecret);
	}

	public String getBucket() {
		return bucket;
	}

	public void setBucket(String bucket) {
		this.bucket = bucket;
	}

	public boolean getAutoCreateBucket() {
		return autoCreateBucket;
	}

	public void setAutoCreateBucket(boolean autoCreateBucket) {
		this.autoCreateBucket = autoCreateBucket;
	}

	public boolean isAutoCreateBucket() {
		return autoCreateBucket;
	}

	public String getBaseUrl() {
		return baseUrl;
	}

	public void setBaseUrl(String baseUrl) {
		this.baseUrl = baseUrl;
	}

	public String getUploadDirPrefix() {
		return uploadDirPrefix;
	}

	public void setUploadDirPrefix(String uploadDirPrefix) {
		this.uploadDirPrefix = uploadDirPrefix;
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}
}

