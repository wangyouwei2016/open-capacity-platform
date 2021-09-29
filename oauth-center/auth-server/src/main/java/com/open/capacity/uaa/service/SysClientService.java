package com.open.capacity.uaa.service;

import com.open.capacity.common.model.SysClient;
import com.open.capacity.common.web.PageResult;
import com.open.capacity.common.web.ResponseEntity;

import java.util.List;
import java.util.Map;

@SuppressWarnings("all")
public interface SysClientService {

	
	ResponseEntity saveOrUpdate(SysClient clientDto);
	
	void delete(Long id);
	
	ResponseEntity updateEnabled(Map<String, Object> params);
	
	SysClient getById(Long id) ;

  
    
    public PageResult<SysClient> list(Map<String, Object> params);
    
    List<SysClient> findList(Map<String, Object> params) ;
    

	
    
}
