package crazypants.enderio.base.integration.pi;

import java.awt.Rectangle;

import com.brandon3055.projectintelligence.api.IGuiDocHandler;
import com.brandon3055.projectintelligence.api.IGuiDocRegistry;
import com.brandon3055.projectintelligence.api.IModPlugin;
import com.brandon3055.projectintelligence.api.ModPlugin;

import crazypants.enderio.base.config.config.IntegrationConfig;
import crazypants.enderio.base.gui.GuiContainerBaseEIO;

@ModPlugin
public class PIPlugin implements IModPlugin {

  @Override
  public void registerModGUIs(IGuiDocRegistry registry) {
    if (IntegrationConfig.experimentalProjectIntelligence.get()) {
      registry.registerGuiDocPages(GuiContainerBaseEIO.class, gui -> gui.getDocumentationPages());
      registry.registerGuiHandler(GuiContainerBaseEIO.class, new IGuiDocHandler<GuiContainerBaseEIO>() {

        @Override
        public Rectangle getCollapsedArea(GuiContainerBaseEIO gui) {
          return gui.getDocumentationButtonArea();
        }

        @Override
        public Rectangle getExpandedArea(GuiContainerBaseEIO gui) {
          return gui.getDocumentationArea();
        }

      });
    }
  }

}
