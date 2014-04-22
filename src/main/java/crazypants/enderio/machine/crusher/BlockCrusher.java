package crazypants.enderio.machine.crusher;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import crazypants.enderio.GuiHandler;
import crazypants.enderio.ModObject;
import crazypants.enderio.machine.AbstractMachineBlock;

public class BlockCrusher extends AbstractMachineBlock {

  public static BlockCrusher create() {

    BlockCrusher res = new BlockCrusher();
    res.init();

    MinecraftForge.EVENT_BUS.register(res);
    return res;
  }

  private BlockCrusher() {
    super(ModObject.blockSagMill, TileCrusher.class);
  }

  @SubscribeEvent
  public void addGrindingBallTooltip(ItemTooltipEvent evt) {
    IGrindingMultiplier gb = CrusherRecipeManager.instance.getGrindballFromStack(evt.itemStack);
    if(gb != null) {
      List<String> list = evt.toolTip;
      list.add(EnumChatFormatting.BLUE + "Grinding Ball Properties");
      list.add(EnumChatFormatting.GRAY + "Ouput x" + gb.getGrindingMultiplier());
      list.add(EnumChatFormatting.GRAY + "Chance Outputs x" + gb.getChanceMultiplier());
      list.add(EnumChatFormatting.GRAY + "Power Used x" + gb.getPowerMultiplier());
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

}
