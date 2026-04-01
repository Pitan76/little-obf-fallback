package net.pitan76.littleobffallback.asm;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.commons.Remapper;

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
        }
        return super.mapMethodName(owner, name, descriptor);
    }
}
