package com.chason.base.relearning.jvmti.instrument.replacedemo;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;

public class CatTransformer implements ClassFileTransformer {

    public static byte[] getBytesFromFile(String fileName) {

        try {
            File file = new File(fileName);
            InputStream is = new FileInputStream(file);
            long length = file.length();
            byte[] bytes = new byte[(int) length];

            // read int bytes
            int offset = 0;
            int numRead = 0;
            while (offset < bytes.length
                    && (numRead = is.read(bytes, offset, bytes.length - offset)) >= 0) {
                offset += numRead;
            }
            if (offset < bytes.length) {
                throw new IOException("Can not read completely read file" + file.getName());
            }
            is.close();
            return bytes;
        } catch (IOException e) {
            System.out.println("error occurs in _ClassTransformer!"
                    + e.getClass().getName());
            return null;
        }
    }

    /**
     *
     * @param loader 定义要转换的类加载器
     * @param className 类的全限定名
     * @param classBeingRedefined 如果是被重定义或重转换的类，则为重定义或重转换的类，否则为null
     * @param protectionDomain 要定义或重定义的类的保护欲
     * @param classfileBuffer 类文件格式的输入字节缓冲区（不得修改）
     * @return
     * @throws IllegalClassFormatException
     */
    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined,
                            ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
        System.out.println("transform class-----" + className);
        if (!"com/chason/base/relearning/jvmti/instrument/replacedemo/Cat".equals(className)) {
            return null;
        }
        // 替换成新的dog类
        return getBytesFromFile("D:/Cat.class");
    }
}
