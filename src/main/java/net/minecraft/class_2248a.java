package net.minecraft;

import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;

// Block
public class class_2248a extends Block {
    public class_2248a(Properties properties) {
        super(properties);
    }

    public class_2248a(class_4970.class_2251 properties) {
        super(properties);
    }

    // BlockBehavior (AbstractBlock)

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

    // ----

    public MutableComponent method_9518() {
        return super.getName();
    }

    public final BlockState method_9564() {
        return this.defaultBlockState();
    }
}
