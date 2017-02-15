package com.yudean.mvc.handler;

import org.apache.commons.logging.Log;  
import org.apache.commons.logging.LogFactory;  
import org.aspectj.lang.ProceedingJoinPoint;  
import org.aspectj.lang.annotation.Around;  
import org.aspectj.lang.annotation.Aspect;  
import org.aspectj.lang.reflect.MethodSignature;  
import org.springframework.stereotype.Component;

import com.yudean.mvc.bean.logstash.Logstash;
import com.yudean.mvc.bean.userinfo.UserInfo;
import com.yudean.mvc.handler.ThreadLocalHandler;
import com.yudean.mvc.util.LogUtil;

/**
 * 在方法执行前后加上切面，统计方法耗时、访问人信息。
 * @author 890151
 * @date 2016-10-11
 */

@Aspect
@Component
public class TimeInterceptor {

    private static Log logger = LogFactory.getLog(TimeInterceptor.class);

    public static final String POINT = "execution(* com.timss.*.service.*.*(..))";

    /**
     * 统计方法执行耗时Around环绕通知
     * @param joinPoint
     * @param action 
     * @return
     * @throws Throwable 
     */
    @Around(POINT)
    public Object timeAround(ProceedingJoinPoint joinPoint) throws Throwable{
        // 定义返回对象、得到方法需要的参数
        Object obj = null;
        Object[] args = joinPoint.getArgs();
        long startTime = System.currentTimeMillis();
        long initm = Runtime.getRuntime().freeMemory();
        
        obj = joinPoint.proceed(args);
        
        long endm = Runtime.getRuntime().freeMemory();
        long diffMemory = initm - endm;

        // 获取执行的方法名
        long endTime = System.currentTimeMillis();
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        //获取模块名
        //如：com_timss_inventory_service_invmatapplyservice  com.yudean.mvc.service.ItcMvcService
        String declaringTypeName = signature.getDeclaringTypeName();
        String moduleWholeName = "";
        String moduleShortName = "";
        if( declaringTypeName.lastIndexOf(".")!=-1 ){
        	moduleShortName = declaringTypeName.substring(declaringTypeName.lastIndexOf(".")+1);
            moduleWholeName = declaringTypeName.substring(0,declaringTypeName.lastIndexOf("."));
        }
        //获取方法名
        String methodName = signature.getName();
        // 打印耗时的信息
        long diffTime = endTime - startTime;
        //获取用户信息
        UserInfo userInfo = null;
        if( ThreadLocalHandler.getVariable()!=null){
    		userInfo = ThreadLocalHandler.getVariable().getUserInfoScope();
        }
        //为logstash收集日志提供参数，统计方法耗时和访问人信息
        Logstash logstash = new Logstash();
        logstash.setModule(moduleWholeName);
        logstash.setMethodName(moduleShortName + "." + methodName);
        logstash.setDiffTime(diffTime);
        logstash.setDiffMemory(diffMemory);
        logstash.setUserDefineInfo("TimeInterceptor");
        if( userInfo != null ){
        	logstash.setUserId(userInfo.getUserId());
            logstash.setSiteId(userInfo.getSiteId());
        }
    	LogUtil.info(logstash);
        return obj;
    }

}