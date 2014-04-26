package crazypants.enderio.fluid;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemBucket;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;

import org.apache.commons.lang3.StringUtils;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import crazypants.enderio.EnderIOTab;
import crazypants.enderio.gui.IAdvancedTooltipProvider;
import crazypants.enderio.gui.TooltipAddera;

public class ItemBucketEio extends ItemBucket implements IAdvancedTooltipProvider{

  public static ItemBucketEio create(Fluid fluid) {
    ItemBucketEio b = new ItemBucketEio(fluid.getBlock(), fluid.getName());
    b.init();

    FluidContainerRegistry.registerFluidContainer(fluid, new ItemStack(b), new ItemStack(Items.bucket));
    BucketHandler.instance.registerFluid(fluid.getBlock(), b);

    return b;
  }

  private String fluidName;

  protected ItemBucketEio(Block block, String fluidName) {
    super(block);
    this.fluidName = fluidName;
    setCreativeTab(EnderIOTab.tabEnderIO);
    setContainerItem(Items.bucket);
    String str = "bucket" + StringUtils.capitalize(fluidName);
    setUnlocalizedName(str);
    setTextureName("enderIO:" + str);
  }

  protected void init() {
    GameRegistry.registerItem(this, "bucket" + StringUtils.capitalize(fluidName));
  }

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
    TooltipAddera.instance.addTooltipForFluid(list, itemstack);
  }





}
