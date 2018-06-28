package crazypants.enderio.base.block.charge;

import javax.annotation.Nonnull;

import crazypants.enderio.base.EnderIO;
import crazypants.enderio.base.events.EnderIOLifecycleEvent;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityTNTPrimed;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.ResourceLocation;
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
public class EntityPrimedCharge extends EntityTNTPrimed {

  @SubscribeEvent
  public static void onEntityRegister(Register<EntityEntry> event) {
    EntityRegistry.registerModEntity(new ResourceLocation(EnderIO.DOMAIN, "primed_charge"), EntityPrimedCharge.class, EnderIO.DOMAIN + ".primed_charge", 0,
        EnderIO.MODID, 64, 100, false);
  }

  @SubscribeEvent
  @SideOnly(Side.CLIENT)
  public static void onPreInit(EnderIOLifecycleEvent.PreInit event) {
    RenderingRegistry.registerEntityRenderingHandler(EntityPrimedCharge.class, RenderPrimedCharge.FACTORY);
  }

  private static final @Nonnull DataParameter<Integer> CHARGE = EntityDataManager.<Integer> createKey(EntityPrimedCharge.class, DataSerializers.VARINT);

  private @Nonnull ICharge charge = ChargeRegister.instance.getCharge(0);

  public EntityPrimedCharge(World world) {
    super(world);
  }

  public EntityPrimedCharge(ICharge charge, World world, double x, double y, double z, EntityLivingBase placedBy) {
    super(world, x, y, z, placedBy);
    setCharge(charge);
  }

  @Override
  protected void entityInit() {
    super.entityInit();
    this.dataManager.register(CHARGE, 0);
  }

  @Override
  public void onUpdate() {
    super.onUpdate();
    if (getFuse() <= 1) {
      this.setDead();
      if (!this.world.isRemote) {
        charge.explode(this);
      }
    }
  }

  @Override
  protected void writeEntityToNBT(@Nonnull NBTTagCompound root) {
    super.writeEntityToNBT(root);
    root.setInteger("charge", charge.getID());
  }

  @Override
  protected void readEntityFromNBT(@Nonnull NBTTagCompound root) {
    super.readEntityFromNBT(root);
    if (root.hasKey("charge")) {
      setChargeID(root.getInteger("charge"));
    }
  }

  public Block getBlock() {
    return charge.getBlock();
  }

  public ICharge getCharge() {
    return charge;
  }

  public void setCharge(ICharge charge) {
    dataManager.set(CHARGE, charge.getID());
    this.charge = charge;
  }

  public void setChargeID(int id) {
    this.charge = ChargeRegister.instance.getCharge(id);
    dataManager.set(CHARGE, charge.getID());
  }

  @Override
  public void notifyDataManagerChange(@Nonnull DataParameter<?> key) {
    super.notifyDataManagerChange(key);
    if (CHARGE.equals(key)) {
      int id = dataManager.get(CHARGE);
      charge = ChargeRegister.instance.getCharge(id);
    }
  }

}