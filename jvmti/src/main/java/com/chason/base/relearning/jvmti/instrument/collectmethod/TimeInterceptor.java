package com.chason.base.relearning.jvmti.instrument.collectmethod;

import net.bytebuddy.implementation.bind.annotation.Origin;
import net.bytebuddy.implementation.bind.annotation.RuntimeType;
import net.bytebuddy.implementation.bind.annotation.SuperCall;

import java.lang.reflect.Method;
import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.Callable;

public class TimeInterceptor {
    @RuntimeType
    public static Object intercept(@Origin Method method,
                                   @SuperCall Callable<?> callable) throws Exception {
        Instant start = Instant.now();
        try {
            // 原有函数执行
            return callable.call();
        } finally {
            Instant end = Instant.now();
            Duration duration = Duration.between(start, end);
            System.out.println(method + " run at:" + start + " to:" + end + " : took " + (duration.toMillis()) + "ms");
        }
    }
}
