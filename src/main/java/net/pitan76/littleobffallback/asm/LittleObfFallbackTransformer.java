package net.pitan76.littleobffallback.asm;

import org.objectweb.asm.commons.ClassRemapper;
import org.objectweb.asm.tree.ClassNode;

import java.util.function.Consumer;

public class LittleObfFallbackTransformer implements Consumer<ClassNode> {

    @Override
    public void accept(ClassNode node) {
        String name = node.name;
        if (name.startsWith("java/") || name.startsWith("sun/") ||
                name.startsWith("jdk/") || name.startsWith("org/objectweb/asm/") ||
                name.startsWith("net/minecraft/") || name.startsWith("net/pitan76/littleobffallback/") ||
                name.startsWith("com/google/") || name.startsWith("org/apache/") ||
                name.startsWith("org/spongepowered/") || name.startsWith("org/slf4j/") ||
                name.startsWith("org/log4j/") || name.startsWith("org/jetbrains/") ||
                name.startsWith("org/gradle/") || name.startsWith("net/fabricmc/") ||
                name.startsWith("com/chocohead/mm/") || name.startsWith("com/mojang/") ||
                name.startsWith("net/pitan76/mcpitanlib/")) {
            return;
        }
        
        LittleObfFallbackRemapper remapper = new LittleObfFallbackRemapper();

        ClassNode remapped = new ClassNode();
        ClassRemapper classRemapper = new ClassRemapper(remapped, remapper);
        node.accept(classRemapper);

        if (remapper.isChanged) {
            node.name = remapped.name;
            node.methods = remapped.methods;
            node.fields = remapped.fields;
            node.interfaces = remapped.interfaces;
            node.superName = remapped.superName;
        }
    }
}
