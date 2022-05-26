package noob.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;

import java.util.Arrays;
import java.util.Objects;
import java.util.StringJoiner;
import java.util.stream.Collectors;

/**
 * 使用 AOP 对 Controller 进行日志记录
 */
@Slf4j
public class LogAspect {

    public Object invoke(ProceedingJoinPoint joinPoint) throws Throwable {
        final long beginTime = System.currentTimeMillis();
        try {
            final Object result = joinPoint.proceed();
            recordLog(joinPoint, System.currentTimeMillis() - beginTime, null);
            return result;
        }catch (Exception e) {
            recordLog(joinPoint, System.currentTimeMillis() - beginTime, e);
            throw e;
        }
    }

    private void recordLog(ProceedingJoinPoint joinPoint, long time, Exception e) {
        final StringJoiner joiner = new StringJoiner("\n");
        final MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        joiner.add("控制器日志记录：");
        joiner.add("==================== log start ====================");
        joiner.add("class:" + joinPoint.getTarget().getClass().getName());
        joiner.add("method:" + signature.getName());
        joiner.add("params:" + Arrays.stream(joinPoint.getArgs()).map(item -> item == null ? "null" : item.getClass().getName()).collect(Collectors.toList()));
        joiner.add("execute time : " + time + " ms");
        if (Objects.nonNull(e)) {
            joiner.add("Exception ==> " + e.getMessage());
        }
        joiner.add("==================== log end ====================\n");
        if (Objects.nonNull(e)) {
            log.error(joiner.toString());
        }else {
            log.debug(joiner.toString());
        }
    }
}
