package crazypants.enderio.conduit.facade;

import java.util.List;

import com.enderio.core.api.client.gui.IAdvancedTooltipProvider;
import com.enderio.core.api.client.gui.IResourceTooltipProvider;
import com.enderio.core.client.handlers.SpecialTooltipHandler;

import crazypants.enderio.EnderIO;
import crazypants.enderio.EnderIOTab;
import crazypants.enderio.ModObject;
import crazypants.enderio.conduit.BlockConduitBundle;
import crazypants.enderio.conduit.ConduitUtil;
import crazypants.enderio.conduit.IConduitBundle;
import crazypants.enderio.machine.MachineRecipeRegistry;
import crazypants.enderio.machine.painter.recipe.FacadePainterRecipe;
import crazypants.enderio.paint.PainterUtil2;
import crazypants.enderio.render.IHaveRenderers;
import crazypants.enderio.render.registry.ItemModelRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import static crazypants.enderio.ModObject.blockConduitBundle;

public class ItemConduitFacade extends Item implements IAdvancedTooltipProvider, IResourceTooltipProvider, IHaveRenderers {

  public static ItemConduitFacade create() {
    ItemConduitFacade result = new ItemConduitFacade(ModObject.itemConduitFacade.getUnlocalisedName());
    GameRegistry.register(result);
    MachineRecipeRegistry.instance.registerRecipe(ModObject.blockPainter.getUnlocalisedName(), new FacadePainterRecipe(result));
    return result;
  }

  public ItemConduitFacade(String name) {
    super();
    setCreativeTab(EnderIOTab.tabEnderIOItems);
    setMaxStackSize(64);
    setHasSubtypes(true);
    setRegistryName(name);
    setUnlocalizedName(name);
  }

  @Override
  public int getMetadata(int damage) {
    return damage;
  }

  @Override
  public String getUnlocalizedName(ItemStack stack) {
    return EnumFacadeType.getTypeFromMeta(stack.getMetadata()).getUnlocName(this);
  }

  @SuppressWarnings("deprecation")
  @Override 
 public EnumActionResult onItemUse(ItemStack itemStack, EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {

    if(world.isRemote) {
      return EnumActionResult.SUCCESS;
    }

    BlockPos placeAt = pos.offset(side);

    if (player.canPlayerEdit(placeAt, side, itemStack) && PainterUtil2.getSourceBlock(itemStack) != null) {
      if (world.isAirBlock(placeAt)) {
        world.setBlockState(placeAt, blockConduitBundle.getBlock().getDefaultState());
        IConduitBundle bundle = (IConduitBundle) world.getTileEntity(placeAt);
        IBlockState bs = PainterUtil2.getSourceBlock(itemStack);
        bundle.setFacadeType(EnumFacadeType.values()[itemStack.getItemDamage()]);
        bundle.setPaintSource(bs);
        ConduitUtil.playPlaceSound(bs.getBlock().getSoundType(), world, pos.getX(), pos.getY(), pos.getZ());
        if (!player.capabilities.isCreativeMode) {
          itemStack.stackSize--;
        }
        return EnumActionResult.SUCCESS;
      } else {
        Block blockAt = world.getBlockState(placeAt).getBlock();
        if (blockAt == blockConduitBundle.getBlock()) {
          if(((BlockConduitBundle) blockAt)
              .handleFacadeClick(world, placeAt, player, side.getOpposite(),
                  (IConduitBundle) world.getTileEntity(placeAt), itemStack, hand, hitX, hitY, hitZ)) {
            return EnumActionResult.SUCCESS;
          }
        }
      }
    }

    return EnumActionResult.PASS;
  }

  @Override
  @SideOnly(Side.CLIENT)
  public void addInformation(ItemStack item, EntityPlayer par2EntityPlayer, List<String> list, boolean par4) {
    super.addInformation(item, par2EntityPlayer, list, par4);
  }

  @Override
  public String getUnlocalizedNameForTooltip(ItemStack itemStack) {
    return getUnlocalizedName();
  }

  @Override
  @SideOnly(Side.CLIENT)
  public void addCommonEntries(ItemStack itemstack, EntityPlayer entityplayer, List<String> list, boolean flag) {

  }
  
  @Override
  @SideOnly(Side.CLIENT)
  public void addBasicEntries(ItemStack itemstack, EntityPlayer entityplayer, List<String> list, boolean flag) {
  }

  @SuppressWarnings({ "unchecked", "rawtypes" })
  @Override
  @SideOnly(Side.CLIENT)
  public void addDetailedEntries(ItemStack itemstack, EntityPlayer entityplayer, List list, boolean flag) {
    SpecialTooltipHandler.addDetailedTooltipFromResources(list, itemstack);
    if (EnumFacadeType.getTypeFromMeta(itemstack.getMetadata()) != EnumFacadeType.BASIC) {
      list.add("");
      for (String line : EnderIO.lang.localizeExact(getUnlocalizedName(itemstack) + ".tooltip").split(";")) {
        list.add(line);
      }
    }
  }
 
  @Override
  @SideOnly(Side.CLIENT)
  public void getSubItems(Item itemIn, CreativeTabs tab, List<ItemStack> subItems) {
    for (EnumFacadeType type : EnumFacadeType.values()) {
      subItems.add(new ItemStack(itemIn, 1, type.ordinal()));
    }
  }

  @Override
  public void registerRenderers() {
    for (EnumFacadeType type : EnumFacadeType.values()) {
      final ModelResourceLocation mrl = new ModelResourceLocation(getRegistryName(), "type=" + type.getName());
      ModelLoader.setCustomModelResourceLocation(this, EnumFacadeType.getMetaFromType(type), mrl);
      ItemModelRegistry.registerFacade(mrl);
    }
  }
}
