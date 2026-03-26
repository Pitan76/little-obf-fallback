package net.minecraft;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

// Item
public class class_1792 extends Item implements class_1935 {

    public class_1792(Properties properties) {
        super(properties);
    }

    public class_1792(class_1793 properties) {
        super(properties);
    }

    public Component method_63680() {
        return super.getName(getDefaultInstance());
    }

    @Override
    public Item method_8389() {
        return asItem();
    }

    public ItemStack method_7854() {
        return super.getDefaultInstance();
    }

    public int method_7882() {
        return super.getDefaultMaxStackSize();
    }

    public static class class_1793 extends Properties {
        public class_1793() {
            super();
        }
    }
}
