package com.yudean.itc.interceptor;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import org.apache.log4j.Logger;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import com.yudean.itc.annotation.CUDTarget;
import com.yudean.itc.annotation.Operator;
import com.yudean.itc.dto.AbstractDTO;
import com.yudean.itc.dto.DataSecuredDTO;
import com.yudean.itc.dto.sec.SecureUser;
import com.yudean.itc.exception.sec.AuthorizationException;
import com.yudean.itc.util.DateHelper;

/**
 * 对Manager Bean进行安全检查的拦截器，拦截器将检查其中的用户信息及是否可访问该接口
 * <br/>如果需要启用该拦截器，@Secured注解应当使用与接口的具体实现上，而@Operator等参数的注解应当使用于接口的声明中
 * 
 * @author yushujiang
 * 
 */
@Aspect
@Component
public class SecurityIntercepter extends SpringIntercepter{

	private static final Logger log = Logger.getLogger(SecurityIntercepter.class);
	
	//@Pointcut("execution(* com.yudean.itc.manager..*Manager.*(..))")
	@Pointcut("@annotation(com.yudean.itc.annotation.Secured)")
	public void validateSecurityInfo(){}
	
	@Around(value = "validateSecurityInfo()") 
	public Object around(ProceedingJoinPoint joinPoint) throws Throwable{
		log.debug("-- before interceptor triggered --");
		
		SecureUser operator = null;
		AbstractDTO target = null;
		Object[] args = joinPoint.getArgs();
		
		Method method = this.getJoinPointMethod(joinPoint);		
		final Annotation[][] parameterAnnotations = method.getParameterAnnotations();
		
		for (int i = 0; i < parameterAnnotations.length; i++) {			

            final Annotation[] annotations = parameterAnnotations[i];
            for (final Annotation annotation : annotations) {
            	//获取操作者信息
                if (annotation instanceof Operator) {
                	operator = (SecureUser) args[i];
                }
                
    			//获取CUD Entity
    			if (annotation instanceof CUDTarget) {
    				target = (AbstractDTO) args[i];
    			}
            }
			
		}
		
		//是否提供操作者信息
		if(operator == null || !operator.isValid()){	
			String errorMsg = "[FATAL ERROR] 缺少@Operator或用户信息无效:"
					+ method;
			log.error(errorMsg);
			throw new AuthorizationException(errorMsg);
		}else{
			//TODO 2015-10-13 By YU 将操作者信息写入线程变量
			
		}
		
		if(target != null){
			log.debug("-- automatically set time stamp");
			target.setUpdatedBy(operator.getId());
			target.setUpdateTime(DateHelper.now());
			if(target instanceof DataSecuredDTO){
				log.debug("-- automatically set site stamp");
				DataSecuredDTO dsd = (DataSecuredDTO) target;
				dsd.setSiteId(operator.getCurrentSite());
			}
		}
		
		
		
		//TODO 访问权限检查
		
		return joinPoint.proceed(args);

	}

	
	
}
