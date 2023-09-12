package com.open.capacity.uaa.controller;

import java.io.Serializable;
import java.security.Principal;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.druid.filter.config.ConfigTools;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.open.capacity.common.constant.CommonConstant;
import com.open.capacity.common.constant.SecurityConstants;
import com.open.capacity.common.constant.UaaConstant;
import com.open.capacity.common.dto.ResponseEntity;
import com.open.capacity.common.exception.BusinessException;
import com.open.capacity.common.model.LoginAppUser;
import com.open.capacity.common.model.SysUser;
import com.open.capacity.common.properties.SecurityProperties;
import com.open.capacity.common.utils.*;
import com.open.capacity.uaa.common.model.DefaultClientDetails;
import com.open.capacity.uaa.common.util.AuthUtils;
import com.open.capacity.uaa.service.ISysTokenService;
import com.open.capacity.uaa.service.impl.CustomTokenServices;
import com.open.capacity.uaa.service.impl.RedisAuthorizationCodeServices;
import com.open.capacity.uaa.service.impl.RedisClientDetailsService;
import com.open.capacity.uaa.util.RsaTools;
import com.xkcoding.http.HttpUtil;
import com.xkcoding.http.support.httpclient.HttpClientImpl;

import org.apache.commons.collections.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2RefreshToken;
import org.springframework.security.oauth2.common.exceptions.InvalidTokenException;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2Request;
import org.springframework.security.oauth2.provider.TokenRequest;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import me.zhyd.oauth.config.AuthConfig;
import me.zhyd.oauth.model.AuthCallback;
import me.zhyd.oauth.request.AuthGiteeRequest;
import me.zhyd.oauth.request.AuthRequest;
import me.zhyd.oauth.utils.AuthStateUtils;

/**
 * @author 作者 owen
 * @version 创建时间：2018年4月28日 下午2:18:54 类说明
 */
@Slf4j
@RestController
@Api(tags = "OAuth API")
@SuppressWarnings("all")
public class OAuth2Controller {

	@Autowired
	private ISysTokenService sysTokenService;
	@Autowired
	private RedisTemplate<String, Object> redisTemplate;
	@Autowired
	private RedisAuthorizationCodeServices redisAuthorizationCodeService;
	private static OAuth2Request storedRequest;
	@Autowired
	private TokenStore tokenStore;
	@Lazy
	@Autowired
	private TokenEnhancer tokenEnhancer;
	@Resource
	private SecurityProperties securityProperties;

	@RequestMapping("/oauth/render")
	@SneakyThrows
	public void render(HttpServletResponse response) {
		AuthRequest authRequest = getAuthRequest();
		response.sendRedirect(authRequest.authorize(AuthStateUtils.createState()));
	}

	@RequestMapping("/oauth/callback")
	@SneakyThrows
	public Object callback(AuthCallback callback) {
		AuthRequest authRequest = getAuthRequest();
		return authRequest.login(callback);
	}
	private AuthRequest getAuthRequest() {
		
		AuthConfig authConfig = new AuthConfig();
		authConfig.setClientId("");
		authConfig.setClientSecret("");
		authConfig.setRedirectUri("http://localhost:8000/api-auth/oauth/callback");
		HttpUtil.setHttp(new HttpClientImpl());
		return new AuthGiteeRequest(authConfig);
	}

	@ApiOperation(value = "clientId获取token")
	@PostMapping("/oauth/client/token")
	public void getClientTokenInfo() {

		ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder
				.getRequestAttributes();
		HttpServletRequest request = servletRequestAttributes.getRequest();
		HttpServletResponse response = servletRequestAttributes.getResponse();
		try {
			String clientId = request.getHeader("client_id");
			String clientSecret = request.getHeader("client_secret");
			OAuth2AccessToken oAuth2AccessToken = sysTokenService.getClientTokenInfo(clientId, clientSecret);
			ResponseUtil.renderJson(response, oAuth2AccessToken);
		} catch (Exception e) {
			Map<String, String> rsp = new HashMap<>();
			rsp.put(CommonConstant.STATUS, HttpStatus.UNAUTHORIZED.value() + "");
			rsp.put("msg", e.getMessage());
			ResponseUtil.renderJsonError(response, rsp, HttpStatus.UNAUTHORIZED.value());
		}
	}

