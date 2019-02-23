package crazypants.enderio.base;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.enderio.core.common.BlockEnder;

import crazypants.enderio.api.IModObject;
import crazypants.enderio.api.tool.ITool;
import crazypants.enderio.base.gui.handler.IEioGuiHandler;
import crazypants.enderio.base.init.ModObjectRegistry;
import crazypants.enderio.base.lang.Lang;
import crazypants.enderio.base.machine.base.te.AbstractMachineEntity;
import crazypants.enderio.base.tool.ToolUtil;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.server.permission.DefaultPermissionLevel;
import net.minecraftforge.server.permission.PermissionAPI;
import net.minecraftforge.server.permission.context.BlockPosContext;

public abstract class BlockEio<T extends TileEntityEio> extends BlockEnder<T> implements IModObject.LifecycleInit, IModObject.WithBlockItem {

  protected @Nonnull String permissionNodeWrenching = "(block not initialized)";
  protected @Nonnull String permissionNodeIOWrenching = "(block not initialized)";

  @SuppressWarnings("unchecked")
  protected BlockEio(@Nonnull IModObject modObject) {
    super((Class<? extends T>) modObject.getTEClass());
    modObject.apply(this);
    setCreativeTab(EnderIOTab.tabEnderIOMachines);
  }

  @SuppressWarnings("unchecked")
  protected BlockEio(@Nonnull IModObject modObject, @Nonnull Material mat) {
    super((Class<? extends T>) modObject.getTEClass(), mat);
    modObject.apply(this);
    setCreativeTab(EnderIOTab.tabEnderIOMachines);
  }

  @SuppressWarnings("unchecked")
  protected BlockEio(@Nonnull IModObject modObject, @Nonnull Material mat, MapColor mapColor) {
    super((Class<? extends T>) modObject.getTEClass(), mat, mapColor);
    modObject.apply(this);
    setCreativeTab(EnderIOTab.tabEnderIOMachines);
  }

  /**
   * Stuff that needs to be done directly after constructing the object
   */
  protected void init() {
  }

  /**
   * Stuff that has to be done in the init phase (as opposed to preInit/postInit)
   */
  @Override
  public void init(@Nonnull IModObject modObject, @Nonnull FMLInitializationEvent event) {
    if (canBeWrenched()) {
      permissionNodeWrenching = PermissionAPI.registerNode(EnderIO.DOMAIN + ".wrench.break." + modObject.getUnlocalisedName(), DefaultPermissionLevel.ALL,
          "Permission to wrench-break the block " + modObject.getUnlocalisedName() + " of Ender IO");
      permissionNodeIOWrenching = PermissionAPI.registerNode(EnderIO.DOMAIN + ".wrench.iomode." + modObject.getUnlocalisedName(), DefaultPermissionLevel.ALL,
          "Permission to set IO mode by wrench-clicking the block " + modObject.getUnlocalisedName() + " of Ender IO");
    }
  }

  @Override
  public @Nullable ItemEIO createBlockItem(@Nonnull IModObject modObject) {
    return modObject.apply(new ItemEIO(this));
  };

  @Override
  public boolean onBlockActivated(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull IBlockState state, @Nonnull EntityPlayer entityPlayer,
      @Nonnull EnumHand hand, @Nonnull EnumFacing side, float hitX, float hitY, float hitZ) {
    if (shouldWrench(world, pos, entityPlayer, side) && ToolUtil.breakBlockWithTool(this, world, pos, side, entityPlayer, hand, permissionNodeWrenching)) {
      return true;
    }

    TileEntity te = getTileEntity(world, pos);
    if (te instanceof AbstractMachineEntity) {
      ITool tool = ToolUtil.getEquippedTool(entityPlayer, hand);
      if (tool != null && !entityPlayer.isSneaking() && tool.canUse(hand, entityPlayer, pos)) {
        if (!world.isRemote) {
          if (!PermissionAPI.hasPermission(entityPlayer.getGameProfile(), permissionNodeIOWrenching, new BlockPosContext(entityPlayer, pos, state, side))) {
            entityPlayer.sendMessage(Lang.WRENCH_DENIED.toChatServer());
          } else {
            ((AbstractMachineEntity) te).toggleIoModeForFace(side);
          }
        }
        return true;
      }
    }

    return super.onBlockActivated(world, pos, state, entityPlayer, hand, side, hitX, hitY, hitZ);
  }

  @Override
  protected @Nonnull ItemStack processPickBlock(@Nonnull IBlockState state, @Nonnull RayTraceResult target, @Nonnull World world, @Nonnull BlockPos pos,
      @Nonnull EntityPlayer player, @Nonnull ItemStack pickBlock) {
    T te = getTileEntity(world, pos);
    if (te != null) {
      return te.processPickBlock(target, player, pickBlock);
    }
    return pickBlock;
  }

  public boolean shouldWrench(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull EntityPlayer entityPlayer, @Nonnull EnumFacing side) {
    return canBeWrenched();
  }

