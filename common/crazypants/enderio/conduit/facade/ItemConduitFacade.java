package crazypants.enderio.conduit.facade;

import java.util.Collections;
import java.util.List;

import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Icon;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import crazypants.enderio.EnderIO;
import crazypants.enderio.EnderIOTab;
import crazypants.enderio.ModObject;
import crazypants.enderio.conduit.IConduitBundle;
import crazypants.enderio.crafting.IEnderIoRecipe;
import crazypants.enderio.crafting.impl.EnderIoRecipe;
import crazypants.enderio.machine.painter.BasicPainterTemplate;
import crazypants.enderio.machine.painter.PainterUtil;

public class ItemConduitFacade extends Item {

  public static ItemConduitFacade create() {
    ItemConduitFacade result = new ItemConduitFacade();
    result.init();
    return result;
  }

  protected Icon overlayIcon;

  protected ItemConduitFacade() {
    super(ModObject.itemConduitFacade.id);
    setCreativeTab(EnderIOTab.tabEnderIO);
    setUnlocalizedName("enderio." + ModObject.itemConduitFacade.name());
    setMaxStackSize(64);
  }

  protected void init() {
    GameRegistry.registerItem(this, ModObject.itemConduitFacade.unlocalisedName);
  }

  @Override
  public void registerIcons(IconRegister iconRegister) {
    itemIcon = iconRegister.registerIcon("enderio:conduitFacade");
    overlayIcon = iconRegister.registerIcon("enderio:conduitFacadeOverlay");
  }

  public Icon getOverlayIcon() {
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
        && PainterUtil.getSourceBlockId(itemStack) > 0) {

      world.setBlock(placeX, placeY, placeZ, EnderIO.blockConduitBundle.blockID);
      IConduitBundle bundle = (IConduitBundle) world.getBlockTileEntity(placeX, placeY, placeZ);
      bundle.setFacadeId(PainterUtil.getSourceBlockId(itemStack));
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

  @Override
  public void onCreated(ItemStack itemStack, World world, EntityPlayer player) {
    if(PainterUtil.getSourceBlock(itemStack) == null) {
      PainterUtil.setSourceBlock(itemStack, ModObject.itemConduitFacade.id, 0);
    }
  }

  public ItemStack createItemStackForSourceBlock(int id, int itemDamage) {
    if(id < 1) {
      id = ModObject.blockConduitFacade.id;
    }
    ItemStack result = new ItemStack(itemID, 1, 0);
    PainterUtil.setSourceBlock(result, id, itemDamage);
    return result;
  }

  @Override
  @SideOnly(Side.CLIENT)
  public void addInformation(ItemStack item, EntityPlayer par2EntityPlayer, List list, boolean par4) {
    super.addInformation(item, par2EntityPlayer, list, par4);
    list.add(PainterUtil.getTooltTipText(item));
  }

  public static final class FacadePainterRecipe extends BasicPainterTemplate {

    public FacadePainterRecipe() {
      super(ModObject.itemConduitFacade.actualId);
    }

    @Override
    public List<IEnderIoRecipe> getAllRecipes() {
      ItemStack is = new ItemStack(ModObject.itemConduitFacade.actualId, 1, 0);
      IEnderIoRecipe recipe = new EnderIoRecipe(IEnderIoRecipe.PAINTER_ID, DEFAULT_ENERGY_PER_TASK, is, is);
      return Collections.singletonList(recipe);
    }

  }

}
