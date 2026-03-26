package net.minecraft;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;

// BlockPos
public class class_2338 extends BlockPos {

    public class_2338(int x, int y, int z) {
        super(x, y, z);
    }

    public class_2338(Vec3i vec3i) {
        super(vec3i);
    }

    public class_2338(class_2382 vec3i) {
        super(vec3i);
    }

    public int method_10263() {
        return super.getX();
    }

    public int method_10264() {
        return super.getY();
    }

    public int method_10260() {
        return super.getZ();
    }

    public BlockPos method_30931() {
        return super.above(); // up()
    }

    public BlockPos method_23228() {
        return super.below(); // down()
    }

    public BlockPos method_35861() {
        return super.north();
    }

    public BlockPos method_35859() {
        return super.south();
    }

    public BlockPos method_35855() {
        return super.east();
    }

    public BlockPos method_35857() {
        return super.west();
    }

    public BlockPos method_35853(class_2382 vec3i) {
        return super.offset(vec3i); // add(vec3i)
    }

    public BlockPos method_34592(int i, int j, int k) {
        return super.offset(i, j, k); // add(i, j, k)
    }

    public BlockPos method_35852(class_2382 vec3i) {
        return super.subtract(vec3i);
    }

    public BlockPos method_35862(int i) {
        return super.multiply(i);
    }
}
