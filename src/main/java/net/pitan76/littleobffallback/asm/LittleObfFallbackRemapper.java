package net.pitan76.littleobffallback.asm;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.commons.Remapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class LittleObfFallbackRemapper extends Remapper {

    public boolean isChanged = false;

    public LittleObfFallbackRemapper() {
        super(Opcodes.ASM9);
    }

    @Override
    public String map(String internalName) {
        if (internalName == null) return null;
        String mapped = MappingRegistry.CLASS_MAP.get(internalName);
        if (mapped != null) {
            isChanged = true;
            return mapped;
        }
        return super.map(internalName);
    }

    @Override
    public String mapFieldName(String owner, String name, String descriptor) {
        if (owner != null && name != null) {
            String key = owner + "#" + name;
            String mapped = MappingRegistry.FIELD_MAP.get(key);
            if (mapped != null) {
                isChanged = true;
                return mapped;
            }
        }
        return super.mapFieldName(owner, name, descriptor);
    }

    @Override
    public String mapMethodName(String owner, String name, String descriptor) {
        if (owner != null && name != null) {
            // ディスクリプタ付きでの完全一致
            if (descriptor != null) {
                String descKey = owner + "#" + name + descriptor;
                String mappedDesc = MappingRegistry.METHOD_MAP.get(descKey);
                if (mappedDesc != null) {
                    isChanged = true;
                    return mappedDesc;
                }
            }

            // メソッド名のみ
            String key = owner + "#" + name;
            String mapped = MappingRegistry.METHOD_MAP.get(key);
            if (mapped != null) {
                isChanged = true;
                return mapped;
            }

            // 親クラスのメソッドもチェック
            String superClass = getSuperClass(owner);
            if (superClass != null) {
                String mappedFromSuper = mapMethodName(superClass, name, descriptor);
                if (mappedFromSuper != null && !mappedFromSuper.equals(name)) {
                    return mappedFromSuper;
                }
            }

            String[] interfaces = getInterfaces(owner);
            if (interfaces != null) {
                for (String iface : interfaces) {
                    String mappedFromInterface = mapMethodName(iface, name, descriptor);
                    if (mappedFromInterface != null && !mappedFromInterface.equals(name)) {
                        return mappedFromInterface;
                    }
                }
            }
        }
        return super.mapMethodName(owner, name, descriptor);
    }

    private final ConcurrentHashMap<String, String> superClassCache = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, String[]> interfaceCache = new ConcurrentHashMap<>();

    private String getSuperClass(String className) {
        return superClassCache.computeIfAbsent(className, key -> {
            try {
                ClassReader classReader = new ClassReader(key);
                SuperClassVisitor visitor = new SuperClassVisitor();
                classReader.accept(visitor, 0);
                return visitor.getSuperClassName();
            } catch (IOException e) {
                return null;
            }
        });
    }

    private String[] getInterfaces(String className) {
        return interfaceCache.computeIfAbsent(className, key -> {
            try {
                ClassReader classReader = new ClassReader(key);
                InterfaceVisitor visitor = new InterfaceVisitor();
                classReader.accept(visitor, 0);
                return visitor.getInterfaces();
            } catch (IOException e) {
                return null;
            }
        });
    }

    // 親クラスを取得するための ClassVisitor
    private static class SuperClassVisitor extends ClassVisitor {
        private String superClassName;

        public SuperClassVisitor() {
            super(Opcodes.ASM9);
        }

        @Override
        public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
            this.superClassName = superName;
        }

        public String getSuperClassName() {
            return superClassName;
        }
    }

    // インターフェースを取得するための ClassVisitor
    private static class InterfaceVisitor extends ClassVisitor {
        private final List<String> interfaces = new ArrayList<>();

        public InterfaceVisitor() {
            super(Opcodes.ASM9);
        }

        @Override
        public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
            if (interfaces != null) {
                this.interfaces.addAll(Arrays.asList(interfaces));
            }
        }

        public String[] getInterfaces() {
            return interfaces.toArray(new String[0]);
        }
    }
}
