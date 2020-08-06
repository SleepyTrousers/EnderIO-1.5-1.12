package crazypants.enderio.base.machine.entity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.common.base.Optional;

import crazypants.enderio.base.EnderIO;
import crazypants.enderio.base.events.EnderIOLifecycleEvent;
import crazypants.enderio.base.machine.base.block.AbstractMachineBlock;
import crazypants.enderio.base.machine.base.te.AbstractMachineEntity;
import crazypants.enderio.base.paint.PaintUtil;
import crazypants.enderio.util.Prep;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFalling;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.item.EntityFallingBlock;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.util.FakePlayerFactory;
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

  // itemstack with the nbt of the dropped machine. super only creates dumb itemstack without nbt, even if we have TE nbt...
  private static final @Nonnull DataParameter<ItemStack> DROP_STACK = EntityDataManager.<ItemStack> createKey(EntityFallingMachine.class,
      DataSerializers.ITEM_STACK);

  // client-only. blockstate to render. The client will find this itself if the client-entity has a chance to tick before the original block is removed by the
  // server-entity's tick
  protected IBlockState renderBS = null;

  public EntityFallingMachine(World worldIn) {
    // super always needs a block so update() doesn't NPE. EntityFallingBlock has a special spawning network package that calls the other constructor, but we
    // don't get that luxury
    super(worldIn, 0, 0, 0, Blocks.BLUE_GLAZED_TERRACOTTA.getDefaultState());
  }

  public <T extends AbstractMachineEntity> EntityFallingMachine(@Nonnull WorldServer worldIn, @Nonnull BlockPos pos, @Nonnull Block block) {
    super(worldIn, pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D, worldIn.getBlockState(pos));
    if (block instanceof AbstractMachineBlock) {
      @SuppressWarnings("unchecked")
      final T te = AbstractMachineBlock.getAnyTileEntity(worldIn, pos, ((AbstractMachineBlock<T>) block).getTeClass());
      if (te != null) {
        tileEntityData = te.writeToNBT(new NBTTagCompound());
      }
      @SuppressWarnings("unchecked")
      final ItemStack nbtDrop = ((AbstractMachineBlock<T>) block).getNBTDrop(worldIn, pos, worldIn.getBlockState(pos), 0, te);
      if (nbtDrop != null) {
        setDropStack(nbtDrop);
      }
    } else {
      final TileEntity te = AbstractMachineBlock.getAnyTileEntity(worldIn, pos, null);
      if (te != null) {
        tileEntityData = te.writeToNBT(new NBTTagCompound());
      }
      // not ideal, but for now only BlockPaintedSand uses this, and for that it works
      setDropStack(block.getPickBlock(worldIn.getBlockState(pos), new RayTraceResult(new Vec3d(0, 0, 0), EnumFacing.DOWN, pos), worldIn, pos,
          FakePlayerFactory.getMinecraft(worldIn)));
    }
    setHurtEntities(true);
    setRealBlockState(super.getBlock());
  }

  @Override
  protected void entityInit() {
    super.entityInit();
    this.dataManager.register(REAL_BLOCK, Optional.absent());
    this.dataManager.register(DROP_STACK, ItemStack.EMPTY);
  }

  @Override
  public void onUpdate() {
    if (world.isRemote && renderBS == null) {
      IBlockState blockState1 = world.getBlockState(getOrigin());
      IBlockState blockState2 = getRealBlockState();
      if (blockState1.equals(blockState2) && blockState1.getBlock().hasTileEntity(blockState1)) {
        // This is the best way to render---this extended blockstate contains a nice model with everything. But this only works before the original block is
        // removed. Usually we are fast enough to get this information, but often enough it fails. See below for 2 fallback solutions.
        renderBS = blockState1.getBlock().getExtendedState(blockState1.getBlock().getActualState(world.getBlockState(getOrigin()), world, getOrigin()), world,
            getOrigin());
      }
    }

    if (super.getBlock() != null) { // this can happen on the client with bad timing
      IBlockState fallTile = super.getBlock();
      // copied from super with hate
      Block block = fallTile.getBlock();

      if (fallTile.getMaterial() == Material.AIR) {
        this.setDead();
      } else {
        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;

        if (this.fallTime++ == 0) {
          BlockPos blockpos = new BlockPos(this);

          if (this.world.getBlockState(blockpos).getBlock() == block) {
            this.world.setBlockToAir(blockpos);
          } else if (!this.world.isRemote) {
            this.setDead();
            return;
          }
        }

        if (!this.hasNoGravity()) {
          this.motionY -= 0.03999999910593033D;
        }

        this.move(MoverType.SELF, this.motionX, this.motionY, this.motionZ);

        if (!this.world.isRemote) {
          BlockPos blockpos1 = new BlockPos(this);
          double d0 = this.motionX * this.motionX + this.motionY * this.motionY + this.motionZ * this.motionZ;

          if (!this.onGround) {
            if (this.fallTime > 100 && !this.world.isRemote && (blockpos1.getY() < 1 || blockpos1.getY() > 256) || this.fallTime > 600) {
              if (this.shouldDropItem && this.world.getGameRules().getBoolean("doEntityDrops")) {
                this.entityDropItem(new ItemStack(block, 1, block.damageDropped(fallTile)), 0.0F);
              }

              this.setDead();
            }
          } else {
            IBlockState iblockstate = this.world.getBlockState(blockpos1);

            if (this.world.isAirBlock(new BlockPos(this.posX, this.posY - 0.009999999776482582D, this.posZ))) // Forge: Don't indent below.
              if (BlockFalling.canFallThrough(this.world.getBlockState(new BlockPos(this.posX, this.posY - 0.009999999776482582D, this.posZ)))) {
                this.onGround = false;
                return;
              }

            this.motionX *= 0.699999988079071D;
            this.motionZ *= 0.699999988079071D;
            this.motionY *= -0.5D;

            if (iblockstate.getBlock() != Blocks.PISTON_EXTENSION) {
              this.setDead();

              if (this.world.mayPlace(block, blockpos1, true, EnumFacing.UP, this) && (!BlockFalling.canFallThrough(this.world.getBlockState(blockpos1.down())))
                  && this.world.setBlockState(blockpos1, fallTile, 3)) {
                if (block instanceof BlockFalling) {
                  ((BlockFalling) block).onEndFalling(this.world, blockpos1, fallTile, iblockstate);
                }

                if (this.tileEntityData != null && block.hasTileEntity(fallTile)) {
                  TileEntity tileentity = this.world.getTileEntity(blockpos1);

                  if (tileentity != null) {
                    // HL: Original doesn't work well with AutoSave as we use multiple keys for one value
                    NBTTagCompound nbttagcompound = /* tileentity.writeToNBT( */new NBTTagCompound();
                    nbttagcompound.setInteger("x", blockpos1.getX());
                    nbttagcompound.setInteger("y", blockpos1.getY());
                    nbttagcompound.setInteger("z", blockpos1.getZ());

                    for (String s : this.tileEntityData.getKeySet()) {
                      NBTBase nbtbase = this.tileEntityData.getTag(s);

                      if (!"x".equals(s) && !"y".equals(s) && !"z".equals(s)) {
                        nbttagcompound.setTag(s, nbtbase.copy());
                      }
                    }

                    tileentity.readFromNBT(nbttagcompound);
                    tileentity.markDirty();
                  }
                }
              } else if (this.shouldDropItem && this.world.getGameRules().getBoolean("doEntityDrops")) {
                this.entityDropItem(new ItemStack(block, 1, block.damageDropped(fallTile)), 0.0F);
              }

              else if (block instanceof BlockFalling) {
                ((BlockFalling) block).onBroken(this.world, blockpos1);
              }
            }
          }
        }

        this.motionX *= 0.9800000190734863D;
        this.motionY *= 0.9800000190734863D;
        this.motionZ *= 0.9800000190734863D;
      }
    }
  }

  @Override
  public @Nullable EntityItem entityDropItem(@Nonnull ItemStack stack, float offsetY) {
    if (Prep.isValid(getDropStack())) {
      return super.entityDropItem(getDropStack(), offsetY);
    } else {
      return super.entityDropItem(stack, offsetY);
    }
  }

  @Override
  protected void writeEntityToNBT(@Nonnull NBTTagCompound compound) {
    super.writeEntityToNBT(compound);
    NBTTagCompound tag = new NBTTagCompound();
    getDropStack().writeToNBT(tag);
    compound.setTag("dropStack", tag);
  }

  @Override
  protected void readEntityFromNBT(@Nonnull NBTTagCompound compound) {
    super.readEntityFromNBT(compound);
    setRealBlockState(super.getBlock());
    setDropStack(new ItemStack(compound.getCompoundTag("dropStack")));
  }

  // only used by the renderer
  @Override
  public @Nullable IBlockState getBlock() {
    if (renderBS != null) {
      return renderBS;
    }
    IBlockState paint = PaintUtil.getSourceBlock(getDropStack());
    if (paint != null) {
      return paint;
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

  public @Nullable IBlockState getRealBlockState() {
    return this.dataManager.get(REAL_BLOCK).orNull();
  }

  public void setDropStack(@Nonnull ItemStack stack) {
    this.dataManager.set(DROP_STACK, stack);
  }

  public @Nonnull ItemStack getDropStack() {
    return this.dataManager.get(DROP_STACK);
  }

}
