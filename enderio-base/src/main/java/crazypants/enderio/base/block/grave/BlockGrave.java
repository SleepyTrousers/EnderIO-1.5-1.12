package crazypants.enderio.base.block.grave;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.enderio.core.api.client.gui.IResourceTooltipProvider;
import com.enderio.core.common.inventory.EnderInventory;

import crazypants.enderio.api.IModObject;
import crazypants.enderio.base.BlockEio;
import crazypants.enderio.base.ItemEIO;
import crazypants.enderio.base.Log;
import crazypants.enderio.base.config.config.ItemConfig;
import crazypants.enderio.base.lang.Lang;
import crazypants.enderio.base.render.IHaveTESR;
import crazypants.enderio.base.render.registry.SmartModelAttacher;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockGrave extends BlockEio<TileGrave> implements IResourceTooltipProvider, IHaveTESR {

  public static BlockGrave create(@Nonnull IModObject modObject) {
    BlockGrave res = new BlockGrave(modObject);
    res.init();
    return res;
  }

  public BlockGrave(@Nonnull IModObject modObject) {
    super(modObject);
    setBlockUnbreakable();
    setResistance(6000000.0F);
    setLightOpacity(0);
    mkShape(BlockFaceShape.UNDEFINED);
  }

  @Override
  protected void init() {
    super.init();
    SmartModelAttacher.registerItemOnly(this);
  }

  @Override
  public @Nullable ItemEIO createBlockItem(@Nonnull IModObject modObject) {
    return (ItemEIO) modObject.apply(new ItemEIO(this) {
      @Override
      public @Nonnull EnumActionResult onItemUse(@Nonnull EntityPlayer player, @Nonnull World worldIn, @Nonnull BlockPos pos, @Nonnull EnumHand hand,
          @Nonnull EnumFacing facing, float hitX, float hitY, float hitZ) {
        return EnumActionResult.FAIL;
      }

      @Override
      public @Nonnull String getUnlocalizedName() {
        return BlockGrave.this.getUnlocalizedName().replace("tile", "item");
      }

      @Override
      public @Nonnull String getUnlocalizedName(@Nonnull ItemStack stack) {
        return getUnlocalizedName();
      }

    }).setMaxStackSize(16);
  };

  @Override
  @SideOnly(Side.CLIENT)
  public void bindTileEntitySpecialRenderer() {
    ClientRegistry.bindTileEntitySpecialRenderer(TileGrave.class, new TESRGrave(this));
  }

  @Override
  public boolean isOpaqueCube(@Nonnull IBlockState state) {
    return false;
  }

  @Override
  public boolean isFullCube(@Nonnull IBlockState state) {
    return false;
  }

  protected static final @Nonnull AxisAlignedBB DEFAULT_AABB = new AxisAlignedBB(0.25D, 0.0D, 0.25D, 0.75D, 0.5D, 0.75D);

  @Override
  public @Nonnull AxisAlignedBB getBoundingBox(@Nonnull IBlockState state, @Nonnull IBlockAccess source, @Nonnull BlockPos pos) {
    return DEFAULT_AABB;
  }

  @Override
  public @Nonnull EnumBlockRenderType getRenderType(@Nonnull IBlockState state) {
    return EnumBlockRenderType.INVISIBLE;
  }

  @Override
  public boolean onBlockActivated(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull IBlockState state, @Nonnull EntityPlayer player, @Nonnull EnumHand hand,
      @Nonnull EnumFacing side, float hitX, float hitY, float hitZ) {
    if (player.isSneaking()) {
      Log.info("Owner data: " + getTileEntity(world, pos).getOwner());
      return false;
    }
    if (!world.isRemote) {
      TileGrave te = getTileEntity(world, pos);
      if (te != null && (te.getOwner().equals(player.getGameProfile()) || player.isCreative() || !ItemConfig.dpPrivate.get())) {
        EnderInventory inventory = te.getInventory();
        for (int slot = 0; slot < inventory.getSlots(); slot++) {
          final ItemStack stack = inventory.getStackInSlot(slot).copy();
          if (!player.inventory.addItemStackToInventory(stack)) {
            spawnAsEntity(world, pos, stack);
          }
        }
        world.setBlockToAir(pos);
      } else {
        player.sendStatusMessage(Lang.GUI_GRAVE_NOT_OWNER.toChatServer(), true);
      }
    }
    return true;
  }

  @Override
  public boolean canHarvestBlock(@Nonnull IBlockAccess world, @Nonnull BlockPos pos, @Nonnull EntityPlayer player) {
    return false;
  }

  @Override
  public boolean removedByPlayer(@Nonnull IBlockState state, @Nonnull World world, @Nonnull BlockPos pos, @Nonnull EntityPlayer player, boolean willHarvest) {
    return false;
  }

  @Override
  protected boolean canBeWrenched() {
    return false;
  }

  @Override
  @Nonnull
  public String getUnlocalizedNameForTooltip(@Nonnull ItemStack itemStack) {
    return itemStack.getUnlocalizedName();
  }

}
