package crazypants.enderio.base.block.darksteel.anvil;

import javax.annotation.Nonnull;

import com.enderio.core.api.client.gui.IResourceTooltipProvider;
import com.enderio.core.common.event.AnvilMaxCostEvent;
import com.enderio.core.common.util.NullHelper;

import crazypants.enderio.base.EnderIOTab;
import crazypants.enderio.base.GuiID;
import crazypants.enderio.base.config.Config;
import crazypants.enderio.base.init.IModObject;
import crazypants.enderio.base.render.IHaveRenderers;
import crazypants.enderio.util.ClientUtil;
import net.minecraft.block.BlockAnvil;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemAnvilBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockDarkSteelAnvil extends BlockAnvil implements IResourceTooltipProvider, IHaveRenderers, GuiID.IEioGuiHandler, IModObject.WithBlockItem {

  public static BlockDarkSteelAnvil create(@Nonnull IModObject modObject) {
    MinecraftForge.EVENT_BUS.register(BlockDarkSteelAnvil.class);
    return new BlockDarkSteelAnvil(modObject);
  }

  private BlockDarkSteelAnvil(@Nonnull IModObject modObject) {
    setHardness(5.0F);
    setSoundType(SoundType.ANVIL);
    setResistance(2000.0F);
    modObject.apply(this);
    setCreativeTab(EnderIOTab.tabEnderIO);
    GuiID.registerGuiHandler(GuiID.GUI_ID_ANVIL, this);
  }

  @Override
  public Item createBlockItem(@Nonnull IModObject modObject) {
    return modObject.apply(new ItemAnvilBlock(this));
  }

  @Override
  public Object getServerGuiElement(int ID, @Nonnull EntityPlayer player, @Nonnull World world, @Nonnull BlockPos pos) {
    return new ContainerDarkSteelAnvil(player.inventory, world, pos, player);
  }

  @Override
  public Object getClientGuiElement(int ID, @Nonnull EntityPlayer player, @Nonnull World world, @Nonnull BlockPos pos) {
    return new GuiDarkSteelAnvil(player.inventory, player.world);
  }

  @SubscribeEvent
  public static void onMaxAnvilCost(AnvilMaxCostEvent evt) {
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
  public void registerRenderers(@Nonnull IModObject modObject) {
    for (Integer dmg : DAMAGE.getAllowedValues()) {
      ClientUtil.regRenderer(this, dmg, DAMAGE.getName() + "=" + DAMAGE.getName(NullHelper.notnullM(dmg, "invalid property")) + "," + FACING.getName() + "="
          + FACING.getName(EnumFacing.WEST));
    }
  }

}
