package crazypants.enderio.conduits.conduit;

import javax.annotation.Nonnull;

import com.enderio.core.common.util.ItemUtil;

import crazypants.enderio.api.IModObject;
import crazypants.enderio.base.BlockEio;
import crazypants.enderio.base.EnderIOTab;
import crazypants.enderio.base.conduit.ConduitUtil;
import crazypants.enderio.base.conduit.IConduit;
import crazypants.enderio.base.conduit.IConduitBundle;
import crazypants.enderio.base.conduit.IConduitItem;
import crazypants.enderio.base.conduit.IServerConduit;
import crazypants.enderio.base.conduit.registry.ConduitRegistry;
import crazypants.enderio.base.render.IHaveRenderers;
import crazypants.enderio.conduits.lang.Lang;
import crazypants.enderio.util.ClientUtil;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public abstract class AbstractItemConduit extends Item implements IConduitItem, IHaveRenderers {

  protected IModObject modObj;

  protected ItemConduitSubtype[] subtypes;

  protected AbstractItemConduit(@Nonnull IModObject modObj, ItemConduitSubtype... subtypes) {
    this.modObj = modObj;
    this.subtypes = subtypes;
    setCreativeTab(EnderIOTab.tabEnderIOConduits);
    setUnlocalizedName(modObj.getUnlocalisedName());
    setMaxStackSize(64);
    setHasSubtypes(true);
    setRegistryName(modObj.getUnlocalisedName());
  }

  @Override
  @SideOnly(Side.CLIENT)
  public void registerRenderers(@Nonnull IModObject modObject) {
    for (int i = 0; i < subtypes.length; i++) {
      ClientUtil.regRenderer(this, i, new ResourceLocation(subtypes[i].getModelLocation()));
    }
  }

  @Override
  @Nonnull
  public EnumActionResult onItemUse(@Nonnull EntityPlayer player, @Nonnull World world, @Nonnull BlockPos pos, @Nonnull EnumHand hand, @Nonnull EnumFacing side,
      float hitX, float hitY, float hitZ) {

    ItemStack held = player.getHeldItem(hand);

    BlockPos placeAt = canPlaceItem(held, ConduitRegistry.getConduitModObjectNN().getBlockNN().getDefaultState(), player, world, pos, side);
    if (placeAt != null) {
      if (!world.isRemote) {
        if (world.setBlockState(placeAt, ConduitRegistry.getConduitModObjectNN().getBlockNN().getDefaultState(), 1)) {
          TileEntity te = world.getTileEntity(placeAt);
          if (te instanceof IConduitBundle) {
            IConduitBundle bundle = (IConduitBundle) te;
            if (bundle.addConduit(createConduit(held, player))) {
              ConduitUtil.playBreakSound(SoundType.METAL, world, placeAt);
            } else {
              player.sendStatusMessage(new TextComponentTranslation(Lang.GUI_CONDUIT_BUNDLE_FULL.getKey()), true);
            }
          }
        }
      }
      if (!player.capabilities.isCreativeMode) {
        held.shrink(1);
      }
      return EnumActionResult.SUCCESS;

    } else {

      BlockPos place = pos.offset(side);

      if (world.getBlockState(place).getBlock() == ConduitRegistry.getConduitModObjectNN().getBlock()) {

        IConduitBundle bundle = (IConduitBundle) world.getTileEntity(place);
        if (bundle == null) {
          return EnumActionResult.PASS;
        }
        if (!bundle.hasType(getBaseConduitType())) {
          if (!world.isRemote) {
            IServerConduit con = createConduit(held, player);
            if (con == null) {
              return EnumActionResult.PASS;
            }
            bundle.addConduit(con);
            ConduitUtil.playBreakSound(SoundType.METAL, world, place);
            if (!player.capabilities.isCreativeMode) {
              held.shrink(1);
            }
          }
          return EnumActionResult.SUCCESS;
        }
      }
    }

    return EnumActionResult.PASS;
  }

  @Override
  @Nonnull
  public EnumActionResult onItemUseFirst(@Nonnull EntityPlayer player, @Nonnull World world, @Nonnull BlockPos pos, @Nonnull EnumFacing side, float hitX,
      float hitY, float hitZ, @Nonnull EnumHand hand) {

    ItemStack held = player.getHeldItem(hand);

    // Conduit replacement
    if (player.isSneaking()) {
      return EnumActionResult.PASS;
    }
    IConduitBundle bundle = BlockEio.getAnyTileEntity(world, pos, IConduitBundle.class);
    if (bundle == null) {
      return EnumActionResult.PASS;
    }
    IConduit existingConduit = bundle.getConduit(getBaseConduitType());
    if (existingConduit == null) {
      return EnumActionResult.PASS;
    }
    ItemStack existingConduitAsItemStack = existingConduit.createItem();
    if (!ItemUtil.areStacksEqual(existingConduitAsItemStack, held)) {
      if (!world.isRemote) {
        IServerConduit newConduit = createConduit(held, player);
        if (newConduit == null) {
          return EnumActionResult.PASS;
        }
        bundle.removeConduit(existingConduit);
        bundle.addConduit(newConduit);
        if (!player.capabilities.isCreativeMode) {
          held.shrink(1);
          for (ItemStack drop : existingConduit.getDrops()) {
            if (drop != null && !player.inventory.addItemStackToInventory(drop)) {
              ItemUtil.spawnItemInWorldWithRandomMotion(world, drop, pos, hitX, hitY, hitZ, 1.1f);
            }
          }
          player.inventoryContainer.detectAndSendChanges();
        }
        return EnumActionResult.SUCCESS;
      } else {
        player.swingArm(hand);
      }
    }
    return EnumActionResult.PASS;
  }

  @Override
  @Nonnull
  public String getUnlocalizedName(@Nonnull ItemStack stack) {
    int i = MathHelper.clamp(stack.getItemDamage(), 0, subtypes.length - 1);
    return subtypes[i].getUnlocalisedName();
  }

  @Override
  public void getSubItems(@Nonnull CreativeTabs tab, @Nonnull NonNullList<ItemStack> stacks) {
    if (isInCreativeTab(tab)) {
      for (int j = 0; j < subtypes.length; ++j) {
        stacks.add(new ItemStack(this, 1, j));
      }
    }
  }

  private BlockPos canPlaceItem(@Nonnull ItemStack held, IBlockState blockToPlace, @Nonnull EntityPlayer player, @Nonnull World world, @Nonnull BlockPos pos,
      @Nonnull EnumFacing side) {
    if (held.isEmpty() || blockToPlace == null) {
      return null;
    }

    IBlockState bs = world.getBlockState(pos);
    Block block = bs.getBlock();
    if (!block.isReplaceable(world, pos)) {
      pos = pos.offset(side);
    }

    if (!player.canPlayerEdit(pos, side, held)) {
      return null;
    } else if (pos.getY() == 255 && blockToPlace.getMaterial().isSolid()) {
      return null;
    }
    if (world.mayPlace(blockToPlace.getBlock(), pos, false, side, player)) {
      return pos;
    }
    return null;
  }

}
