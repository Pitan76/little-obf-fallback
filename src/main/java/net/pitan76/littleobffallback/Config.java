package net.pitan76.littleobffallback;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
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
            map.put("modsHash", calcModsHash()); // modが追加されたときとかの対応のため
            save();
            return;
        }
        load();

        String currentHash = calcModsHash();
        String savedHash = (String) map.get("modsHash");

        if (!Objects.equals(currentHash, savedHash)) {
            List<String> current = getStringList("targetPackages");
            List<String> auto = createTargetPackages();

            Set<String> merged = new LinkedHashSet<>(current);
            merged.addAll(auto);

            map.put("targetPackages", new ArrayList<>(merged));
            map.put("modsHash", currentHash);

            save();
        }
    }

    public static boolean isEnabled() {
        return getBoolean("enabled");
    }

    private static String calcModsHash() {
        List<String> mods = FabricLoader.getInstance().getAllMods().stream()
                .map(mod ->
                        mod.getMetadata().getId() + ":"
                                + mod.getMetadata().getVersion().getFriendlyString())
                .sorted().toList();

        String state = String.join(",", mods);
        return Integer.toHexString(state.hashCode());
    }

    public static List<String> createTargetPackages() {
        List<ModContainer> targetMods = FabricLoader.getInstance().getAllMods().stream()
                .filter(mod -> mod.getMetadata().getDependencies().stream().anyMatch(
                        depend -> depend.getModId().equals("mcpitanlib"))).toList();

        targetMods.forEach(container -> System.out.println("[LittleObfFallback] Detected mod: " + container.getMetadata().getId()));

        List<EntrypointContainer<Object>> entries =
                FabricLoader.getInstance().getEntrypointContainers("main", Object.class);

        return targetMods.stream().map(mod -> {
            for (EntrypointContainer<Object> container : entries) {
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
        }).filter(s -> !s.isEmpty()).toList();
    }

    public static List<String> getTargetPackages() {
        if (!isEnabled()) return Collections.emptyList();
        return getStringList("targetPackages");
    }

    public static void load() {
        // Load littleobffallback.json
        if (!file.exists()) return;

        try (var reader = new FileReader(file)) {
            map = gson.fromJson(reader, new TypeToken<Map<String, Object>>(){}.getType());
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

    public static boolean getBoolean(String key) {
        if (!map.containsKey(key)) return true;
        Object v = map.get(key);
        return v instanceof Boolean b ? b : true;
    }

    public static List<String> getStringList(String key) {
        Object v = map.get(key);
        if (v instanceof List<?> list) {
            return list.stream()
                    .filter(String.class::isInstance)
                    .map(String.class::cast)
                    .toList();
        }
        return Collections.emptyList();
    }
}
