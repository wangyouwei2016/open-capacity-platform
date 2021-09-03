package com.open.capacity.item.controller;

import com.open.capacity.common.exception.service.ServiceException;
import com.open.capacity.common.web.ResponseEntity;
import com.open.capacity.item.service.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotNull;

@RestController
public class ItemController {

    @Autowired
    private ItemService itemService;

    @RequestMapping("/deductInventory")
    public ResponseEntity deductInventory(@NotNull String productId, HttpServletRequest request) throws ServiceException {
        return  itemService.deductInventory(productId);
    }

}
