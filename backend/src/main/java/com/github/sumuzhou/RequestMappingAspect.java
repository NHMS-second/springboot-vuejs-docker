package com.github.sumuzhou;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class RequestMappingAspect {

	private static final Logger LOG = LoggerFactory.getLogger(RequestMappingAspect.class);

	@Around("@annotation(org.springframework.web.bind.annotation.RequestMapping)")
	public Object aroundRequestMapping(ProceedingJoinPoint joinPoint) throws Throwable {
		try {
			long start = System.nanoTime();
			Object obj = joinPoint.proceed();
			LOG.debug("!!PERFORMANCE-PROFILING: method {} took {} ns!!",
					joinPoint.getSignature().toString(), System.nanoTime() - start);
			return obj;
		} catch (RuntimeException e) {
			return handleError(e);
		}
	}

	private Either handleError(RuntimeException e) throws Throwable {
		LOG.error("Caught error: ", e);
		return Either.of(e);
	}

}
