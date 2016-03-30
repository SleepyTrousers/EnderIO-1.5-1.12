package crazypants.enderio.fluid;

import net.minecraft.block.Block;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemBucket;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.BlockFluidClassic;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import org.apache.commons.lang3.StringUtils;

import crazypants.enderio.EnderIOTab;
import crazypants.util.ClientUtil;

public class ItemBucketEio extends ItemBucket {

  public static ItemBucketEio create(BlockFluidClassic block, Fluid fluid) {
    ItemBucketEio b = new ItemBucketEio(block != null ? block : Blocks.air, fluid.getName());
    b.init();

    FluidContainerRegistry.registerFluidContainer(fluid, new ItemStack(b), new ItemStack(Items.bucket));
    if (block != null) {
      BucketHandler.instance.registerFluid(block, b);
    }

    return b;
  }
  
  private String itemName;

  protected ItemBucketEio(Block block, String fluidName) {
    super(block);  
    setCreativeTab(EnderIOTab.tabEnderIO);
    setContainerItem(Items.bucket);
    itemName = "bucket" + StringUtils.capitalize(fluidName);
    setUnlocalizedName(itemName);
  }

  protected void init() {
    GameRegistry.registerItem(this,itemName);
  }
  
  public String getItemName() {
    return itemName;
  }

  @SideOnly(Side.CLIENT)
  public void addRenderers() {       
    // TODO 1.8 what??? copy&paste error?
    ModelBakery.registerItemVariants(this, new ResourceLocation("enderio:filterUpgradeBasic"),new ResourceLocation("enderio:filterUpgradeAdvanced"));        
    ClientUtil.regRenderer(this, 0,"filterUpgradeBasic");
    ClientUtil.regRenderer(this, 1 ,"filterUpgradeAdvanced");    
  }  
}
