package com.yudean.itc.interceptor;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.text.MessageFormat;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.ibatis.annotations.Param;
import org.apache.log4j.Logger;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.yudean.itc.annotation.Secured;
import com.yudean.itc.dao.sec.DataFilterMapper;
import com.yudean.itc.dto.sec.DataFilterRule;
import com.yudean.itc.util.ApplicationConfig;

@Aspect
@Component
public class MapperInterceptor {
	private static final Logger log = Logger.getLogger(MapperInterceptor.class);
	
	
	@Autowired
	private DataFilterMapper dataFilterMapper;
	
	@Pointcut("execution(* com.yudean.itc.dao..*Mapper.*(..))||execution(* com.timss.*.dao..*Mapper.*(..))")	
	public void updateDataFilter() {
	}
	
	@Around(value = "updateDataFilter()") 
	public Object around(ProceedingJoinPoint joinPoint) throws Throwable{

		MethodSignature joinPointObject = (MethodSignature) joinPoint.getSignature();   
		Method method = joinPointObject.getMethod();
		Object[] args = joinPoint.getArgs();
		
		Annotation methodAnno = method.getAnnotation(Secured.class);
		if(methodAnno != null){
			log.debug("** Mapper secured intercepter triggered");
			Secured securedAnno = (Secured) methodAnno;
			String functionId = securedAnno.functionId();	
			if(StringUtils.isEmpty(functionId))
					throw new IllegalArgumentException("Mapper使用@secured时必须提供functionId");

			Object[] filterBy = null;
			int filterArgNum = -1;
			Boolean filterParamDeclared = false;
			
			final Annotation[][] parameterAnnotations = method.getParameterAnnotations();
			for (int i = 0; i < parameterAnnotations.length; i++) {			

	            final Annotation[] annotations = parameterAnnotations[i];
	            for (final Annotation annotation : annotations) {  
	                //识别Filter参数，准备替换传入的参数
	                if (annotation instanceof Param) {
	                	Param p = (Param) annotation;
	                	if(ApplicationConfig.PARAM_FILTER.equalsIgnoreCase(p.value())){
	                		filterParamDeclared = true;
	                		filterBy = (Object[]) args[i];
		                	filterArgNum = i;
	                	}	                	
	                }	
	                
	            }				
			}	
	
			if (filterParamDeclared == false)				
				throw new IllegalArgumentException("Mapper使用@secured的方法必须包含@Param('FILTER')的参数");
			else{
				if(filterBy == null){
					log.debug("@Param('FILTER')未赋值，除非当前调用者为SA，否则应当赋值");
				}else{
					// 生成过滤语句
					String filter = assembleFilter(functionId, filterBy);
					// 使用过滤替换参数
					args[filterArgNum] = new String[] { filter };
				}
			}
		}else{
			//@Secured与@Param("FILTER")必须成对使用
			final Annotation[][] parameterAnnotations = method.getParameterAnnotations();
			for (int i = 0; i < parameterAnnotations.length; i++) {		

	            final Annotation[] annotations = parameterAnnotations[i];
	            for (final Annotation annotation : annotations) {  
	                if (annotation instanceof Param) {
	                	Param p = (Param) annotation;
	                	if(ApplicationConfig.PARAM_FILTER.equalsIgnoreCase(p.value())){
	                		throw new IllegalArgumentException("使用@Param('FILTER')参数的方法必须使用@Secured标注");
	                	}	                	
	                }	
	                
	            }				
			}	
		}

		//更改参数后必须使用proceed(args)才能生效
		return joinPoint.proceed(args);
		
	}

	

	private String assembleFilter(String functionId, Object[] filterBy) {

		List<DataFilterRule> rules = dataFilterMapper.selectFilterByFunction(functionId);
		
		if(rules == null || rules.isEmpty())
			log.error("方法指定的FunctionID尚未配置对应的数据权限: " + functionId);
		
		StringBuilder sb = new StringBuilder();
		for(DataFilterRule rule : rules){
			String formula = rule.getFormula();
			
			//TODO 使用MessageFormat还是使用Map
			//使用MessageFormat的时候要替换掉单引号
			formula = StringUtils.replace(formula, "'", "''");
			String exp = MessageFormat.format(formula, filterBy);
			sb.append(exp);
		}
		
		String statement = sb.toString();
		return statement;
	}
	
	
}
