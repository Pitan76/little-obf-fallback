package net.pitan76.littleobffallback.transformer;

import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;

import java.util.Objects;

public class MethodFallbackVisitor extends MethodVisitor {
    protected final String className;

    public MethodFallbackVisitor(int api, MethodVisitor methodVisitor, String className) {
        super(api, methodVisitor);
        this.className = className;
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

    private String remapClass(String name) {
        if (name == null) return null;
        if (name.startsWith("net/minecraft/class_")) {
            return AutoRemap.autoRemapClass(name);
        }
        return name;
    }

    private String remapField(String fieldName) {
        if (fieldName == null) return null;
        if (fieldName.startsWith("field_")) {
            return AutoRemap.autoRemapField(fieldName);
        }
        return fieldName;
    }

    private String remapMethod(String methodName) {
        if (methodName == null) return null;
        if (methodName.startsWith("method_")) {
            return AutoRemap.autoRemapClass(methodName);
        }
        return methodName;
    }

    @Override
    public void visitFieldInsn(int opcode, String owner, String fieldName, String desc) {
        String newOwner = (owner != null && owner.contains("class_")) ? remapClass(owner) : owner;
        String newFieldName = (fieldName != null && fieldName.contains("field_")) ? remapField(fieldName) : fieldName;
        String newDesc = (desc != null && desc.contains("class_")) ? remapType(desc) : desc;
        boolean changed = !Objects.equals(owner, newOwner)
                || !Objects.equals(fieldName, newFieldName)
                || !Objects.equals(desc, newDesc);

        if (changed) {
            System.out.println("[LittleObfFallback] visitFieldInsn: " + owner + "." + fieldName +
                    " => " + newOwner + "." + newFieldName + ", desc: " + desc + " => " + newDesc);
        }
        super.visitFieldInsn(opcode, newOwner, newFieldName, newDesc);
    }

    @Override
    public void visitMethodInsn(int opcode, String owner, String name, String descriptor, boolean isInterface) {
        String newOwner = (owner != null && owner.contains("class_")) ? remapClass(owner) : owner;
        String newName = (name != null && name.contains("method_")) ? remapMethod(name) : name;
        String newDesc = (descriptor != null && descriptor.contains("class_")) ? remapType(descriptor) : descriptor;
        boolean changed = !Objects.equals(owner, newOwner) || !Objects.equals(name, newName) || !Objects.equals(descriptor, newDesc);
        if (changed) {
            System.out.println("[LittleObfFallback] visitMethodInsn: " + owner + "." + name + " => " +
                    " => " + newOwner + "." + newName + ", desc: " + descriptor + " => " + newDesc);
        }
        super.visitMethodInsn(opcode, newOwner, newName, newDesc, isInterface);
    }

    @Override
    public void visitTypeInsn(int opcode, String type) {
        if (type == null || !type.contains("class_")) {
            super.visitTypeInsn(opcode, type);
            return;
        }

        String newType = remapType(type);
        System.out.println("[LittleObfFallback] visitTypeInsn: " + type + " => " + newType);
        super.visitTypeInsn(opcode, newType);
    }

    @Override
    public void visitLocalVariable(String name, String desc, String signature, Label start, Label end, int index) {
        if ((desc == null || !desc.contains("class_")) && (signature == null || !signature.contains("class_"))) {
            super.visitLocalVariable(name, desc, signature, start, end, index);
            return;
        }

        String newDesc = remapType(desc);
        String newSig = remapType(signature);
        System.out.println("[LittleObfFallback] visitLocalVariable: " + name + " : " + desc + " => " + newDesc + ", signature: " + signature + " => " + newSig);
        super.visitLocalVariable(name, newDesc, newSig, start, end, index);
    }
}
