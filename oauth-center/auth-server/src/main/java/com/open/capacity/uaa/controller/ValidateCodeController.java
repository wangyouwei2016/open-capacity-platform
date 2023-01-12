package com.open.capacity.uaa.controller;

import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.code.kaptcha.Producer;
import com.open.capacity.common.constant.SecurityConstants;
import com.open.capacity.common.dto.ResponseEntity;
import com.open.capacity.uaa.google.GoogleOTPAuthUtil;
import com.open.capacity.uaa.service.IValidateCodeService;

import io.swagger.annotations.ApiOperation;
import lombok.SneakyThrows;

/**
 * 验证码提供
 * @author someday
 * @date 2018/12/18
 */
@Controller
public class ValidateCodeController {
	
	
    @Autowired
    private IValidateCodeService validateCodeService;

    @Autowired
	private Producer producer;
    
    /**
     * 创建验证码
     *
     * @throws Exception
     */
    @SneakyThrows
    @GetMapping(SecurityConstants.DEFAULT_VALIDATE_CODE_URL_PREFIX + "/{deviceId}")
    public void createCode(@PathVariable String deviceId, HttpServletResponse response)  {
    	Assert.notNull(deviceId, "机器码不能为空");
		response.setHeader("Cache-Control", "no-store, no-cache");
		response.setContentType("image/jpeg");
		// 生成文字验证码
		String text = producer.createText();
		// 生成图片验证码
		BufferedImage image = producer.createImage(text);
        validateCodeService.saveImageCode(deviceId, text);
		try (ServletOutputStream out = response.getOutputStream()) {
			ImageIO.write(image, "JPEG", out);
		}
    }

    /**
     * 发送手机验证码
     * 后期要加接口限制
     *
     * @param mobile 手机号
     * @return R
     */
    @ResponseBody
    @GetMapping(SecurityConstants.MOBILE_VALIDATE_CODE_URL_PREFIX + "/{mobile}")
    public ResponseEntity createCode(@PathVariable String mobile) {
        Assert.notNull(mobile, "手机号不能为空");
        return validateCodeService.sendSmsCode(mobile);
    }
    @SneakyThrows
    @ApiOperation(value = "获取动态令牌二维码")
    @GetMapping(SecurityConstants.GOOGLE_VALIDATE_CODE_URL_PREFIX + "/{deviceId}")
	public void createQrCode(@PathVariable  String deviceId, HttpServletResponse response) {
    	Assert.notNull(deviceId, "客户端指纹不能为空");
    	//生成base32字符串
    	String secret = GoogleOTPAuthUtil.generateSecret(64);
    	// 生成动态令牌二维码
    	BufferedImage image =validateCodeService.createQrCode(deviceId,secret);
    	//保存应用密钥
    	validateCodeService.saveQrSecret(deviceId,secret);
    	try (ServletOutputStream out = response.getOutputStream()) {
			ImageIO.write(image, "JPEG", out);
		}
	}
    
}
