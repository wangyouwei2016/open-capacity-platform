package com.open.capacity.uaa.common.store;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.approval.ApprovalStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;

import com.open.capacity.common.feign.AsycUserService;
import com.open.capacity.common.model.LoginAppUser;

import lombok.SneakyThrows;

@SuppressWarnings("all")		
public class CustomerJwtTokenStore extends JwtTokenStore{
	
	private AsycUserService asycUserService;
	
	public CustomerJwtTokenStore(JwtAccessTokenConverter jwtTokenEnhancer, AsycUserService  asycUserService) {
		super(jwtTokenEnhancer);
		this.asycUserService=asycUserService;
	}

	@Override
	public void setApprovalStore(ApprovalStore approvalStore) {
		super.setApprovalStore(approvalStore);
	}

	@Override
	@SneakyThrows
	public OAuth2Authentication readAuthentication(OAuth2AccessToken token) {
		
	   OAuth2Authentication oauth2Authentication = super.readAuthentication(token) ;
 
	   LoginAppUser loginAppUser = asycUserService.findByUserName(oauth2Authentication.getUserAuthentication().getName()).get();
		 
	   UsernamePasswordAuthenticationToken userAuth = new UsernamePasswordAuthenticationToken(loginAppUser, "N/A", oauth2Authentication.getAuthorities());
		
	   return new OAuth2Authentication(oauth2Authentication.getOAuth2Request(), userAuth);
	}

 
	
}
