package net.pitan76.littleobffallback.asm;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.core.Vec3i;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.nbt.*;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.StatType;
import net.minecraft.stats.Stats;
import net.minecraft.world.*;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.entity.projectile.throwableitemprojectile.ThrowableItemProjectile;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Half;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.state.properties.StairsShape;
import net.minecraft.world.level.entity.EntityAccess;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.storage.SavedDataStorage;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MappingRegistry {

    public static final Map<String, String> CLASS_MAP = new ConcurrentHashMap<>();
    public static final Map<String, String> FIELD_MAP = new ConcurrentHashMap<>();
    public static final Map<String, String> METHOD_MAP = new ConcurrentHashMap<>();

    public static final Map<String, String> GLOBAL_FIELD_MAP = new ConcurrentHashMap<>();
    public static final Map<String, String> GLOBAL_METHOD_MAP = new ConcurrentHashMap<>();

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
        GLOBAL_FIELD_MAP.put(fieldIntermediary, fieldMojmap);
    }

    /**
     * メソッドのマッピングを追加する（シグネチャを無視して名前にマッチさせる場合）
     * @param ownerIntermediary メソッドを持つ元のクラス名 (例: net/minecraft/class_1297)
     * @param methodIntermediary 元のメソッド名 (例: method_5628)
     * @param methodMojmap 変換後のメソッド名 (例: tick)
     */
    public static void addMethod(String ownerIntermediary, String methodIntermediary, String methodMojmap) {
        METHOD_MAP.put(ownerIntermediary.replace('.', '/') + "#" + methodIntermediary, methodMojmap);
        GLOBAL_METHOD_MAP.put(methodIntermediary, methodMojmap);
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
        GLOBAL_METHOD_MAP.put(methodIntermediary, methodMojmap);
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
        addClass("net/minecraft/class_2960", Identifier.class);
        addClass("net/minecraft/class_1309", LivingEntity.class);
        addClass("net/minecraft/class_1676", Projectile.class);
        addClass("net/minecraft/class_1682", ThrowableProjectile.class);
        addClass("net/minecraft/class_3857", ThrowableItemProjectile.class);
        addClass("net/minecraft/class_1657", Player.class); // PlayerEntity
        addClass("net/minecraft/class_1297", Entity.class);
        addClass("net/minecraft/class_1299", EntityType.class);
        addClass("net/minecraft/class_1542", ItemEntity.class);
        addClass("net/minecraft/class_1308", Mob.class); // MobEntity
        addClass("net/minecraft/class_1303", ExperienceOrb.class); // ExperienceOrbEntity
        addClass("net/minecraft/class_2248", Block.class);
        addClass("net/minecraft/class_2246", Blocks.class);
        addClass("net/minecraft/class_2350", Direction.class);
        addClass("net/minecraft/class_1799", ItemStack.class);
        addClass("net/minecraft/class_4970", BlockBehaviour.class); // AbstractBlock
        addClass("net/minecraft/class_4970$class_2251", BlockBehaviour.Properties.class); // AbstractBlock.Settings
        addClass("net/minecraft/class_243", Vec3.class); // Vec3d
        addClass("net/minecraft/class_3218", ServerLevel.class); // ServerWorld
        addClass("net/minecraft/class_1937", Level.class); // World
        addClass("net/minecraft/class_2586", BlockEntity.class);
        addClass("net/minecraft/class_2591", BlockEntityType.class);
        addClass("net/minecraft/class_1703", AbstractContainerMenu.class); // ScreenHandler
        addClass("net/minecraft/class_2338", BlockPos.class);
        addClass("net/minecraft/class_2382", Vec3i.class);
        addClass("net/minecraft/class_2397", LeavesBlock.class);
        addClass("net/minecraft/class_2680", BlockState.class);
        addClass("net/minecraft/class_4970$class_4971", BlockBehaviour.BlockStateBase.class); // AbstractBlockState
        addClass("net/minecraft/class_1792", Item.class);
        addClass("net/minecraft/class_1802", Items.class);
        addClass("net/minecraft/class_1747", BlockItem.class);
        addClass("net/minecraft/class_1793", Item.Properties.class); // Item.Settings
        addClass("net/minecraft/class_3737", SimpleWaterloggedBlock.class); // Waterloggable
        addClass("net/minecraft/class_265", VoxelShape.class);
        addClass("net/minecraft/class_259", Shapes.class); // VoxelShapes
        addClass("net/minecraft/class_2540", FriendlyByteBuf.class); // PacketByteBuf
        addClass("net/minecraft/class_1263", Container.class); // Inventory
        addClass("net/minecraft/class_1661", Inventory.class); // PlayerInventory
        addClass("net/minecraft/class_1735", Slot.class);
        addClass("net/minecraft/class_2561", Component.class); // Text
        addClass("net/minecraft/class_5250", MutableComponent.class); // MutableText
        addClass("net/minecraft/class_2371", NonNullList.class); // DefaultedList
        addClass("net/minecraft/class_1277", SimpleContainer.class); // SimpleInventory
        addClass("net/minecraft/class_3908", MenuProvider.class); // ScreenHandlerProvider
        addClass("net/minecraft/class_3917", MenuType.class); // ScreenHandlerType
        addClass("net/minecraft/class_1935", ItemLike.class); // ItemConvertible
        addClass("net/minecraft/class_2769", Property.class);
        addClass("net/minecraft/class_3222", ServerPlayer.class); // ServerPlayerEntity
        addClass("net/minecraft/class_2189", AirBlock.class);
        addClass("net/minecraft/class_2404", LiquidBlock.class); // FluidBlock
        addClass("net/minecraft/class_2487", CompoundTag.class); // NbtCompound
        addClass("net/minecraft/class_1301", EntitySelector.class); // EntityPredicates
        addClass("net/minecraft/class_238", AABB.class); // Box
        addClass("net/minecraft/class_3610", FluidState.class);
        addClass("net/minecraft/class_3611", Fluid.class);
        addClass("net/minecraft/class_9331", DataComponentType.class); // DataComponentType
        addClass("net/minecraft/class_9334", DataComponents.class); // DataComponentTypes
        addClass("net/minecraft/class_9331$class_9332", DataComponentType.Builder.class); // DataComponentType$Builder
        addClass("net/minecraft/class_1761", CreativeModeTab.class); // ItemGroup
        addClass("net/minecraft/class_7706", CreativeModeTabs.class); // ItemGroups
        addClass("net/minecraft/class_1936", LevelAccessor.class); // WorldAccess
        addClass("net/minecraft/class_3609", FlowingFluid.class); // FlowableFluid
        addClass("net/minecraft/class_4538", LevelReader.class); // WorldView
        addClass("net/minecraft/class_2394", ParticleType.class);
        addClass("net/minecraft/class_3414", SoundEvent.class);
        addClass("net/minecraft/class_2400", SimpleParticleType.class); // DefaultParticleType
        addClass("net/minecraft/class_5348", FormattedText.class); // StringVisitable
        addClass("net/minecraft/class_2520", Tag.class); // NbtElement
        addClass("net/minecraft/class_2499", ListTag.class); // NbtList
        addClass("net/minecraft/class_2519", StringTag.class); // NbtString
        addClass("net/minecraft/class_2497", IntTag.class); // NbtInt
        addClass("net/minecraft/class_2489", DoubleTag.class); // NbtDouble
        addClass("net/minecraft/class_2503", LongTag.class); // NbtLong
        addClass("net/minecraft/class_2494", FloatTag.class); // NbtFloat
        addClass("net/minecraft/class_2481", ByteTag.class); // NbtByte

        addClass("net/minecraft/class_26", SavedDataStorage.class); // PersistentStateManager
        addClass("net/minecraft/class_18", SavedData.class); // PersistentState
        addClass("net/minecraft/class_239", HitResult.class); // HitResult
        addClass("net/minecraft/class_239$class_240", HitResult.Type.class); // HitResult$Type
        addClass("net/minecraft/class_4185", Button.class); // ButtonWidget
        addClass("net/minecraft/class_4185$class_4241", Button.OnPress.class); // ButtonWidget.PressAction
        addClass("net/minecraft/class_1311", MobCategory.class); // SpawnGroup
        addClass("net/minecraft/class_1278", WorldlyContainer.class); // SidedInventory
        addClass("net/minecraft/class_2394", ParticleOptions.class); // ItemStackParticleEffect (ParticleOptions/ItemParticleOption)
        addClass("net/minecraft/class_2392", ItemParticleOption.class); // ItemStackParticleEffect (ParticleOptions/ItemParticleOption)
        addClass("net/minecraft/class_1268", InteractionHand.class); // Hand
        addClass("net/minecraft/class_1923", ChunkPos.class); // Hand
        addClass("net/minecraft/class_5568", EntityAccess.class); // EntityLike
        addClass("net/minecraft/class_2760", Half.class); // Half (BlockHalf)
        addClass("net/minecraft/class_2778", StairsShape.class); // StairsShape (StairShape)
        addClass("net/minecraft/class_3468", Stats.class); // Stats
        addClass("net/minecraft/class_3448", StatType.class); // StatType
        addClass("net/minecraft/class_3419", SoundSource.class); // SoundCategory
        addClass("net/minecraft/class_3414", SoundEvent.class); // SoundEvent
        addClass("net/minecraft/class_3417", SoundEvents.class); // SoundEvents
        addClass("net/minecraft/class_1860", Recipe.class); // Recipe
        addClass("net/minecraft/class_3955", CraftingRecipe.class); // CraftingRecipe
        addClass("net/minecraft/class_7710", RecipeCategory.class); // CraftingRecipeCategory
        addClass("net/minecraft/class_3956", RecipeType.class); // RecipeType
        addClass("net/minecraft/class_1869", ShapedRecipe.class); // ShapedRecipe
        addClass("net/minecraft/class_1867", ShapelessRecipe.class); // ShapelessRecipe

        // Fabric API
        addClass("net/fabricmc/fabric/api/transfer/v1/item/InventoryStorage", "net/fabricmc/fabric/api/transfer/v1/item/ContainerStorage"); // InventoryStorage

        if (!isServerOnly) {
            try {
                addClass("net/minecraft/class_310", Minecraft.class); // MinecraftClient
                addClass("net/minecraft/class_327", Font.class); // TextRenderer
                addClass("net/minecraft/class_437", Screen.class);
                addClass("net/minecraft/class_364", GuiEventListener.class); // Element
                addClass("net/minecraft/class_827", BlockEntityRenderer.class);
                addClass("net/minecraft/class_638", ClientLevel.class); // ClientWorld
                addClass("net/minecraft/class_746", LocalPlayer.class); // ClientPlayerEntity
                addClass("net/minecraft/class_1074", I18n.class);
                addClass("net/minecraft/class_342", EditBox.class); // TextFieldWidget
                addClass("net/minecraft/class_339", AbstractWidget.class); // ClickableWidget
            } catch (Exception _) {
                // クライアントでない場合は次から無視する
                isServerOnly = true;
            }
        }

        // EntityLike
        addMethod("net/minecraft/class_5568", "method_5667", "getUUID");

        addMethod("net/minecraft/class_1799", "method_7909", "getItem");
        addMethod("net/minecraft/class_1792", "method_7854", "getDefaultInstance");
        addMethod("net/minecraft/class_1792", "method_7882", "getDefaultMaxStackSize");

        addMethod("net/minecraft/class_2248", "method_9541", "box");
        addMethod("net/minecraft/class_259", "method_1073", "empty");
        addMethod("net/minecraft/class_259", "method_1077", "block");
        addMethod("net/minecraft/class_259", "box", "cuboid");
        addMethod("net/minecraft/class_1735", "method_53512", "set");
        addMethod("net/minecraft/class_1735", "method_7677", "getItem");
        addMethod("net/minecraft/class_1735", "method_7680", "mayPlace");

        // CompoundTag (NbtCompound)
        addMethod("net/minecraft/class_2487", "method_10566", "put");
        addMethod("net/minecraft/class_2487", "method_10567", "putByte");
        addMethod("net/minecraft/class_2487", "method_10575", "putShort");
        addMethod("net/minecraft/class_2487", "method_10569", "putInt");
        addMethod("net/minecraft/class_2487", "method_10544", "putLong");
        addMethod("net/minecraft/class_2487", "method_10548", "putFloat");
        addMethod("net/minecraft/class_2487", "method_10549", "putDouble");
        addMethod("net/minecraft/class_2487", "method_10582", "putString");
        addMethod("net/minecraft/class_2487", "method_10580", "get");
        addMethod("net/minecraft/class_2487", "method_10545", "contains");
        addMethod("net/minecraft/class_2487", "method_10562", "getCompoundOrEmpty");

        // Container (Inventory)
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

        // SimpleContainer (SimpleInventory)
        addMethod("net/minecraft/class_1277", "method_5439", "getContainerSize");
        addMethod("net/minecraft/class_1277", "method_5442", "isEmpty");
        addMethod("net/minecraft/class_1277", "method_5438", "getItem");
        addMethod("net/minecraft/class_1277", "method_5434", "removeItem");
        addMethod("net/minecraft/class_1277", "method_5441", "removeItemNoUpdate");
        addMethod("net/minecraft/class_1277", "method_5447", "setItem");
        addMethod("net/minecraft/class_1277", "method_5431", "setChanged");
        addMethod("net/minecraft/class_1277", "method_5443", "stillValid");
        addMethod("net/minecraft/class_1277", "method_5448", "clearContent");
        addMethod("net/minecraft/class_1277", "method_18861", "countItem");
        addMethod("net/minecraft/class_1277", "method_5437", "canPlaceItem");

        // Inventory (PlayerInventory)
        addMethod("net/minecraft/class_1661", "method_5439", "getContainerSize");
        addMethod("net/minecraft/class_1661", "method_5442", "isEmpty");
        addMethod("net/minecraft/class_1661", "method_5438", "getItem");
        addMethod("net/minecraft/class_1661", "method_5434", "removeItem");
        addMethod("net/minecraft/class_1661", "method_5441", "removeItemNoUpdate");
        addMethod("net/minecraft/class_1661", "method_5447", "setItem");
        addMethod("net/minecraft/class_1661", "method_5431", "setChanged");
        addMethod("net/minecraft/class_1661", "method_5443", "stillValid");
        addMethod("net/minecraft/class_1661", "method_5448", "clearContent");
        addMethod("net/minecraft/class_1661", "method_18861", "countItem");
        addMethod("net/minecraft/class_1661", "method_5437", "canPlaceItem");

        addMethod("net/minecraft/class_1661", "method_7379", "contains");

        // Identifier
        addMethod("net/minecraft/class_2960", "method_60655", "fromNamespaceAndPath"); // of
        addMethod("net/minecraft/class_2960", "method_60654", "parse"); // of
        addMethod("net/minecraft/class_2960", "method_60656", "withDefaultNamespace"); // ofVanilla
        addMethod("net/minecraft/class_2960", "method_12829", "tryParse"); // tryParse
        addMethod("net/minecraft/class_2960", "method_43902", "tryBuild"); // tryParse
        addMethod("net/minecraft/class_2960", "method_12838", "bySeparator"); // splitOn
        addMethod("net/minecraft/class_2960", "method_60935", "tryBySeparator"); // trySplitOn

        // ServerPlayer (ServerPlayerEntity)
        addMethod("net/minecraft/class_3222", "method_5667", "getUUID");
        addMethod("net/minecraft/class_3222", "method_19538", "position");
        addMethod("net/minecraft/class_3222", "method_24515", "blockPosition");

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

        // DataComponentType
        addMethod("net/minecraft/class_9331", "method_57873", "builder"); // builder
        addMethod("net/minecraft/class_9331$class_9332", "method_57881", "persistent"); // builder

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
        addMethod("net/minecraft/class_1799", "method_7972", "copy");

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
        addMethod("net/minecraft/class_1297", "method_5805", "isAlive");
        addMethod("net/minecraft/class_1297", "method_23317", "getX");
        addMethod("net/minecraft/class_1297", "method_23318", "getY");
        addMethod("net/minecraft/class_1297", "method_23321", "getZ");
        addMethod("net/minecraft/class_1297", "method_5735", "getDirection");


        // LivingEntity
        addMethod("net/minecraft/class_1309", "method_5805", "isAlive");
        addMethod("net/minecraft/class_1309", "method_5735", "getDirection");

        // BlockEntity
        addMethod("net/minecraft/class_2586", "method_11017", "getType");
        addMethod("net/minecraft/class_2586", "method_10997", "getLevel");
        addMethod("net/minecraft/class_2586", "method_11016", "getBlockPos");
        addMethod("net/minecraft/class_2586", "method_11010", "getBlockState");
        addMethod("net/minecraft/class_2586", "method_31662", "setLevel");
        addMethod("net/minecraft/class_2586", "method_11015", "isRemoved");
        addMethod("net/minecraft/class_2586", "method_5431", "setChanged"); // markDirty

        // Block
        addMethod("net/minecraft/class_2248", "method_26160", "asBlock");
        addMethod("net/minecraft/class_2248", "method_8389", "asItem");
        addMethod("net/minecraft/class_2248", "method_9545", "getFluidState");
        addMethod("net/minecraft/class_2248", "method_36555", "defaultDestroyTime");
        addMethod("net/minecraft/class_2248", "method_9518", "getName");
        addMethod("net/minecraft/class_2248", "method_9564", "defaultBlockState");

        // BlockState
        addMethod("net/minecraft/class_2680", "method_11654", "getValue");
        addMethod("net/minecraft/class_2680", "method_11657", "setValue");
        addMethod("net/minecraft/class_2680", "method_28498", "hasProperty");

        // BlockBehaviour.BlockStateBase
        addMethod("net/minecraft/class_4970$class_4971", "method_26204", "getBlock");
        addMethod("net/minecraft/class_4970$class_4971", "method_26196", "getMenuProvider");
        addMethod("net/minecraft/class_4970$class_4971", "method_26204", "getBlock");
        addMethod("net/minecraft/class_4970$class_4971", "method_26227", "getFluidState");

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

        // Vec3d
        addMethod("net/minecraft/class_243", "method_10216", "getX");
        addMethod("net/minecraft/class_243", "method_10214", "getY");
        addMethod("net/minecraft/class_243", "method_10215", "getZ");

        // Level (World)
        addMethod("net/minecraft/class_1937", "method_8608", "isClientSide");
        addMethod("net/minecraft/class_1937", "method_8321", "getBlockEntity");
        addMethod("net/minecraft/class_1937", "method_8320", "getBlockState");
        addMethod("net/minecraft/class_1937", "method_8501", "setBlock");
        addMethod("net/minecraft/class_1937", "method_8652", "setBlock");
        addMethod("net/minecraft/class_1937", "method_8316", "getFluidState");
        addMethod("net/minecraft/class_1937", "method_8503", "getServer");

        // ServerLevel (ServerWorld)
        addMethod("net/minecraft/class_3218", "method_8503", "getServer");

        // BlockPos
        addMethod("net/minecraft/class_2338", "method_10263", "getX");
        addMethod("net/minecraft/class_2338", "method_10264", "getY");
        addMethod("net/minecraft/class_2338", "method_10260", "getZ");
        addMethod("net/minecraft/class_2338", "method_10093", "relative"); // offset
        addMethod("net/minecraft/class_2338", "method_10079", "relative"); // offset

        // BlockPos 1.20.4
        addMethod("net/minecraft/class_2338", "method_10084", "above"); // up
        addMethod("net/minecraft/class_2338", "method_10074", "below"); // down
        addMethod("net/minecraft/class_2338", "method_10095", "north");
        addMethod("net/minecraft/class_2338", "method_10072", "south");
        addMethod("net/minecraft/class_2338", "method_10078", "east");
        addMethod("net/minecraft/class_2338", "method_10067", "west");

        addMethod("net/minecraft/class_2338", "method_10086", "above"); // up(int distance)
        addMethod("net/minecraft/class_2338", "method_10087", "below"); // down(int distance)
        addMethod("net/minecraft/class_2338", "method_10076", "north");
        addMethod("net/minecraft/class_2338", "method_10077", "south");
        addMethod("net/minecraft/class_2338", "method_10089", "east");
        addMethod("net/minecraft/class_2338", "method_10088", "west");

        addMethod("net/minecraft/class_2338", "method_10069", "offset"); // add
        addMethod("net/minecraft/class_2338", "method_10081", "offset"); // add
        addMethod("net/minecraft/class_2338", "method_10059", "subtract");
        addMethod("net/minecraft/class_2338", "method_35830", "multiply");

        addMethod("net/minecraft/class_2338", "method_19455", "distManhattan"); // getManhattanDistance

        // BlockPos 1.21.11
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

        if (!isServerOnly) {
            try {
                // I18n
                addMethod("net/minecraft/class_1074", "method_4662", "get"); // translate
                addMethod("net/minecraft/class_1074", "method_4663", "exists"); // hasTranslation
            } catch (Exception _) {
                // クライアントでない場合は次から無視する
                isServerOnly = true;
            }
        }

        // FluidState
        addMethod("net/minecraft/class_3610", "method_15772", "getType"); // getFluid
        addMethod("net/minecraft/class_3610", "method_15769", "isEmpty");
        addMethod("net/minecraft/class_3610", "method_15771", "isSource"); // isStill

        // AbstractContainerMenu (ScreenHandler)
        addMethod("net/minecraft/class_1703", "method_34254", "setCarried"); // setCursorStack
        addMethod("net/minecraft/class_1703", "method_34255", "getCarried"); // getCursorStack
        addMethod("net/minecraft/class_1703", "method_7613", "canTakeItemForPickAll"); // canInsertIntoSlot
        addMethod("net/minecraft/class_1703", "method_7615", "canDragTo"); // canInsertIntoSlot

        // ChunkPos
        addMethod("net/minecraft/class_1923", "net/minecraft/class_2338#<init>(III)V", "containing");
        addMethod("net/minecraft/class_1923", "net/minecraft/core/BlockPos#<init>(III)V", "containing");

        // EntitySelector
        addField("net/minecraft/class_1301", "field_6154", "ENTITY_STILL_ALIVE");
        addField("net/minecraft/class_1301", "field_6157", "ENTITY_STILL_ALIVE");

        // Items
        addField("net/minecraft/class_1802", "field_10124", "AIR");
        addField("net/minecraft/class_1802", "field_20412", "COBBLESTONE");
        addField("net/minecraft/class_1802", "field_20391", "STONE");
        addField("net/minecraft/class_1802", "field_8270", "GRASS_BLOCK");
        addField("net/minecraft/class_1802", "field_8831", "DIRT");
        addField("net/minecraft/class_1802", "field_8460", "COARSE_DIRT");
        addField("net/minecraft/class_1802", "field_20390", "BRICKS");
        addField("net/minecraft/class_1802", "field_8342", "BRICK_SLAB");
        addField("net/minecraft/class_1802", "field_8280", "GLASS");
        addField("net/minecraft/class_1802", "field_8705", "WATER_BUCKET");

        // Blocks
        addField("net/minecraft/class_2246", "field_8162", "AIR");
        addField("net/minecraft/class_2246", "field_10445", "COBBLESTONE");
        addField("net/minecraft/class_2246", "field_10340", "STONE");
        addField("net/minecraft/class_2246", "field_10474", "GRANITE");
        addField("net/minecraft/class_2246", "field_10508", "DIORITE");
        addField("net/minecraft/class_2246", "field_10115", "ANDESITE");
        addField("net/minecraft/class_2246", "field_10219", "GRASS_BLOCK");
        addField("net/minecraft/class_2246", "field_10566", "DIRT");
        addField("net/minecraft/class_2246", "field_10253", "COARSE_DIRT");
        addField("net/minecraft/class_2246", "field_10520", "PODZOL");
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

        // ItemStack
        addField("net/minecraft/class_1799", "field_8037", "EMPTY");

        // Direction
        addField("net/minecraft/class_2350", "field_11033", "DOWN");
        addField("net/minecraft/class_2350", "field_11036", "UP");
        addField("net/minecraft/class_2350", "field_11043", "NORTH");
        addField("net/minecraft/class_2350", "field_11035", "SOUTH");
        addField("net/minecraft/class_2350", "field_11039", "WEST");
        addField("net/minecraft/class_2350", "field_11034", "EAST");

        addField("net/minecraft/class_1661", "field_7546", "player");

        // MobCategory (SpawnGroup)
        addField("net/minecraft/class_1311", "field_6302", "MONSTER");
        addField("net/minecraft/class_1311", "field_6294", "CREATURE");
        addField("net/minecraft/class_1311", "field_6303", "AMBIENT");
        addField("net/minecraft/class_1311", "field_34447", "AXOLOTLS");
        addField("net/minecraft/class_1311", "field_30092", "UNDERGROUND_WATER_CREATURE");
        addField("net/minecraft/class_1311", "field_6300", "WATER_CREATURE");
        addField("net/minecraft/class_1311", "field_24460", "WATER_AMBIENT");
        addField("net/minecraft/class_1311", "field_17715", "MISC");

        // InteractionHand (Hand)
        addField("net/minecraft/class_1268", "field_5808", "MAIN_HAND");
        addField("net/minecraft/class_1268", "field_5810", "OFF_HAND");

        // Half (BlockHalf)
        addField("net/minecraft/class_2760", "field_12619", "TOP");
        addField("net/minecraft/class_2760", "field_12617", "BOTTOM");

        // StairsShape (StairShape)
        addField("net/minecraft/class_2778", "field_12710", "STRAIGHT");
        addField("net/minecraft/class_2778", "field_12712", "INNER_LEFT");
        addField("net/minecraft/class_2778", "field_12713", "INNER_RIGHT");
        addField("net/minecraft/class_2778", "field_12708", "OUTER_LEFT");
        addField("net/minecraft/class_2778", "field_12709", "OUTER_RIGHT");

        addField("net/minecraft/class_3468", "field_15427", "BLOCK_MINED");
        addField("net/minecraft/class_3468", "field_15370", "ITEM_CRAFTED");
        addField("net/minecraft/class_3468", "field_15372", "ITEM_USED");
        addField("net/minecraft/class_3468", "field_15383", "ITEM_BROKEN");
        addField("net/minecraft/class_3468", "field_15392", "ITEM_PICKED_UP");
        addField("net/minecraft/class_3468", "field_15405", "ITEM_DROPPED");

        // SoundCategory
        addField("net/minecraft/class_3419", "field_15250", "MASTER");
        addField("net/minecraft/class_3419", "field_15253", "MUSIC");
        addField("net/minecraft/class_3419", "field_15247", "RECORDS");
        addField("net/minecraft/class_3419", "field_15252", "WEATHER");
        addField("net/minecraft/class_3419", "field_15245", "BLOCKS");
        addField("net/minecraft/class_3419", "field_15251", "HOSTILE");
        addField("net/minecraft/class_3419", "field_15254", "NEUTRAL");
        addField("net/minecraft/class_3419", "field_15248", "PLAYERS");
        addField("net/minecraft/class_3419", "field_15256", "AMBIENT");
        addField("net/minecraft/class_3419", "field_15246", "VOICE");

        // SoundEvents
        addField("net/minecraft/class_3417", "field_14562", "ZOMBIE_ATTACK_WOODEN_DOOR"); // ENTITY_ZOMBIE_ATTACK_WOODEN_DOOR
        addField("net/minecraft/class_3417", "field_14670", "ZOMBIE_ATTACK_IRON_DOOR"); // ENTITY_ZOMBIE_ATTACK_IRON_DOOR
        addField("net/minecraft/class_3417", "field_14742", "ZOMBIE_BREAK_WOODEN_DOOR"); // ENTITY_ZOMBIE_BREAK_WOODEN_DOOR
        addField("net/minecraft/class_3417", "field_14727", "ANVIL_FALL"); // BLOCK_ANVIL_FALL
    }
}
