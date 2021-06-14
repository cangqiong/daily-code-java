package com.chason.base.relearning.jvmti.instrument.hellodemo;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;
import java.time.LocalDateTime;

public class HelloTransformer implements ClassFileTransformer {

    /**
     * @param loader              定义要转换的类加载器
     * @param className           类的全限定名,以 / 作为分隔符
     * @param classBeingRedefined 如果是被重定义或重转换的类，则为重定义或重转换的类，否则为null
     * @param protectionDomain    要定义或重定义的类的保护欲
     * @param classfileBuffer     类文件格式的输入字节缓冲区（不得修改）
     * @return
     * @throws IllegalClassFormatException
     */
    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined,
                            ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
        if ("com/chason/base/relearning/jvmti/instrument/hellodemo/Dog".equals(className)) {
            System.out.println("transform class-----" + className);
            System.out.println("Dog's method invoke at\t" + LocalDateTime.now());
        }
        return null;
    }
}
