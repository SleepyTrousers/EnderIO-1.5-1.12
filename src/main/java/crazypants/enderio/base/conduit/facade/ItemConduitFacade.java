package crazypants.enderio.base.conduit.facade;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.enderio.core.api.client.gui.IAdvancedTooltipProvider;
import com.enderio.core.api.client.gui.IResourceTooltipProvider;
import com.enderio.core.client.handlers.SpecialTooltipHandler;
import com.enderio.core.common.util.NullHelper;

import crazypants.enderio.base.EnderIOTab;
import crazypants.enderio.base.conduit.ConduitUtil;
import crazypants.enderio.base.conduit.IConduitBundle;
import crazypants.enderio.base.init.IModObject;
import crazypants.enderio.base.paint.PaintUtil;
import crazypants.enderio.base.recipe.MachineRecipeRegistry;
import crazypants.enderio.base.recipe.painter.FacadePainterRecipe;
import crazypants.enderio.base.registry.Registry;
import crazypants.enderio.base.render.IHaveRenderers;
import crazypants.enderio.base.render.registry.ItemModelRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemConduitFacade extends Item implements IAdvancedTooltipProvider, IResourceTooltipProvider, IHaveRenderers {

  public static ItemConduitFacade create(@Nonnull IModObject modObject) {
    ItemConduitFacade result = new ItemConduitFacade(modObject);
    MachineRecipeRegistry.instance.registerRecipe(MachineRecipeRegistry.PAINTER, new FacadePainterRecipe(result));
    return result;
  }

  public ItemConduitFacade(@Nonnull IModObject modObject) {
    super();
    setCreativeTab(EnderIOTab.tabEnderIOItems);
    setMaxStackSize(64);
    setHasSubtypes(true);
    modObject.apply(this);
  }

  @Override
  public int getMetadata(int damage) {
    return damage;
  }

  @Override
  public @Nonnull String getUnlocalizedName(@Nonnull ItemStack stack) {
    return EnumFacadeType.getTypeFromMeta(stack.getMetadata()).getUnlocName(this);
  }

  @SuppressWarnings("deprecation")
  @Override
  public @Nonnull EnumActionResult onItemUse(@Nonnull EntityPlayer player, @Nonnull World world, @Nonnull BlockPos pos, @Nonnull EnumHand hand,
      @Nonnull EnumFacing side, float hitX, float hitY, float hitZ) {

    if (world.isRemote) {
      return EnumActionResult.SUCCESS;
    }

    Block conduitBlock = Registry.getConduitBlock();

    if (conduitBlock != null) {

      ItemStack stack = player.getHeldItem(hand);

      BlockPos placeAt = pos.offset(side);

      if (player.canPlayerEdit(placeAt, side, stack) && PaintUtil.getSourceBlock(stack) != null) {
        if (world.isAirBlock(placeAt)) {
          world.setBlockState(placeAt, conduitBlock.getDefaultState());
          IConduitBundle bundle = NullHelper.notnullM((IConduitBundle) world.getTileEntity(placeAt), "placing block yielded no tileentity");
          IBlockState bs = PaintUtil.getSourceBlock(stack);
          bundle.setFacadeType(EnumFacadeType.values()[stack.getItemDamage()]);
          bundle.setPaintSource(bs);
          ConduitUtil.playPlaceSound(bs.getBlock().getSoundType(), world, pos.getX(), pos.getY(), pos.getZ());
          if (!player.capabilities.isCreativeMode) {
            stack.shrink(1);
          }
          return EnumActionResult.SUCCESS;
        } else {
          TileEntity tileEntity = world.getTileEntity(pos);
          if (tileEntity instanceof IConduitBundle) {
            if (((IConduitBundle) tileEntity).handleFacadeClick(world, placeAt, player, side.getOpposite(), stack, hand, hitX, hitY, hitZ)) {
              return EnumActionResult.SUCCESS;
            }
          }
        }
      }

    }

    return EnumActionResult.PASS;
  }

  @Override
  @SideOnly(Side.CLIENT)
  public void addInformation(@Nonnull ItemStack item, @Nonnull EntityPlayer par2EntityPlayer, @Nonnull List<String> list, boolean par4) {
    super.addInformation(item, par2EntityPlayer, list, par4);
  }

  @Override
  public @Nonnull String getUnlocalizedNameForTooltip(@Nonnull ItemStack itemStack) {
    return getUnlocalizedName();
  }

  @Override
  @SideOnly(Side.CLIENT)
  public void addCommonEntries(@Nonnull ItemStack itemstack, @Nullable EntityPlayer entityplayer, @Nonnull List<String> list, boolean flag) {

  }

  @Override
  @SideOnly(Side.CLIENT)
  public void addBasicEntries(@Nonnull ItemStack itemstack, @Nullable EntityPlayer entityplayer, @Nonnull List<String> list, boolean flag) {
  }

  @SuppressWarnings({ "unchecked", "rawtypes" })
  @Override
  @SideOnly(Side.CLIENT)
  public void addDetailedEntries(@Nonnull ItemStack itemstack, @Nullable EntityPlayer entityplayer, @Nonnull List list, boolean flag) {
    SpecialTooltipHandler.addDetailedTooltipFromResources(list, itemstack);
    if (EnumFacadeType.getTypeFromMeta(itemstack.getMetadata()) != EnumFacadeType.BASIC) {
      list.add("");
      SpecialTooltipHandler.addDetailedTooltipFromResources(list, getUnlocalizedName(itemstack));
    }
  }

  @Override
  @SideOnly(Side.CLIENT)
  public void getSubItems(@Nonnull Item itemIn, @Nullable CreativeTabs tab, @Nonnull NonNullList<ItemStack> subItems) {
    for (EnumFacadeType type : EnumFacadeType.values()) {
      subItems.add(new ItemStack(itemIn, 1, type.ordinal()));
    }
  }

  @Override
  public void registerRenderers(@Nonnull IModObject modObject) {
    for (EnumFacadeType type : EnumFacadeType.values()) {
      final ModelResourceLocation mrl = new ModelResourceLocation(NullHelper.notnull(getRegistryName(), "unregistered item?"), "type=" + type.getName());
      ModelLoader.setCustomModelResourceLocation(this, EnumFacadeType.getMetaFromType(type), mrl);
      ItemModelRegistry.registerFacade(mrl);
    }
  }

}
