package com.yudean.mvc.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import com.yudean.mvc.bean.branch.BranchMethodData;
import com.yudean.mvc.util.FrameworkServiceBranchUtil;

/**
 * 定义在@service 包上的切面处理类
 * @author kChen
 *
 */
@Aspect
@Component 
public class MvcServiceAop {
	/**
	 * 定义在系统service层core包里的Arroung切面。
	 * @param pjp
	 * @return
	 * @throws Throwable
	 */
	@Around("execution(* com.timss.*.service.core.*.*(..))")
	public Object serviceAround(ProceedingJoinPoint pjp) throws Throwable {
		try {
			Object retVal = null;
			Class<?> targetClass = pjp.getTarget().getClass();//获取当前被调用的class对象，反射
			Object[] args = pjp.getArgs();//获取传递参数
			Signature sign = pjp.getSignature();//获取方法签名
			String methodName = sign.getName();//获取方法名称
			BranchMethodData _data = FrameworkServiceBranchUtil.ServiceBranchMethodCheck(targetClass, args, methodName);//调用分支版本处理类处理
			if(_data.isHasBranch){
				retVal = FrameworkServiceBranchUtil.ServiceBranchMethodDual(_data);
			}else{
				retVal = pjp.proceed();
			}
			return retVal;
		} catch (Exception e) {
			throw e;
		}
	}
}