	@ApiOperation(value = "用户名密码获取token")
	@PostMapping("/oauth/user/token")
	public void getUserTokenInfo(
			@ApiParam(required = true, name = "username", value = "账号") @RequestParam(value = "username") String username,
			@ApiParam(required = true, name = "password", value = "密码") @RequestParam(value = "password") String password) {

		ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder
				.getRequestAttributes();
		HttpServletRequest request = servletRequestAttributes.getRequest();
		HttpServletResponse response = servletRequestAttributes.getResponse();
		try {
			String clientId = request.getHeader("client_id");
			String clientSecret = request.getHeader("client_secret");
			OAuth2AccessToken oAuth2AccessToken = sysTokenService.getUserTokenInfo(clientId, clientSecret, username,
					password);
			ResponseUtil.renderJson(response, oAuth2AccessToken);
		} catch (Exception e) {
			Map<String, String> rsp = new HashMap<>();
			rsp.put(CommonConstant.STATUS, HttpStatus.UNAUTHORIZED.value() + "");
			rsp.put("msg", e.getMessage());
			ResponseUtil.renderJsonError(response, rsp, HttpStatus.UNAUTHORIZED.value());
		}
	}

	@ApiOperation(value = "获取token信息")
	@PostMapping(value = "/oauth/get/token", params = "access_token")
	public OAuth2AccessToken getTokenInfo(String access_token) {
		return sysTokenService.getTokenInfo(access_token);

	}

	/**
	 * 当前登陆用户信息
	 * security获取当前登录用户的方法是SecurityContextHolder.getContext().getAuthentication()
	 * 这里的实现类是org.springframework.security.oauth2.provider.OAuth2Authentication
	 * 
	 * @return
	 */
	@ApiOperation(value = "当前登陆用户信息")
	@GetMapping(value = { "/oauth/userinfo" }, produces = "application/json") // 获取用户信息。/auth/user
	public Map<String, Object> getCurrentUserDetail(HttpServletRequest request) {
		Map<String, Object> userInfo = new HashMap<>();
		try {
			SysUser sysUser = UserUtils.getCurrentUser(request, false);
			userInfo.put(CommonConstant.STATUS, CommonConstant.SUCCESS);
			LoginAppUser loginUser = AuthUtils.getLoginAppUser();
			userInfo.put("user", loginUser);
			userInfo.put("username", loginUser.getUsername());
			userInfo.put("permissions", loginUser.getPermissions());
		} catch (Exception e) {
		}
		return userInfo;

	}

	/**
	 * 单点登录获取uuid（密码）
	 * 
	 * @param userName
	 * @return
	 */
	@GetMapping("/oauth/ssoBeforeLogin")
	public ResponseEntity ssoBeforeLogin(@RequestParam String userName, HttpServletRequest request) {
		try {
			if (StringUtil.isEmpty(userName)) {
				return ResponseEntity.failed("账号不能为空");
			}
			String uuid = UUIDUtils.getGUID32();
			redisTemplate.opsForValue().set(StringUtil.SSO_LOGIN_USER + userName + "_" + uuid, uuid);
			redisTemplate.expire(StringUtil.SSO_LOGIN_USER + userName + "_" + uuid, 60, TimeUnit.SECONDS); // 有效时间1分钟
			return ResponseEntity.succeed(uuid, "请在一分钟内登录系统");
		} catch (Exception e) {
			log.error(e.toString(), e);
			throw new BusinessException(e.getMessage());
		}
	}

