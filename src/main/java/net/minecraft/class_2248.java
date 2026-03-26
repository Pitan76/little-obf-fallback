package net.minecraft;

import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;

// Block
public interface class_2248 {

    default Block asBlock() {
        return (Block) (Object) this;
    }

    default Item method_8389() {
        return asBlock().asItem();
    }

    default Block method_26160() {
        return asBlock();
    }

    default FluidState method_9545(BlockState state) {
        return asBlock().getFluidState(state);
    }

    default FluidState method_9545(class_2680 state) {
        return asBlock().getFluidState((BlockState) (Object) state);
    }

    default float method_36555() {
        return asBlock().defaultDestroyTime();
    }

    default MutableComponent method_9518() {
        return asBlock().getName();
    }

    default BlockState method_9564() {
        return asBlock().defaultBlockState();
    }
}