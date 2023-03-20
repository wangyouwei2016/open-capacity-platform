package com.open.capacity.common.core.crypto;

public interface ReqEncVo {
    /**
     * 请求参数
     * @return
     */
    Long findReqTime();
    void putReqTime(Long reqTime);

    /**
     * 请求序列号
     * @return
     */
    String findReqId();
    void putReqId(String reqId);

    /**
     * 请求来源，可以为发送请求平台
     * @return
     */
    String findReqSource();
    void putReqSource(String reqSource);

    /**
     * 对称加密数据密钥
     * @return
     */
    String findEncDataKey();
    void putEncDataKey(String encDataKey);

    /**
     * 请求数据
     * @return
     */
    String findEncReqData();
    void putEncReqData(String encReqData);

    /**
     * 请求md5签名
     * @return
     */
    String findReqSign();
    void putReqSign(String reqSign);
}