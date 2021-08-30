package com.open.capacity.item.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.open.capacity.common.exception.service.ServiceException;
import com.open.capacity.common.web.ResponseEntity;
import com.open.capacity.item.entity.OcpItem;

public interface ItemService extends IService<OcpItem> {

    ResponseEntity deductInventory(String productId) throws ServiceException;

}
