package crazypants.enderio.zoo.entity;

import crazypants.enderio.zoo.EnderZoo;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.item.Item;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

public class EntityOwlEgg extends EntityThrowable {
  
  public EntityOwlEgg(World worldIn) {
    super(worldIn);
  }

  public EntityOwlEgg(World worldIn, EntityLivingBase throwerIn) {
    super(worldIn, throwerIn);
  }

  public EntityOwlEgg(World worldIn, double x, double y, double z) {
    super(worldIn, x, y, z);
  }

  @Override
  protected void onImpact(RayTraceResult impact) {
    if (impact.entityHit != null) {
      impact.entityHit.attackEntityFrom(DamageSource.causeThrownDamage(this, getThrower()), 0.0F);
    }

    if (!world.isRemote && rand.nextInt(8) == 0) {
      EntityOwl entitychicken = new EntityOwl(world);
      entitychicken.setGrowingAge(-24000);
      entitychicken.setLocationAndAngles(posX, posY, posZ, rotationYaw, 0.0F);
      world.spawnEntity(entitychicken);
    }
    for (int i = 0; i < 8; ++i) {
      world.spawnParticle(EnumParticleTypes.ITEM_CRACK, posX, posY, posZ, (rand.nextFloat() - 0.5D) * 0.08D,
          (rand.nextFloat() - 0.5D) * 0.08D, (rand.nextFloat() - 0.5D) * 0.08D, new int[] { Item.getIdFromItem(EnderZoo.itemOwlEgg) });
    }
    if (!world.isRemote) {
      setDead();
    }
  }
}
