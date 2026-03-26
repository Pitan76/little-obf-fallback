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
                        !className.startsWith("net/pitan76/smallstairs/")
//                    className.startsWith("java/") ||
//                    className.startsWith("jdk/") ||
//                    className.startsWith("sun/") ||
//                    className.startsWith("com/sun/") ||
//                    className.startsWith("com/ibm/") ||
//                    className.startsWith("net/minecraft/") ||
//                    className.startsWith("com/mojang/") ||
//                    className.startsWith("net/fabricmc/") ||
//                    className.startsWith("com/llamalad7/") ||
//                    className.startsWith("org/spongepowered/asm/") ||
//                    className.startsWith("org/objectweb/asm/") ||
//                    className.startsWith("net/bytebuddy/") ||
//                    className.startsWith("net/pitan76/littleobffallback/") ||
//                    className.startsWith("net/pitan76/mcpitanlib/") ||
//                    className.startsWith("joptsimple/") ||
//                    className.startsWith("org/apache/") ||
//                    className.startsWith("com/google/") ||
//                    className.startsWith("org/slf4j/") ||
//                    className.startsWith("org/lwjgl/")
                ) {
                    return null;
                }

                ClassReader cr = new ClassReader(classfileBuffer);
//                ClassWriter cw = new ClassWriter(cr, 0);

                ClassWriter cw = new ClassWriter(cr, ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES) {
                    @Override
                    protected String getCommonSuperClass(String type1, String type2) {
                        return "java/lang/Object";
                    }
                };

                ClassVisitor cv = new ClassVisitor(Opcodes.ASM9, cw) {

                    @Override
                    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
                        // class_2246 -> Blocks
                        if (interfaces != null) {
                            for (int i = 0; i < interfaces.length; i++) {
                                if (interfaces[i].equals("net/minecraft/class_2246")) {
                                    interfaces[i] = "net/minecraft/world/level/block/Blocks";
                                }
                            }
                        }
                        super.visit(version, access, name, signature, superName, interfaces);
                    }

                    @Override
                    public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
                        MethodVisitor mv = super.visitMethod(access, name, descriptor, signature, exceptions);
                        return new MethodVisitor(Opcodes.ASM9, mv) {

                            @Override
                            public void visitMethodInsn(int opcode, String owner, String name, String descriptor, boolean isInterface) {

                                // class_2246 -> Blocks
                                if (owner.equals("net/minecraft/class_2246"))
                                    owner = "net/minecraft/world/level/block/Blocks";

                                // MCPitanLib BlockState.of(class_2248)
                                if (owner.equals("net/pitan76/mcpitanlib/midohra/block/BlockState") && name.equals("of")) {
                                    if (descriptor.contains("Lnet/minecraft/class_2248;")) {
                                        String fixedDescriptor = descriptor.replace("Lnet/minecraft/class_2248;"
                                                , "Lnet/minecraft/world/level/block/Block;");

                                        super.visitTypeInsn(Opcodes.CHECKCAST, "net/minecraft/world/level/block/Block");
                                        super.visitMethodInsn(opcode, owner, name, fixedDescriptor, isInterface);
                                        System.out.println("[LittleObfFallback] ASM Patched Method Descriptor: " + owner + "." + name + " -> " + fixedDescriptor);
                                        return;
                                    }
                                }


                                // MCPitanLib CompatibleBlockSettings.copy()
                                if (owner.equals("net/pitan76/mcpitanlib/api/block/v2/CompatibleBlockSettings") && name.equals("copy")) {
                                    if (descriptor.contains("Lnet/minecraft/class_4970;")) {
                                        // Replace the obfuscated class reference with the real class reference in the method descriptor
                                        String fixedDescriptor = descriptor.replace(
                                                "Lnet/minecraft/class_4970;",
                                                "Lnet/minecraft/world/level/block/state/BlockBehaviour;"
                                        );

                                        // Force cast class_4970 to BlockBehaviour when it's used as a method argument or return type
                                        super.visitTypeInsn(Opcodes.CHECKCAST, "net/minecraft/world/level/block/state/BlockBehaviour");
                                        super.visitMethodInsn(opcode, owner, name, fixedDescriptor, isInterface);

                                        System.out.println("[LittleObfFallback] ASM Patched Method Descriptor: " + owner + "." + name + " -> " + fixedDescriptor);
                                        return;
                                    }
                                }

                                if (descriptor.contains("Lnet/minecraft/world/level/block/state/BlockBehaviour$Properties;)") ||
                                        descriptor.contains("Lnet/minecraft/class_2251;)")) {

                                    boolean isShapeMethod = name.equals("getShape") || name.equals("getOutlineShape") || name.equals("getCollisionShape");
                                    boolean isInternalCall = owner.startsWith("net/pitan76/mcpitanlib/") || owner.startsWith("net/minecraft/");

                                    if (!isShapeMethod && !isInternalCall) {
                                        super.visitMethodInsn(Opcodes.INVOKESTATIC,
                                                "net/pitan76/littleobffallback/LittleObfFallbackPreLaunch",
                                                "unwrapSettings",
                                                "(Ljava/lang/Object;)Lnet/minecraft/world/level/block/state/BlockBehaviour$Properties;",
                                                false);
                                    }
                                }

                                super.visitMethodInsn(opcode, owner, name, descriptor, isInterface);
                            }

                            @Override
                            public void visitFieldInsn(int opcode, String owner, String fieldName, String desc) {

                                // class_2246 -> Blocks
                                if (owner.equals("net/minecraft/class_2246"))
                                    owner = "net/minecraft/world/level/block/Blocks";

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

                                // class_2246 (Blocks)
                                if (opcode == Opcodes.GETSTATIC && owner.equals("net/minecraft/world/level/block/Blocks")) {

                                    String realBlock = switch (fieldName) {
                                        case "field_10124" -> "AIR";
                                        case "field_10340" -> "STONE";
                                        case "field_10474" -> "GRANITE";
                                        case "field_10508" -> "DIORITE";
                                        case "field_10115" -> "ANDESITE";
                                        case "field_10219" -> "GRASS_BLOCK";
                                        case "field_10566" -> "DIRT";
                                        case "field_10253" -> "COARSE_DIRT";
                                        case "field_10520" -> "PODZOL";
                                        case "field_10445" -> "COBBLESTONE";
                                        case "field_10161" -> "OAK_PLANKS";
                                        case "field_9975" -> "SPRUCE_PLANKS";
                                        case "field_10148" -> "BIRCH_PLANKS";
                                        case "field_10334" -> "JUNGLE_PLANKS";
                                        case "field_10218" -> "ACACIA_PLANKS";
                                        case "field_42751" -> "CHERRY_PLANKS";
                                        case "field_10075" -> "DARK_OAK_PLANKS";
                                        case "field_54734" -> "PALE_OAK_WOOD";
                                        case "field_54735" -> "PALE_OAK_PLANKS";
                                        case "field_37577" -> "MANGROVE_PLANKS";
                                        case "field_40294" -> "BAMBOO_PLANKS";
                                        case "field_22126" -> "CRIMSON_PLANKS";
                                        case "field_22127" -> "WARPED_PLANKS";
                                        case "field_10033" -> "GLASS";
                                        case "field_9979" -> "SANDSTONE";
                                        case "field_10344" -> "RED_SANDSTONE";
                                        case "field_10104" -> "BRICKS";
                                        case "field_10056" -> "STONE_BRICKS";
                                        case "field_10266" -> "NETHER_BRICKS";
                                        case "field_9986" -> "RED_NETHER_BRICKS";
                                        case "field_10462" -> "END_STONE_BRICKS";
                                        case "field_10360" -> "SMOOTH_STONE";
                                        case "field_10467" -> "SMOOTH_SANDSTONE";
                                        case "field_10483" -> "SMOOTH_RED_SANDSTONE";
                                        case "field_9978" -> "SMOOTH_QUARTZ";
                                        case "field_10490" -> "YELLOW_WOOL";
                                        case "field_10028" -> "LIME_WOOL";
                                        case "field_10459" -> "PINK_WOOL";
                                        case "field_10423" -> "GRAY_WOOL";
                                        case "field_10222" -> "LIGHT_GRAY_WOOL";
                                        case "field_10619" -> "CYAN_WOOL";
                                        case "field_10259" -> "PURPLE_WOOL";
                                        case "field_10514" -> "BLUE_WOOL";
                                        case "field_10113" -> "BROWN_WOOL";
                                        case "field_10170" -> "GREEN_WOOL";
                                        case "field_10314" -> "RED_WOOL";
                                        case "field_10146" -> "BLACK_WOOL";
                                        case "field_10153" -> "QUARTZ_BLOCK";
                                        case "field_10286" -> "PURPUR_BLOCK";
                                        default -> null;
                                    };

                                    if (realBlock != null) {
                                        // Replace the obfuscated field access with the real field name
                                        super.visitFieldInsn(Opcodes.GETSTATIC, "net/minecraft/world/level/block/Blocks", realBlock, "Lnet/minecraft/world/level/block/Block;");
                                        super.visitTypeInsn(Opcodes.CHECKCAST, "net/minecraft/class_2248");
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

    public static net.minecraft.world.level.block.state.BlockBehaviour.Properties unwrapSettings(Object obj) {
        // すでに Properties ならそのまま返す
        if (obj instanceof net.minecraft.world.level.block.state.BlockBehaviour.Properties) {
            return (net.minecraft.world.level.block.state.BlockBehaviour.Properties) obj;
        }
        try {
            // CompatibleBlockSettings の .build() をリフレクションで叩いて変換する
            java.lang.reflect.Method m = obj.getClass().getMethod("build");
            return (net.minecraft.world.level.block.state.BlockBehaviour.Properties) m.invoke(obj);
        } catch (Exception e) {
            System.err.println("[LittleObfFallback] Failed to unwrap settings: " + e.getMessage());
            return (net.minecraft.world.level.block.state.BlockBehaviour.Properties) obj;
        }
    }
}