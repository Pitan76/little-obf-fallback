package net.minecraft;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

// ItemStack
public interface class_1799 {
    default ItemStack asStack() {
        return (ItemStack) this;
    }

    default Item method_7909() {
        return asStack().getItem();
    }

    default int method_7947() {
        return asStack().getCount();
    }

    default int method_7914() {
        return asStack().getMaxStackSize();
    }

    default int method_7936() {
        return asStack().getMaxDamage();
    }

    default Component method_7964() {
        return asStack().getDisplayName();
    }

    default int method_7919() {
        return asStack().getDamageValue();
    }

    default void method_7933(int amount) {
        asStack().grow(amount);
    }

    default void method_7934(int amount) {
        asStack().shrink(amount);
    }

    default void method_7939(int count) {
        asStack().setCount(count);
    }

    default void method_7974(int damage) {
        asStack().setDamageValue(damage);
    }
}