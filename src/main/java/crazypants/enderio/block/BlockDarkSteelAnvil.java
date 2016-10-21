package crazypants.enderio.block;

import javax.annotation.Nullable;

import com.enderio.core.api.client.gui.IResourceTooltipProvider;
import com.enderio.core.common.event.AnvilMaxCostEvent;

import crazypants.enderio.EnderIO;
import crazypants.enderio.EnderIOTab;
import crazypants.enderio.GuiHandler;
import crazypants.enderio.ModObject;
import crazypants.enderio.config.Config;
import crazypants.enderio.render.IHaveRenderers;
import crazypants.util.ClientUtil;
import net.minecraft.block.BlockAnvil;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemAnvilBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.IGuiHandler;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockDarkSteelAnvil extends BlockAnvil implements IResourceTooltipProvider, IHaveRenderers {

  public static BlockDarkSteelAnvil create() {
    BlockDarkSteelAnvil res = new BlockDarkSteelAnvil();
    MinecraftForge.EVENT_BUS.register(res);
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
        return new GuiDarkSteelAnvil(player.inventory, player.worldObj);
      }
    });
  }

  @SubscribeEvent
  public void onMaxAnvilCost(AnvilMaxCostEvent evt) {    
    if(evt.getSource() instanceof ContainerDarkSteelAnvil) {      
      evt.setMaxAnvilCost(Config.darkSteelAnvilMaxLevel);  
    } else if (FMLCommonHandler.instance().getSide() == Side.CLIENT && isClientShowingOurGui()) {             
      evt.setMaxAnvilCost(Config.darkSteelAnvilMaxLevel);      
    } 
  }
    
  @SideOnly(Side.CLIENT)
  private static boolean isClientShowingOurGui() {
    GuiScreen gui = Minecraft.getMinecraft().currentScreen;
    return gui != null && GuiDarkSteelAnvil.class == gui.getClass();      
  }

  @Override
  public String getUnlocalizedNameForTooltip(ItemStack itemStack) {
    return this.getUnlocalizedName();
  }

  @Override
  public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, @Nullable ItemStack heldItem,
      EnumFacing side,
      float hitX, float hitY, float hitZ) {
    playerIn.openGui(EnderIO.instance, GuiHandler.GUI_ID_ANVIL, worldIn, pos.getX(), pos.getY(), pos.getZ());
    return true;
  }

  @Override
  @SideOnly(Side.CLIENT)
  public void registerRenderers() {
    for (Integer dmg : DAMAGE.getAllowedValues()) {
      ClientUtil.regRenderer(this, dmg, DAMAGE.getName() + "=" + DAMAGE.getName(dmg) + "," + FACING.getName() + "=" + FACING.getName(EnumFacing.WEST));
    }
  }

}
