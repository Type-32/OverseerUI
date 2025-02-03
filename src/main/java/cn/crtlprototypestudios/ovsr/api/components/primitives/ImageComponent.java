package cn.crtlprototypestudios.ovsr.api.components.primitives;

import cn.crtlprototypestudios.ovsr.api.components.BaseComponent;
import cn.crtlprototypestudios.ovsr.api.xml.ComponentData;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import org.apache.commons.io.IOUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Base64;
import java.util.concurrent.CompletableFuture;

public class ImageComponent extends BaseComponent {
    private ResourceLocation texture;
    private String url;
    private int textureWidth = 256;
    private int textureHeight = 256;
    private int u = 0;
    private int v = 0;
    private int regionWidth = -1;
    private int regionHeight = -1;
    private float scale = 1.0f;
    private ScaleMode scaleMode = ScaleMode.FIT;
    private int tintColor = 0xFFFFFF;
    private float alpha = 1.0f;
    private boolean loading = false;
    private boolean error = false;
    private ResourceLocation errorTexture = new ResourceLocation("ovsr", "textures/gui/error.png");
    private ResourceLocation loadingTexture = new ResourceLocation("ovsr", "textures/gui/loading.png");
    private int animationTicks = 0;

    public ImageComponent(ComponentData data) {
        super(
                data.getIntAttribute("x", 0),
                data.getIntAttribute("y", 0),
                data.getIntAttribute("width", 64),
                data.getIntAttribute("height", 64)
        );

        // Parse source - can be either a resource location or URL
        String source = data.getChildText("source");
        if (source != null) {
            if (source.startsWith("http://") || source.startsWith("https://")) {
                setUrl(source);
            } else {
                setTexture(new ResourceLocation(source));
            }
        }

        // Parse other attributes
        this.u = data.getIntAttribute("u", 0);
        this.v = data.getIntAttribute("v", 0);
        this.regionWidth = data.getIntAttribute("region-width", -1);
        this.regionHeight = data.getIntAttribute("region-height", -1);
        this.scale = Float.parseFloat(data.getAttribute("scale", "1.0"));
        this.scaleMode = ScaleMode.valueOf(data.getAttribute("scale-mode", "FIT").toUpperCase());
        this.tintColor = Integer.parseInt(data.getAttribute("tint-color", "FFFFFF"), 16);
        this.alpha = Float.parseFloat(data.getAttribute("alpha", "1.0"));
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        if (!visible) return;

        animationTicks++;

        // Setup rendering
        RenderSystem.enableBlend();
        RenderSystem.setShaderColor(
                ((tintColor >> 16) & 0xFF) / 255f,
                ((tintColor >> 8) & 0xFF) / 255f,
                (tintColor & 0xFF) / 255f,
                alpha
        );

        ResourceLocation renderTexture = texture;
        if (loading) {
            renderTexture = loadingTexture;
            renderLoadingAnimation(graphics);
        } else if (error || texture == null) {
            renderTexture = errorTexture;
        }

        // Calculate rendering dimensions based on scale mode
        int renderWidth = width;
        int renderHeight = height;
        int renderX = x;
        int renderY = y;

        if (renderTexture != null) {
            switch (scaleMode) {
                case FIT -> {
                    float scaleX = (float) width / (regionWidth > 0 ? regionWidth : textureWidth);
                    float scaleY = (float) height / (regionHeight > 0 ? regionHeight : textureHeight);
                    float scale = Math.min(scaleX, scaleY);
                    renderWidth = (int) (textureWidth * scale);
                    renderHeight = (int) (textureHeight * scale);
                    renderX = x + (width - renderWidth) / 2;
                    renderY = y + (height - renderHeight) / 2;
                }
                case FILL -> {
                    float scaleX = (float) width / (regionWidth > 0 ? regionWidth : textureWidth);
                    float scaleY = (float) height / (regionHeight > 0 ? regionHeight : textureHeight);
                    float scale = Math.max(scaleX, scaleY);
                    renderWidth = (int) (textureWidth * scale);
                    renderHeight = (int) (textureHeight * scale);
                    renderX = x + (width - renderWidth) / 2;
                    renderY = y + (height - renderHeight) / 2;
                }
                case STRETCH -> {
                    // Use full width/height
                }
                case NONE -> {
                    renderWidth = (int) (textureWidth * scale);
                    renderHeight = (int) (textureHeight * scale);
                    renderX = x + (width - renderWidth) / 2;
                    renderY = y + (height - renderHeight) / 2;
                }
            }

            // Draw the texture
            if (regionWidth > 0 && regionHeight > 0) {
                graphics.blit(renderTexture, renderX, renderY, renderWidth, renderHeight,
                        u, v, regionWidth, regionHeight, textureWidth, textureHeight);
            } else {
                graphics.blit(renderTexture, renderX, renderY, renderWidth, renderHeight,
                        0, 0, textureWidth, textureHeight, textureWidth, textureHeight);
            }
        }

        // Reset color
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.disableBlend();
    }

