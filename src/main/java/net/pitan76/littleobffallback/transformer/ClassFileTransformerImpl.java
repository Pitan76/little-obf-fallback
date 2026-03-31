package net.pitan76.littleobffallback.transformer;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;

import java.lang.instrument.ClassFileTransformer;
import java.security.ProtectionDomain;

public class ClassFileTransformerImpl implements ClassFileTransformer {
    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) {
        if (className == null
                || className.startsWith("net/pitan76/littleobffallback/")
                || className.startsWith("net/minecraft/")
        ) {
            return null;
        }
//                System.out.println("[LittleObfFallback] Transforming: " + className);
        try {
            ClassReader cr = new ClassReader(classfileBuffer);
            ClassWriter cw = new ClassWriter(cr, 0);
            ClassVisitor cv = new ClassFallbackVisitor(Opcodes.ASM9, cw, className);
            cr.accept(cv, 0);
            return cw.toByteArray();
        } catch (Throwable t) {
            System.err.println("[LittleObfFallback] Transform failed: " + className + " : " + t);
            return null;
        }
    }
}
