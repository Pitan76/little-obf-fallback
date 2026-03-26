package net.minecraft;

import net.minecraft.core.Direction;

// Direction
public interface class_2350 {
    default Direction asDirection() {
        return (Direction) (Object) this;
    }

    default Direction method_10153() {
        return asDirection().getOpposite();
    }

    static class_2350[] values() {
        return (class_2350[]) (Object[]) Direction.values();
    }
}
