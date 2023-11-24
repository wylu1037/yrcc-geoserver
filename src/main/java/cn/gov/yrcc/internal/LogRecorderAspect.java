package cn.gov.yrcc.internal;

import cn.gov.yrcc.utils.json.JsonUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;

@Slf4j
@Aspect
@Component
public class LogRecorderAspect {

    /**
     * 日志样式
     */
    private static class LogPattern {
        public static final String ENTRANCE = "【%s】 %s() called with: %s";
        public static final String RET = "【%s】 %s() execute end, return result: %s";
        public static final String RET_EMPTY = "【%s】 %s() execute end";
        public static final String DURATION = "【%s】 %s() consume time: %s ms";
    }

    /**
     * 定义切点
     *
     * <p>*-任意类型返回值</p>
     * <p>com.zkjg.baas.admin-匹配包名</p>
     * <p>..-当前包及其子包</p>
     * <p>*-类名</p>
     * <p>.*(..)..-任意方法名任意参数</p>
     * <p>@Pointcut("execution(* com.zkjg.baas.user.service..*.*(..))")</p>
     */
    @Pointcut("execution(* cn.gov.yrcc.app.module.*.service.impl..*.*(..))")
    private void recorderPointcut() {
        // empty method body
    }

    @Around("recorderPointcut()")
    @SuppressWarnings("all")
    public Object aroundAdvice(ProceedingJoinPoint joinPoint) throws Throwable {
        var watcher = new StopWatch();
        watcher.start();

        var paramMap = new HashMap<String, Object>();
        var signature = joinPoint.getSignature();
        var methodSignature = (MethodSignature) signature;
        var paramNames = methodSignature.getParameterNames();
        var paramValues = joinPoint.getArgs();

        for (var i = 0; i < paramNames.length; i++) {
            var value = paramValues[i];
            if (value instanceof HttpServletRequest || value instanceof HttpServletResponse) {
                continue;
            }

            if (value instanceof MultipartFile file) {
                value = file.getOriginalFilename();
                paramMap.put(paramNames[i], file.getOriginalFilename());
            }
            paramMap.put(paramNames[i], value);
        }

        var clazzName = joinPoint.getTarget().getClass().getName();
        var methodName = joinPoint.getSignature().getName();

        log.info(">>>>>>>>>>>> Start");
        log.info(String.format(LogPattern.ENTRANCE, clazzName, methodName, JsonUtils.toJsonString(paramMap)));
        var result = joinPoint.proceed();
        if (result == null) {
            log.info(String.format(LogPattern.RET_EMPTY, clazzName, methodName));
        } else {
            log.info(String.format(LogPattern.RET, clazzName, methodName, JsonUtils.toJsonString(result)));
        }
        watcher.stop();
        log.info(String.format(LogPattern.DURATION, clazzName, methodName, watcher.getTotalTimeMillis()));
        log.info(">>>>>>>>>>>>> End");
        return result;
    }
}
