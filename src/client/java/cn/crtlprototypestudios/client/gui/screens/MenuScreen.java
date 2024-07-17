package cn.crtlprototypestudios.client.gui.screens;

import cn.crtlprototypestudios.controlui_refactored.client.BaritoneWrapper;
import cn.crtlprototypestudios.controlui_refactored.client.gui.components.QuickActionsBarContainer;
import cn.crtlprototypestudios.controlui_refactored.client.gui.utils.ScreenStackUtils;
import io.wispforest.owo.ui.base.BaseUIModelScreen;
import io.wispforest.owo.ui.component.ButtonComponent;
import io.wispforest.owo.ui.container.FlowLayout;
import io.wispforest.owo.ui.parsing.UIParsing;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class MenuScreen extends BaseUIModelScreen<FlowLayout> {

    private String menuName = "Menu";
    @Nullable
    protected FlowLayout quickActionBarHolder = null;
    @Nullable
    protected ButtonComponent pauseButton, resumeButton, stopButton, homeButton;

    private boolean implementQuickActionBar = true;

    public MenuScreen(String path, String name, boolean implementBar) {
        super(FlowLayout.class, new Identifier("controlui_refactored", path));
        menuName = name;
        implementQuickActionBar = implementBar;
    }

    @Override
    protected void init(){
        super.init();
        if (implementQuickActionBar) InitializeQuickActionsBar();
        InitializeProcessUI();
    }

    @Override
    protected void build(FlowLayout rootComponent){
        // It is impossible to get the quick actions bar in this build() method
        // The reason? I don't know why, but the build() method seems to run before init(), which is where the quick action bar is initialized
    }

    static {
        UIParsing.registerFactory("quick-actions-bar-container", element -> new QuickActionsBarContainer());
    }

    public void SetMenuName(){
        if(quickActionBarHolder == null) return;
        quickActionBarHolder.<FlowLayout>configure(component -> {
            component.clearChildren();
            component.child(this.model.expandTemplate(QuickActionsBarContainer.class, "quick-actions-bar@controlui_refactored:components/quick_actions_bar", Map.of(
                    "current-menu", menuName
            )));
        });
    }

    public boolean hasQuickActionsBar() { return quickActionBarHolder != null; }

    public void InitializeQuickActionsBar(){
        if (this.uiAdapter != null) {
            quickActionBarHolder = this.uiAdapter.rootComponent.childById(FlowLayout.class, "bar-holder");
        }

        SetMenuName();

        if(quickActionBarHolder != null){
            pauseButton = quickActionBarHolder.childById(ButtonComponent.class, "action.pause-all");
            resumeButton = quickActionBarHolder.childById(ButtonComponent.class, "action.resume-all");
            stopButton = quickActionBarHolder.childById(ButtonComponent.class, "action.stop-all");
            homeButton = quickActionBarHolder.childById(ButtonComponent.class, "action.home-menu");
        }

        if (pauseButton != null || stopButton != null || homeButton != null || resumeButton != null) {
            pauseButton.onPress(buttonComponent -> {
                BaritoneWrapper.pauseAllActions();
                System.out.println("[Control UI] Paused All Baritone Actions");
            });
            resumeButton.onPress(buttonComponent -> {
                BaritoneWrapper.resumeAllActions();
                System.out.println("[Control UI] Resumed All Baritone Actions");
            });
            stopButton.onPress(buttonComponent -> {
                BaritoneWrapper.stopAllActions();
                System.out.println("[Control UI] Stopped All Baritone Actions");
            });
            homeButton.onPress(buttonComponent -> {
                ScreenStackUtils.back();
                System.out.println("[Control UI] Switch to Main Menu");
            });
        }
    }
    public void InitializeProcessUI(){
        if (this.uiAdapter == null) return;
//        this.uiAdapter.rootComponent.child(this.model.expandTemplate(FlowLayout.class, "processes-info@controlui_refactored:components/processes_info", Map.of()));
    }
}
