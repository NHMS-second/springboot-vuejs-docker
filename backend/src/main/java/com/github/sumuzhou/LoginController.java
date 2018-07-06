package com.github.sumuzhou;

import java.security.Principal;
import java.util.Objects;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.security.web.authentication.rememberme.TokenBasedRememberMeServices;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Strings;

@RestController
@RequestMapping(value = "/api/v1")
public class LoginController {
	
	private static final Logger LOG = LoggerFactory.getLogger(LoginController.class);
	
	@Autowired
	private TokenBasedRememberMeServices rememberMeService;
	@Autowired
	private AuthenticationManager authenticationManager;
	
	@RequestMapping(value = "/login", method = RequestMethod.POST)
	public Either forumLogin(@RequestBody String json, HttpServletRequest request, HttpServletResponse response) {
		try {
			JSONObject obj = JSON.parseObject(json);
			Authentication authentication = setAuthentication(
					obj.getString("j_username"), obj.getString("j_passwd"), request);
			if (obj.getBooleanValue("remember-me")) 
				rememberMeService.onLoginSuccess(request, response, authentication);
			return Either.of(Boolean.TRUE);
		} catch (Exception e) {
			LOG.error("登录失败", e);
			throw e;
		}
	}
	
	@RequestMapping(value = "/open/isLogin", method = RequestMethod.GET)
	public Either isLogin(Principal principal, HttpServletRequest request, HttpServletResponse response) {
		Boolean isLogin = Boolean.FALSE;
		if (Objects.nonNull(principal) && !Strings.isNullOrEmpty(principal.getName())
				|| Objects.nonNull(rememberMeService.autoLogin(request, response))) 
			isLogin = Boolean.TRUE;
		return Either.of(isLogin);
	}
	
	private Authentication setAuthentication(String principal, String credential, HttpServletRequest request) {
		try {
	        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
	        		principal, credential);
	        token.setDetails(new WebAuthenticationDetails(request));
	        Authentication authentication = authenticationManager.authenticate(token);
	        LOG.debug("Login as {}", JSON.toJSONString(authentication.getPrincipal()));
	        SecurityContextHolder.getContext().setAuthentication(authentication);
	        return authentication;
	    } catch (Exception e) {
	        SecurityContextHolder.getContext().setAuthentication(null);
	        LOG.error("Failure in login", e);
	        throw e;
	    }
	}
	
}