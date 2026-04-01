package net.pitan76.littleobffallback.asm;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MappingRegistry {

    public static final Map<String, String> CLASS_MAP = new ConcurrentHashMap<>();
    public static final Map<String, String> FIELD_MAP = new ConcurrentHashMap<>();
    public static final Map<String, String> METHOD_MAP = new ConcurrentHashMap<>();

    public static boolean isServerOnly = false;

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
        addClass("net/minecraft/class_1299", EntityType.class);
        addClass("net/minecraft/class_1542", ItemEntity.class);
        addClass("net/minecraft/class_1308", Mob.class);
        addClass("net/minecraft/class_1303", ExperienceOrb.class);
        addClass("net/minecraft/class_2248", Block.class);
        addClass("net/minecraft/class_2246", Blocks.class);
        addClass("net/minecraft/class_2350", Direction.class);
        addClass("net/minecraft/class_1799", ItemStack.class);
        addClass("net/minecraft/class_4970", BlockBehaviour.class);
        addClass("net/minecraft/class_4970$class_2251", BlockBehaviour.Properties.class);
        addClass("net/minecraft/class_243", Vec3.class);
        addClass("net/minecraft/class_3218", ServerLevel.class);
        addClass("net/minecraft/class_1937", Level.class);
        addClass("net/minecraft/class_2586", BlockEntity.class);
        addClass("net/minecraft/class_2591", BlockEntityType.class);
        addClass("net/minecraft/class_1703", AbstractContainerMenu.class);
        addClass("net/minecraft/class_3917", MenuType.class);
        addClass("net/minecraft/class_2338", BlockPos.class);
        addClass("net/minecraft/class_2382", Vec3i.class);
        addClass("net/minecraft/class_2397", LeavesBlock.class);
        addClass("net/minecraft/class_2680", BlockState.class);
        addClass("net/minecraft/class_4970$class_4971", BlockBehaviour.BlockStateBase.class);
        addClass("net/minecraft/class_1792", Item.class);
        addClass("net/minecraft/class_1802", Items.class);
        addClass("net/minecraft/class_1747", BlockItem.class);
        addClass("net/minecraft/class_1793", Item.Properties.class);
        addClass("net/minecraft/class_3737", SimpleWaterloggedBlock.class);
        addClass("net/minecraft/class_265", VoxelShape.class);
        addClass("net/minecraft/class_259", Shapes.class);
        addClass("net/minecraft/class_2540", FriendlyByteBuf.class);
        addClass("net/minecraft/class_1263", Container.class);
        addClass("net/minecraft/class_1661", Inventory.class);
        addClass("net/minecraft/class_1735", Slot.class);
        addClass("net/minecraft/class_2561", Component.class);
        addClass("net/minecraft/class_5250", MutableComponent.class);
        addClass("net/minecraft/class_2371", NonNullList.class);
        addClass("net/minecraft/class_1277", SimpleContainer.class);
        addClass("net/minecraft/class_3908", MenuProvider.class);
        addClass("net/minecraft/class_3917", MenuType.class);
        addClass("net/minecraft/class_1935", ItemLike.class);
        addClass("net/minecraft/class_2769", Property.class);
        addClass("net/minecraft/class_3222", ServerPlayer.class);
        addClass("net/minecraft/class_2189", AirBlock.class);
        addClass("net/minecraft/class_2404", LiquidBlock.class); // FluidBlock
        addClass("net/minecraft/class_2487", CompoundTag.class);
        addClass("net/minecraft/class_1301", EntitySelector.class); // EntityPredicates
        addClass("net/minecraft/class_238", AABB.class); // Box
        addClass("net/minecraft/class_3610", FluidState.class);
        addClass("net/minecraft/class_3611", Fluid.class);

        // Fabric API
        addClass("net/fabricmc/fabric/api/transfer/v1/item/InventoryStorage", "net/fabricmc/fabric/api/transfer/v1/item/ContainerStorage"); // InventoryStorage

        if (!isServerOnly) {
            try {
                addClass("net/minecraft/class_310", Minecraft.class);
                addClass("net/minecraft/class_327", Font.class);
                addClass("net/minecraft/class_437", Screen.class);
                addClass("net/minecraft/class_364", GuiEventListener.class);
                addClass("net/minecraft/class_827", BlockEntityRenderer.class);
                addClass("net/minecraft/class_638", ClientLevel.class);
                addClass("net/minecraft/class_746", LocalPlayer.class);
            } catch (Exception _) {
                // クライアントでない場合は次から無視する
                isServerOnly = true;
            }
        }

        addMethod("net/minecraft/class_1799", "method_7909", "getItem");
        addMethod("net/minecraft/class_1792", "method_7854", "getDefaultInstance");
        addMethod("net/minecraft/class_1792", "method_7882", "getDefaultMaxStackSize");
        addMethod("net/minecraft/class_4970$class_4971", "method_26204", "getBlock");
        addMethod("net/minecraft/class_2248", "method_9541", "box");
        addMethod("net/minecraft/class_259", "method_1073", "empty");
        addMethod("net/minecraft/class_259", "method_1077", "block");
        addMethod("net/minecraft/class_259", "box", "cuboid");
        addMethod("net/minecraft/class_1297", "method_5735", "getDirection");
        addMethod("net/minecraft/class_1309", "method_5735", "getDirection");
        addMethod("net/minecraft/class_1263", "method_5439", "getContainerSize");
        addMethod("net/minecraft/class_1263", "method_5442", "isEmpty");
        addMethod("net/minecraft/class_1263", "method_5438", "getItem");
        addMethod("net/minecraft/class_1263", "method_5434", "removeItem");
        addMethod("net/minecraft/class_1263", "method_5441", "removeItemNoUpdate");
        addMethod("net/minecraft/class_1263", "method_5447", "setItem");
        addMethod("net/minecraft/class_1263", "method_5431", "setChanged");
        addMethod("net/minecraft/class_1263", "method_5443", "stillValid");
        addMethod("net/minecraft/class_1263", "method_5448", "clearContent");
        addMethod("net/minecraft/class_1263", "method_18861", "countItem");
        addMethod("net/minecraft/class_1263", "method_5437", "canPlaceItem");
        addMethod("net/minecraft/class_1735", "method_53512", "set");
        addMethod("net/minecraft/class_1735", "method_7677", "getItem");
        addMethod("net/minecraft/class_1735", "method_7680", "mayPlace");

        // Component
        addMethod("net/minecraft/class_2561", "method_10866", "getStyle");
        addMethod("net/minecraft/class_2561", "method_10851", "getContents");
        addMethod("net/minecraft/class_2561", "method_10858", "getString");
        addMethod("net/minecraft/class_2561", "method_10855", "getSiblings");
        addMethod("net/minecraft/class_2561", "method_44745", "contains");
        addMethod("net/minecraft/class_2561", "method_30163", "nullToEmpty");
        addMethod("net/minecraft/class_2561", "method_43470", "literal");
        addMethod("net/minecraft/class_2561", "method_43471", "translatable");
        addMethod("net/minecraft/class_2561", "method_43469", "translatableWithArgs");
        addMethod("net/minecraft/class_2561", "method_43473", "empty");

        // NonNullList
        addMethod("net/minecraft/class_2371", "method_10211", "create");
        addMethod("net/minecraft/class_2371", "method_37434", "createWithCapacity");
        addMethod("net/minecraft/class_2371", "method_10213", "withSize");
        addMethod("net/minecraft/class_2371", "method_10212", "of");

        // ItemStack
        addMethod("net/minecraft/class_1799", "method_7960", "isEmpty");
        addMethod("net/minecraft/class_1799", "method_7909", "getItem");
        addMethod("net/minecraft/class_1799", "method_7947", "getCount");
        addMethod("net/minecraft/class_1799", "method_7914", "getMaxStackSize");
        addMethod("net/minecraft/class_1799", "method_7936", "getMaxDamage");
        addMethod("net/minecraft/class_1799", "method_7964", "getDisplayName");
        addMethod("net/minecraft/class_1799", "method_7919", "getDamageValue");
        addMethod("net/minecraft/class_1799", "method_7933", "grow");
        addMethod("net/minecraft/class_1799", "method_7934", "shrink");
        addMethod("net/minecraft/class_1799", "method_7939", "setCount");
        addMethod("net/minecraft/class_1799", "method_7974", "setDamageValue");

        // Entity
        addMethod("net/minecraft/class_1297", "method_5864", "getType");
        addMethod("net/minecraft/class_1297", "method_36455", "getYRot");
        addMethod("net/minecraft/class_1297", "method_36454", "getXRot");
        addMethod("net/minecraft/class_1297", "method_24515", "blockPosition");
        addMethod("net/minecraft/class_1297", "method_5715", "isShiftKeyDown");
        addMethod("net/minecraft/class_1297", "method_51502", "setLevel");
        addMethod("net/minecraft/class_1297", "method_5814", "setPos");
        addMethod("net/minecraft/class_1297", "method_18798", "getDeltaMovement");
        addMethod("net/minecraft/class_1297", "method_18799", "setDeltaMovement");
        addMethod("net/minecraft/class_1297", "method_18800", "setDeltaMovement");
        addMethod("net/minecraft/class_1309", "method_5805", "isAlive");

        // BlockEntity
        addMethod("net/minecraft/class_2586", "method_11017", "getType");
        addMethod("net/minecraft/class_2586", "method_10997", "getLevel");
        addMethod("net/minecraft/class_2586", "method_11016", "getBlockPos");
        addMethod("net/minecraft/class_2586", "method_11010", "getBlockState");
        addMethod("net/minecraft/class_2586", "method_31662", "setLevel");
        addMethod("net/minecraft/class_2586", "method_11015", "isRemoved");

        // Block
        addMethod("net/minecraft/class_2248", "method_26160", "asBlock");
        addMethod("net/minecraft/class_2248", "method_8389", "asItem");
        addMethod("net/minecraft/class_2248", "method_9545", "getFluidState");
        addMethod("net/minecraft/class_2248", "method_36555", "defaultDestroyTime");
        addMethod("net/minecraft/class_2248", "method_9518", "getName");
        addMethod("net/minecraft/class_2248", "method_9564", "defaultBlockState");

        // BlockState
        addMethod("net/minecraft/class_2680", "method_26204", "getBlock");
        addMethod("net/minecraft/class_2680", "method_26227", "getFluidState");
        addMethod("net/minecraft/class_2680", "method_11654", "getValue");
        addMethod("net/minecraft/class_2680", "method_11657", "setValue");
        addMethod("net/minecraft/class_2680", "method_28498", "hasProperty");

        // BlockBehaviour
        addMethod("net/minecraft/class_4970", "method_8389", "asItem");
        addMethod("net/minecraft/class_4970", "method_26160", "asBlock");
        addMethod("net/minecraft/class_4970", "method_36555", "defaultDestroyTime");

        // ItemLike
        addMethod("net/minecraft/class_1935", "method_8389", "asItem");

        // Direction
        addMethod("net/minecraft/class_2350", "method_10153", "getOpposite");

        // Vec3i
        addMethod("net/minecraft/class_2382", "method_10263", "getX");
        addMethod("net/minecraft/class_2382", "method_10264", "getY");
        addMethod("net/minecraft/class_2382", "method_10260", "getZ");

        // Level (World)
        addMethod("net/minecraft/class_1937", "method_8608", "isClientSide");
        addMethod("net/minecraft/class_1937", "method_8321", "getBlockEntity");
        addMethod("net/minecraft/class_1937", "method_8320", "getBlockState");
        addMethod("net/minecraft/class_1937", "method_8501", "setBlock");
        addMethod("net/minecraft/class_1937", "method_8652", "setBlock");
        addMethod("net/minecraft/class_1937", "method_8316", "getFluidState");

        // BlockPos
        addMethod("net/minecraft/class_2338", "method_10263", "getX");
        addMethod("net/minecraft/class_2338", "method_10264", "getY");
        addMethod("net/minecraft/class_2338", "method_10260", "getZ");
        addMethod("net/minecraft/class_2338", "method_10093", "relative"); // offset
        addMethod("net/minecraft/class_2338", "method_10079", "relative"); // offset
        addMethod("net/minecraft/class_2338", "method_30931", "above"); // up
        addMethod("net/minecraft/class_2338", "method_23228", "below"); // down
        addMethod("net/minecraft/class_2338", "method_35861", "north");
        addMethod("net/minecraft/class_2338", "method_35859", "south");
        addMethod("net/minecraft/class_2338", "method_35855", "east");
        addMethod("net/minecraft/class_2338", "method_35857", "west");
        addMethod("net/minecraft/class_2338", "method_35853", "offset"); // add
        addMethod("net/minecraft/class_2338", "method_34592", "offset"); // add
        addMethod("net/minecraft/class_2338", "method_35852", "subtract");
        addMethod("net/minecraft/class_2338", "method_35862", "multiply");

        // FluidState
        addMethod("net/minecraft/class_3610", "method_15772", "getType"); // getFluid
        addMethod("net/minecraft/class_3610", "method_15769", "isEmpty");
        addMethod("net/minecraft/class_3610", "method_15771", "isSource"); // isStill

        // Items
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

        // EntitySelector
        addField("net/minecraft/class_1301", "field_6154", "ENTITY_STILL_ALIVE");
        addField("net/minecraft/class_1301", "field_6157", "ENTITY_STILL_ALIVE");

        // Blocks
        addField("net/minecraft/class_1802", "field_8162", "AIR");
        addField("net/minecraft/class_1802", "field_20390", "BRICKS");
        addField("net/minecraft/class_1802", "field_8342", "BRICK_SLAB");
        addField("net/minecraft/class_1802", "field_8280", "GLASS");
        addField("net/minecraft/class_1802", "field_8705", "WATER_BUCKET");

        // ItemStack.EMPTY
        addField("net/minecraft/class_1799", "field_8037", "EMPTY");

        // Direction
        addField("net/minecraft/class_2350", "field_11033", "DOWN");
        addField("net/minecraft/class_2350", "field_11036", "UP");
        addField("net/minecraft/class_2350", "field_11043", "NORTH");
        addField("net/minecraft/class_2350", "field_11035", "SOUTH");
        addField("net/minecraft/class_2350", "field_11039", "WEST");
        addField("net/minecraft/class_2350", "field_11034", "EAST");

        addField("net/minecraft/class_1661", "field_7546", "player");
    }
}
