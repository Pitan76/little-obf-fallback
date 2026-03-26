package net.pitan76.littleobffallback;

import net.bytebuddy.agent.ByteBuddyAgent;
import net.fabricmc.loader.api.entrypoint.PreLaunchEntrypoint;
import org.objectweb.asm.*;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.Instrumentation;
import java.security.ProtectionDomain;

public class LittleObfFallbackPreLaunch implements PreLaunchEntrypoint {

    @Override
    public void onPreLaunch() {
        // Attach a dynamic agent to the running JVM
        Instrumentation inst = ByteBuddyAgent.install();

        inst.addTransformer(new ClassFileTransformer() {
            @Override
            public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) {
                // mod class only
                if (className == null ||
                        className.startsWith("java/") ||
                        className.startsWith("net/minecraft/") ||
                        className.startsWith("net/fabricmc/") ||
                        className.startsWith("org/spongepowered/asm/"))
                    return null;

                ClassReader cr = new ClassReader(classfileBuffer);
                ClassWriter cw = new ClassWriter(cr, 0);
                ClassVisitor cv = new ClassVisitor(Opcodes.ASM9, cw) {
                    @Override
                    public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
                        MethodVisitor mv = super.visitMethod(access, name, descriptor, signature, exceptions);
                        return new MethodVisitor(Opcodes.ASM9, mv) {
                            @Override
                            public void visitFieldInsn(int opcode, String owner, String fieldName, String desc) {
                                // class_2350 (Direction)
                                if (opcode == Opcodes.GETSTATIC && owner.equals("net/minecraft/class_2350")) {
                                    String realDir = switch (fieldName) {
                                        case "field_11043" -> "NORTH";
                                        case "field_11035" -> "SOUTH";
                                        case "field_11034" -> "EAST";
                                        case "field_11039" -> "WEST";
                                        case "field_11036" -> "UP";
                                        case "field_11033" -> "DOWN";
                                        default -> null;
                                    };
                                    if (realDir != null) {
                                        // Replace the obfuscated field access with the real field name
                                        super.visitFieldInsn(Opcodes.GETSTATIC, "net/minecraft/core/Direction", realDir, "Lnet/minecraft/core/Direction;");
                                        return;
                                    }
                                }
                                super.visitFieldInsn(opcode, owner, fieldName, desc);
                            }
                        };
                    }
                };

                cr.accept(cv, 0);
                return cw.toByteArray(); // return the modified class bytes
            }
        });

        System.out.println("[LittleObfFallback] Global ASM Transformer injected successfully.");
    }
}