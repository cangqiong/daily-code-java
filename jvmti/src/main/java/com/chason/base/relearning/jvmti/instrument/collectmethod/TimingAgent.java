package com.chason.base.relearning.jvmti.instrument.collectmethod;

import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.matcher.ElementMatchers;
import net.bytebuddy.utility.JavaModule;

import java.lang.instrument.Instrumentation;
import java.util.ArrayList;
import java.util.List;

public class TimingAgent {

    private static List<Class> collectClassList = new ArrayList<>();
    private static List<String> collectClassNameList = new ArrayList<>();

    static {
        collectClassList.add(Normal.class);
        for (Class clazz : collectClassList) {
            collectClassNameList.add(clazz.getName());
        }
    }

    public static void premain(String agentArgs, Instrumentation inst) {

        AgentBuilder.Transformer transformer = new AgentBuilder.Transformer() {
            @Override
            public DynamicType.Builder<?> transform(DynamicType.Builder<?> builder, TypeDescription typeDescription,
                                                    ClassLoader classLoader, JavaModule javaModule) {
                return builder
                        .method(ElementMatchers.<MethodDescription>any()) // 拦截任意方法
                        .intercept(MethodDelegation.to(TimeInterceptor.class)); // 委托
            }
        };
        new AgentBuilder
                .Default()
                .type(ElementMatchers.nameStartsWith("com.chason.base.relearning.jvmti.instrument")) // 指定需要拦截的类
                .transform(transformer)
                .installOn(inst);
    }
}