	/**
	 * 自定义sso
	 * 
	 * @param params   client_id client_secret username password
	 * @param request
	 * @param response
	 */
	@PostMapping("/oauth/ssoSysLogin")
	public void ssoSysLogin(@RequestParam Map<String, Object> params, HttpServletRequest request,
			HttpServletResponse response) {
		String clientId = request.getParameter("client_id");
		String clientSecret = request.getParameter("client_secret");
		try {
			OAuth2AccessToken oAuth2AccessToken = sysTokenService.ssoSysLogin(clientId, clientSecret, params);
			ResponseUtil.renderJson(response, oAuth2AccessToken);
		} catch (Exception e) {
			Map<String, String> rsp = new HashMap<>();
			rsp.put(CommonConstant.STATUS, HttpStatus.UNAUTHORIZED.value() + "");
			rsp.put("msg", e.getMessage());
			ResponseUtil.renderJsonError(response, rsp, HttpStatus.UNAUTHORIZED.value());
		}
	}

	/**
	 * 授权码模式特例-创建code
	 * @param approvalParameters client_id redirect_uri scope state
	 * @param sessionStatus
	 * @param principal
	 * @return
	 * @author xh
	 * @since 2023-9-11 21:15:01
	 */
	@PostMapping(value = "/auth/v2/authorize")
	public ResponseEntity authorize (@RequestBody Map<String, String> approvalParameters,
											  SessionStatus sessionStatus, Principal principal) {

		Map<String,String> dataMap = new HashMap<>(16);
		try {
			String clientId = String.valueOf(approvalParameters.get("client_id"));
			String redirectUri = String.valueOf(approvalParameters.get("redirect_uri"));
			String scope = String.valueOf(approvalParameters.get("scope"));
			String state = String.valueOf(approvalParameters.get("state"));
			// 支持token不透传模式
			String accessToken = String.valueOf(approvalParameters.get("token"));
			if (StringUtil.isEmpty(clientId)) {
				return ResponseEntity.failedWith(Collections.emptyMap(), UaaConstant.AUTH_CODE_INVALIDATECLIENTID,"缺失client_id");
			}
			if ((principal == null || !(principal instanceof Authentication) ||
					!((Authentication) principal).isAuthenticated()) && StringUtil.isEmpty(accessToken)) {
				return ResponseEntity.failedWith(Collections.emptyMap(), UaaConstant.AUTH_CODE_INVALIDATEUSER,"用户未授权");
			}
			//通过ClientDetailsService获取客户端详细信息-ClientDetails对象,其中，ClientDetailsService对象在抽象类中已经完成了初始化
			//SysClient client = sysClientService.getSysClientByClientId(clientId);
			RedisClientDetailsService clientDetailsService = SpringUtil.getBean(RedisClientDetailsService.class);

			ClientDetails client = null;
			try {
				client = clientDetailsService.loadClientByClientId(clientId);
			} catch (Exception e) {
				return ResponseEntity.failedWith(Collections.emptyMap(), UaaConstant.AUTH_CODE_UNAUTHCLIENT,"应用未认证");
			}
			if (StringUtil.isEmpty(redirectUri) || (client.getRegisteredRedirectUri() == null) || (!client.getRegisteredRedirectUri().contains(redirectUri))) {
				return ResponseEntity.failedWith(Collections.emptyMap(), UaaConstant.AUTH_CODE_ERROR_URL,"redirect_url不正确");
			}
			if (!validateScope(scope, client.getScope())) {
				return ResponseEntity.failedWith(Collections.emptyMap(), UaaConstant.AUTH_CODE_INVALID_SCOPE,"非法的scope");
			}
			Authentication authentication = null;
			if (StringUtil.isEmpty(accessToken)) {
				authentication = SecurityContextHolder.getContext().getAuthentication();
			} else {
				OAuth2Authentication auth2Authentication = tokenStore.readAuthentication(accessToken);
				authentication = auth2Authentication.getUserAuthentication();
			}
			Set<String> scopeSet = new HashSet<String>();
			//to-do
			String[] scopeArray = scope.split(",");
			for (String sc:scopeArray) {
				scopeSet.add(sc);
			}
			Set<String> responseTypeSet = new HashSet<String>();
			responseTypeSet.add("code");
			Map<String, Serializable> extensionProperties = new ConcurrentHashMap<>(16);
			extensionProperties.put("state", approvalParameters.get("state"));
			//  storedRequest = storedRequest.createOAuth2Request(parameters);\
			storedRequest = new OAuth2Request(
					approvalParameters
					, clientId
					, authentication.getAuthorities() //Collection<? extends GrantedAuthority > authorities
					, false // boolean approved
					, scopeSet  // Set<String> scope,
					, null  // Set<String> resourceIds
					, String.valueOf(approvalParameters.get("redirect_uri")) //String redirectUri,
					, null // Set<String> responseTypes,
					, null //Map<String, Serializable > extensionProperties
			);
			OAuth2Authentication combinedAuth = new OAuth2Authentication(storedRequest, authentication);
			String code = redisAuthorizationCodeService.createAuthorizationCode(combinedAuth);
			dataMap.put("sate",state);
			dataMap.put("auth_code",code);
			return ResponseEntity.succeedWith(dataMap, UaaConstant.AUTH_CODE_SUCCESS,"操作成功");
		} catch (Exception e) {
			log.error("获取code失败 >>>>" + e.getMessage());
			return ResponseEntity.failedWith(dataMap, UaaConstant.AUTH_CODE_FAILE, "未知错误，请联系管理员");
		}
	}

