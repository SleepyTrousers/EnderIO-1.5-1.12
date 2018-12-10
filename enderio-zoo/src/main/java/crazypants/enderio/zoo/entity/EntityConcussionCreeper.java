package crazypants.enderio.zoo.entity;

import java.lang.reflect.Field;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import crazypants.enderio.base.Log;
import crazypants.enderio.base.events.EnderIOLifecycleEvent;
import crazypants.enderio.base.loot.EntityLootHelper;
import crazypants.enderio.base.teleport.RandomTeleportUtil;
import crazypants.enderio.zoo.EnderIOZoo;
import crazypants.enderio.zoo.config.ZooConfig;
import crazypants.enderio.zoo.entity.render.RenderConcussionCreeper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraft.world.storage.loot.LootTableList;
import net.minecraftforge.event.RegistryEvent.Register;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@EventBusSubscriber(modid = EnderIOZoo.MODID)
public class EntityConcussionCreeper extends EntityCreeper implements IEnderZooMob {

  @SubscribeEvent
  public static void onEntityRegister(@Nonnull Register<EntityEntry> event) {
    LootTableList.register(new ResourceLocation(EnderIOZoo.DOMAIN, NAME));
    IEnderZooMob.register(event, NAME, EntityConcussionCreeper.class, EGG_BG_COL, EGG_FG_COL, MobID.CCREEPER);
  }

  @SubscribeEvent
  @SideOnly(Side.CLIENT)
  public static void onPreInit(EnderIOLifecycleEvent.PreInit event) {
    RenderingRegistry.registerEntityRenderingHandler(EntityConcussionCreeper.class, RenderConcussionCreeper.FACTORY);
  }

  public static final @Nonnull String NAME = "concussioncreeper";
  public static final int EGG_BG_COL = 0x56FF8E;
  public static final int EGG_FG_COL = 0xFF0A22;

  private Field fTimeSinceIgnited;
  private Field fFuseTime;

  public EntityConcussionCreeper(World world) {
    super(world);
    try {
      fTimeSinceIgnited = ReflectionHelper.findField(EntityCreeper.class, "timeSinceIgnited", "field_70833_d");
      fFuseTime = ReflectionHelper.findField(EntityCreeper.class, "fuseTime", "field_82225_f");
    } catch (Exception e) {
      Log.error("Could not create ender creeper logic as fields not found");
    }
  }

  @Override
  protected void applyEntityAttributes() {
    super.applyEntityAttributes();
    applyAttributes(this, ZooConfig.creeperHealth, NO_ATTACK);
  }

  @Override
  public void onUpdate() {

    if (isEntityAlive()) {
      int timeSinceIgnited = getTimeSinceIgnited();
      int fuseTime = getFuseTime();

      if (timeSinceIgnited >= fuseTime - 1) {
        setTimeSinceIgnited(0);
        world.playSound(posX, posY, posZ, SoundEvents.ENTITY_GENERIC_EXPLODE, SoundCategory.HOSTILE, 4.0F,
            (1.0F + (world.rand.nextFloat() - world.rand.nextFloat()) * 0.2F) * 0.7F, false);
        world.spawnParticle(EnumParticleTypes.EXPLOSION_HUGE, posX, posY, posZ, 1.0D, 0.0D, 0.0D);
        setDead();

        int range = ZooConfig.explosionRange.get();
        AxisAlignedBB bb = new AxisAlignedBB(posX - range, posY - range, posZ - range, posX + range, posY + range, posZ + range);
        List<EntityLivingBase> ents = world.getEntitiesWithinAABB(EntityLivingBase.class, bb);
        for (EntityLivingBase ent : ents) {
          if (!ent.isDead) {
            if (!world.isRemote) {
              RandomTeleportUtil.teleportEntity(ent.world, ent, false, true, ZooConfig.teleportRange.get());
            }
            if (ent instanceof EntityPlayer) {
              EnderIOZoo.proxy.setInstantConfusionOnPlayer((EntityPlayer) ent, ZooConfig.confusionDuration.get());
            }
          }
        }
      }
    }

    super.onUpdate();

  }

  @Override
  @Nullable
  protected ResourceLocation getLootTable() {
    return new ResourceLocation(EnderIOZoo.DOMAIN, NAME);
  }

  @Override
  protected void dropLoot(boolean wasRecentlyHit, int lootingModifier, @Nonnull DamageSource source) {
    EntityLootHelper.dropLoot(this, getLootTable(), source);
    dropEquipment(wasRecentlyHit, lootingModifier);
  }

  private void setTimeSinceIgnited(int i) {
    if (fTimeSinceIgnited == null) {
      return;
    }
    try {
      fTimeSinceIgnited.setInt(this, i);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private int getTimeSinceIgnited() {
    if (fTimeSinceIgnited == null) {
      return 0;
    }
    try {
      return fTimeSinceIgnited.getInt(this);
    } catch (Exception e) {
      e.printStackTrace();
      return 0;
    }
  }

  private int getFuseTime() {
    if (fFuseTime == null) {
      return 0;
    }
    try {
      return fFuseTime.getInt(this);
    } catch (Exception e) {
      e.printStackTrace();
      return 0;
    }
  }

}
