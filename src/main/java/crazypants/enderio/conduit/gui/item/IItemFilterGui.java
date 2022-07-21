package crazypants.enderio.conduit.gui.item;

import net.minecraft.client.gui.GuiButton;

public interface IItemFilterGui {

    void deactivate();

    void updateButtons();

    void actionPerformed(GuiButton guiButton);

    void renderCustomOptions(int top, float par1, int par2, int par3);

    void mouseClicked(int x, int y, int par3);
}
