package net.pitan76.littleobffallback.compat;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;

// Item
public interface class_1792_bridge {
    default Item asItem() {
        return (Item) this;
    }

    default Component method_63680() {
        return asItem().getName(asItem().getDefaultInstance());
    }
}
