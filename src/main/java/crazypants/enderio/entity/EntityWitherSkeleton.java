package crazypants.enderio.entity;

import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import java.util.Calendar;

public class EntityWitherSkeleton extends EntitySkeleton {
  public EntityWitherSkeleton(World world) {
    super(world);
  }

  public EntityWitherSkeleton(EntitySkeleton entity) {
    this(entity.worldObj);
    this.copyLocationAndAnglesFrom(entity);
    for (int i = 0; i < entity.getLastActiveItems().length; i++) {
      this.setCurrentItemOrArmor(i, entity.getEquipmentInSlot(i));
    }
  }

  @Override
  public void setFire(int p_70015_1_) {
  }

  @Override
  public IEntityLivingData onSpawnWithEgg(IEntityLivingData entityData) {
    this.getEntityAttribute(SharedMonsterAttributes.followRange)
        .applyModifier(new AttributeModifier("Random spawn bonus", this.rand.nextGaussian() * 0.05D, 1));
    this.setSkeletonType(1);
    this.setCurrentItemOrArmor(0, new ItemStack(Items.stone_sword));
    this.getEntityAttribute(SharedMonsterAttributes.attackDamage).setBaseValue(4.0D);
    this.setCombatTask();
    this.setCanPickUpLoot(this.rand.nextFloat() < 0.55F * this.worldObj.func_147462_b(this.posX, this.posY, this.posZ));

    if(this.getEquipmentInSlot(4) == null) {
      Calendar calendar = this.worldObj.getCurrentDate();

      if(calendar.get(2) + 1 == 10 && calendar.get(5) == 31 && this.rand.nextFloat() < 0.25F) {
        this.setCurrentItemOrArmor(4, new ItemStack(this.rand.nextFloat() < 0.1F ? Blocks.lit_pumpkin : Blocks.pumpkin));
        this.equipmentDropChances[4] = 0.0F;
      }
    }

    return entityData;
  }

  @Override
  public int getSkeletonType() {
    return 1;
  }
}
