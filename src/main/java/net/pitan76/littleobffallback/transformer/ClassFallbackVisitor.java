package net.pitan76.littleobffallback.transformer;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;

import java.util.Objects;

public class ClassFallbackVisitor extends ClassVisitor {
    private final String className;

    public ClassFallbackVisitor(int api, ClassVisitor classVisitor, String className) {
        super(api, classVisitor);
        this.className = className;
    }

    @Override
    public FieldVisitor visitField(int access, String name, String descriptor, String signature, Object value) {
        String newDesc = descriptor;
        String newSig = signature;
        if (descriptor != null && (descriptor.contains("class_") || descriptor.contains("field_") || descriptor.contains("method_"))) {
            newDesc = remapType(descriptor);
        }
        if (signature != null && signature.contains("class_")) {
            newSig = remapType(signature);
        }
        if (!java.util.Objects.equals(descriptor, newDesc)) {
            System.out.println("[LittleObfFallback] visitField(def): " + name + " : " + descriptor + " => " + newDesc);
        }
        return super.visitField(access, name, newDesc, newSig, value);
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
        String newDesc = descriptor;
        String newSig = signature;
        if (descriptor != null && descriptor.contains("class_")) {
            newDesc = remapType(descriptor);
        }
        if (signature != null && signature.contains("class_")) {
            newSig = remapType(signature);
        }
        if (!Objects.equals(descriptor, newDesc)) {
            System.out.println("[LittleObfFallback] visitMethod(def): " + name + " : " + descriptor + " => " + newDesc + ", sig: " + signature + " => " + newSig);
        }
        MethodVisitor mv = super.visitMethod(access, name, newDesc, newSig, exceptions);
        return new MethodFallbackVisitor(api, mv, className);
    }

    private String remapType(String type) {
        if (type == null) return null;
        if (type.startsWith("[")) {
            return "[" + remapType(type.substring(1));
        }
        if (type.startsWith("L") && type.endsWith(";")) {
            String internal = type.substring(1, type.length() - 1);
            if (internal.startsWith("net/minecraft/class_")) {
                String mapped = AutoRemap.autoRemapClass(internal);
                if (mapped != null && !mapped.equals(internal)) {
                    return "L" + mapped + ";";
                }
            }
            return "L" + internal + ";";
        }
        return type;
    }
}