	/**
	 * 校验scope
	 * @param requestScopes
	 * @param clientScopes
	 * @return
	 * @author xh
	 * @since 2023-9-12 08:40:04
	 */
	private Boolean validateScope(String requestScopes, Set<String> clientScopes) {
		if (StringUtil.isEmpty(requestScopes)) {
			return Boolean.FALSE;
		}
		String[] reqScope = requestScopes.split(",");
		for (int i = 0; i < reqScope.length; i ++) {
			String scope = reqScope[i];
			if (!clientScopes.contains(scope)) {
				return Boolean.FALSE;
			}
		}
		return Boolean.TRUE;
	}

	/**
	 * 创建token
	 * @param parameters
	 * @return
	 */
	@PostMapping(value = "/auth/v2/token")
	public ResponseEntity createToken (@RequestBody Map<String, String> parameters) {
		try {
			String clientId = MapUtils.getString(parameters,"client_id");
//		String clientSecret = "thirdApplication"; // 前端不传递 后端默认一个值
			String grantType = MapUtils.getString(parameters,"grant_type");
			String code = MapUtils.getString(parameters,"auth_code");
			String sign = MapUtils.getString(parameters,"sign");
			// 校验数据完整性
			if (StringUtil.isEmpty(clientId)){
				return ResponseEntity.failedWith(Collections.emptyMap(), UaaConstant.AUTH_CODE_INVALIDATECLIENTID,"非法的client_id");
			}
			if (StringUtil.isEmpty(grantType) || !"authorization_code".equals(grantType)){
				return ResponseEntity.failedWith(Collections.emptyMap(), UaaConstant.AUTH_CODE_INVALIDATE_GRANTTYPE,"非法的grant_type");
			}
			if (StringUtil.isEmpty(code)){
				return ResponseEntity.failedWith(Collections.emptyMap(), UaaConstant.AUTH_CODE_INVALIDATE_CODE,"非法的auth_code");
			}
			if (StringUtil.isEmpty(sign)){
				return ResponseEntity.failedWith(Collections.emptyMap(), UaaConstant.AUTH_CODE_INVALID_SIGN,"非法的sign");
			}
			DefaultClientDetails sysClient = (DefaultClientDetails) redisTemplate.opsForValue().get(SecurityConstants.CACHE_CLIENT_KEY + ":" + clientId);

			if (sysClient == null) {
				return ResponseEntity.failedWith(Collections.emptyMap(),UaaConstant.AUTH_CODE_UNAUTHCLIENT,"客户端未认证");
			}
			// DefaultClientDetails sysClient = JSONObject.parseObject(clientStr, DefaultClientDetails.class);
			// 验证签名
			String descryptSign = sign;
			String publicKey = sysClient.getPublicKey();
			try {
				if (!StringUtil.isEmpty(publicKey)){
					descryptSign = RsaTools.descrypt(publicKey, sign);
				}
			} catch (Exception e) {
				log.error("sign校验错误>>>>>>" + e.getMessage());
				return ResponseEntity.failedWith(Collections.emptyMap(),UaaConstant.AUTH_CODE_INVALID_SIGN,"非法的签名");
			}
			Map<String,String> newParam = new HashMap<>();
			newParam.put("client_id",clientId);
			newParam.put("grant_type",grantType);
			newParam.put("auth_code",code);
			String sortParams = JSONObject.toJSONString(newParam, SerializerFeature.WriteMapNullValue,SerializerFeature.MapSortField);
			if (!descryptSign.equals(RsaTools.md5(sortParams).toUpperCase())) {
				return ResponseEntity.failedWith(Collections.emptyMap(),UaaConstant.AUTH_CODE_INVALID_SIGN,"非法的签名");
			}
			// 校验数据一致性
			Set<String> registeredRedirectUris = sysClient.getRegisteredRedirectUri();
			if (registeredRedirectUris == null || registeredRedirectUris.size() == 0) {
				return ResponseEntity.failedWith(Collections.emptyMap(),UaaConstant.AUTH_CODE_ERROR_URL,"redirect_url不正确");
			}
			//	OAuth2Authentication storedAuth = this.codeService.consumeAuthorizationCode(code);
			OAuth2Authentication storedAuth = this.redisAuthorizationCodeService.consumeAuthorizationCode(code);
			if (storedAuth == null) {
				return ResponseEntity.failedWith(Collections.emptyMap(),UaaConstant.AUTH_CODE_ERROR_CODE,"auth_code错误或已失效");
			} else {
				OAuth2Request pendingOAuth2Request = storedAuth.getOAuth2Request();
				String redirectUriApprovalParameter = (String)pendingOAuth2Request.getRequestParameters().get("redirect_uri");

				if (!registeredRedirectUris.contains(redirectUriApprovalParameter)) {
					return ResponseEntity.failedWith(Collections.emptyMap(),UaaConstant.AUTH_CODE_ERROR_URL,"redirect_url不正确");
				} else {
					String pendingClientId = pendingOAuth2Request.getClientId();
					if (!clientId.equals(pendingClientId)) {
						return ResponseEntity.failedWith(Collections.emptyMap(),UaaConstant.AUTH_CODE_NOT_MATCH_CODE,"client_id不匹配");
					} else {
						// 创建token
						Map<String, String> combinedParameters = new HashMap(pendingOAuth2Request.getRequestParameters());
						combinedParameters.putAll(parameters);
						OAuth2Request finalStoredOAuth2Request = pendingOAuth2Request.createOAuth2Request(combinedParameters);
						Authentication userAuth = storedAuth.getUserAuthentication();
						OAuth2Authentication oAuth2Authentication = new OAuth2Authentication(finalStoredOAuth2Request, userAuth);
						oAuth2Authentication.setAuthenticated(true);
						// todo
						DefaultTokenServices myTokenService = new CustomTokenServices(securityProperties.getAuth());
						myTokenService.setSupportRefreshToken(true);
						myTokenService.setTokenStore(tokenStore);
						myTokenService.setTokenEnhancer(tokenEnhancer);
						OAuth2AccessToken accessToken = myTokenService.createAccessToken(oAuth2Authentication);
						return ResponseEntity.succeedWith(accessToken,UaaConstant.AUTH_CODE_SUCCESS,"请求成功");
					}
				}
			}
		} catch (Exception e) {
			log.error("获取token失败>>>>>",e);
			return ResponseEntity.failedWith(Collections.emptyMap(),UaaConstant.AUTH_CODE_FAILE,"未知错误，请联系管理员");
		}
	}

