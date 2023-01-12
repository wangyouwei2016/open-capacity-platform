package com.open.capacity.file.entity;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DownloadDto {
	private String fileName ;
	
	private byte[] bytes ;
}
