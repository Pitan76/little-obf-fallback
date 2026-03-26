package net.minecraft;

import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;

// BlockBehavior (AbstractBlock)
public abstract class class_4970 extends BlockBehaviour {
    public class_4970(Properties properties) {
        super(properties);
    }

    public class_4970(class_2251 properties) {
        super(properties);
    }

    public Item method_8389() {
        return asItem();
    }

    public Block method_26160() {
        return asBlock();
    }

    protected FluidState method_9545(BlockState state) {
        return super.getFluidState(state);
    }

    protected FluidState method_9545(class_2680 state) {
        return super.getFluidState(state);
    }

    public float method_36555() {
        return super.defaultDestroyTime(); // getHardness()
    }

    public static class class_2251 extends Properties {
        public class_2251() {
            super();
        }
    }
}
