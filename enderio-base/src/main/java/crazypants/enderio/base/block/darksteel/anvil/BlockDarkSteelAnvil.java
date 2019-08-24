package crazypants.enderio.base.block.darksteel.anvil;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.enderio.core.api.client.gui.IResourceTooltipProvider;
import com.enderio.core.common.util.NullHelper;

import crazypants.enderio.api.IModObject;
import crazypants.enderio.base.EnderIO;
import crazypants.enderio.base.EnderIOTab;
import crazypants.enderio.base.config.config.BaseConfig;
import crazypants.enderio.base.config.config.BlockConfig;
import crazypants.enderio.base.gui.handler.IEioGuiHandler;
import crazypants.enderio.base.handler.darksteel.gui.DSUContainer;
import crazypants.enderio.base.handler.darksteel.gui.DSUGui;
import crazypants.enderio.base.init.ModObjectRegistry;
import crazypants.enderio.base.render.IHaveRenderers;
import crazypants.enderio.util.ClientUtil;
import net.minecraft.block.BlockAnvil;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemAnvilBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@EventBusSubscriber(modid = EnderIO.MODID)
public class BlockDarkSteelAnvil extends BlockAnvil
    implements IResourceTooltipProvider, IHaveRenderers, IEioGuiHandler.WithServerComponent, IModObject.WithBlockItem {

  public static BlockDarkSteelAnvil create(@Nonnull IModObject modObject) {
    return new BlockDarkSteelAnvil(modObject);
  }

  BlockDarkSteelAnvil(@Nonnull IModObject modObject) {
    setHardness(5.0F);
    setSoundType(SoundType.ANVIL);
    setResistance(BaseConfig.explosionResistantBlockHardness.get());
    modObject.apply(this);
    setCreativeTab(EnderIOTab.tabEnderIO);
  }

  @Override
  public Item createBlockItem(@Nonnull IModObject modObject) {
    return modObject.apply(new ItemAnvilBlock(this));
  }

  @Override
  @Nullable
  public Object getGuiElement(boolean server, @Nonnull EntityPlayer player, @Nonnull World world, @Nonnull BlockPos pos, @Nullable EnumFacing facing,
      int param1, int param2, int param3) {
    DSUContainer container = DSUContainer.create(player, world, pos, facing, param1, this);
    return container == null ? null : server ? container : makeGuiObject(container, param1);
  }

  @SideOnly(Side.CLIENT)
  private static Object makeGuiObject(@Nonnull DSUContainer container, int param1) {
    return new DSUGui(container, param1);
  }

  @Override
  public @Nonnull String getUnlocalizedNameForTooltip(@Nonnull ItemStack itemStack) {
    return this.getUnlocalizedName();
  }

  @Override
  public boolean onBlockActivated(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull IBlockState state, @Nonnull EntityPlayer entityPlayer,
      @Nonnull EnumHand hand, @Nonnull EnumFacing side, float hitX, float hitY, float hitZ) {
    return ModObjectRegistry.getModObjectNN(this).openGui(world, pos, entityPlayer, side, -2);
  }

  @Override
  @SideOnly(Side.CLIENT)
  public void registerRenderers(@Nonnull IModObject modObject) {
    for (Integer dmg : DAMAGE.getAllowedValues()) {
      ClientUtil.regRenderer(this, dmg, DAMAGE.getName() + "=" + DAMAGE.getName(NullHelper.notnullM(dmg, "invalid property")) + "," + FACING.getName() + "="
          + FACING.getName(EnumFacing.NORTH));
    }
  }

  public float getDamageChance() {
    return BlockConfig.darkSteelAnvilDamageChance.get();
  }

  public int getUseEvent() {
    return 1030;
  }

  public int getBreakEvent() {
    return 1029;
  }

}
