package com.open.capacity.oss.service.impl;


import com.obs.services.ObsClient;
import com.obs.services.model.InitiateMultipartUploadRequest;
import com.obs.services.model.InitiateMultipartUploadResult;
import com.obs.services.model.ObjectMetadata;
import com.open.capacity.oss.dao.FileDao;
import com.open.capacity.oss.model.FileInfo;
import com.open.capacity.oss.model.FileType;
import com.open.capacity.oss.utils.ConcurrentUploadPort;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

/**
 * 华为云obs存储文件
*/
@Service("huaweiOssServiceImpl")
@Slf4j
public class HuaweiOssServiceImpl extends AbstractFileService {

	@Autowired
	private FileDao fileDao;

	@Override
	protected FileDao getFileDao() {
		return fileDao;
	}

	@Override
	protected FileType fileType() {
		return FileType.HUAWEI;
	}

	@Autowired
	private ObsClient obsClient;

	@Value("${obs.bucketName}")
	private String bucketName;
	@Value("${obs.domain}")
	private String domain;

	@Override
	protected void uploadFile(MultipartFile file, FileInfo fileInfo) throws Exception {
		obsClient.putObject(bucketName, fileInfo.getName(), file.getInputStream());
		fileInfo.setUrl(domain + "/" + fileInfo.getName());
	}

	@Override
	protected void uploadBigFile(MultipartFile file, FileInfo fileInfo) throws Exception {
		//MultipartFile转File
		File sampleFile = null;
		String filename = file.getOriginalFilename();
		String prefix = filename.substring(filename.lastIndexOf("."));
		try{
			sampleFile = File.createTempFile(filename, prefix);
			file.transferTo(sampleFile);
		}catch (Exception e){
			e.printStackTrace();
		}

		ConcurrentUploadPort.upload(obsClient,bucketName,"BigFile/"+filename,sampleFile);
	}

	@Override
	protected boolean deleteFile(FileInfo fileInfo) {
		obsClient.deleteObject(bucketName, fileInfo.getName());
		return true;
	}

	/**
	 * 上传大文件
	 * 分段上传 每段一个临时文件
	 */
	@Override
	protected void chunkFile( String guid, Integer chunk, MultipartFile file, Integer chunks,String filePath) throws Exception {

	}


	/**
	 * 合并分段文件
	 * 每一个小段合并一个完整文件
	 */
	@Override
	protected FileInfo mergeFile(String guid, String fileName, String filePath) throws Exception {
		return null;
	}
}
