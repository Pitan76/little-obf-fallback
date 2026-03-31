package net.pitan76.littleobffallback.asm;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MappingRegistry {

    public static final Map<String, String> CLASS_MAP = new ConcurrentHashMap<>();
    public static final Map<String, String> FIELD_MAP = new ConcurrentHashMap<>();
    public static final Map<String, String> METHOD_MAP = new ConcurrentHashMap<>();

    /**
     * クラスのマッピングを追加する
     * @param intermediary 元の難読化クラス名 (例: net.minecraft.class_1297 または net/minecraft/class_1297)
     * @param mojmap 変換後のクラス名 (例: net.minecraft.world.entity.Entity または net/minecraft/world/entity/Entity)
     */
    public static void addClass(String intermediary, String mojmap) {
        CLASS_MAP.put(intermediary.replace('.', '/'), mojmap.replace('.', '/'));
    }

    public static void addClass(String intermediary, Class<?> mojmap) {
        addClass(intermediary, simplifiedDesc(mojmap));
    }

    /**
     * フィールドのマッピングを追加する
     * @param ownerIntermediary フィールドを持つ元のクラス名 (例: net/minecraft/class_2246)
     * @param fieldIntermediary 元のフィールド名 (例: field_10124)
     * @param fieldMojmap 変換後のフィールド名 (例: AIR)
     */
    public static void addField(String ownerIntermediary, String fieldIntermediary, String fieldMojmap) {
        FIELD_MAP.put(ownerIntermediary.replace('.', '/') + "#" + fieldIntermediary, fieldMojmap);
    }

    /**
     * メソッドのマッピングを追加する（シグネチャを無視して名前にマッチさせる場合）
     * @param ownerIntermediary メソッドを持つ元のクラス名 (例: net/minecraft/class_1297)
     * @param methodIntermediary 元のメソッド名 (例: method_5628)
     * @param methodMojmap 変換後のメソッド名 (例: tick)
     */
    public static void addMethod(String ownerIntermediary, String methodIntermediary, String methodMojmap) {
        METHOD_MAP.put(ownerIntermediary.replace('.', '/') + "#" + methodIntermediary, methodMojmap);
    }

    /**
     * メソッドのマッピングを追加する（シグネチャによる完全一致）
     * @param ownerIntermediary メソッドを持つ元のクラス名
     * @param methodIntermediary 元のメソッド名
     * @param desc メソッドのディスクリプタ (例: (Lnet/minecraft/class_1297;)V など)
     * @param methodMojmap 変換後のメソッド名
     */
    public static void addMethodWithDesc(String ownerIntermediary, String methodIntermediary, String desc, String methodMojmap) {
        METHOD_MAP.put(ownerIntermediary.replace('.', '/') + "#" + methodIntermediary + desc, methodMojmap);
    }

    /**
     * L...; 形式のクラス名を簡易的に変換
     */
    public static String simplifiedDesc(String name) {
        if (name.startsWith("L") && name.endsWith(";")) {
            return name.substring(1, name.length() - 1);
        }
        return name;
    }

    public static String simplifiedDesc(Class<?> clazz) {
        return simplifiedDesc(clazz.descriptorString());
    }

    static {
        addClass("net/minecraft/class_1309", LivingEntity.class);
        addClass("net/minecraft/class_1657", Player.class);
        addClass("net/minecraft/class_1297", Entity.class);
        addClass("net/minecraft/class_1542", ItemEntity.class);
        addClass("net/minecraft/class_1303", ExperienceOrb.class);
        addClass("net/minecraft/class_2246", "net/minecraft/world/level/block/Blocks");
        addClass("net/minecraft/class_2248", "net/minecraft/world/level/block/Block");
        addClass("net/minecraft/class_2350", "net/minecraft/core/Direction");
        addClass("net/minecraft/class_1799", "net/minecraft/world/item/ItemStack");
        addClass("net/minecraft/class_4970", "net/minecraft/world/level/block/state/BlockBehaviour");
        addClass("net/minecraft/class_4970$class_2251", "net/minecraft/world/level/block/state/BlockBehaviour$Properties");
        addClass("net/minecraft/class_243", Vec3.class);
        addClass("net/minecraft/class_3218", ServerLevel.class);
        addClass("net/minecraft/class_1937", Level.class);
        addClass("net/minecraft/class_2586", BlockEntity.class);
        addClass("net/minecraft/class_2591", BlockEntityType.class);
        addClass("net/minecraft/class_1703", AbstractContainerMenu.class);
        addClass("net/minecraft/class_2338", BlockPos.class);
        addClass("net/minecraft/class_2382", Vec3i.class);
        addClass("net/minecraft/class_2397", LeavesBlock.class);
        addClass("net/minecraft/class_2680", BlockState.class);
        addClass("net/minecraft/class_4970$class_4971", BlockBehaviour.BlockStateBase.class);
        addClass("net/minecraft/class_1792", Item.class);
        addClass("net/minecraft/class_1747", BlockItem.class);
        addClass("net/minecraft/class_1793", Item.Properties.class);
        addClass("net/minecraft/class_3737", SimpleWaterloggedBlock.class);
        addClass("net/minecraft/class_265", VoxelShape.class);
        addClass("net/minecraft/class_259", Shapes.class);

        addMethod("net/minecraft/class_1799", "method_7909", "getItem");
        addMethod("net/minecraft/class_1792", "method_7854", "getDefaultInstance");
        addMethod("net/minecraft/class_1792", "method_7882", "getDefaultMaxStackSize");
        addMethod("net/minecraft/class_4970$class_4971", "method_26204", "getBlock");
        addMethod("net/minecraft/class_2248", "method_9541", "box");
        addMethod("net/minecraft/class_259", "method_1073", "empty");
        addMethod("net/minecraft/class_259", "method_1077", "block");
        addMethod("net/minecraft/class_259", "box", "cuboid");

        addField("net/minecraft/class_2246", "field_10124", "AIR");
        addField("net/minecraft/class_2246", "field_10340", "STONE");
        addField("net/minecraft/class_2246", "field_10474", "GRANITE");
        addField("net/minecraft/class_2246", "field_10508", "DIORITE");
        addField("net/minecraft/class_2246", "field_10115", "ANDESITE");
        addField("net/minecraft/class_2246", "field_10219", "GRASS_BLOCK");
        addField("net/minecraft/class_2246", "field_10566", "DIRT");
        addField("net/minecraft/class_2246", "field_10253", "COARSE_DIRT");
        addField("net/minecraft/class_2246", "field_10520", "PODZOL");
        addField("net/minecraft/class_2246", "field_10445", "COBBLESTONE");
        addField("net/minecraft/class_2246", "field_10161", "OAK_PLANKS");
        addField("net/minecraft/class_2246", "field_9975", "SPRUCE_PLANKS");
        addField("net/minecraft/class_2246", "field_10148", "BIRCH_PLANKS");
        addField("net/minecraft/class_2246", "field_10334", "JUNGLE_PLANKS");
        addField("net/minecraft/class_2246", "field_10218", "ACACIA_PLANKS");
        addField("net/minecraft/class_2246", "field_42751", "CHERRY_PLANKS");
        addField("net/minecraft/class_2246", "field_10075", "DARK_OAK_PLANKS");
        addField("net/minecraft/class_2246", "field_54734", "PALE_OAK_WOOD");
        addField("net/minecraft/class_2246", "field_54735", "PALE_OAK_PLANKS");
        addField("net/minecraft/class_2246", "field_37577", "MANGROVE_PLANKS");
        addField("net/minecraft/class_2246", "field_40294", "BAMBOO_PLANKS");
        addField("net/minecraft/class_2246", "field_22126", "CRIMSON_PLANKS");
        addField("net/minecraft/class_2246", "field_22127", "WARPED_PLANKS");
        addField("net/minecraft/class_2246", "field_10033", "GLASS");
        addField("net/minecraft/class_2246", "field_9979", "SANDSTONE");
        addField("net/minecraft/class_2246", "field_10344", "RED_SANDSTONE");
        addField("net/minecraft/class_2246", "field_10104", "BRICKS");
        addField("net/minecraft/class_2246", "field_10056", "STONE_BRICKS");
        addField("net/minecraft/class_2246", "field_10266", "NETHER_BRICKS");
        addField("net/minecraft/class_2246", "field_9986", "RED_NETHER_BRICKS");
        addField("net/minecraft/class_2246", "field_10462", "END_STONE_BRICKS");
        addField("net/minecraft/class_2246", "field_10360", "SMOOTH_STONE");
        addField("net/minecraft/class_2246", "field_10467", "SMOOTH_SANDSTONE");
        addField("net/minecraft/class_2246", "field_10483", "SMOOTH_RED_SANDSTONE");
        addField("net/minecraft/class_2246", "field_9978", "SMOOTH_QUARTZ");
        addField("net/minecraft/class_2246", "field_10490", "YELLOW_WOOL");
        addField("net/minecraft/class_2246", "field_10028", "LIME_WOOL");
        addField("net/minecraft/class_2246", "field_10459", "PINK_WOOL");
        addField("net/minecraft/class_2246", "field_10423", "GRAY_WOOL");
        addField("net/minecraft/class_2246", "field_10222", "LIGHT_GRAY_WOOL");
        addField("net/minecraft/class_2246", "field_10619", "CYAN_WOOL");
        addField("net/minecraft/class_2246", "field_10259", "PURPLE_WOOL");
        addField("net/minecraft/class_2246", "field_10514", "BLUE_WOOL");
        addField("net/minecraft/class_2246", "field_10113", "BROWN_WOOL");
        addField("net/minecraft/class_2246", "field_10170", "GREEN_WOOL");
        addField("net/minecraft/class_2246", "field_10314", "RED_WOOL");
        addField("net/minecraft/class_2246", "field_10146", "BLACK_WOOL");
        addField("net/minecraft/class_2246", "field_10153", "QUARTZ_BLOCK");
        addField("net/minecraft/class_2246", "field_10286", "PURPUR_BLOCK");

        addField("net/minecraft/class_2350", "field_11033", "DOWN");
        addField("net/minecraft/class_2350", "field_11036", "UP");
        addField("net/minecraft/class_2350", "field_11043", "NORTH");
        addField("net/minecraft/class_2350", "field_11035", "SOUTH");
        addField("net/minecraft/class_2350", "field_11039", "WEST");
        addField("net/minecraft/class_2350", "field_11034", "EAST");
    }
}
