package crazypants.enderio.fluid;

import javax.annotation.Nullable;

import org.apache.commons.lang3.StringUtils;

import crazypants.enderio.EnderIOTab;
import crazypants.util.ClientUtil;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemBucket;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fluids.BlockFluidClassic;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemBucketEio extends ItemBucket {

  public static ItemBucketEio create(BlockFluidClassic block, Fluid fluid) {
    ItemBucketEio b = new ItemBucketEio(block != null ? block : Blocks.AIR, fluid.getName());
    b.init();

    FluidContainerRegistry.registerFluidContainer(fluid, new ItemStack(b), new ItemStack(Items.BUCKET));
    if (block != null) {
      BucketHandler.instance.registerFluid(block, b);
    }

    return b;
  }
  
  private String itemName;

  protected ItemBucketEio(Block block, String fluidName) {
    super(block);
    setCreativeTab(EnderIOTab.tabEnderIOItems);
    setContainerItem(Items.BUCKET);
    itemName = "bucket" + StringUtils.capitalize(fluidName);
    setUnlocalizedName(itemName);
    setRegistryName(itemName);
  }

  protected void init() {
    GameRegistry.register(this);
  }
  
  public String getItemName() {
    return itemName;
  }

  @SideOnly(Side.CLIENT)
  public void addRenderers() {
    ModelBakery.registerItemVariants(this, new ResourceLocation("enderio:filterUpgradeBasic"),new ResourceLocation("enderio:filterUpgradeAdvanced"));
    ClientUtil.regRenderer(this, 0,"filterUpgradeBasic");
    ClientUtil.regRenderer(this, 1 ,"filterUpgradeAdvanced");
  }

  @Override
  public ICapabilityProvider initCapabilities(ItemStack stack, net.minecraft.nbt.NBTTagCompound nbt) {
    return new ICapabilityProvider() {

      @Override
      public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing) {
        return false;
      }

      @Override
      public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing) {
        return null;
      }
    };
  }

}
