package crazypants.enderio.machine.crusher;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import crazypants.enderio.GuiHandler;
import crazypants.enderio.ModObject;
import crazypants.enderio.gui.IAdvancedTooltipProvider;
import crazypants.enderio.gui.TooltipAddera;
import crazypants.enderio.machine.AbstractMachineBlock;
import crazypants.util.Lang;

public class BlockCrusher extends AbstractMachineBlock {

  public static BlockCrusher create() {

    BlockCrusher res = new BlockCrusher();
    res.init();

    MinecraftForge.EVENT_BUS.register(res);
    return res;
  }

  private BallTipProvider btp = new BallTipProvider();

  private BlockCrusher() {
    super(ModObject.blockSagMill, TileCrusher.class);
  }

  @SubscribeEvent
  public void addGrindingBallTooltip(ItemTooltipEvent evt) {
    IGrindingMultiplier gb = CrusherRecipeManager.instance.getGrindballFromStack(evt.itemStack);
    if(gb != null) {
      btp.ball = gb;
      TooltipAddera.instance.addInformation(btp, evt.itemStack, evt.entityPlayer, evt.toolTip, false);
    }
  }



  @Override
  public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
    // The server needs the container as it manages the adding and removing of
    // items, which are then sent to the client for display
    TileEntity te = world.getTileEntity(x, y, z);
    if(te instanceof TileCrusher) {
      return new ContainerCrusher(player.inventory, (TileCrusher) te);
    }
    return null;
  }

  @Override
  public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
    TileEntity te = world.getTileEntity(x, y, z);
    if(te instanceof TileCrusher) {
      return new GuiCrusher(player.inventory, (TileCrusher) te);
    }
    return null;
  }

  @Override
  protected int getGuiId() {
    return GuiHandler.GUI_ID_CRUSHER;
  }

  @Override
  protected String getMachineFrontIconKey(boolean active) {
    if(active) {
      return "enderio:crusherFrontOn";
    }
    return "enderio:crusherFront";
  }

  private static class BallTipProvider implements IAdvancedTooltipProvider {

    IGrindingMultiplier ball;

    @Override
    @SideOnly(Side.CLIENT)
    public void addCommonEntries(ItemStack itemstack, EntityPlayer entityplayer, List list, boolean flag) {

    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addBasicEntries(ItemStack itemstack, EntityPlayer entityplayer, List list, boolean flag) {
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addDetailedEntries(ItemStack itemstack, EntityPlayer entityplayer, List list, boolean flag) {
      if(ball == null) {
        return;
      }
      list.add(EnumChatFormatting.BLUE + Lang.localize("darkGrindingBall.tooltip.detailed.line1"));
      list.add(EnumChatFormatting.GRAY + Lang.localize("darkGrindingBall.tooltip.detailed.line2") + toPercent(ball.getGrindingMultiplier()));
      list.add(EnumChatFormatting.GRAY + Lang.localize("darkGrindingBall.tooltip.detailed.line3") + toPercent(ball.getChanceMultiplier()));
      list.add(EnumChatFormatting.GRAY + Lang.localize("darkGrindingBall.tooltip.detailed.line4") + toPercent(1 - ball.getPowerMultiplier()));
    }

    private String toPercent(float fl) {
      fl = fl * 100;
      int per = Math.round(fl);
      return " " + per + "%";
    }

  }

}
