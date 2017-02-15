package com.yudean.mvc.aop;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONObject;

import org.apache.log4j.Logger;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.yudean.itc.annotation.AopNone;
import com.yudean.itc.annotation.ReturnEnumsBind;
import com.yudean.itc.annotation.VaildParam;
import com.yudean.itc.annotation.ValidFormToken;
import com.yudean.itc.bean.exception.ExceptionData;
import com.yudean.mvc.bean.handler.ThreadLocalVariable;
import com.yudean.mvc.exception.ExceptionFramework;
import com.yudean.mvc.exception.MvcRuntimeException;
import com.yudean.mvc.exception.RuntimeEnumNoFoundException;
import com.yudean.mvc.handler.InitThreadHandler;
import com.yudean.mvc.handler.ThreadLocalHandler;
import com.yudean.mvc.service.impl.CacheMethodAnnotationService;
import com.yudean.mvc.service.impl.FrameWorkServiceImpl;
import com.yudean.mvc.util.ExceptionUtil;
import com.yudean.mvc.util.FormTokenUtil;
import com.yudean.mvc.util.ReflectUtil;
import com.yudean.mvc.web.FrameworkController;

/**
 * 定义在@controller 包上的切面处理类,由于使用了spring-servlet的容器，采用scheme定义。改切面的定义是在spring-mvc中
 * 
 * @author kChen
 */
public class ControllAop {
    private static final Logger log = Logger.getLogger( ControllAop.class );
    @Autowired
    ExceptionFramework excep;

    @Autowired
    FrameworkController frameworkController;

    @Autowired
    CacheMethodAnnotationService cacheMethodAnnotationService;

    /**
     * 定义在系统controller包里的Arround切面。
     * 由于使用了spring(cglib或JAVAproxy)的切面，在获取service类时，从spring容器获取，
     * 切面包含了返回参数处理和系统容错框架处理。 容错框架在拦截所有异常时，会尝试将所有的异常以指定的格式返回到前端。
     * 
     * @param pjp
     * @return
     * @throws Throwable
     */
    public Object controllerArround(ProceedingJoinPoint pjp) throws Throwable {
        Object retVal = null;
        Method method = null;
        AopNone aopNone = null;
        VaildParam vaildParam = null;
        Boolean initMethodSuccess = false;
        try {
            // 获取切面信息，如果获取过程中出现异常，则用容错框架处理并返回异常
            Class<?> targetClass = pjp.getTarget().getClass();// 获取目标类
            Signature sign = pjp.getSignature();// 获取方法签名
            method = ReflectUtil.getReflectMethod( sign.getName(), pjp.getArgs(), targetClass );// 探查方法、过滤重载
            aopNone = method.getAnnotation( AopNone.class );// 提起绑定注解
            vaildParam = method.getAnnotation( VaildParam.class );
            ThreadLocalVariable threadLocalVariable = ThreadLocalHandler.getVariable();
            if ( null != vaildParam ) {
                ThreadLocalHandler.getVariable().setThreadLocalAttribute(
                        ThreadLocalVariable.GlobalVarableScopeType.Frame_Annotation_VaildParam.toString(), vaildParam );
            }
            Exception runE = threadLocalVariable.getMvcRunException();
            if ( null != runE ) {
                throw runE;
            }
            initMethodSuccess = true;
        } catch (Exception e) {
            ExceptionData exceptionData = null;
            if ( null != method ) {
                exceptionData = caseException( e, method );
            } else {
                exceptionData = excep.TimssRunException( e );
            }
            InitThreadHandler.initExceptionData( exceptionData );
            retVal = getRetInstance( method );
            return retVal;
        }
        if ( initMethodSuccess ) {
            if ( null != aopNone ) {
                retVal = pjp.proceed();
            } else {
                try {
                    validFormToken( method );
                    retVal = cacheMethodAnnotationService.doCache( method, pjp );
                    caseEnumList( method );// 反向绑定枚举变量
                    // created by yuanzh 2015-8-6
                    getDynamicFormFieldAttr();// 加入动态表单绑定枚举变量
                    buildFormToken( method );
                } catch (Exception e) {
                    ExceptionData exceptionData = caseException( e, method );
                    InitThreadHandler.initExceptionData( exceptionData );
                    if ( null == retVal ) {// 如果返回参数为空，尝试模拟构造一个controll的返回参数
                        retVal = getRetInstance( method );
                    }
                }
            }
        }
        return retVal;

    }

    /**
     * 模拟构造返回参数
     * 
     * @description:
     * @author: kChen
     * @createDate: 2014-11-27
     * @param method
     * @return:
     */
    Object getRetInstance(Method method) {
        Object retVal = null;
        try {
            Constructor<?>[] ConstructorList = method.getReturnType().getConstructors();// 获取构造器列表
            if ( null != ConstructorList && 0 < ConstructorList.length ) {
                for ( Constructor<?> constructor : ConstructorList ) {
                    Class<?>[] paramClass = constructor.getParameterTypes();
                    if ( null == paramClass || paramClass.length < 1 ) {
                        retVal = constructor.newInstance();// 使用无参数构造器进行参数构造
                        break;
                    }
                }
            }
        } catch (Exception ex) {
            log.warn( "异常框架模拟构造数据异常", ex );
        }
        return retVal;
    }

