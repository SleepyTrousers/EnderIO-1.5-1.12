package crazypants.enderio.machines.machine.obelisk.weather;

import javax.annotation.Nonnull;

import crazypants.enderio.machines.machine.obelisk.weather.TileWeatherObelisk.WeatherTask;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.ParticleFirework;
import net.minecraft.entity.item.EntityFireworkRocket;
import net.minecraft.init.SoundEvents;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class EntityWeatherRocket extends EntityFireworkRocket {

  private static final @Nonnull DataParameter<Integer> DATA_ID = EntityDataManager.<Integer> createKey(EntityWeatherRocket.class, DataSerializers.VARINT);

  private static final int MAX_AGE = 70;

  public EntityWeatherRocket(World world) {
    super(world);
    ReflectionHelper.setPrivateValue(EntityFireworkRocket.class, this, MAX_AGE, "lifetime", "field_92055_b");
  }

  public EntityWeatherRocket(World world, WeatherTask task) {
    this(world);
    dataManager.set(DATA_ID, task.ordinal());
  }

  @Override
  protected void entityInit() {
    super.entityInit();
    dataManager.register(DATA_ID, 0);
  }

  @Override
  public void onEntityUpdate() {
    super.onEntityUpdate();
    if (world.isRemote && ticksExisted % (MAX_AGE / 10) == 0 && ticksExisted > 30) {
      doEffect();
    }
  }

  @Override
  public void setDead() {
    super.setDead();
    WeatherTask task = WeatherTask.values()[dataManager.get(DATA_ID)];
    task.complete(world);
  }

  @Override
  public void handleStatusUpdate(byte id) {
  }

  @SideOnly(Side.CLIENT)
  private void doEffect() {
    SoundEvent se = SoundEvents.ENTITY_FIREWORK_LARGE_BLAST;
    if (ticksExisted > 40) {
      se = SoundEvents.ENTITY_FIREWORK_LARGE_BLAST_FAR;
    }
    world.playSound(this.posX, this.posY, this.posZ, se, SoundCategory.BLOCKS, 20.0F, 0.95F + this.rand.nextFloat() * 0.1F, true);

    double d1 = this.posX;
    double d2 = this.posY;
    double d3 = this.posZ;

    int size = 5;
    double speed = 1;

    for (int j = -size; j <= size; ++j) {
      for (int k = -size; k <= size; ++k) {
        for (int l = -size; l <= size; ++l) {
          double d4 = k + (this.rand.nextDouble() - this.rand.nextDouble()) * 0.5D;
          double d5 = j + (this.rand.nextDouble() - this.rand.nextDouble()) * 0.5D;
          double d6 = l + (this.rand.nextDouble() - this.rand.nextDouble()) * 0.5D;
          double d7 = MathHelper.sqrt(d4 * d4 + d5 * d5 + d6 * d6) / speed + this.rand.nextGaussian() * 0.05D;

          ParticleFirework.Spark entityfireworksparkfx = new ParticleFirework.Spark(this.world, d1, d2, d3, d4 / d7, d5 / d7, d6 / d7,
              Minecraft.getMinecraft().effectRenderer);

          entityfireworksparkfx.setTrail(true);
          entityfireworksparkfx.setTwinkle(false);

          entityfireworksparkfx.setColor(WeatherTask.values()[dataManager.get(DATA_ID)].color.getRGB());

          Minecraft.getMinecraft().effectRenderer.addEffect(entityfireworksparkfx);
          if (j != -size && j != size && k != -size && k != size) {
            l += size * 2 - 1;
          }
        }
      }
    }
  }
}
