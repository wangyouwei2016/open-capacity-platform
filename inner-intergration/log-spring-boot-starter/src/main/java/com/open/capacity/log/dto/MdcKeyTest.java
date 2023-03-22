package com.open.capacity.log.dto;

/**
 * @program: open-capacity-platform
 * @author: GuoGaoJu
 * @create: 2023-02-16
 **/
public class MdcKeyTest {
    public final static String TRACE_ID_KEY = "apm-traceid";	//只有被采样的数据才有apm-traceId
    public final static String GTRACE_ID_KEY = "apm-gtraceid";	//所有包括未采样的调用链都有apm-gtraceId
    public final static String SPAN_ID_KEY = "apm-spanid";
}