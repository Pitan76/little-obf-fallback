package net.pitan76.littleobffallback.asm;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.commons.ClassRemapper;

import java.lang.instrument.ClassFileTransformer;
import java.security.ProtectionDomain;

public class LittleObfFallbackTransformer implements ClassFileTransformer {

    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) {
        if (className == null) return null;

        // システムクラスやライブラリ群は変換対象から除外して高速化
        if (className.startsWith("java/") || className.startsWith("sun/") || 
            className.startsWith("jdk/") || className.startsWith("org/objectweb/asm/") ||
            className.startsWith("net/minecraft/") || className.startsWith("net/pitan76/littleobffallback/") ||
            className.startsWith("com/google/") || className.startsWith("org/apache/") ||
            className.startsWith("org/spongepowered/") || className.startsWith("org/slf4j/") ||
            className.startsWith("org/log4j/") || className.startsWith("org/jetbrains/") ||
            className.startsWith("org/gradle/") || className.startsWith("org/fabricmc/") ||
            className.startsWith("net/pitan76/mcpitanlib/")) {
            return null;
        }

        try {
            ClassReader reader = new ClassReader(classfileBuffer);
            ClassWriter writer = new ClassWriter(reader, ClassWriter.COMPUTE_MAXS);

            LittleObfFallbackRemapper remapper = new LittleObfFallbackRemapper();

            ClassRemapper classRemapper = new ClassRemapper(writer, remapper);
            reader.accept(classRemapper, 0);

            // 変換があった場合のみ、新しいバイトコードを返す
            if (remapper.isChanged) {
                return writer.toByteArray();
            }

            return null; // 変更なしの場合はnullを返すと、元のバイトコードがそのまま使われる
        } catch (Throwable t) {
            System.err.println("[LittleObfFallback] Failed to transform class: " + className);
            t.printStackTrace();
            return null;
        }
    }
}
