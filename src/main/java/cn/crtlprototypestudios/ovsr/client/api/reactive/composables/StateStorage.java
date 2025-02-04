package cn.crtlprototypestudios.ovsr.client.api.reactive.composables;

import cn.crtlprototypestudios.ovsr.Ovsr;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraft.client.Minecraft;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.ConcurrentHashMap;

public class StateStorage {
    private static final Path STORAGE_DIR = Minecraft.getInstance().gameDirectory.toPath().resolve(String.format("config/%s/state", Ovsr.MODID));
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final ConcurrentHashMap<String, Object> memoryCache = new ConcurrentHashMap<>();

    static {
        try {
            Files.createDirectories(STORAGE_DIR);
        } catch (IOException e) {
            throw new RuntimeException("Failed to create state storage directory", e);
        }
    }

    public static <T> T get(String key, Class<T> type, T defaultValue) {
        // Check memory cache first
        if (memoryCache.containsKey(key)) {
            return type.cast(memoryCache.get(key));
        }

        // Try to load from disk
        Path filePath = STORAGE_DIR.resolve(key + ".json");
        if (Files.exists(filePath)) {
            try (Reader reader = Files.newBufferedReader(filePath)) {
                T value = GSON.fromJson(reader, type);
                memoryCache.put(key, value);
                return value;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return defaultValue;
    }

    public static void set(String key, Object value) {
        // Update memory cache
        memoryCache.put(key, value);

        // Save to disk
        Path filePath = STORAGE_DIR.resolve(key + ".json");
        try (Writer writer = Files.newBufferedWriter(filePath)) {
            GSON.toJson(value, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void clear(String key) {
        memoryCache.remove(key);
        try {
            Files.deleteIfExists(STORAGE_DIR.resolve(key + ".json"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}