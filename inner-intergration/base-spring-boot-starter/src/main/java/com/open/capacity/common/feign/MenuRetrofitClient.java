package com.open.capacity.common.feign;

import java.util.List;

import org.springframework.cloud.square.retrofit.core.RetrofitClient;

import com.open.capacity.common.constant.ServiceNameConstants;
import com.open.capacity.common.model.SysMenu;

import reactor.core.publisher.Flux;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
@RetrofitClient(ServiceNameConstants.USER_SERVICE)
public interface MenuRetrofitClient {

	/**
	 * 角色菜单列表
	 * @param roleCodes
	 */
	@GET(value = "/menus/{roleCodes}")
	Flux<List<SysMenu>> findByRoleCodes(@Path("roleCodes") String roleCodes);
	
}

