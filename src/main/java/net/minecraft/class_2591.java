package net.minecraft;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

import java.util.Set;

public class class_2591<T extends BlockEntity> extends BlockEntityType<T> {
    public class_2591(BlockEntitySupplier<? extends T> factory, final Set<Block> validBlocks) {
        super(factory, validBlocks);
    }
}
