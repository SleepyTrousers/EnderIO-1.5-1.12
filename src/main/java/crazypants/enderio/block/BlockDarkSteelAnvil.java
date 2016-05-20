package crazypants.enderio.block;

import com.enderio.core.api.client.gui.IResourceTooltipProvider;

import crazypants.enderio.EnderIO;
import crazypants.enderio.EnderIOTab;
import crazypants.enderio.GuiHandler;
import crazypants.enderio.IHaveRenderers;
import crazypants.enderio.ModObject;
import crazypants.util.ClientUtil;
import net.minecraft.block.BlockAnvil;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.gui.GuiRepair;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemAnvilBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockDarkSteelAnvil extends BlockAnvil implements IResourceTooltipProvider, IHaveRenderers {

  public static BlockDarkSteelAnvil create() {
    BlockDarkSteelAnvil res = new BlockDarkSteelAnvil();
    res.init();
    return res;
  }

  private BlockDarkSteelAnvil() {
    super();

    setHardness(5.0F);
    setSoundType(SoundType.ANVIL);
    setResistance(2000.0F);

    setUnlocalizedName(ModObject.blockDarkSteelAnvil.getUnlocalisedName());
    setRegistryName(ModObject.blockDarkSteelAnvil.getUnlocalisedName());
    setCreativeTab(EnderIOTab.tabEnderIO);
  }

  protected void init() {
    GameRegistry.register(this);
    ItemAnvilBlock item = new ItemAnvilBlock(this);
    item.setRegistryName(ModObject.blockDarkSteelAnvil.getUnlocalisedName());    
    GameRegistry.register(item);
    
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
  public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, ItemStack heldItem, EnumFacing side,
      float hitX, float hitY, float hitZ) {
    playerIn.openGui(EnderIO.instance, GuiHandler.GUI_ID_ANVIL, worldIn, pos.getX(), pos.getY(), pos.getZ());
    return true;
  }

  @Override
  @SideOnly(Side.CLIENT)
  public void registerRenderers() {
    Item item = Item.getItemFromBlock(this);           
    ClientUtil.regRenderer(item, 0,"anvil_undamaged");
    ClientUtil.regRenderer(item, 1,"anvil_slightly_damaged");
    ClientUtil.regRenderer(item, 2 ,"anvil_very_damaged");
  }

}
