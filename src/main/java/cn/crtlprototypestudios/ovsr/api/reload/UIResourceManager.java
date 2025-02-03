package cn.crtlprototypestudios.ovsr.api.reload;

import cn.crtlprototypestudios.ovsr.Ovsr;
import cn.crtlprototypestudios.ovsr.api.screen.BaseScreen;
import cn.crtlprototypestudios.ovsr.api.xml.ComponentData;
import com.google.common.hash.Hashing;
import net.minecraft.client.Minecraft;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class UIResourceManager {
    private static UIResourceManager INSTANCE;

    private final Path uiDirectory;
    private final WatchService watchService;
    private final Map<Path, String> fileHashes;
    private final Map<String, UIDefinition> definitions;
    private final Map<String, BaseScreen> activeScreens;
    private volatile boolean running;
    private Thread watchThread;

    private UIResourceManager() throws IOException {
        this.uiDirectory = Paths.get("config", "ovsr", "ui");
        Files.createDirectories(this.uiDirectory);
        this.watchService = FileSystems.getDefault().newWatchService();
        this.fileHashes = new ConcurrentHashMap<>();
        this.definitions = new ConcurrentHashMap<>();
        this.activeScreens = new ConcurrentHashMap<>();

        // Register directory for watching
        this.uiDirectory.register(watchService,
                StandardWatchEventKinds.ENTRY_CREATE,
                StandardWatchEventKinds.ENTRY_MODIFY,
                StandardWatchEventKinds.ENTRY_DELETE
        );
    }

    public static UIResourceManager getInstance() {
        if (INSTANCE == null) {
            try {
                INSTANCE = new UIResourceManager();
            } catch (IOException e) {
                Ovsr.LOGGER.error("Failed to initialize UI Resource Manager", e);
                throw new RuntimeException(e);
            }
        }
        return INSTANCE;
    }

    public void startWatching() {
        if (running) return;

        running = true;
        watchThread = new Thread(this::watcherLoop, "UI-Watcher");
        watchThread.setDaemon(true);
        watchThread.start();

        // Initial load of all UI definitions
        loadAllDefinitions();
    }

    public void stopWatching() {
        running = false;
        if (watchThread != null) {
            watchThread.interrupt();
            watchThread = null;
        }
    }

    private void watcherLoop() {
        try {
            while (running) {
                WatchKey key = watchService.take();
                for (WatchEvent<?> event : key.pollEvents()) {
                    if (event.kind() == StandardWatchEventKinds.OVERFLOW) {
                        continue;
                    }

                    Path fileName = (Path) event.context();
                    Path fullPath = uiDirectory.resolve(fileName);

                    if (fileName.toString().endsWith(".xml")) {
                        if (event.kind() == StandardWatchEventKinds.ENTRY_DELETE) {
                            handleFileDeleted(fileName.toString());
                        } else {
                            handleFileModified(fullPath);
                        }
                    }
                }
                key.reset();
            }
        } catch (InterruptedException e) {
            // Normal shutdown
        } catch (Exception e) {
            Ovsr.LOGGER.error("Error in UI watcher thread", e);
        }
    }

    private void handleFileModified(Path path) {
        try {
            String content = Files.readString(path);
            String newHash = Hashing.sha256().hashString(content, StandardCharsets.UTF_8).toString();
            String oldHash = fileHashes.get(path);

            if (!newHash.equals(oldHash)) {
                fileHashes.put(path, newHash);
                reloadDefinition(path.getFileName().toString(), content);
            }
        } catch (IOException e) {
            Ovsr.LOGGER.error("Failed to read UI definition file: {}", path, e);
        }
    }

    private void handleFileDeleted(String fileName) {
        definitions.remove(fileName);
        // Notify any active screens that use this definition
        notifyScreensOfChange(fileName);
    }

    private void loadAllDefinitions() {
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(uiDirectory, "*.xml")) {
            for (Path path : stream) {
                handleFileModified(path);
            }
        } catch (IOException e) {
            Ovsr.LOGGER.error("Failed to load UI definitions", e);
        }
    }

    public void registerScreen(String definitionId, BaseScreen screen) {
        activeScreens.put(definitionId, screen);
    }

    public void unregisterScreen(String definitionId) {
        activeScreens.remove(definitionId);
    }

    private void notifyScreensOfChange(String definitionId) {
        BaseScreen screen = activeScreens.get(definitionId);
        if (screen != null) {
            Minecraft.getInstance().execute(() -> {
                screen.reloadLayout();
            });
        }
    }

    public UIDefinition getDefinition(String id) {
        return definitions.get(id);
    }

    private void reloadDefinition(String id, String content) {
        try {
            UIDefinition definition = new UIDefinition(id, ComponentData.fromXML(content));
            definitions.put(id, definition);
            notifyScreensOfChange(id);
        } catch (Exception e) {
            Ovsr.LOGGER.error("Failed to reload UI definition: {}", id, e);
        }
    }
}
