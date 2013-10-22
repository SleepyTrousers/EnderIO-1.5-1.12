package crazypants.enderio.machine.monitor;

import java.text.NumberFormat;

import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import crazypants.enderio.EnderIOTab;
import crazypants.enderio.ModObject;
import crazypants.enderio.PacketHandler;

public class ItemMJReader extends Item {

  private static final NumberFormat NF = NumberFormat.getIntegerInstance();

  public static ItemMJReader create() {

    PacketHandler.instance.addPacketProcessor(new MJReaderPacketHandler());

    ItemMJReader result = new ItemMJReader();
    result.init();
    return result;
  }

  protected ItemMJReader() {
    super(ModObject.itemMJReader.id);
    setCreativeTab(EnderIOTab.tabEnderIO);
    setUnlocalizedName(ModObject.itemMJReader.unlocalisedName);
    setMaxStackSize(64);
  }

  @Override
  public boolean onItemUseFirst(ItemStack itemStack, EntityPlayer player, World world, int x, int y, int z, int side, float par8,
      float par9, float par10) {

    if(MJReaderPacketHandler.canCreatePacket(world, x, y, z)) {
      PacketDispatcher.sendPacketToServer(MJReaderPacketHandler.createInfoRequestPacket(x, y, z, side));
      return true;
    }

    return false;
  }

  protected void init() {
    LanguageRegistry.addName(this, ModObject.itemMJReader.name);
    GameRegistry.registerItem(this, ModObject.itemMJReader.unlocalisedName);
  }

  @Override
  @SideOnly(Side.CLIENT)
  public void registerIcons(IconRegister iconRegister) {
    itemIcon = iconRegister.registerIcon("enderio:mJReader");
  }

}
