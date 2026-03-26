package net.minecraft;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.storage.WritableLevelData;
import org.jetbrains.annotations.Nullable;

// Level (World)
public abstract class class_1937 extends Level {

    protected class_1937(WritableLevelData levelData, ResourceKey<Level> dimension, RegistryAccess registryAccess, Holder<DimensionType> dimensionTypeRegistration, boolean isClientSide, boolean isDebug, long biomeZoomSeed, int maxChainedNeighborUpdates) {
        super(levelData, dimension, registryAccess, dimensionTypeRegistration, isClientSide, isDebug, biomeZoomSeed, maxChainedNeighborUpdates);
    }

    public boolean method_8608() {
        return super.isClientSide();
    }

    public @Nullable BlockEntity method_8321(BlockPos pos) {
        return super.getBlockEntity(pos);
    }

    public @Nullable BlockEntity method_8321(class_2338 pos) {
        return super.getBlockEntity(pos);
    }

    public BlockState method_8320(BlockPos pos) {
        return super.getBlockState(pos);
    }

    public BlockState method_8320(class_2338 pos) {
        return super.getBlockState(pos);
    }

    public boolean method_8501(BlockPos pos, BlockState state) {
        return super.setBlock(pos, state, 3); // setBlockState(pos, state, 3);
    }

    public boolean method_8652(BlockPos pos, BlockState state, int flags) {
        return super.setBlock(pos, state, flags); // setBlockState(pos, state, flags);
    }

    public boolean method_8501(class_2338 pos, class_2680 state) {
        return super.setBlock(pos, state, 3); // setBlockState(pos, state, 3);
    }

    public boolean method_8652(class_2338 pos, class_2680 state, int flags) {
        return super.setBlock(pos, state, flags); // setBlockState(pos, state, flags);
    }
}
