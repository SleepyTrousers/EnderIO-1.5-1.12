package crazypants.enderio.conduit.item;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.item.Item;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import crazypants.enderio.EnderIOTab;
import crazypants.enderio.ModObject;
import crazypants.enderio.machine.monitor.PacketConduitProbe;
import crazypants.enderio.network.PacketHandler;

public class ItemExtractSpeedUpgrade extends Item {
  
  public static ItemExtractSpeedUpgrade create() {

    PacketHandler.INSTANCE.registerMessage(PacketConduitProbe.class, PacketConduitProbe.class, PacketHandler.nextID(), Side.SERVER);

    ItemExtractSpeedUpgrade result = new ItemExtractSpeedUpgrade();
    result.init();
    return result;
  }

  protected ItemExtractSpeedUpgrade() {
    setCreativeTab(EnderIOTab.tabEnderIO);
    setUnlocalizedName(ModObject.itemExtractSpeedUpgrade.unlocalisedName);
    setMaxStackSize(64);
  }
  
  protected void init() {
    GameRegistry.registerItem(this, ModObject.itemExtractSpeedUpgrade.unlocalisedName);
  }

  @Override
  @SideOnly(Side.CLIENT)
  public void registerIcons(IIconRegister IIconRegister) {
    itemIcon = IIconRegister.registerIcon("enderio:extractSpeedUpgrade");
  }
  
}
