package cn.crtlprototypestudios.ovsr.api.components.primitives;

import cn.crtlprototypestudios.ovsr.api.components.BaseComponent;
import cn.crtlprototypestudios.ovsr.api.xml.ComponentData;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import org.joml.Quaternionf;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

public class IconComponent extends BaseComponent {
    private static final ConcurrentHashMap<String, ResourceLocation> PLAYER_SKIN_CACHE = new ConcurrentHashMap<>();

    private IconType type = IconType.PLAYER_HEAD;
    private String identifier; // Player name or entity ID
    private ResourceLocation skinTexture;
    private LivingEntity entity;
    private float rotation = 0f;
    private boolean animated = true;
    private boolean loading = false;
    private float scale = 1.0f;

    public IconComponent(ComponentData data) {
        super(
                data.getIntAttribute("x", 0),
                data.getIntAttribute("y", 0),
                data.getIntAttribute("width", 32),
                data.getIntAttribute("height", 32)
        );

        // Parse attributes
        this.type = IconType.valueOf(data.getAttribute("type", "PLAYER_HEAD").toUpperCase());
        this.identifier = data.getChildText("identifier");
        this.animated = data.getBoolAttribute("animated", true);
        this.scale = data.getFloatAttribute("scale", 1.0f);

        // Initialize based on type
        if (type == IconType.PLAYER_HEAD) {
            initializePlayerHead();
        } else if (type == IconType.ENTITY_HEAD) {
            initializeEntityHead();
        }
    }

    private void initializePlayerHead() {
        if (identifier == null || identifier.isEmpty()) return;

        // Check cache first
        skinTexture = PLAYER_SKIN_CACHE.get(identifier.toLowerCase());
        if (skinTexture != null) return;

        // Load skin asynchronously
        loading = true;
        CompletableFuture.runAsync(() -> {
            GameProfile profile = new GameProfile(UUID.randomUUID(), identifier);
            Minecraft.getInstance().getSkinManager().registerSkins(
                    profile,
                    (type, texture, profileTexture) -> {
                        if (type == MinecraftProfileTexture.Type.SKIN) {
                            skinTexture = texture;
                            PLAYER_SKIN_CACHE.put(identifier.toLowerCase(), texture);
                            loading = false;
                        }
                    },
                    true
            );
        });
    }

    private void initializeEntityHead() {
        if (identifier == null || identifier.isEmpty()) return;

        try {
            ResourceLocation entityId = new ResourceLocation(identifier);
            EntityType<?> entityType = EntityType.byString(identifier).orElse(null);
            if (entityType != null) {
                entity = (LivingEntity) entityType.create(
                        Minecraft.getInstance().level
                );
            }
        } catch (Exception e) {
            // Handle invalid entity ID
        }
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        if (!visible) return;

        if (animated) {
            rotation += 1f;
            if (rotation >= 360f) rotation = 0f;
        }

        switch (type) {
            case PLAYER_HEAD -> renderPlayerHead(graphics);
            case ENTITY_HEAD -> renderEntityHead(graphics);
        }
    }

    private void renderPlayerHead(GuiGraphics graphics) {
        if (skinTexture == null) {
            // Render loading indicator or default head
            renderDefaultHead(graphics);
            return;
        }

        // Setup rendering
        graphics.pose().pushPose();
        graphics.pose().translate(x + width / 2.0f, y + height / 2.0f, 100.0f);
        graphics.pose().scale(scale * -width / 16.0f, scale * height / 16.0f, scale * 16.0f);
        graphics.pose().mulPose(new Quaternionf().rotationXYZ(
                (float) Math.toRadians(180),
                (float) Math.toRadians(rotation),
                0f
        )); // TODO Check if it's mulPose or mulPoseMatrix

        // Bind skin texture
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShaderTexture(0, skinTexture);

        // Render head overlay
        graphics.blit(
                skinTexture,
                -4, -4, 8, 8,
                32, 16, 8, 8,
                64, 64
        );

        // Render base head
        graphics.blit(
                skinTexture,
                -4, -4, 8, 8,
                8, 16, 8, 8,
                64, 64
        );

        graphics.pose().popPose();
        RenderSystem.disableBlend();
    }

    private void renderEntityHead(GuiGraphics graphics) {
        if (entity == null) {
            renderDefaultHead(graphics);
            return;
        }

        EntityRenderDispatcher dispatcher = Minecraft.getInstance().getEntityRenderDispatcher();

        graphics.pose().pushPose();
        graphics.pose().translate(x + width / 2.0f, y + height / 2.0f, 100.0f);
        graphics.pose().scale(scale * -width / 16.0f, scale * height / 16.0f, scale * 16.0f);
        graphics.pose().mulPose(new Quaternionf().rotationXYZ(
                (float) Math.toRadians(180),
                (float) Math.toRadians(rotation),
                0f
        )); // TODO Check if it's mulPose or mulPoseMatrix

        // Create render buffers
        MultiBufferSource.BufferSource bufferSource = Minecraft.getInstance().renderBuffers().bufferSource();

        // Render entity head
        dispatcher.render(
                entity,
                0, 0, 0,
                0f, 1f,
                graphics.pose(),
                bufferSource,
                0xF000F0
        );

        bufferSource.endBatch();
        graphics.pose().popPose();
    }

    private void renderDefaultHead(GuiGraphics graphics) {
        ResourceLocation steve = new ResourceLocation("textures/entity/steve.png");

        graphics.pose().pushPose();
        graphics.pose().translate(x + width / 2.0f, y + height / 2.0f, 0f);
        graphics.pose().scale(scale, scale, 1f);

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShaderTexture(0, steve);

        graphics.blit(
                steve,
                -8, -8, 16, 16,
                8, 8, 8, 8,
                64, 64
        );

        graphics.pose().popPose();
        RenderSystem.disableBlend();
    }

    public enum IconType {
        PLAYER_HEAD,
        ENTITY_HEAD
    }

    // Getters and setters
    public void setIdentifier(String identifier) {
        this.identifier = identifier;
        if (type == IconType.PLAYER_HEAD) {
            initializePlayerHead();
        } else if (type == IconType.ENTITY_HEAD) {
            initializeEntityHead();
        }
    }

    public void setRotation(float rotation) {
        this.rotation = rotation;
    }

    public void setAnimated(boolean animated) {
        this.animated = animated;
    }

    public void setScale(float scale) {
        this.scale = scale;
    }
}
