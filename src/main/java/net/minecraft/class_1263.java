package net.minecraft;

import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

// Container (Inventory)
public interface class_1263 extends Container {
    
    default int method_5439() {
        return this.getContainerSize(); // size();
    }

    default boolean method_5442() {
        return this.isEmpty();
    }

    default ItemStack method_5438(int slot) {
        return this.getItem(slot); // getStack(int slot);
    }

    default ItemStack method_5434(int slot, int amount) {
        return this.removeItem(slot, amount); // removeStack(int slot, int amount);
    }

    default ItemStack method_5441(int slot) {
        return this.removeItemNoUpdate(slot); // removeStack(int slot);
    }

    default void method_5447(int slot, ItemStack stack) {
        this.setItem(slot, stack); // setStack(int slot, ItemStack stack);
    }

    default void method_5431() {
        this.setChanged(); // markDirty();
    }

    default boolean method_5443(Player player) {
        return this.stillValid(player); // canPlayerUse(PlayerEntity player);
    }

    default void method_5448() {
        this.clearContent(); // clear();
    }

    default int method_18861(class_1792 item) {
        return this.countItem(item); // count(Item item);
    }

    default boolean method_5437(int slot, ItemStack stack) {
        return this.canPlaceItem(slot, stack); // isValid(int slot, ItemStack stack);
    }
}
