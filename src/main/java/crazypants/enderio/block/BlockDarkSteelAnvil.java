package crazypants.enderio.block;

import javax.annotation.Nonnull;

import com.enderio.core.api.client.gui.IResourceTooltipProvider;
import com.enderio.core.common.event.AnvilMaxCostEvent;
import com.enderio.core.common.util.NullHelper;

import crazypants.enderio.EnderIOTab;
import crazypants.enderio.GuiID;
import crazypants.enderio.IModObject;
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

  public static BlockDarkSteelAnvil create(@Nonnull IModObject modObject) {
    BlockDarkSteelAnvil res = new BlockDarkSteelAnvil(modObject);
    MinecraftForge.EVENT_BUS.register(res);
    res.init();
    return res;
  }

  private final @Nonnull IModObject modObject;

  private BlockDarkSteelAnvil(@Nonnull IModObject modObject) {
    setHardness(5.0F);
    setSoundType(SoundType.ANVIL);
    setResistance(2000.0F);

    setUnlocalizedName(modObject.getUnlocalisedName());
    setRegistryName(modObject.getUnlocalisedName());
    setCreativeTab(EnderIOTab.tabEnderIO);
    this.modObject = modObject;
  }

  protected void init() {
    GameRegistry.register(this);
    ItemAnvilBlock item = new ItemAnvilBlock(this);
    item.setRegistryName(modObject.getUnlocalisedName());
    GameRegistry.register(item);

    GuiID.registerGuiHandler(GuiID.GUI_ID_ANVIL, new IGuiHandler() {

      @Override
      public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        return new ContainerDarkSteelAnvil(player.inventory, NullHelper.notnullF(world, "getServerGuiElement without world?"), x, y, z, player);
      }

      @Override
      public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        return new GuiDarkSteelAnvil(player.inventory, player.world);
      }
    });
  }

  @SubscribeEvent
  public void onMaxAnvilCost(AnvilMaxCostEvent evt) {
    if (evt.getSource() instanceof ContainerDarkSteelAnvil) {
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
  public @Nonnull String getUnlocalizedNameForTooltip(@Nonnull ItemStack itemStack) {
    return this.getUnlocalizedName();
  }

  @Override
  public boolean onBlockActivated(@Nonnull World worldIn, @Nonnull BlockPos pos, @Nonnull IBlockState state, @Nonnull EntityPlayer playerIn,
      @Nonnull EnumHand hand, @Nonnull EnumFacing side, float hitX, float hitY, float hitZ) {
    GuiID.GUI_ID_ANVIL.openGui(worldIn, pos, playerIn, side);
    return true;
  }

  @Override
  @SideOnly(Side.CLIENT)
  public void registerRenderers() {
    for (Integer dmg : DAMAGE.getAllowedValues()) {
      ClientUtil.regRenderer(this, dmg, DAMAGE.getName() + "=" + DAMAGE.getName(NullHelper.notnullM(dmg, "invalid property")) + "," + FACING.getName() + "="
          + FACING.getName(EnumFacing.WEST));
    }
  }

}
