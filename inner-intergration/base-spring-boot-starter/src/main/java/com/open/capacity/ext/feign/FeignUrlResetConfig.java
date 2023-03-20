package com.open.capacity.ext.feign;

import lombok.Data;

@Data
public class FeignUrlResetConfig {
    private String type;
    private String value;
    private String newurl;
}
