package com.memopet.memopet.global.config;

import com.memopet.memopet.domain.member.entity.Member;
import com.memopet.memopet.domain.member.entity.MemberSocial;
import com.memopet.memopet.domain.member.service.MemberService;
import com.memopet.memopet.global.common.service.AccessLogRabbitPublisher;
import com.memopet.memopet.global.common.service.ThreadLocalService;
import com.memopet.memopet.global.common.service.UserAgentService;
import com.memopet.memopet.global.common.utils.BusinessUtil;
import com.memopet.memopet.global.common.utils.Utils;
import com.memopet.memopet.global.config.annotation.Authed;
import com.memopet.memopet.global.filter.AccessLogFilter;
import com.memopet.memopet.global.interceptor.LoggingInterceptor;
import jakarta.servlet.FilterRegistration;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.metamodel.mapping.FilterRestrictable;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.MethodParameter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;
import java.util.Optional;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

	private final LoggingInterceptor loggingInterceptor;
	private final MemberService memberService;
	private final BusinessUtil businessUtil;
	private final ThreadLocalService threadLocalService;
	private final AccessLogRabbitPublisher accessLogRabbitPublisher;
	private final UserAgentService userAgentService;

	@Bean
	public FilterRegistrationBean filterBea() {
		final FilterRegistrationBean registrationBean = new FilterRegistrationBean(new AccessLogFilter(businessUtil,threadLocalService,userAgentService,accessLogRabbitPublisher));
		registrationBean.addUrlPatterns("/*");
		return registrationBean;
	}

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		registry
			.addResourceHandler("/resources/**")
			.addResourceLocations("/resources/");
	}

	@Override
	public void addCorsMappings(CorsRegistry registry) {
		registry.addMapping("/**").allowedOrigins("*");
	}

	@Override
	public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
		resolvers.add(memberArgumentResolver());
	}

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(loggingInterceptor)
			.addPathPatterns("/**");
	}

	public HandlerMethodArgumentResolver memberArgumentResolver() {

		return new HandlerMethodArgumentResolver() {

			@Override
			public boolean supportsParameter(MethodParameter parameter) {
				return parameter.hasParameterAnnotation(Authed.class);
			}

			@Override
			public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
										  NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {

				final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
				log.debug("authentication ::: > {}", Utils.toJson(authentication));

				final UserDetails userDetails = (UserDetails) authentication.getPrincipal();
				final String username = userDetails.getUsername();
				final Optional<MemberSocial> memberOptional = memberService.getMemberByEmail(username);

                return memberOptional.orElse(null);

            }
		};
	}
}