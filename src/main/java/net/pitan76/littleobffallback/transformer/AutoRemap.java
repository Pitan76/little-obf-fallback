package net.pitan76.littleobffallback.transformer;

public class AutoRemap {
    // Intermediary名やフィールド名を公式名に変換する簡易リマップ
    public static String autoRemapClass(String name) {
        System.out.println("[LittleObfFallback] autoRemapClass: " + name);

        return switch (name) {
            case "net/minecraft/class_1297" -> "net/minecraft/world/entity/Entity";
            case "net/minecraft/class_1309" -> "net/minecraft/world/entity/LivingEntity";
            case "net/minecraft/class_2246" -> "net/minecraft/world/level/block/Blocks";
            case "net/minecraft/class_2248" -> "net/minecraft/world/level/block/Block";
            case "net/minecraft/class_4970" -> "net/minecraft/world/level/block/state/BlockBehaviour";
            case "net/minecraft/class_4970$class_2251" ->
                    "net/minecraft/world/level/block/state/BlockBehaviour$Properties";
            case "net/minecraft/class_2350" -> "net/minecraft/core/Direction";
            case "net/minecraft/class_1799" -> "net/minecraft/world/item/ItemStack";
            default -> name;
        };

    }

    public static String autoRemapField(String name) {
        System .out.println("[LittleObfFallback] autoRemapField: " + name);

        switch (name) {
            case "field_11043": return "NORTH";
            case "field_11035": return "SOUTH";
            case "field_11034": return "EAST";
            case "field_11039": return "WEST";
            case "field_11036": return "UP";
            case "field_11033": return "DOWN";
        }
        if (name.contains("field_10124")) return name.replace("field_10124", "AIR");
        if (name.contains("field_10340")) return name.replace("field_10340", "STONE");
        if (name.contains("field_10474")) return name.replace("field_10474", "GRANITE");
        if (name.contains("field_10508")) return name.replace("field_10508", "DIORITE");
        if (name.contains("field_10115")) return name.replace("field_10115", "ANDESITE");
        if (name.contains("field_10219")) return name.replace("field_10219", "GRASS_BLOCK");
        if (name.contains("field_10566")) return name.replace("field_10566", "DIRT");
        if (name.contains("field_10253")) return name.replace("field_10253", "COARSE_DIRT");
        if (name.contains("field_10520")) return name.replace("field_10520", "PODZOL");
        if (name.contains("field_10445")) return name.replace("field_10445", "COBBLESTONE");
        if (name.contains("field_10161")) return name.replace("field_10161", "OAK_PLANKS");
        if (name.contains("field_9975")) return name.replace("field_9975", "SPRUCE_PLANKS");
        if (name.contains("field_10148")) return name.replace("field_10148", "BIRCH_PLANKS");
        if (name.contains("field_10334")) return name.replace("field_10334", "JUNGLE_PLANKS");
        if (name.contains("field_10218")) return name.replace("field_10218", "ACACIA_PLANKS");
        if (name.contains("field_42751")) return name.replace("field_42751", "CHERRY_PLANKS");
        if (name.contains("field_10075")) return name.replace("field_10075", "DARK_OAK_PLANKS");
        if (name.contains("field_54734")) return name.replace("field_54734", "PALE_OAK_WOOD");
        if (name.contains("field_54735")) return name.replace("field_54735", "PALE_OAK_PLANKS");
        if (name.contains("field_37577")) return name.replace("field_37577", "MANGROVE_PLANKS");
        if (name.contains("field_40294")) return name.replace("field_40294", "BAMBOO_PLANKS");
        if (name.contains("field_22126")) return name.replace("field_22126", "CRIMSON_PLANKS");
        if (name.contains("field_22127")) return name.replace("field_22127", "WARPED_PLANKS");
        if (name.contains("field_10033")) return name.replace("field_10033", "GLASS");
        if (name.contains("field_9979")) return name.replace("field_9979", "SANDSTONE");
        if (name.contains("field_10344")) return name.replace("field_10344", "RED_SANDSTONE");
        if (name.contains("field_10104")) return name.replace("field_10104", "BRICKS");
        if (name.contains("field_10056")) return name.replace("field_10056", "STONE_BRICKS");
        if (name.contains("field_10266")) return name.replace("field_10266", "NETHER_BRICKS");
        if (name.contains("field_9986")) return name.replace("field_9986", "RED_NETHER_BRICKS");
        if (name.contains("field_10462")) return name.replace("field_10462", "END_STONE_BRICKS");
        if (name.contains("field_10360")) return name.replace("field_10360", "SMOOTH_STONE");
        if (name.contains("field_10467")) return name.replace("field_10467", "SMOOTH_SANDSTONE");
        if (name.contains("field_10483")) return name.replace("field_10483", "SMOOTH_RED_SANDSTONE");
        if (name.contains("field_9978")) return name.replace("field_9978", "SMOOTH_QUARTZ");
        if (name.contains("field_10490")) return name.replace("field_10490", "YELLOW_WOOL");
        if (name.contains("field_10028")) return name.replace("field_10028", "LIME_WOOL");
        if (name.contains("field_10459")) return name.replace("field_10459", "PINK_WOOL");
        if (name.contains("field_10423")) return name.replace("field_10423", "GRAY_WOOL");
        if (name.contains("field_10222")) return name.replace("field_10222", "LIGHT_GRAY_WOOL");
        if (name.contains("field_10619")) return name.replace("field_10619", "CYAN_WOOL");
        if (name.contains("field_10259")) return name.replace("field_10259", "PURPLE_WOOL");
        if (name.contains("field_10514")) return name.replace("field_10514", "BLUE_WOOL");
        if (name.contains("field_10113")) return name.replace("field_10113", "BROWN_WOOL");
        if (name.contains("field_10170")) return name.replace("field_10170", "GREEN_WOOL");
        if (name.contains("field_10314")) return name.replace("field_10314", "RED_WOOL");
        if (name.contains("field_10146")) return name.replace("field_10146", "BLACK_WOOL");
        if (name.contains("field_10153")) return name.replace("field_10153", "QUARTZ_BLOCK");
        if (name.contains("field_10286")) return name.replace("field_10286", "PURPUR_BLOCK");

        return name;
    }
}
