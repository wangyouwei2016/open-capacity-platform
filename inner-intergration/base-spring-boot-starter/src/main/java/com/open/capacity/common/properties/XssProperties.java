package com.open.capacity.common.properties;

import com.beust.jcommander.internal.Lists;
import lombok.Data;

import java.util.List;

@Data
public class XssProperties {
    /**
     * 是否开启xss保护
     */
    private Boolean enable = false;

    private List<String> whiteHttpUrls = Lists.newArrayList("/api-user/menus/*") ;
}
