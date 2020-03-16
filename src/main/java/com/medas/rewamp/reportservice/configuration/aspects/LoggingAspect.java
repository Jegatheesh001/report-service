package com.medas.rewamp.reportservice.configuration.aspects;

import java.util.Arrays;
import java.util.Collection;

import javax.servlet.http.HttpServletRequest;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.medas.rewamp.reportservice.business.vo.ApiResponse;

import lombok.extern.slf4j.Slf4j;

/**
 * This Aspect will log request and response and total process time
 * 
 * @author Jegatheesh <br>
 *         <b>Created</b> On Jan 11, 2020
 *
 */

@Slf4j
@Component
@Aspect
public class LoggingAspect {
	
	@Autowired
	private HttpServletRequest request;

	@Pointcut("@annotation(Loggable)")
	public void executeLogging() {

	}

	@Around("executeLogging()")
	public Object logMethodCall(ProceedingJoinPoint joinPoint) throws Throwable {
		// Setting Basic Details
		StringBuilder logInfo = new StringBuilder();
		logInfo.append("Url: ").append(request.getRequestURI());
		logInfo.append(" | Class: ").append(joinPoint.getSignature().getDeclaringType().getName());
		logInfo.append(" | Method: ").append(joinPoint.getSignature().getName());
		// Setting Request Parameters
		Object[] args = joinPoint.getArgs();
		if (args != null && args.length > 0) {
			logInfo.append(" | Requests ");
			Arrays.asList(args).forEach(arg -> logInfo.append("[").append(arg).append("]"));
		}
		// Setting Total Time of Execution
		Object returnValue = null;
		long startTime = System.currentTimeMillis();
		try {
			returnValue = joinPoint.proceed();
		} catch (Throwable e) {
			logInfo.append(" | Total time ").append(System.currentTimeMillis() - startTime).append("ms ")
					.append(" | Exception: ").append(e.getMessage());
			log.info(logInfo.toString());
			throw e;
		}
		logInfo.append(" | Total time ").append(System.currentTimeMillis() - startTime).append("ms ");
		// Setting Response
		setResponseToLog(logInfo, returnValue);
		log.info(logInfo.toString());
		return returnValue;
	}

	/**
	 * Setting Response details to log
	 * 
	 * @param logInfo
	 * @param returnValue
	 */
	private void setResponseToLog(StringBuilder logInfo, Object returnValue) {
		if (returnValue != null) {
			logInfo.append(" | Response ");
			if (returnValue instanceof ApiResponse) {
				// API response status
				logInfo.append("- " + ((ApiResponse<?>) returnValue).isSuccess() + " ");
				// API response data
				Object data = ((ApiResponse<?>) returnValue).getData();
				if (data != null) {
					if (data instanceof Collection) {
						logInfo.append("[" + ((Collection<?>) data).size() + " records]");
					} else {
						logInfo.append("[" + data + "]");
					}
				}
			} else if (returnValue instanceof Collection) {
				logInfo.append("[" + ((Collection<?>) returnValue).size() + " records]");
			} else {
				logInfo.append("[" + returnValue + "]");
			}
		}
	}

}
