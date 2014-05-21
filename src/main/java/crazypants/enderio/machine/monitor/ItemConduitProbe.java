package crazypants.enderio.machine.monitor;

import java.text.NumberFormat;
import java.util.HashSet;
import java.util.Set;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import crazypants.enderio.EnderIO;
import crazypants.enderio.EnderIOTab;
import crazypants.enderio.GuiHandler;
import crazypants.enderio.ModObject;
import crazypants.enderio.conduit.IConduit;
import crazypants.enderio.conduit.IConduitBundle;
import crazypants.enderio.gui.IResourceTooltipProvider;

public class ItemConduitProbe extends Item implements IResourceTooltipProvider {

  private static final NumberFormat NF = NumberFormat.getIntegerInstance();

  public static ItemConduitProbe create() {

    EnderIO.packetPipeline.registerPacket(PacketConduitProbe.class);

    ItemConduitProbe result = new ItemConduitProbe();
    result.init();
    return result;
  }

  protected ItemConduitProbe() {
    setCreativeTab(EnderIOTab.tabEnderIO);
    setUnlocalizedName("enderio." + ModObject.itemConduitProbe.name());
    setMaxStackSize(64);
  }

  @Override
  public boolean onItemUse(ItemStack itemStack, EntityPlayer player, World world, int x, int y, int z, int side, float par8,
      float par9, float par10) {

    if(player.isSneaking()) {

      TileEntity te = world.getTileEntity(x, y, z);
      if(te instanceof IConduitBundle) {
        IConduitBundle cb = (IConduitBundle) te;
        Set<ForgeDirection> cons = new HashSet<ForgeDirection>();
        for (IConduit con : cb.getConduits()) {
          cons.addAll(con.getExternalConnections());
        }

        if(cons.isEmpty()) {
          return false;
        }
        if(cons.size() == 1) {
          player.openGui(EnderIO.instance, GuiHandler.GUI_ID_EXTERNAL_CONNECTION_BASE + cons.iterator().next().ordinal(), world, x, y, z);
          return true;
        }
        player.openGui(EnderIO.instance, GuiHandler.GUI_ID_EXTERNAL_CONNECTION_SELECTOR, world, x, y, z);

      }
      return false;
    }

    TileEntity te = world.getTileEntity(x, y, z);
    if(!(te instanceof IConduitBundle) && PacketConduitProbe.canCreatePacket(world, x, y, z)) {
      if(world.isRemote) {
        EnderIO.packetPipeline.sendToServer(new PacketConduitProbe(x, y, z, side));
      }
      return true;
    }

    return false;
  }

  protected void init() {
    GameRegistry.registerItem(this, ModObject.itemConduitProbe.unlocalisedName);
  }

  @Override
  @SideOnly(Side.CLIENT)
  public void registerIcons(IIconRegister IIconRegister) {
    itemIcon = IIconRegister.registerIcon("enderio:mJReader");
  }

  @Override
  public String getUnlocalizedNameForTooltip(ItemStack stack) {
    return getUnlocalizedName();
  }

}
