package crazypants.enderio.conduit.facade;

import java.util.List;

import com.enderio.core.api.client.gui.IAdvancedTooltipProvider;
import com.enderio.core.api.client.gui.IResourceTooltipProvider;
import com.enderio.core.client.handlers.SpecialTooltipHandler;
import com.enderio.core.common.util.Util;

import crazypants.enderio.EnderIO;
import crazypants.enderio.EnderIOTab;
import crazypants.enderio.IHaveRenderers;
import crazypants.enderio.ModObject;
import crazypants.enderio.conduit.BlockConduitBundle;
import crazypants.enderio.conduit.ConduitUtil;
import crazypants.enderio.conduit.IConduitBundle;
import crazypants.enderio.config.Config;
import crazypants.enderio.machine.MachineRecipeInput;
import crazypants.enderio.machine.painter.BasicPainterTemplate;
import crazypants.enderio.machine.painter.IPaintedBlock;
import crazypants.enderio.machine.painter.PaintSourceValidator;
import crazypants.enderio.machine.painter.PainterUtil;
import crazypants.enderio.machine.painter.PainterUtil2;
import crazypants.util.ClientUtil;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemConduitFacade extends Item implements IAdvancedTooltipProvider, IResourceTooltipProvider,IHaveRenderers {

  public static enum FacadeType {
    BASIC,
    HARDENED;

    public String getUnlocName(Item me) {
      return this == BASIC ? me.getUnlocalizedName() : me.getUnlocalizedName() + ".hardened";
    }
  }

  public static ItemConduitFacade create() {
    ItemConduitFacade result = new ItemConduitFacade();
    result.init();
    return result;
  }

  protected ItemConduitFacade() {
    setCreativeTab(EnderIOTab.tabEnderIO);
    setMaxStackSize(64);
    setHasSubtypes(true);
  }

  protected void init() {
    GameRegistry.registerItem(this, ModObject.itemConduitFacade.unlocalisedName);
  }

  @Override
  @SideOnly(Side.CLIENT)
  public void getSubItems(Item item, CreativeTabs p_150895_2_, List<ItemStack> list) {
    for (FacadeType t : FacadeType.values()) {
      ItemStack is = new ItemStack(item, 1, t.ordinal());
      //TODO: Testing
      PainterUtil.setSourceBlock(is, Blocks.cobblestone.getDefaultState());
      list.add(is);
    }
  }

  @Override
  public String getUnlocalizedName(ItemStack stack) {
    return FacadeType.values()[stack.getItemDamage()].getUnlocName(this);
  }

  @Override
  public String getUnlocalizedName() {
    return "item.enderio." + ModObject.itemConduitFacade.name();
  }
  

  @SideOnly(Side.CLIENT)
  @Override
  public void registerRenderers() {
    ClientUtil.regRenderer(this, 0, ModObject.itemConduitFacade.unlocalisedName);
    ClientUtil.regRenderer(this, 1, ModObject.itemConduitFacade.unlocalisedName + "Hardened");
//  overlayIcon = IIconRegister.registerIcon("enderio:conduitFacadeOverlay");
  }
  
  @Override 
 public boolean onItemUse(ItemStack itemStack, EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ) {

    if(world.isRemote) {
      return true;
    }

    BlockPos placeAt = pos.offset(side);

    if (player.canPlayerEdit(placeAt, side, itemStack) && PainterUtil.getSourceBlock(itemStack) != null) {
      if (world.isAirBlock(placeAt)) {
        world.setBlockState(placeAt, EnderIO.blockConduitBundle.getDefaultState());
        IConduitBundle bundle = (IConduitBundle) world.getTileEntity(placeAt);
        IBlockState bs = PainterUtil2.getSourceBlock(itemStack);
        bundle.setFacade(bs);
        bundle.setFacadeType(FacadeType.values()[itemStack.getItemDamage()]);
        ConduitUtil.playPlaceSound(bs.getBlock().stepSound, world, pos.getX(), pos.getY(), pos.getZ());
        if (!player.capabilities.isCreativeMode) {
          itemStack.stackSize--;
        }
        return true;
      } else {
        Block block = world.getBlockState(placeAt).getBlock();
        if (block == EnderIO.blockConduitBundle) {
          ((BlockConduitBundle) block).handleFacadeClick(world, placeAt, player, side.getOpposite(),
              (IConduitBundle) world.getTileEntity(placeAt), itemStack);
        }
      }
    }

    return false;
  }


  @Override
  @SideOnly(Side.CLIENT)
  public boolean isFull3D() {
    return true;
  }

  public ItemStack createItemStackForSourceBlock(Block id, int itemDamage) {
    if(id == null) {
      id = EnderIO.blockConduitFacade;
    }
    ItemStack result = new ItemStack(id, 1, 0);
    PainterUtil.setSourceBlock(result, id, itemDamage);
    return result;
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
    if(PainterUtil.getSourceBlock(itemstack) == null) {
      list.add(EnderIO.lang.localize("item.itemConduitFacade.tooltip.notpainted"));
    } else {
      list.add(PainterUtil2.getTooltTipText(itemstack));
    }
  }

  @SuppressWarnings({ "unchecked", "rawtypes" })
  @Override
  @SideOnly(Side.CLIENT)
  public void addDetailedEntries(ItemStack itemstack, EntityPlayer entityplayer, List list, boolean flag) {
    SpecialTooltipHandler.addDetailedTooltipFromResources(list, itemstack);
    if(itemstack.getItemDamage() == FacadeType.HARDENED.ordinal()) {
      list.add("");
      list.add(EnderIO.lang.localizeExact(getUnlocalizedName(itemstack) + ".tooltip"));
    }
  }

  // public final class FacadePainterRecipe extends BasicPainterTemplate {
  //
  // @Override
  // public boolean isValidPaintSource(ItemStack paintSource) {
  // if(paintSource == null) {
  // return false;
  // }
  // if (paintSource.getItem() == ItemConduitFacade.this) {
  // return true;
  // }
  // Block block = Util.getBlockFromItemId(paintSource);
  // if(block == null || block instanceof IPaintedBlock) {
  // return false;
  // }
  // if(PaintSourceValidator.instance.isBlacklisted(paintSource)) {
  // return false;
  // }
  // if(PaintSourceValidator.instance.isWhitelisted(paintSource)) {
  // return true;
  // }
  // if (!Config.allowTileEntitiesAsPaintSource && block.hasTileEntity(block.getStateFromMeta(paintSource.getItemDamage()))) {
  // return false;
  // }
  // if(block == EnderIO.blockFusedQuartz && paintSource.getItemDamage() < 2) {
  // return true;
  // }
  // return block.getRenderType() == 0 || block.isOpaqueCube() || block.isNormalCube();
  // }
  //
  // @Override
  // public boolean isValidTarget(ItemStack target) {
  // return target != null && target.getItem() == ItemConduitFacade.this;
  // }
  //
  // @Override
  // public ResultStack[] getCompletedResult(float chance, MachineRecipeInput... inputs) {
  // ResultStack[] res = super.getCompletedResult(chance, inputs);
  // return res;
  // }
  // }
  //
  //


}
