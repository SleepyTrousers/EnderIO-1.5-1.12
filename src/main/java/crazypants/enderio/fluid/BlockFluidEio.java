package crazypants.enderio.fluid;

import org.apache.commons.lang3.StringUtils;

import crazypants.enderio.config.Config;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fluids.BlockFluidClassic;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fml.common.registry.GameRegistry;

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
    setUnlocalizedName(fluid.getUnlocalizedName());
  }

  protected void init() {
    GameRegistry.registerBlock(this, "block" + StringUtils.capitalize(fluidName));
  }

//  @SideOnly(Side.CLIENT)
//  protected IIcon[] icons;
//
//  @Override
//  @SideOnly(Side.CLIENT)
//  public IIcon getIcon(int side, int meta) {
//    return side != 0 && side != 1 ? this.icons[1] : this.icons[0];
//  }
//
//  @Override
//  @SideOnly(Side.CLIENT)
//  public void registerBlockIcons(IIconRegister iconRegister) {
//    icons = new IIcon[] { iconRegister.registerIcon("enderio:" + fluidName + "_still"),
//        iconRegister.registerIcon("enderio:" + fluidName + "_flow") };
//
//    fluid.setIcons(icons[0], icons[1]);
//  }
  
  @Override
  public boolean canDisplace(IBlockAccess world, BlockPos pos) {
    if(world.getBlockState(pos).getBlock().getMaterial().isLiquid()) {
      return false;
    }
    return super.canDisplace(world, pos);
  }

  @Override
  public boolean displaceIfPossible(World world, BlockPos pos) {   
    if(world.getBlockState(pos).getBlock().getMaterial().isLiquid()) {
      return false;
    }
    return super.displaceIfPossible(world, pos);
  }
  
  
  

  @Override
  public void onEntityCollidedWithBlock(World world, BlockPos pos, Entity entity) {

    if(entity.worldObj.isRemote) {
      super.onEntityCollidedWithBlock(world, pos, entity);
      return;
    }

    if(this == Fluids.blockFireWater) {
      entity.setFire(50);
    } else if(this == Fluids.blockRocketFuel && entity instanceof EntityLivingBase) {
      ((EntityLivingBase) entity).addPotionEffect(new PotionEffect(Potion.jump.id, 150, 3, true, true));
    } else if(this == Fluids.blockNutrientDistillation && entity instanceof EntityPlayerMP) {
      long time = entity.worldObj.getTotalWorldTime();
      EntityPlayerMP player = (EntityPlayerMP) entity;
      if(time % Config.nutrientFoodBoostDelay == 0 && player.getEntityData().getLong("eioLastFoodBoost") != time) {
        player.getFoodStats().addStats(1, 0.1f);
        player.getEntityData().setLong("eioLastFoodBoost", time);
      }
    } else if (this == Fluids.blockHootch && entity instanceof EntityLivingBase) {
      ((EntityLivingBase) entity).addPotionEffect(new PotionEffect(Potion.confusion.id, 150, 0, true, true));
    }

    super.onEntityCollidedWithBlock(world,pos, entity);
  }
}