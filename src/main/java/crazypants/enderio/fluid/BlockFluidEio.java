package crazypants.enderio.fluid;

import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fluids.BlockFluidClassic;
import net.minecraftforge.fluids.Fluid;

import org.apache.commons.lang3.StringUtils;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import crazypants.enderio.EnderIO;
import crazypants.enderio.config.Config;

public class BlockFluidEio extends BlockFluidClassic {

  public static BlockFluidEio create(Fluid fluid, Material material) {
    BlockFluidEio res = new BlockFluidEio(fluid, material);
    res.init();
    fluid.setBlock(res);
    return res;
  }

  protected Fluid fluid;

  protected BlockFluidEio(Fluid fluid, Material material) {
    super(fluid, material);
    this.fluid = fluid;
    setBlockName(fluid.getUnlocalizedName());
  }

  protected void init() {
    GameRegistry.registerBlock(this, "block" + StringUtils.capitalize(fluidName));
  }

  @SideOnly(Side.CLIENT)
  protected IIcon[] icons;

  @Override
  @SideOnly(Side.CLIENT)
  public IIcon getIcon(int side, int meta) {
    return side != 0 && side != 1 ? this.icons[1] : this.icons[0];
  }

  @Override
  @SideOnly(Side.CLIENT)
  public void registerBlockIcons(IIconRegister iconRegister) {
    icons = new IIcon[] { iconRegister.registerIcon("enderio:" + fluidName + "_still"),
        iconRegister.registerIcon("enderio:" + fluidName + "_flow") };

    fluid.setIcons(icons[0], icons[1]);
  }

  @Override
  public boolean canDisplace(IBlockAccess world, int x, int y, int z) {
    if(world.getBlock(x, y, z).getMaterial().isLiquid()) {
      return false;
    }
    return super.canDisplace(world, x, y, z);
  }

  @Override
  public boolean displaceIfPossible(World world, int x, int y, int z) {
    if(world.getBlock(x, y, z).getMaterial().isLiquid()) {
      return false;
    }
    return super.displaceIfPossible(world, x, y, z);
  }

  @Override
  public void onEntityCollidedWithBlock(World p_149670_1_, int p_149670_2_, int p_149670_3_, int p_149670_4_, Entity entity) {
    if(entity.worldObj.isRemote) {
      super.onEntityCollidedWithBlock(p_149670_1_, p_149670_2_, p_149670_3_, p_149670_4_, entity);
      return;
    }

    if(this == EnderIO.blockFireWater) {
      entity.setFire(50);
    } else if(this == EnderIO.blockRocketFuel && entity instanceof EntityLivingBase) {
      ((EntityLivingBase) entity).addPotionEffect(new PotionEffect(Potion.jump.id, 150, 3, true));
    } else if(this == EnderIO.blockNutrientDistillation && entity instanceof EntityPlayerMP) {
      long time = entity.worldObj.getTotalWorldTime();
      EntityPlayerMP player = (EntityPlayerMP) entity;
      if(time % Config.nutrientFoodBoostDelay == 0 && player.getEntityData().getLong("eioLastFoodBoost") != time) {
        player.getFoodStats().addStats(1, 0.1f);
        player.getEntityData().setLong("eioLastFoodBoost", time);
      }
    } else if (this == EnderIO.blockHootch && entity instanceof EntityLivingBase) {
      ((EntityLivingBase) entity).addPotionEffect(new PotionEffect(Potion.confusion.id, 150, 0, true));
    }

    super.onEntityCollidedWithBlock(p_149670_1_, p_149670_2_, p_149670_3_, p_149670_4_, entity);
  }
}