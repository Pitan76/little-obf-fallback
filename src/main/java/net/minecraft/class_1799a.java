package net.minecraft;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;

// ItemStack
public class class_1799a extends ItemStack {

    public class_1799a(ItemLike item, int count) {
        super(item, count);
    }

    public class_1799a(class_1935 item, int count) {
        super(item, count);
    }

    public class_1799a(ItemLike item) {
        super(item);
    }

    public class_1799a(class_1935 item) {
        super(item);
    }

    public Item method_7909() {
        return super.getItem();
    }

    public int method_7947() {
        return super.getCount();
    }

    public int method_7914() {
        return super.getMaxStackSize();
    }

    public int method_7936() {
        return super.getMaxDamage();
    }

    public Component method_7964() {
        return super.getDisplayName();
    }

    public int method_7919() {
        return super.getDamageValue();
    }

    public void method_7933(int amount) {
        super.grow(amount); // increment
    }

    public void method_7934(int amount) {
        super.shrink(amount); // decrement
    }

    public void method_7939(int count) {
        super.setCount(count);
    }

    public void method_7974(int damage) {
        super.setDamageValue(damage);
    }
}
