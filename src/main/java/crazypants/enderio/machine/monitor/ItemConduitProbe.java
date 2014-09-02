package crazypants.enderio.machine.monitor;

import java.text.NumberFormat;
import java.util.HashSet;
import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.enchantment.Enchantment;
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
import crazypants.enderio.conduit.ConduitUtil;
import crazypants.enderio.conduit.IConduit;
import crazypants.enderio.conduit.IConduitBundle;
import crazypants.enderio.conduit.redstone.IInsulatedRedstoneConduit;
import crazypants.enderio.gui.IResourceTooltipProvider;
import crazypants.enderio.network.PacketHandler;

public class ItemConduitProbe extends Item implements IResourceTooltipProvider {

  private static final NumberFormat NF = NumberFormat.getIntegerInstance();

  public static ItemConduitProbe create() {

    PacketHandler.INSTANCE.registerMessage(PacketConduitProbe.class,PacketConduitProbe.class,PacketHandler.nextID(),Side.SERVER);

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
      ConduitUtil.openConduitGui(world, x, y, z, player);
      return false;
    }

    TileEntity te = world.getTileEntity(x, y, z);
    if(!(te instanceof IConduitBundle) && PacketConduitProbe.canCreatePacket(world, x, y, z)) {
      if(world.isRemote) {
        PacketHandler.INSTANCE.sendToServer(new PacketConduitProbe(x, y, z, side));
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
