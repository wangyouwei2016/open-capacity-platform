package com.open.capacity.common.constant;

/**
 * UaaConstant
 * @author xh
 * @date 2023-9-12 08:34:08
 */
public class UaaConstant {

    // 单点登录模式
    public static final String SCOPE_BASE_USER_INFO = "base_user_info";
    public static final String SCOPE_EXTEND_USER_INFO = "extend_user_info";


    /**
     * code
     */
    public static final String ACCESS_CODE = "access_code::";
    public static final Integer AUTH_CODE_SUCCESS = 20000; // 请求成功
    public static final Integer AUTH_CODE_INVALIDATECLIENTID = 20001; // 缺失client_id
    public static final Integer AUTH_CODE_INVALIDATEUSER = 20002; // 用户未授权
    public static final Integer AUTH_CODE_UNAUTHCLIENT = 20003; // 应用未注册
    public static final Integer AUTH_CODE_ERROR_URL = 20004; // redirect_url不正确
    public static final Integer AUTH_CODE_INVALID_SCOPE = 20005; // 非法的scope
    public static final Integer AUTH_CODE_INVALID_TOKEN = 20006; // 非法的token
    public static final Integer AUTH_CODE_INVALID_SIGN = 20007; // 非法的签名
    public static final Integer AUTH_CODE_INVALID_PARAMETER = 20008; // 参数错误
    public static final Integer AUTH_CODE_FAILE = 20009; // 未知错误，请联系管理员
    public static final Integer AUTH_CODE_REFRESH_ERROR = 20010; // 刷新token异常
    public static final Integer AUTH_CODE_REFRESH_EXPIRED = 20011; // refreshToken过期
    public static final Integer AUTH_CODE_INVALIDATE_GRANTTYPE = 20012; // 非法的grant_type
    public static final Integer AUTH_CODE_INVALIDATE_CODE = 20013; // 非法的code
    public static final Integer AUTH_CODE_ERROR_CODE = 20014; // code错误或失效
    public static final Integer AUTH_CODE_NOT_MATCH_CODE = 20015; // client_id不匹配
    public static final Integer AUTH_CODE_REFRESH_EMPTY = 20016; // refresh token缺失
    public static final Integer AUTH_CODE_REFRESH_ILLEGAL = 20017; // refresh token非法
}