    private void renderLoadingAnimation(GuiGraphics graphics) {
        // Render a spinning animation
        float rotation = (animationTicks % 360) * 2;
        graphics.pose().pushPose();
        graphics.pose().translate(x + width / 2f, y + height / 2f, 0);
        graphics.pose().rotateAround(com.mojang.math.Axis.ZP.rotationDegrees(rotation), 0, 0, 0);
        graphics.pose().translate(-width / 4f, -height / 4f, 0);

        graphics.blit(loadingTexture, 0, 0, width / 2, height / 2,
                0, 0, 16, 16, 16, 16);

        graphics.pose().popPose();
    }

    public void setUrl(String url) {
        this.url = url;
        this.loading = true;
        this.error = false;

        CompletableFuture.runAsync(() -> {
            try {
                URL imageUrl = new URL(url);
                byte[] imageData = IOUtils.toByteArray(imageUrl);

                // Generate a unique identifier for this image
                String base64Hash = Base64.getEncoder().encodeToString(
                        url.getBytes()
                ).substring(0, 8);
                ResourceLocation textureLocation = new ResourceLocation(
                        "ovsr",
                        "dynamic/" + base64Hash
                );

                // Create NativeImage directly from bytes
                NativeImage nativeImage = NativeImage.read(
                        new ByteArrayInputStream(imageData)
                );

                // Upload texture on the main thread
                Minecraft.getInstance().execute(() -> {
                    try {
                        Minecraft.getInstance().getTextureManager().register(
                                textureLocation,
                                new net.minecraft.client.renderer.texture.DynamicTexture(nativeImage)
                        );
                        this.texture = textureLocation;
                        this.textureWidth = nativeImage.getWidth();
                        this.textureHeight = nativeImage.getHeight();
                        this.loading = false;
                    } catch (Exception e) {
                        this.error = true;
                        this.loading = false;
                        nativeImage.close();
                    }
                });
            } catch (IOException e) {
                this.error = true;
                this.loading = false;
            }
        });
    }

    private void uploadTexture(ResourceLocation location, BufferedImage bufferedImage) {
        try {
            // Convert BufferedImage to NativeImage
            NativeImage nativeImage = new NativeImage(
                    NativeImage.Format.RGBA,
                    bufferedImage.getWidth(),
                    bufferedImage.getHeight(),
                    false  // useStb
            );

            // Copy pixel data
            for (int x = 0; x < bufferedImage.getWidth(); x++) {
                for (int y = 0; y < bufferedImage.getHeight(); y++) {
                    int rgb = bufferedImage.getRGB(x, y);
                    int alpha = (rgb >> 24) & 0xFF;
                    int red = (rgb >> 16) & 0xFF;
                    int green = (rgb >> 8) & 0xFF;
                    int blue = rgb & 0xFF;

                    // ABGR format for NativeImage
                    nativeImage.setPixelRGBA(x, y,
                            (alpha << 24) | (blue << 16) | (green << 8) | red
                    );
                }
            }

            Minecraft.getInstance().getTextureManager().register(
                    location,
                    new net.minecraft.client.renderer.texture.DynamicTexture(nativeImage)
            );
        } catch (Exception e) {
            this.error = true;
            this.loading = false;
//            nativeImage.close();  // Don't forget to close the NativeImage
        }
    }

    public void setTexture(ResourceLocation texture) {
        this.texture = texture;
        this.loading = false;
        this.error = false;
    }

    public enum ScaleMode {
        FIT,      // Maintain aspect ratio, fit within bounds
        FILL,     // Maintain aspect ratio, fill bounds
        STRETCH,  // Stretch to fill bounds
        NONE      // No scaling, use original size
    }

    // Getters and setters
    public ResourceLocation getTexture() { return texture; }
    public String getUrl() { return url; }
    public float getScale() { return scale; }
    public void setScale(float scale) { this.scale = scale; }
    public ScaleMode getScaleMode() { return scaleMode; }
    public void setScaleMode(ScaleMode scaleMode) { this.scaleMode = scaleMode; }
    public int getTintColor() { return tintColor; }
    public void setTintColor(int tintColor) { this.tintColor = tintColor; }
    public float getAlpha() { return alpha; }
    public void setAlpha(float alpha) { this.alpha = alpha; }
}
