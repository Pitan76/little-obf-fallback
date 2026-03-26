package net.minecraft;

import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

// Container (Inventory)
public interface class_1263 {
    default Container asContainer() {
        return (Container) (Object) this;
    }
    
    default int method_5439() {
        return asContainer().getContainerSize(); // size();
    }

    default boolean method_5442() {
        return asContainer().isEmpty();
    }

    default ItemStack method_5438(int slot) {
        return asContainer().getItem(slot); // getStack(int slot);
    }

    default ItemStack method_5434(int slot, int amount) {
        return asContainer().removeItem(slot, amount); // removeStack(int slot, int amount);
    }

    default ItemStack method_5441(int slot) {
        return asContainer().removeItemNoUpdate(slot); // removeStack(int slot);
    }

    default void method_5447(int slot, ItemStack stack) {
        asContainer().setItem(slot, stack); // setStack(int slot, ItemStack stack);
    }

    default void method_5431() {
        asContainer().setChanged(); // markDirty();
    }

    default boolean method_5443(Player player) {
        return asContainer().stillValid(player); // canPlayerUse(PlayerEntity player);
    }

    default void method_5448() {
        asContainer().clearContent(); // clear();
    }

    default int method_18861(Item item) {
        return asContainer().countItem(item); // count(Item item);
    }

    default boolean method_5437(int slot, ItemStack stack) {
        return asContainer().canPlaceItem(slot, stack); // isValid(int slot, ItemStack stack);
    }
}
