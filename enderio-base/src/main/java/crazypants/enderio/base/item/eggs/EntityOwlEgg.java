package crazypants.enderio.base.item.eggs;

import javax.annotation.Nonnull;

import com.enderio.core.common.util.NullHelper;

import crazypants.enderio.base.EnderIO;
import crazypants.enderio.base.Log;
import crazypants.enderio.base.events.EnderIOLifecycleEvent;
import crazypants.enderio.base.init.ModObject;
import crazypants.enderio.base.lang.Lang;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.item.Item;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.event.RegistryEvent.Register;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@EventBusSubscriber(modid = EnderIO.MODID)
public class EntityOwlEgg extends EntityThrowable {

  @SubscribeEvent
  public static void onEntityRegister(Register<EntityEntry> event) {
    EntityRegistry.registerModEntity(new ResourceLocation(EnderIO.MODID, "owl_egg"), EntityOwlEgg.class, EnderIO.MODID + ".owl_egg", 9, EnderIO.MODID, 64, 10,
        true);
  }

  @SubscribeEvent
  @SideOnly(Side.CLIENT)
  public static void onPreInit(EnderIOLifecycleEvent.PreInit event) {
    RenderingRegistry.registerEntityRenderingHandler(EntityOwlEgg.class, RenderEntityOwlEgg.FACTORY);
  }

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
  protected void onImpact(@Nonnull RayTraceResult impact) {
    if (NullHelper.untrust(impact.entityHit) != null) {
      impact.entityHit.attackEntityFrom(DamageSource.causeThrownDamage(this, getThrower()), 0.0F);
    }

    if (!world.isRemote && rand.nextInt(8) == 0) {
      Entity entitychicken = EntityList.createEntityByIDFromName(new ResourceLocation("enderiozoo", "owl"), world);
      if (entitychicken instanceof EntityAgeable) {
        ((EntityAgeable) entitychicken).setGrowingAge(-24000);
        entitychicken.setLocationAndAngles(posX, posY, posZ, rotationYaw, 0.0F);
        world.spawnEntity(entitychicken);
      } else if (thrower instanceof EntityPlayer) {
        ((EntityPlayer) thrower).sendStatusMessage(Lang.OWL_NO_OWL.toChatServer(), true);
      } else {
        Log.warn(Lang.OWL_NO_OWL.toChatServer());
      }
    }
    for (int i = 0; i < 8; ++i) {
      world.spawnParticle(EnumParticleTypes.ITEM_CRACK, posX, posY, posZ, (rand.nextFloat() - 0.5D) * 0.08D, (rand.nextFloat() - 0.5D) * 0.08D,
          (rand.nextFloat() - 0.5D) * 0.08D, new int[] { Item.getIdFromItem(ModObject.item_owl_egg.getItemNN()) });
    }
    if (!world.isRemote) {
      setDead();
    }
  }

}