    /**
     * 异常处理方法，容错框架的处理接口
     * 
     * @description: 容错处理的切入口，只有到业务模块跑出到@Controller层之后才能被这里的容错框架获取。
     *               容错框架主要处理2个事，1.识别异常类型。2根据异常类型和当前的请求类型返回指定数据或页面。
     * @author: kChen
     * @createDate: 2014-7-21
     * @param e
     * @param pjp
     * @return
     * @throws Throwable :
     */
    private ExceptionData caseException(Exception e, Method method) throws Throwable {
        return ExceptionUtil.caseException( e, method, excep );
    }

    /**
     * 枚举变量的注释处理方法
     * 
     * @description:枚举变量的注释放置在@Controller切面被拦截，经过注解反射，如果变量中加入了@ReturnEnumsBind注释，则会根据注释值获取全局枚举变量，并将其添加到线程全局变量中，并在jsp容器中生成前端可识别的JavaScript对象
     * @author: kChen
     * @createDate: 2014-7-21
     * @param pjp
     * @throws Throwable :
     */
    private void caseEnumList(Method method) throws Throwable {
        ReturnEnumsBind annotation = method.getAnnotation( ReturnEnumsBind.class );
        if ( null != annotation ) {
            String annValue = annotation.value();
            Boolean isEachError = new Boolean( Boolean.FALSE );
            JSONObject enumsObj = frameworkController.enumParam( annValue, isEachError );
            if ( null != annotation && ReturnEnumsBind.NULLMODE.EachException.equals( annotation.nullMode() ) ) {
                // 当查询不到单个枚举变量时，抛出
                throw new RuntimeEnumNoFoundException( "当前站点枚举变量,某个值不存在！" );
            }
            if ( (enumsObj == null || enumsObj.isEmpty()) && null != annotation
                    && ReturnEnumsBind.NULLMODE.Exception.equals( annotation.nullMode() ) ) {
                // 当所有枚举变量都不存在时，抛出
                throw new RuntimeEnumNoFoundException( "当前站点枚举变量：" + annValue + "不存在！" );
            }
            // 将数据添加到线程变量中
            ThreadLocalHandler.getVariable().setThreadLocalAttribute(
                    ThreadLocalVariable.GlobalVarableScopeType.Frame_Enums_Type.toString(), enumsObj.toString() );
        }
    }

    /**
     * 生成表单令牌
     * 
     * @param method
     * @author 890157
     * @throws Throwable
     */
    private void buildFormToken(Method method) {

        ResponseBody annotation = method.getAnnotation( ResponseBody.class );

        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
                .getRequest();// 将request和线程变量隔离开,kchen
        FormTokenUtil.bulidFormToken( request, annotation );
    }

    /**
     * 验证令牌
     * 
     * @param method 被AOP的方法
     */
    private void validFormToken(Method method) {
        ValidFormToken annotation = method.getAnnotation( ValidFormToken.class );
        if ( annotation != null ) {
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
                    .getRequest();// 将request和线程变量隔离开,kchen
            if ( !FormTokenUtil.validFormToken( request ) ) {
                throw new MvcRuntimeException( "token over time or error!" );
            }
        }
    }

    /**
     * @description: 获取页面对应的字段属性
     * @author: 890166
     * @createDate: 2015-8-6:
     */
    @SuppressWarnings("unchecked")
    private void getDynamicFormFieldAttr() throws Throwable {
        try {

            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
                    .getRequest();// 将request和线程变量隔离开,kchen
            // Object formIdObj = request.getParameter( "formId" );
            // Object pageCodeObj = request.getParameter( "pageCode" );

            // if ( null != formIdObj && null != pageCodeObj ) {
            // 查询动态表单中是否存在字段是枚举的
            Object[] args = null;
            Object obj = FrameWorkServiceImpl.getBean( "BDynamicFormFieldAttrServiceImpl" );
            Method method = ReflectUtil.getReflectMethod( "queryFieldAttrEnumByGroup", args, obj.getClass() );
            Object reObj = method.invoke( obj, args );
            List<String> list = (List<String>) reObj;

            if ( null != list && !list.isEmpty() ) {
                // 若有字段是枚举的话就获取枚举信息
                String enumCatArr = "";
                for ( String str : list ) {
                    enumCatArr += str.replace( "\"", "" ) + ",";
                }
                enumCatArr = enumCatArr.substring( 0, enumCatArr.length() - 1 );
                Boolean isEachError = new Boolean( Boolean.FALSE );
                JSONObject enumsObj = frameworkController.enumParam( enumCatArr, isEachError );

                // 获取原来已经设置的枚举变量
                Object enumAnnoObj = ThreadLocalHandler.getVariable().getThreadLocalAttribute(
                        ThreadLocalVariable.GlobalVarableScopeType.Frame_Enums_Type.toString() );
                if ( null != enumAnnoObj ) {
                    JSONObject enumAnno = JSONObject.fromObject( enumAnnoObj );
                    enumsObj.putAll( enumAnno );
                }

                if ( !enumsObj.isEmpty() ) {
                    // 将数据添加到线程变量中
                    ThreadLocalHandler.getVariable()
                            .setThreadLocalAttribute(
                                    ThreadLocalVariable.GlobalVarableScopeType.Frame_Enums_Type.toString(),
                                    enumsObj.toString() );
                }
            }
            // }

        } catch (Exception e) {
            log.info( "----------------- ControllAop.getDynamicFormFieldAttr ----------------- bean : BDynamicFormFieldAttrServiceImpl no found!!"
                    + e.getMessage() );
        }
    }
}
