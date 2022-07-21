package crazypants.enderio.machine.soul;

import com.enderio.core.client.gui.button.IconButton;
import com.enderio.core.common.util.SoundUtil;
import crazypants.enderio.gui.IconEIO;
import crazypants.enderio.machine.gui.GuiPoweredMachineBase;
import crazypants.enderio.network.PacketHandler;
import crazypants.enderio.xp.ExperienceBarRenderer;
import crazypants.enderio.xp.PacketDrainPlayerXP;
import crazypants.enderio.xp.XpUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.InventoryPlayer;
import org.lwjgl.opengl.GL11;

public class GuiSoulBinder extends GuiPoweredMachineBase<TileSoulBinder> {

    private static final int PLAYER_XP_ID = 985162394;

    private final IconButton usePlayerXP;

    public GuiSoulBinder(InventoryPlayer par1InventoryPlayer, TileSoulBinder te) {
        super(te, new ContainerSoulBinder(par1InventoryPlayer, te), "soulFuser");
        usePlayerXP = new IconButton(this, PLAYER_XP_ID, 125, 57, IconEIO.XP);
        usePlayerXP.visible = false;
        usePlayerXP.setToolTip("Use Player XP");

        addProgressTooltip(80, 34, 24, 16);
    }

    @Override
    public void initGui() {
        super.initGui();
        usePlayerXP.onGuiInit();
        ((ContainerSoulBinder) inventorySlots).createGhostSlots(getGhostSlots());
    }

    @Override
    protected void actionPerformed(GuiButton b) {
        super.actionPerformed(b);
        if (b.id == PLAYER_XP_ID) {
            int xp = XpUtil.getPlayerXP(Minecraft.getMinecraft().thePlayer);
            if (xp > 0 || Minecraft.getMinecraft().thePlayer.capabilities.isCreativeMode) {
                PacketHandler.INSTANCE.sendToServer(
                        new PacketDrainPlayerXP(getTileEntity(), getTileEntity().getCurrentlyRequiredLevel(), true));
                SoundUtil.playClientSoundFX("random.orb", getTileEntity());
            }
        }
    }

    /**
     * Draw the background layer for the GuiContainer (everything behind the
     * items)
     */
    @Override
    protected void drawGuiContainerBackgroundLayer(float par1, int par2, int par3) {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        bindGuiTexture();
        int k = guiLeft;
        int l = guiTop;

        drawTexturedModalRect(k, l, 0, 0, this.xSize, this.ySize);
        int i1;

        TileSoulBinder binder = getTileEntity();

        if (shouldRenderProgress()) {
            i1 = getProgressScaled(24);
            drawTexturedModalRect(k + 80, l + 34, 176, 14, i1 + 1, 16);
        }

        usePlayerXP.visible = binder.needsXP();

        ExperienceBarRenderer.render(
                this,
                getGuiLeft() + 56,
                getGuiTop() + 68,
                65,
                binder.getContainer(),
                binder.getCurrentlyRequiredLevel());

        bindGuiTexture();
        super.drawGuiContainerBackgroundLayer(par1, par2, par3);
    }
}
