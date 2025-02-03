package cn.crtlprototypestudios.ovsr.api.screen;

import cn.crtlprototypestudios.ovsr.Ovsr;
import cn.crtlprototypestudios.ovsr.api.components.BaseComponent;
import cn.crtlprototypestudios.ovsr.api.components.ContainerComponent;
import cn.crtlprototypestudios.ovsr.api.components.primitives.*;
import cn.crtlprototypestudios.ovsr.api.template.TemplateManager;
import cn.crtlprototypestudios.ovsr.api.xml.ComponentData;
import net.minecraft.network.chat.Component;

import java.util.HashMap;
import java.util.Map;

// TestScreen.java
public class TestScreen extends BaseScreen {
    public TestScreen() {
        super(Component.literal("My Screen"), "my_screen.xml");
    }

    @Override
    protected void initComponents() {
        try {
            // Load and parse XML layout
            String xmlLayout = """
                <?xml version="1.0" encoding="UTF-8"?>
                <screen>
                    <!-- Title Label -->
                    <label
                        x="10"
                        y="10"
                        text="Test Screen Components"
                        color="FFFFFF"
                        scale="1.5"
                        shadow="true"
                    />
                    
                    <!-- Test buttons with different styles -->
                    <button
                        x="10"
                        y="30"
                        width="100"
                        height="20"
                        text="Vanilla Button"
                        style="VANILLA"
                        active="true"
                    />
                    
                    <button
                        x="10"
                        y="55"
                        width="100"
                        height="20"
                        text="Flat Button"
                        style="FLAT"
                    />
                    
                    <button
                        x="10"
                        y="80"
                        width="100"
                        height="20"
                        text="Outline Button"
                        style="OUTLINE"
                    />
                    
                    <!-- Input box -->
                    <inputbox
                        x="120"
                        y="30"
                        width="150"
                        height="20"
                        placeholder="Enter text here..."
                        max-length="32"
                    />
                    
                    <!-- Checkboxes with different styles -->
                    <checkbox
                        x="120"
                        y="55"
                        text="Vanilla Checkbox"
                        style="VANILLA"
                        checked="false"
                    />
                    
                    <checkbox
                        x="120"
                        y="80"
                        text="Modern Checkbox"
                        style="MODERN"
                        checked="true"
                    />
                    
                    <!-- Progress bars -->
                    <progressbar
                        x="10"
                        y="110"
                        width="200"
                        height="10"
                        progress="0.7"
                        style="MODERN"
                        show-percentage="true"
                        fill-color="00FF00"
                    />
                    
                    <progressbar
                        x="10"
                        y="130"
                        width="200"
                        height="10"
                        progress="0.4"
                        style="GRADIENT"
                        fill-color="FF0000"
                        fill-color-secondary="FFFF00"
                    />
                    
                    <!-- Container example -->
                    <container
                        x="300"
                        y="30"
                        width="200"
                        height="150"
                        padding="10"
                        background-color="80000000"
                    >
                        <label
                            text="Container Title"
                            color="FFFFFF"
                            alignment="CENTER"
                        />
                        
                        <button
                            width="180"
                            height="20"
                            text="Container Button"
                            style="FLAT"
                        />
                        
                        <inputbox
                            width="180"
                            height="20"
                            placeholder="Container Input"
                        />
                    </container>
                </screen>
            """;

            // Parse XML and create components
            ComponentData screenData = ComponentData.fromXML(xmlLayout);

            // Add event handlers
            for (ComponentData componentData : screenData.getChildren()) {
                BaseComponent component = createComponent(componentData);
                if (component != null) {
                    addComponent(component);
                }
            }

        } catch (Exception e) {
            Ovsr.LOGGER.error("Failed to initialize test screen", e);
        }
    }
}

