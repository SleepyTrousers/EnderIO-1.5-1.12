package crazypants.enderio.block;

import com.enderio.core.api.client.gui.IResourceTooltipProvider;

import crazypants.enderio.EnderIO;
import crazypants.enderio.EnderIOTab;
import crazypants.enderio.GuiHandler;
import crazypants.enderio.ModObject;
import net.minecraft.block.BlockAnvil;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.gui.GuiRepair;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemAnvilBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class BlockDarkSteelAnvil extends BlockAnvil implements IResourceTooltipProvider {

//  private static final String[] anvilIconNames = new String[] { "anvil_0", "anvil_1", "anvil_2" };

//  @SideOnly(Side.CLIENT)
//  private IIcon[] anvilIcons;

  public static BlockDarkSteelAnvil create() {
    BlockDarkSteelAnvil res = new BlockDarkSteelAnvil();
    res.init();
    return res;
  }

  private BlockDarkSteelAnvil() {
    super();

    setHardness(5.0F);
    setStepSound(soundTypeAnvil);
    setResistance(2000.0F);

    setUnlocalizedName(ModObject.blockDarkSteelAnvil.unlocalisedName);
    setCreativeTab(EnderIOTab.tabEnderIO);
  }

  protected void init() {
    GameRegistry.registerBlock(this, ItemAnvilBlock.class, ModObject.blockDarkSteelAnvil.unlocalisedName);
    EnderIO.guiHandler.registerGuiHandler(GuiHandler.GUI_ID_ANVIL, new IGuiHandler() {

      @Override
      public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        return new ContainerDarkSteelAnvil(player.inventory, world, x, y, z, player);
      }

      @Override
      public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        return new GuiRepair(player.inventory, world);
      }
    });
  }

  @Override
  public String getUnlocalizedNameForTooltip(ItemStack itemStack) {
    return this.getUnlocalizedName();
  }
  
  @Override
  public boolean onBlockActivated(World w, BlockPos pos, IBlockState state, EntityPlayer p, EnumFacing side, float hitX, float hitY, float hitZ) {   
    p.openGui(EnderIO.instance, GuiHandler.GUI_ID_ANVIL, w, pos.getX(), pos.getY(), pos.getZ());
    return true;
  }
  
//  @SideOnly(Side.CLIENT)
//  public IIcon getIcon(int p_149691_1_, int p_149691_2_)
//  {
//    if(this.anvilRenderSide == 3 && p_149691_1_ == 1)
//    {
//      int k = (p_149691_2_ >> 2) % this.anvilIcons.length;
//      return this.anvilIcons[k];
//    }
//    else
//    {
//      return this.blockIcon;
//    }
//  }
//
//  @SideOnly(Side.CLIENT)
//  public void registerBlockIcons(IIconRegister register)
//  {
//    this.blockIcon = register.registerIcon(EnderIO.DOMAIN + ":anvil_base");
//    this.anvilIcons = new IIcon[anvilIconNames.length];
//
//    for (int i = 0; i < this.anvilIcons.length; ++i)
//    {
//      this.anvilIcons[i] = register.registerIcon(EnderIO.DOMAIN + ":" + anvilIconNames[i]);
//    }
//  }

  
}
