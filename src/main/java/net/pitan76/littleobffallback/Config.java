package net.pitan76.littleobffallback;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.api.entrypoint.EntrypointContainer;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.*;

public class Config {
    private static final File configDir = FabricLoader.getInstance().getConfigDir().toFile();
    private static final File file = new File(configDir, "littleobffallback.json");

    private static Map<String, Object> map = new HashMap<>();

    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public static void init() {
        if (!file.exists()) {
            // Default values
            map.put("enabled", true);
            map.put("targetPackages", createTargetPackages());
            save();
        }
        load();
    }

    public static boolean isEnabled() {
        return getBoolean("enabled");
    }

    public static List<String> createTargetPackages() {
        List<ModContainer> targetMods = FabricLoader.getInstance().getAllMods().stream()
                .filter(mod -> mod.getMetadata().getDependencies().stream().anyMatch(
                        depend -> depend.getModId().equals("mcpitanlib"))).toList();

        targetMods.forEach(container -> System.out.println("[LittleObfFallback] Detected mod: " + container.getMetadata().getId()));

        return targetMods.stream().map(mod -> {
            for (EntrypointContainer<Object> container : FabricLoader.getInstance().getEntrypointContainers("main", Object.class)) {
                if (container.getProvider().getMetadata().getId().equals(mod.getMetadata().getId())) {
                    String definition = container.getDefinition().replace('.', '/');;

                    // class名を取り除く
                    String packageName = definition.contains("/") ? definition.substring(0, definition.lastIndexOf('/')) : definition;

                    // エントリーポイントのパッケージ名から、"/fabric", "/forge", "/neoforge"で終わる部分を削除
                    if (packageName.endsWith("/fabric") || packageName.endsWith("/forge") || packageName.endsWith("/neoforge")) {
                        packageName = packageName.substring(0, packageName.lastIndexOf('/'));
                    }

//                    System.out.printf("[LittleObfFallback] Target mod: %s, Entry point class: %s%n", mod.getMetadata().getId(), packageName);
                    return packageName + "/";
                }
            }
            return "";
        }).toList();
    }

    public static List<String> getTargetPackages() {
        if (!isEnabled()) return Collections.emptyList();
        return getStringList("targetPackages");
    }

    public static void load() {
        // Load littleobffallback.json
        if (!file.exists()) return;

        try (var reader = new FileReader(file)) {
            map = gson.fromJson(reader, map.getClass());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void save() {
        // Save littleobffallback.json
        try {
            String json = gson.toJson(map);
            if (configDir.exists() || configDir.mkdirs())
                file.createNewFile();

            try (var writer = new FileWriter(file)) {
                writer.write(json);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void put(String key, Object value) {
        map.put(key, value);
    }

    public static Object get(String key) {
        return map.get(key);
    }

    public static boolean contains(String key) {
        return map.containsKey(key);
    }

    public static void remove(String key) {
        map.remove(key);
    }

    public static boolean getBoolean(String key) {
        if (!map.containsKey(key)) return true;
        return (boolean) map.get(key);
    }

    public static int getInt(String key) {
        return (int) map.get(key);
    }

    public static double getDouble(String key) {
        return (double) map.get(key);
    }

    public static String getString(String key) {
        return (String) map.get(key);
    }

    public static List<String> getStringList(String key) {
        return (List<String>) map.get(key);
    }
}
