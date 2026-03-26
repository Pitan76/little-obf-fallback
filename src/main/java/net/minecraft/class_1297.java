package net.minecraft;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public abstract class class_1297 extends Entity {
    public class_1297(EntityType<?> type, Level level) {
        super(type, level);
    }

    public EntityType<?> method_5864() {
        return super.getType();
    }

    public float method_36455() {
        return super.getYRot();
    }

    public float method_36454() {
        return super.getXRot();
    }

    public Level getWorld() {
        return super.level();
    }

    public Level getEntityWorld() {
        return super.level();
    }

    public BlockPos method_24515() {
        return super.blockPosition();
    }

    public boolean method_5715() {
        return super.isShiftKeyDown();
    }

    protected void method_51502(Level world) {
        super.setLevel(world);
    }

    public void method_5814(double x, double y, double z) {
        super.setPos(x, y, z);
    }

    public Vec3 getPos() {
        return super.position();
    }

    public Vec3 method_18798() {
        return super.getDeltaMovement();
    }

    public void method_18799(Vec3 velocity) {
        super.setDeltaMovement(velocity); // setVelocity()
    }

    public void method_18800(double x, double y, double z) {
        super.setDeltaMovement(x, y, z); // setVelocity()(
    }

    public boolean method_5805() {
        return super.isAlive();
    }
}