  /**
   * 
   * @return <code>true</code> if this block can be wrenched at all. If this returns <code>false</code>,
   *         {@link #shouldWrench(World, BlockPos, EntityPlayer, EnumFacing)} <strong>must never</strong> return <code>true</code>.
   */
  protected boolean canBeWrenched() {
    return true;
  }

  // GUI

  @Override
  protected boolean openGui(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull EntityPlayer entityPlayer, @Nonnull EnumFacing side) {
    return openGui(world, pos, entityPlayer, side, 0);
  }

  /**
   * To be called from mod code, e.g. a GUI button's network packet to switch GUIs.
   */
  public boolean openGui(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull EntityPlayer entityPlayer, @Nullable EnumFacing side, int param) {
    if (this instanceof IEioGuiHandler) {
      return ModObjectRegistry.getModObjectNN(this).openGui(world, pos, entityPlayer, side, param);
    }
    return false;
  }

  // BlockFaceShape

  @Override
  protected @Nonnull IShape<T> mkShape(@Nonnull BlockFaceShape allFaces) {
    return new IShape<T>() {
      @Override
      @Nonnull
      public BlockFaceShape getBlockFaceShape(@Nonnull IBlockAccess worldIn, @Nonnull IBlockState state, @Nonnull BlockPos pos, @Nonnull EnumFacing face,
          @Nonnull T te) {
        IBlockState paintSource = te.getPaintSource();
        if (paintSource != null) {
          try {
            return paintSource.getBlockFaceShape(worldIn, pos, face);
          } catch (Exception e) {
          }
        }
        return IShape.super.getBlockFaceShape(worldIn, state, pos, face, te);
      }

      @Override
      @Nonnull
      public BlockFaceShape getBlockFaceShape(@Nonnull IBlockAccess worldIn, @Nonnull IBlockState state, @Nonnull BlockPos pos, @Nonnull EnumFacing face) {
        return allFaces;
      }
    };
  }

  @Override
  protected @Nonnull IShape<T> mkShape(@Nonnull BlockFaceShape upDown, @Nonnull BlockFaceShape allSides) {
    return new IShape<T>() {
      @Override
      @Nonnull
      public BlockFaceShape getBlockFaceShape(@Nonnull IBlockAccess worldIn, @Nonnull IBlockState state, @Nonnull BlockPos pos, @Nonnull EnumFacing face,
          @Nonnull T te) {
        IBlockState paintSource = te.getPaintSource();
        if (paintSource != null) {
          try {
            return paintSource.getBlockFaceShape(worldIn, pos, face);
          } catch (Exception e) {
          }
        }
        return IShape.super.getBlockFaceShape(worldIn, state, pos, face, te);
      }

      @Override
      @Nonnull
      public BlockFaceShape getBlockFaceShape(@Nonnull IBlockAccess worldIn, @Nonnull IBlockState state, @Nonnull BlockPos pos, @Nonnull EnumFacing face) {
        return face == EnumFacing.UP || face == EnumFacing.DOWN ? upDown : allSides;
      }
    };
  }

  @Override
  protected @Nonnull IShape<T> mkShape(@Nonnull BlockFaceShape down, @Nonnull BlockFaceShape up, @Nonnull BlockFaceShape allSides) {
    return new IShape<T>() {
      @Override
      @Nonnull
      public BlockFaceShape getBlockFaceShape(@Nonnull IBlockAccess worldIn, @Nonnull IBlockState state, @Nonnull BlockPos pos, @Nonnull EnumFacing face,
          @Nonnull T te) {
        IBlockState paintSource = te.getPaintSource();
        if (paintSource != null) {
          try {
            return paintSource.getBlockFaceShape(worldIn, pos, face);
          } catch (Exception e) {
          }
        }
        return IShape.super.getBlockFaceShape(worldIn, state, pos, face, te);
      }

      @Override
      @Nonnull
      public BlockFaceShape getBlockFaceShape(@Nonnull IBlockAccess worldIn, @Nonnull IBlockState state, @Nonnull BlockPos pos, @Nonnull EnumFacing face) {
        return face == EnumFacing.UP ? up : face == EnumFacing.DOWN ? down : allSides;
      }
    };
  }

  @Override
  protected @Nonnull IShape<T> mkShape(@Nonnull BlockFaceShape... faces) {
    return new IShape<T>() {
      @Override
      @Nonnull
      public BlockFaceShape getBlockFaceShape(@Nonnull IBlockAccess worldIn, @Nonnull IBlockState state, @Nonnull BlockPos pos, @Nonnull EnumFacing face,
          @Nonnull T te) {
        IBlockState paintSource = te.getPaintSource();
        if (paintSource != null) {
          try {
            return paintSource.getBlockFaceShape(worldIn, pos, face);
          } catch (Exception e) {
          }
        }
        return IShape.super.getBlockFaceShape(worldIn, state, pos, face, te);
      }

      @SuppressWarnings("null")
      @Override
      @Nonnull
      public BlockFaceShape getBlockFaceShape(@Nonnull IBlockAccess worldIn, @Nonnull IBlockState state, @Nonnull BlockPos pos, @Nonnull EnumFacing face) {
        return faces[face.ordinal()];
      }
    };
  }

}
