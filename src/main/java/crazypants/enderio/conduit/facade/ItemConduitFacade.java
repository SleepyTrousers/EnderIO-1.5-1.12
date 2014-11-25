package crazypants.enderio.conduit.facade;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import crazypants.enderio.EnderIO;
import crazypants.enderio.EnderIOTab;
import crazypants.enderio.ModObject;
import crazypants.enderio.conduit.IConduitBundle;
import crazypants.enderio.config.Config;
import crazypants.enderio.gui.IAdvancedTooltipProvider;
import crazypants.enderio.gui.TooltipAddera;
import crazypants.enderio.machine.painter.BasicPainterTemplate;
import crazypants.enderio.machine.painter.IPaintedBlock;
import crazypants.enderio.machine.painter.PaintSourceValidator;
import crazypants.enderio.machine.painter.PainterUtil;
import crazypants.util.Util;

public class ItemConduitFacade extends Item implements IAdvancedTooltipProvider {

  public static ItemConduitFacade create() {
    ItemConduitFacade result = new ItemConduitFacade();
    result.init();
    return result;
  }

  protected IIcon overlayIcon;

  protected ItemConduitFacade() {
    setCreativeTab(EnderIOTab.tabEnderIO);
    setUnlocalizedName("enderio." + ModObject.itemConduitFacade.name());
    setMaxStackSize(64);
  }

  protected void init() {
    GameRegistry.registerItem(this, ModObject.itemConduitFacade.unlocalisedName);
  }

  @Override
  public void registerIcons(IIconRegister IIconRegister) {
    itemIcon = IIconRegister.registerIcon("enderio:conduitFacade");
    overlayIcon = IIconRegister.registerIcon("enderio:conduitFacadeOverlay");
  }

  public IIcon getOverlayIcon() {
    return overlayIcon;
  }

  @Override
  public boolean onItemUse(ItemStack itemStack, EntityPlayer player, World world, int x, int y, int z, int side, float par8,
      float par9, float par10) {

    if(world.isRemote) {
      return true;
    }

    ForgeDirection dir = ForgeDirection.values()[side];
    int placeX = x + dir.offsetX;
    int placeY = y + dir.offsetY;
    int placeZ = z + dir.offsetZ;

    if(player.canPlayerEdit(placeX, placeY, placeZ, side, itemStack) && world.isAirBlock(placeX, placeY, placeZ)
        && PainterUtil.getSourceBlock(itemStack) != null) {

      world.setBlock(placeX, placeY, placeZ, EnderIO.blockConduitBundle);
      IConduitBundle bundle = (IConduitBundle) world.getTileEntity(placeX, placeY, placeZ);
      bundle.setFacadeId(PainterUtil.getSourceBlock(itemStack));
      bundle.setFacadeMetadata(PainterUtil.getSourceBlockMetadata(itemStack));
      if(!player.capabilities.isCreativeMode) {
        itemStack.stackSize--;
      }
      return true;
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
  public void addInformation(ItemStack item, EntityPlayer par2EntityPlayer, List list, boolean par4) {
    super.addInformation(item, par2EntityPlayer, list, par4);

  }

  @Override
  @SideOnly(Side.CLIENT)
  public void addCommonEntries(ItemStack itemstack, EntityPlayer entityplayer, List list, boolean flag) {


  }

  @Override
  @SideOnly(Side.CLIENT)
  public void addBasicEntries(ItemStack itemstack, EntityPlayer entityplayer, List list, boolean flag) {
    if(PainterUtil.getSourceBlock(itemstack) == null) {
      list.add("Not Painted");
    } else {
      list.add(PainterUtil.getTooltTipText(itemstack));
    }
  }

  @Override
  @SideOnly(Side.CLIENT)
  public void addDetailedEntries(ItemStack itemstack, EntityPlayer entityplayer, List list, boolean flag) {
    TooltipAddera.addDetailedTooltipFromResources(list, itemstack);
  }

  public final class FacadePainterRecipe extends BasicPainterTemplate {

    @Override
    public boolean isValidPaintSource(ItemStack paintSource) {
      if(paintSource == null) {
        return false;
      }
      Block block = Util.getBlockFromItemId(paintSource);
      if(block == null || block instanceof IPaintedBlock) {
        return false;
      }
      if(PaintSourceValidator.instance.isBlacklisted(paintSource)) {
        return false;
      }
      if(PaintSourceValidator.instance.isWhitelisted(paintSource)) {
        return true;
      }
      if(!Config.allowTileEntitiesAsPaintSource && block instanceof ITileEntityProvider) {
        return false;
      }
      return block.getRenderType() == 0 || block.isOpaqueCube() || block.isNormalCube();
    }
    
    @Override
    public boolean isValidTarget(ItemStack target) {
      return target != null && target.getItem() == ItemConduitFacade.this;
    }  
  }
  
}