	/**
	 * 刷新token
	 * @param params
	 * {"grant_type":"refresh_token","refresh_token":"304f8cb3-d2eb-44e7-9dfc-b57bddf7048f",
	 * "client_id":"testxh","sign":grant_type + refresh_token + client_id md5加密}
	 * @return
	 */
	@PostMapping("/auth/v2/refresh/token")
	public ResponseEntity refreshTokenCode(@RequestBody Map<String, Object> params) {
		Map<String, Object> data = new HashMap<>(); //自定义返回值
		try {
			String grantType = MapUtils.getString(params, "grant_type");
			String refresh_token = MapUtils.getString(params, "refresh_token");
			String clientId = MapUtils.getString(params, "client_id");
//			String scope = MapUtils.getString(params, "scope");
			String sign = MapUtils.getString(params,"sign");
			Set<String> scopeSet = null;
//			if (Tools.notEmpty(scope)) {
//				scopeSet = Arrays.stream(scope.split(",")).collect(Collectors.toSet());
//			}
			// 校验
			if (StringUtil.isEmpty(clientId)){
				return ResponseEntity.failedWith(Collections.emptyMap(), UaaConstant.AUTH_CODE_INVALIDATECLIENTID,"缺失client_id");
			}
			if (StringUtil.isEmpty(grantType) || !"refresh_token".equals(grantType)){
				return ResponseEntity.failedWith(Collections.emptyMap(), UaaConstant.AUTH_CODE_INVALIDATE_GRANTTYPE,"非法的grant_type");
			}
			if (StringUtil.isEmpty(refresh_token)) {
				return ResponseEntity.failedWith(Collections.emptyMap(), UaaConstant.AUTH_CODE_REFRESH_EMPTY, "refresh token缺失");
			}
			if (StringUtil.isEmpty(sign)){
				return ResponseEntity.failedWith(Collections.emptyMap(), UaaConstant.AUTH_CODE_INVALID_SIGN,"非法的sign");
			}
			OAuth2RefreshToken refreshToken = tokenStore.readRefreshToken(refresh_token);
			if (refreshToken == null) {
				return ResponseEntity.failedWith(Collections.emptyMap(), UaaConstant.AUTH_CODE_REFRESH_ILLEGAL, "refresh token非法");
			}
			DefaultClientDetails clientDetails = (DefaultClientDetails) redisTemplate.opsForValue().get(SecurityConstants.CACHE_CLIENT_KEY + ":" + clientId);
			if (clientDetails == null) {
				return ResponseEntity.failedWith(Collections.emptyMap(), UaaConstant.AUTH_CODE_NOT_MATCH_CODE, "client_id非法");
			}
			scopeSet = clientDetails.getScope();
			Map<String, String> map = new HashMap<>();
			map.put("grant_type", grantType);
			map.put("refresh_token", refresh_token);
			TokenRequest tokenRequest = new TokenRequest(map, clientId, scopeSet, grantType);
			OAuth2Authentication oAuth2Authentication = tokenStore.readAuthenticationForRefreshToken(refreshToken);
			String refreshClientId = oAuth2Authentication.getOAuth2Request().getClientId();
			if (!clientId.equals(refreshClientId)) {
				return ResponseEntity.failedWith(Collections.emptyMap(), UaaConstant.AUTH_CODE_NOT_MATCH_CODE, "client_id不匹配");
			}
			String publicKey = clientDetails.getPublicKey();
			if (!StringUtil.isEmpty(publicKey)) {
				try {
					sign = RsaTools.descrypt(publicKey, sign);
				} catch (Exception e) {
					log.error(e.toString(), e);
					log.error("签名校验错误>>>>>>" + e.getMessage());
					return ResponseEntity.failedWith(Collections.emptyMap(), UaaConstant.AUTH_CODE_FAILE, "未知错误，请联系管理员");
				}
			}
			Map<String,String> newParam = new HashMap<>();
			newParam.put("grant_type", grantType);
			newParam.put("refresh_token", refresh_token);
			newParam.put("client_id", clientId);
//			newParam.put("scope", scope);
			String sortParams = JSONObject.toJSONString(newParam, SerializerFeature.WriteMapNullValue, SerializerFeature.MapSortField);
			if (!sign.equals(RsaTools.md5(sortParams).toUpperCase())) {
				return ResponseEntity.failedWith(Collections.emptyMap(),UaaConstant.AUTH_CODE_INVALID_SIGN,"非法的签名");
			}
			//自定义token逻辑 todo
			DefaultTokenServices myTokenService = new CustomTokenServices(securityProperties.getAuth());
			myTokenService.setSupportRefreshToken(true);
			myTokenService.setTokenStore(tokenStore);
			myTokenService.setTokenEnhancer(tokenEnhancer);
			OAuth2AccessToken oAuth2AccessToken = myTokenService.refreshAccessToken(refresh_token, tokenRequest);
			data.put("access_token", oAuth2AccessToken.getValue());
			data.put("token_type", oAuth2AccessToken.getTokenType());
			data.put("refresh_token", oAuth2AccessToken.getRefreshToken().getValue());
			data.put("expires_in", oAuth2AccessToken.getExpiresIn());
			data.put("scope", String.join(",", oAuth2AccessToken.getScope()));
//			LoginAppUser loginAppUser = (LoginAppUser) oAuth2Authentication.getPrincipal();
//			data.put("skin", loginAppUser.getByzd1()); //主题
			oAuth2Authentication.setAuthenticated(true);
			return ResponseEntity.succeedWith(data, UaaConstant.AUTH_CODE_SUCCESS, "操作成功");
		} catch (InvalidTokenException e) {
			log.error(e.toString(), e);
			return ResponseEntity.failedWith(Collections.emptyMap(), UaaConstant.AUTH_CODE_REFRESH_EXPIRED, "refresh token过期");
		} catch (Exception e) {
			log.error(e.toString(), e);
			log.error("create refresh_token error");
			return ResponseEntity.failedWith(Collections.emptyMap(), UaaConstant.AUTH_CODE_FAILE, e.getMessage());
		}
	}

