package com.chason.base.relearning.jvmti.instrument.hellodemo;

import java.lang.instrument.Instrumentation;

public class GreetingAgent {

    public static void premain(String agentArgs, Instrumentation inst) {
        System.out.println("premain1 with instrumentation args:" + agentArgs);
        inst.addTransformer(new HelloTransformer());
    }
}
