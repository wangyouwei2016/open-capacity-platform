package com.open.capacity.oss.config;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import com.open.capacity.oss.model.FileType;
import com.open.capacity.oss.service.FileService;


/**
 * @author 作者 owen 
 * @version 创建时间：2017年11月12日 上午22:57:51
 * FileService工厂<br>
 * 将各个实现类放入map
*/
@Configuration
public class OssServiceFactory {

	private Map<FileType, FileService> map = new HashMap<>();

 
	@Autowired
	private FileService huaweiOssServiceImpl;


	@PostConstruct
	public void init() {
		map.put(FileType.HUAWEI,  huaweiOssServiceImpl);
	}

	public FileService getFileService(String fileType) {
		//当有多种上传方式时，可以判断选择
	   /*if (StringUtils.isBlank(fileType)) {
			return huaweiOssServiceImpl;
		}*/

		return map.get(FileType.valueOf(fileType));
	}
}
