package crazypants.enderio.material.fusedQuartz;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import crazypants.enderio.EnderIO;
import crazypants.enderio.EnderIOTab;

public class ItemFusedQuartz extends ItemBlock {

  public ItemFusedQuartz(Block block) {
    super(block);
    setCreativeTab(EnderIOTab.tabEnderIO);
    setMaxDamage(0);
    setHasSubtypes(true);
  }

  @Override
  public String getUnlocalizedName(ItemStack par1ItemStack) {
    int meta = par1ItemStack.getItemDamage();
    FusedQuartzType type = FusedQuartzType.getTypeFromMeta(meta);
    return "enderio.blockFusedQuartz." + type.getUnlocalisedName();
  }

  @Override
  public int getMetadata(int damage) {
    return damage;
  }

  @Override
  @SideOnly(Side.CLIENT)
  public void addInformation(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, List<String> par3List, boolean par4) {
    int meta = par1ItemStack.getItemDamage();
    FusedQuartzType type = FusedQuartzType.getTypeFromMeta(meta);
    if (type.isBlastResistant()) {
      par3List.add(EnderIO.lang.localize("blastResistant"));
    }
    if (type.isEnlightened()) {
      par3List.add(EnderIO.lang.localize("lightEmitter"));
    }
    if (type.getLightOpacity() > 0) {
      par3List.add(EnderIO.lang.localize("lightBlocker"));
    }
  }
}
