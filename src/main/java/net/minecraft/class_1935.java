package net.minecraft;

import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;

// ItemLike (ItemConvertible)
public interface class_1935 extends ItemLike {
    default Item method_8389() {
        return asItem();
    }
}
