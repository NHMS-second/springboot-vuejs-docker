package com.github.sumuzhou;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.rememberme.TokenBasedRememberMeServices;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	@Autowired
	private UserDetailsService userDetailsService;
	@Autowired
	private PasswordEncoder passwordEncoder;

	private static CookieCsrfTokenRepository csrfTokenRepository = CookieCsrfTokenRepository.withHttpOnlyFalse();
	private static String REMEMBER_ME_KEY = "_REMEMBER_ME";

	@Autowired
	public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder);
	}

	@Configuration
	@Order(8)
	public static class ApiLoginWebSecurityConfigurationAdapter extends WebSecurityConfigurerAdapter {
		@Override
		protected void configure(HttpSecurity http) throws Exception {
			http
				.antMatcher("/api/v1/login").authorizeRequests().anyRequest().permitAll()
				.and().csrf().csrfTokenRepository(csrfTokenRepository).disable();
		}
	}
	@Configuration
	@Order(9)
	public static class ApiLogoutWebSecurityConfigurationAdapter extends WebSecurityConfigurerAdapter {
		@Override
		protected void configure(HttpSecurity http) throws Exception {
			http
				.antMatcher("/api/v1/logout").authorizeRequests().anyRequest().authenticated()
				.and().logout().logoutUrl("/api/v1/logout")
					.logoutSuccessHandler((req, res, auth) -> res.setStatus(HttpServletResponse.SC_OK))
					.deleteCookies("SESSIONID", "remember-me", "XSRF-TOKEN")
				.and().csrf().csrfTokenRepository(csrfTokenRepository);
		}
	}
	@Configuration
	@Order(10)
	public static class UserApiWebSecurityConfigurationAdapter extends WebSecurityConfigurerAdapter {
		@Autowired
		private TokenBasedRememberMeServices rememberMeServices;
		@Override
		protected void configure(HttpSecurity http) throws Exception {
			http
				.antMatcher("/api/v1/user/**").authorizeRequests().anyRequest().hasRole("USER")
				.and().rememberMe().rememberMeServices(rememberMeServices).key(REMEMBER_ME_KEY)
					.tokenValiditySeconds(24 * 60 * 60)
				.and().httpBasic().disable().csrf().csrfTokenRepository(csrfTokenRepository);
		}
	}
	@Configuration
	@Order(15)
	public static class OpenApiWebSecurityConfigurationAdapter extends WebSecurityConfigurerAdapter {
		@Override
		protected void configure(HttpSecurity http) throws Exception {
			http
				.antMatcher("/api/v1/open/**").authorizeRequests().anyRequest().permitAll()
				.and().csrf().disable();
		}
	}
	@Configuration
	@Order(30)
	public static class OperatorWebSecurityConfigurationAdapter extends WebSecurityConfigurerAdapter {
		@Override
		protected void configure(HttpSecurity http) throws Exception {
			http
				.authorizeRequests().anyRequest().authenticated()
				.and().formLogin().loginPage("/signin")
				.and().httpBasic()
				.and().csrf().csrfTokenRepository(csrfTokenRepository);
		}
	}

	@Bean
	public AuthenticationManager authenticationProvider() throws Exception {
		return this.authenticationManagerBean();
	}

	@Bean
    public TokenBasedRememberMeServices rememberMeServices() {
    	return new TokenBasedRememberMeServices(REMEMBER_ME_KEY, userDetailsService);
    }

	@Bean
	public CookieCsrfTokenRepository csrfTokenRepository() {
		return new CookieCsrfTokenRepository();
	}

}
