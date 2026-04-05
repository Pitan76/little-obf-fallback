package net.pitan76.littleobffallback;

import net.bytebuddy.agent.ByteBuddyAgent;
import net.fabricmc.loader.api.entrypoint.PreLaunchEntrypoint;
import net.pitan76.littleobffallback.asm.LittleObfFallbackTransformer2;

import java.lang.instrument.Instrumentation;

public class LittleObfFallbackPreLaunch implements PreLaunchEntrypoint {
    @Override
    public void onPreLaunch() {
        // MCPitanLibを前提とするmodをリストにする
//        List<ModContainer> targetMods = FabricLoader.getInstance().getAllMods().stream()
//                .filter(mod -> mod.getMetadata().getDependencies().stream().anyMatch(
//                        depend -> depend.getModId().equals("mcpitanlib"))).toList();
//
//        targetMods.forEach(container -> System.out.println("[LittleObfFallback] Detected mod: " + container.getMetadata().getId()));
//
//        List<String> targetPackages = targetMods.stream().map(mod -> {
//            for (EntrypointContainer<Object> container : FabricLoader.getInstance().getEntrypointContainers("main", Object.class)) {
//                if (container.getProvider().getMetadata().getId().equals(mod.getMetadata().getId())) {
//                    String definition = container.getDefinition().replace('.', '/');;
//
//                    // class名を取り除く
//                    String packageName = definition.contains("/") ? definition.substring(0, definition.lastIndexOf('/')) : definition;
//
//                    // エントリーポイントのパッケージ名から、"/fabric", "/forge", "/neoforge"で終わる部分を削除
//                    if (packageName.endsWith("/fabric") || packageName.endsWith("/forge") || packageName.endsWith("/neoforge")) {
//                        packageName = packageName.substring(0, packageName.lastIndexOf('/'));
//                    }
//
////                    System.out.printf("[LittleObfFallback] Target mod: %s, Entry point class: %s%n", mod.getMetadata().getId(), packageName);
//                    return packageName + "/";
//                }
//            }
//            return "";
//        }).toList();

        Config.init();
        if (!Config.isEnabled()) return;

        Instrumentation inst = ByteBuddyAgent.install();
        // 時間を計測してみる
        int startTime = (int) System.currentTimeMillis();
        System.out.println("[LittleObfFallback] PreLaunch: Instrumentation attached");
        inst.addTransformer(new LittleObfFallbackTransformer2(Config.getTargetPackages()));
        int endTime = (int) System.currentTimeMillis();
        System.out.println("[LittleObfFallback] Global ASM Transformer injected successfully. Convert time: " + (endTime - startTime) + "ms.");
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