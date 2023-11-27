package com.open.capacity.user.controller;

import com.open.capacity.common.annotation.Permission;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.open.capacity.common.dto.ResponseEntity;
import com.open.capacity.common.signature.annotation.SignatureValidation;

import lombok.extern.slf4j.Slf4j;

/**
 * @author lzw
 * @description
 * @date 2023/2/24 15:28
 */
@Slf4j
@RestController
@RequestMapping("/demo")
public class DemoController {

    @GetMapping("/signatureValidation")
    @SignatureValidation
    public ResponseEntity signatureValidation() {
        return ResponseEntity.succeed("操作成功");
    }

    @GetMapping("/methodPermission")
    @PreAuthorize("hasAuthority('user-list')")
//    @Permission(value = "user-list")
    public ResponseEntity methodPermission() {
        return ResponseEntity.succeed("操作成功");
    }
}
