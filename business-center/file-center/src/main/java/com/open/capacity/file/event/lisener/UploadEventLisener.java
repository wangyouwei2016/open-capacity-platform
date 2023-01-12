package com.open.capacity.file.event.lisener;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.ServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.open.capacity.common.context.TenantContextHolder;
import com.open.capacity.common.disruptor.annocation.Channel;
import com.open.capacity.common.disruptor.event.BaseEvent;
import com.open.capacity.common.disruptor.listener.EventListener;
import com.open.capacity.common.disruptor.thread.ExecutorService;
import com.open.capacity.common.exception.BusinessException;
import com.open.capacity.file.constant.CommandType;
import com.open.capacity.file.context.UploadContext;
import com.open.capacity.file.entity.FileInfo;
import com.open.capacity.file.event.UploadEvent;
import com.open.capacity.file.service.FileServiceFactory;

import cn.hutool.core.io.IoUtil;
import lombok.SneakyThrows;

@Service
@Channel(CommandType.UPLOAD)
public class UploadEventLisener extends EventListener<UploadEvent, UploadContext> {

	@Resource
	private FileServiceFactory fileServiceFactory;

	@Resource
	private ObjectMapper objectMapper;

	@Override
	public boolean accept(BaseEvent event) {

		UploadEvent uploadEvent = (UploadEvent) event;

		// s3 单个文件上传
		if (CommandType.UPLOAD.equals(uploadEvent.getCommandType())) {
			return true;
		}
		return false;
	}

	@Override
	@SneakyThrows
	public void onEvent(UploadEvent event, UploadContext eventContext) {

		executorService.execute(CommandType.UPLOAD, () -> {
			ServletResponse response = eventContext.getAsyncContext().getResponse();
			response.setCharacterEncoding("UTF-8");
			response.setContentType("application/json;charset=UTF-8");
			try (ServletOutputStream out = response.getOutputStream()) {
				TenantContextHolder.setTenant(event.getTenant());
				MultipartFile file = event.getFiles().get(0);
				FileInfo fileInfo = fileServiceFactory.getService(event.getFileType()).upload(file);
				IoUtil.write(out, false,objectMapper.writeValueAsString(fileInfo).getBytes(StandardCharsets.UTF_8));
			} catch (IOException e) {
				throw new BusinessException(e.getMessage());
			} finally {
				eventContext.getAsyncContext().complete();
			}
		});
	}

}
