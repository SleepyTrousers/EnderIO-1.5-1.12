package crazypants.enderio.base.machine.entity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.common.base.Optional;

import crazypants.enderio.base.EnderIO;
import crazypants.enderio.base.events.EnderIOLifecycleEvent;
import crazypants.enderio.base.machine.base.block.AbstractMachineBlock;
import crazypants.enderio.base.machine.base.te.AbstractMachineEntity;
import crazypants.enderio.util.Prep;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityFallingBlock;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
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
public class EntityFallingMachine extends EntityFallingBlock {

  @SubscribeEvent
  public static void onEntityRegister(Register<EntityEntry> event) {
    EntityRegistry.registerModEntity(new ResourceLocation(EnderIO.DOMAIN, "falling_machine"), EntityFallingMachine.class, EnderIO.DOMAIN + ".falling_machine",
        1, EnderIO.MODID, 64, 100, true);
  }

  @SubscribeEvent
  @SideOnly(Side.CLIENT)
  public static void onPreInit(EnderIOLifecycleEvent.PreInit event) {
    RenderingRegistry.registerEntityRenderingHandler(EntityFallingMachine.class, RenderFallingMachine.FACTORY);
  }

  // block to be sent from the server to the client as fallback if the client cannot find the block on its own
  private static final @Nonnull DataParameter<Optional<IBlockState>> REAL_BLOCK = EntityDataManager
      .<Optional<IBlockState>> createKey(EntityFallingMachine.class, DataSerializers.OPTIONAL_BLOCK_STATE);

  // client-only. blockstate to render. The client will find this itself if the client-entity has a chance to tick before the original block is removed by the
  // server-entity's tick
  protected IBlockState renderBS = null;
  // itemstack with the nbt of the dropped machine. super only creates dumb itemstack without nbt, even if we have TE nbt...
  protected @Nonnull ItemStack dropStack = Prep.getEmpty();

  public EntityFallingMachine(World worldIn) {
    // super always needs a block so update() doesn't NPE. EntityFallingBlock has a special spawning network package that calls the other constructor, but we
    // don't get that luxury
    super(worldIn, 0, 0, 0, Blocks.BLUE_GLAZED_TERRACOTTA.getDefaultState());
  }

  public <T extends AbstractMachineEntity> EntityFallingMachine(@Nonnull World worldIn, @Nonnull BlockPos pos, @Nonnull AbstractMachineBlock<T> block) {
    super(worldIn, pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D, worldIn.getBlockState(pos));
    final T te = AbstractMachineBlock.getAnyTileEntity(worldIn, pos, block.getTeClass());
    if (te != null) {
      tileEntityData = te.writeToNBT(new NBTTagCompound());
    }
    final ItemStack nbtDrop = block.getNBTDrop(worldIn, pos, worldIn.getBlockState(pos), 0, te);
    if (nbtDrop != null) {
      dropStack = nbtDrop;
    }
    setHurtEntities(true);
    setRealBlockState(super.getBlock());
  }

  @Override
  protected void entityInit() {
    super.entityInit();
    this.dataManager.register(REAL_BLOCK, Optional.absent());
  }

  @Override
  public void onUpdate() {
    if (world.isRemote && renderBS == null) {
      IBlockState blockState = world.getBlockState(getOrigin());
      if (blockState.getBlock() instanceof AbstractMachineBlock) {
        renderBS = blockState.getBlock().getExtendedState(blockState.getBlock().getActualState(world.getBlockState(getOrigin()), world, getOrigin()), world,
            getOrigin());
      }
    }

    if (super.getBlock() != null) {
      super.onUpdate();
    }
  }

  @Override
  @Nullable
  public EntityItem entityDropItem(@Nonnull ItemStack stack, float offsetY) {
    if (Prep.isValid(dropStack)) {
      return super.entityDropItem(dropStack, offsetY);
    } else {
      return super.entityDropItem(stack, offsetY);
    }
  }

  @Override
  protected void writeEntityToNBT(@Nonnull NBTTagCompound compound) {
    super.writeEntityToNBT(compound);
    NBTTagCompound tag = new NBTTagCompound();
    dropStack.writeToNBT(tag);
    compound.setTag("dropStack", tag);
  }

  @Override
  protected void readEntityFromNBT(@Nonnull NBTTagCompound compound) {
    super.readEntityFromNBT(compound);
    setRealBlockState(super.getBlock());
    dropStack = new ItemStack(compound.getCompoundTag("dropStack"));
  }

  @Override
  @Nullable
  public IBlockState getBlock() {
    if (renderBS != null) {
      return renderBS;
    }
    IBlockState realBlockState = getRealBlockState();
    if (realBlockState != null) {
      return realBlockState;
    }
    return super.getBlock();
  }

  public void setRealBlockState(@Nullable IBlockState state) {
    this.dataManager.set(REAL_BLOCK, Optional.fromNullable(state));
  }

  @Nullable
  public IBlockState getRealBlockState() {
    return this.dataManager.get(REAL_BLOCK).orNull();
  }

}
