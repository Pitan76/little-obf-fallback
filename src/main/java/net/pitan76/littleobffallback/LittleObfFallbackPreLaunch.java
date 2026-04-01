package net.pitan76.littleobffallback;

import net.bytebuddy.agent.ByteBuddyAgent;
import net.fabricmc.loader.api.entrypoint.PreLaunchEntrypoint;
import net.pitan76.littleobffallback.asm.LittleObfFallbackTransformer2;

import java.lang.instrument.Instrumentation;

public class LittleObfFallbackPreLaunch implements PreLaunchEntrypoint {
    @Override
    public void onPreLaunch() {
        Instrumentation inst = ByteBuddyAgent.install();
        System.out.println("[LittleObfFallback] PreLaunch: Instrumentation attached");
        inst.addTransformer(new LittleObfFallbackTransformer2());
        System.out.println("[LittleObfFallback] Global ASM Transformer injected successfully.");
    }

//        System.out.println("[LittleObfFallback] PreLaunch: Adding ClassTinkerers transformation");
//
//        MappingRegistry.CLASS_MAP.forEach((from, to) -> {
//            ClassNode node = new ClassNode();
//            node.version = Opcodes.V17;
//            node.access = Opcodes.ACC_PUBLIC;
//            node.name = from;
//            node.superName = to;
//
//            MethodNode constructor = new MethodNode(Opcodes.ACC_PUBLIC, "<init>", "()V", null, null);
//            constructor.instructions.add(new VarInsnNode(Opcodes.ALOAD, 0));
//            constructor.instructions.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, to, "<init>", "()V", false));
//            constructor.instructions.add(new InsnNode(Opcodes.RETURN));
//            node.methods.add(constructor);
//
//            MappingRegistry.METHOD_MAP.forEach((key, mojName) -> {
//                // keyは "owner#name" または "owner#nameDesc" の形式
//                if (key.startsWith(from + "#")) {
//                    String originalName = key.split("#")[1];
//                    String desc = "";
//
//                    if (originalName.contains("(")) {
//                        desc = originalName.substring(originalName.indexOf("("));
//                        originalName = originalName.substring(0, originalName.indexOf("("));
//                    } else {
//                        return;
//                    }
//
//                    MethodNode bridge = new MethodNode(Opcodes.ACC_PUBLIC, originalName, desc, null, null);
//                    int slot = 1;
//                    bridge.instructions.add(new VarInsnNode(Opcodes.ALOAD, 0)); // this
////
//                    bridge.instructions.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, to, mojName, desc, false));
//
//                    bridge.instructions.add(new InsnNode(getReturnOpcode(desc)));
//                    node.methods.add(bridge);
//                }
//            });
//
//            ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
//            node.accept(writer);
//            ClassTinkerers.define(from.replace('/', '.'), writer.toByteArray());
//        });
//
//        ClassTinkerers.addTransformation("", new LittleObfFallbackTransformer());
//        System.out.println("[LittleObfFallback] PreLaunch: ClassTinkerers transformation added successfully");
//    }
//
//    public static int getReturnOpcode(String desc) {
//        Type returnType = Type.getReturnType(desc);
//        return returnType.getOpcode(Opcodes.IRETURN);
//    }
}