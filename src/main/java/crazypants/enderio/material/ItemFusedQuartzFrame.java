package crazypants.enderio.material;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import crazypants.enderio.EnderIO;
import crazypants.enderio.EnderIOTab;
import crazypants.enderio.ModObject;

public class ItemFusedQuartzFrame extends Item {

  public static ItemFusedQuartzFrame create() {
    ItemFusedQuartzFrame result = new ItemFusedQuartzFrame();
    result.init();
    return result;
  }

  protected ItemFusedQuartzFrame() {
    setCreativeTab(EnderIOTab.tabEnderIO);
    setUnlocalizedName("enderio." + ModObject.itemFusedQuartzFrame.name());
    setMaxStackSize(64);
  }

  protected void init() {
    GameRegistry.registerItem(this, ModObject.itemFusedQuartzFrame.unlocalisedName);
    //TODO:1.7
    //MachineRecipeRegistry.instance.registerRecipe(ModObject.blockPainter.unlocalisedName, new FramePainterRecipe());
  }

  @Override
  public void registerIcons(IIconRegister IIconRegister) {
  }

  @Override
  public boolean onItemUse(ItemStack itemStack, EntityPlayer player, World world, int x, int y, int z, int side, float par8,
      float par9, float par10) {

    if(world.getBlock(x, y, z) == EnderIO.blockFusedQuartz) {
      //TODO:1.7
      //      TileEntityCustomBlock tecb = (TileEntityCustomBlock) world.getTileEntity(x, y, z);
      //      if(tecb == null) {
      //        return false;
      //      }
      //      tecb.setSourceBlockId(PainterUtil.getSourceBlockId(itemStack));
      //      tecb.setSourceBlockMetadata(PainterUtil.getSourceBlockMetadata(itemStack));
      //      world.markBlockForUpdate(x, y, z);
      //      world.markBlockForRenderUpdate(x, y, z);
      //      if(!world.isRemote) {
      //        if(!player.capabilities.isCreativeMode) {
      //          itemStack.stackSize--;
      //        }
      //      }
      return true;
    } else {
      return false;
    }
  }

  @Override
  @SideOnly(Side.CLIENT)
  public boolean isFull3D() {
    return true;
  }

  //  @Override
  //  public String getUnlocalizedName(ItemStack par1ItemStack) {
  //    System.out.println("ItemFusedQuartzFrame.getUnlocalizedName: ");
  //    return "john";
  //  }
  //
  //  @Override
  //  public String getUnlocalizedName() {
  //    System.out.println("ItemFusedQuartzFrame.getUnlocalizedName: ");
  //    return super.getUnlocalizedName();
  //  }

  //TODO:1.7

  //  @Override
  //  @SideOnly(Side.CLIENT)
  //  public void addInformation(ItemStack item, EntityPlayer par2EntityPlayer, List list, boolean par4) {
  //    super.addInformation(item, par2EntityPlayer, list, par4);
  //    list.add(PainterUtil.getTooltTipText(item));
  //  }
  //
  //  public static final class FramePainterRecipe extends BasicPainterTemplate {
  //
  //    public FramePainterRecipe() {
  //      super(ModObject.itemFusedQuartzFrame.actualId);
  //    }
  //
  //    @Override
  //    public List<IEnderIoRecipe> getAllRecipes() {
  //      ItemStack is = new ItemStack(ModObject.itemFusedQuartzFrame.actualId, 1, 0);
  //      IEnderIoRecipe recipe = new EnderIoRecipe(IEnderIoRecipe.PAINTER_ID, DEFAULT_ENERGY_PER_TASK, is, is);
  //      return Collections.singletonList(recipe);
  //    }
  //  }
}