	/**
	 * 根据授权码模式生成的token获取用户信息
	 * @param params
	 * @return
	 */
	@PostMapping("/auth/v2/userInfo")
	public ResponseEntity userInfo(@RequestBody Map<String,Object> params){
		try {
			// 校验参数
			String clientId = MapUtils.getString(params,"client_id");
			String authToken = MapUtils.getString(params,"auth_token");
			String sign = MapUtils.getString(params,"sign");
			if (StringUtil.isEmpty(clientId)){
				return ResponseEntity.failedWith(Collections.emptyMap(), UaaConstant.AUTH_CODE_INVALIDATECLIENTID,"缺失client_id");
			}
			if (StringUtil.isEmpty(authToken) ){
				return ResponseEntity.failedWith(Collections.emptyMap(), UaaConstant.AUTH_CODE_INVALID_TOKEN,"非法的token");
			}
			if (StringUtil.isEmpty(sign)){
				return ResponseEntity.failedWith(Collections.emptyMap(), UaaConstant.AUTH_CODE_INVALID_SIGN,"非法的签名");
			}
			DefaultClientDetails clientDetails = (DefaultClientDetails) redisTemplate.opsForValue().get(SecurityConstants.CACHE_CLIENT_KEY + ":" + clientId);
			if (clientDetails == null){
				return ResponseEntity.failedWith(Collections.emptyMap(), UaaConstant.AUTH_CODE_UNAUTHCLIENT,"客户端未注册");
			}
			// 签名校验
			String descryptSign = sign;
			String publicKey = clientDetails.getPublicKey();
			try {
				if (!StringUtil.isEmpty(publicKey)){
					descryptSign = RsaTools.descrypt(publicKey, sign);
				}
			} catch (Exception e) {
				log.error("sign校验错误>>>>>>" + e.getMessage());
				return ResponseEntity.failedWith(Collections.emptyMap(),UaaConstant.AUTH_CODE_INVALID_SIGN,"非法的签名");
			}
			Map<String,String> newParam = new HashMap<>(2);
			newParam.put("auth_token",authToken);
			newParam.put("client_id",clientId);
			String sortParams = JSONObject.toJSONString(newParam, SerializerFeature.WriteMapNullValue,SerializerFeature.MapSortField);
			if (!descryptSign.equals(RsaTools.md5(sortParams).toUpperCase())) {
				return ResponseEntity.failedWith(Collections.emptyMap(),UaaConstant.AUTH_CODE_INVALID_SIGN,"非法的签名");
			}
			// 获取用户信息
			OAuth2Authentication authentication = tokenStore.readAuthentication(authToken);
			if (authentication == null) {
				return ResponseEntity.failedWith(Collections.emptyMap(),UaaConstant.AUTH_CODE_INVALID_TOKEN,"非法的token");
			}
			LoginAppUser principal = (LoginAppUser) authentication.getUserAuthentication().getPrincipal();
			Map<String,Object> data = new HashMap<>();
			Set<String> clientScope = authentication.getOAuth2Request().getScope();
			Map<String, String> requestParameters = authentication.getOAuth2Request().getRequestParameters();
			String requestScope = requestParameters.get("scope");
			if (!clientScope.contains(requestScope)) {
				return ResponseEntity.failedWith(Collections.emptyMap(),UaaConstant.AUTH_CODE_INVALID_SCOPE,"非法的scope");
			} else {
				// 根据scope获取不同用户信息
				switch (requestScope) {
					// 单点登录模式 extend_user_info
					case UaaConstant.SCOPE_EXTEND_USER_INFO:
						data.put("userName",principal.getUsername());
						data.put("name",principal.getUsername());
						data.put("phone",principal.getMobile());
						// data.put("checkCode",principal.getCheckCode());
						break;
					// base_user_info
					default:
						data.put("userName",principal.getUsername());
						data.put("name",principal.getUsername());
						data.put("phone",principal.getMobile());
						break;
				}
			}
			// token续约 -todo
			OAuth2AccessToken accessToken = tokenStore.readAccessToken(authToken);
			tokenStore.readAuthentication(accessToken);
			return ResponseEntity.succeedWith(data,UaaConstant.AUTH_CODE_SUCCESS,"请求成功");
		} catch (Exception e) {
			log.error("获取用户信息异常>>>>>>>", e);
			return ResponseEntity.failedWith(Collections.emptyMap(),UaaConstant.AUTH_CODE_FAILE,"未知错误，请联系管理员");
		}
	}

	/**
	 * 获取秘钥对
	 * @return
	 * @throws BusinessException
	 * @author xh
	 * @since 2023-9-12 09:54:29
	 */
	@GetMapping(value = "/auth/v2/getKeyPair")
	public ResponseEntity getKeyPair () throws BusinessException {
		Map dataMap = new HashMap(16);
		try {
			String[] arr = ConfigTools.genKeyPair(512);
			String privateKey = arr[0];
			String publicKey = arr[1];
			dataMap.put("privateKey",privateKey);
			dataMap.put("publicKey",publicKey);
		} catch ( Exception e ) {
			log.error("生成秘钥对失败," + e.getMessage());
			return ResponseEntity.failed(dataMap, "操作失败");
		}
		return ResponseEntity.succeed(dataMap, "操作成功");
	}

}
