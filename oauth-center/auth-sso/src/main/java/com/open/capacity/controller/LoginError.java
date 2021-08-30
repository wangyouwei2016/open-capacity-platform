package com.open.capacity.controller;

import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.google.common.collect.Lists;
import com.open.capacity.common.auth.details.LoginAppUser;
import com.open.capacity.common.util.SysUserUtil;
import com.open.capacity.domain.Foo;

@Controller
public class LoginError {

    @RequestMapping("/login/oauth2/code/custom")
    public String dashboard(Model model) {
        return "index";
    }

    
    @GetMapping("/content")
    public String getFoos(Model model) {
    	List<Foo> list = Lists.newArrayList();
    	Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    	list.add(new Foo("Client-1", "This is first client"));
    	model.addAttribute("foos", list);
    	 return "demo";
    }
    
    @GetMapping("/users")
    public Authentication user(Authentication user) {
        return user;
    }
    
}

