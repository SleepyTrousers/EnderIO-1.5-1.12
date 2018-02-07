package crazypants.enderio.base.material.alloy;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.enderio.core.api.client.gui.IAdvancedTooltipProvider;
import com.enderio.core.common.util.NNList;
import com.enderio.core.common.util.NNList.Callback;

import crazypants.enderio.base.BlockEio;
import crazypants.enderio.base.EnderIOTab;
import crazypants.enderio.base.TileEntityEio;
import crazypants.enderio.base.init.IModObject;
import crazypants.enderio.base.lang.Lang;
import crazypants.enderio.base.render.IHaveRenderers;
import crazypants.enderio.util.ClientUtil;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockAlloy extends BlockEio<TileEntityEio> implements IAdvancedTooltipProvider, IHaveRenderers {

  public static BlockAlloy create(@Nonnull IModObject modObject) {
    BlockAlloy res = new BlockAlloy(modObject);
    res.init();
    return res;
  }

  public static final @Nonnull PropertyEnum<Alloy> VARIANT = PropertyEnum.<Alloy> create("variant", Alloy.class);

  private BlockAlloy(@Nonnull IModObject modObject) {
    super(modObject, Material.IRON);
    setSoundType(SoundType.METAL);
    setCreativeTab(EnderIOTab.tabEnderIOMaterials);
  }

  @Override
  public Item createBlockItem(@Nonnull IModObject modObject) {
    return modObject.apply(new ItemBlockAlloy(this));
  }

  @Override
  @SideOnly(Side.CLIENT)
  public void registerRenderers(@Nonnull IModObject modObject) {
    NNList.of(Alloy.class).apply(new Callback<Alloy>() {
      @Override
      public void apply(@Nonnull Alloy alloy) {
        ClientUtil.regRenderer(BlockAlloy.this, Alloy.getMetaFromType(alloy), VARIANT.getName() + "=" + VARIANT.getName(alloy));
      }
    });
  }

  @Override
  public int damageDropped(@Nonnull IBlockState state) {
    return state.getValue(VARIANT).ordinal();
  }

  @Override
  public @Nonnull IBlockState getStateFromMeta(int meta) {
    return getDefaultState().withProperty(VARIANT, Alloy.getTypeFromMeta(meta));
  }

  @Override
  public int getMetaFromState(@Nonnull IBlockState state) {
    return state.getValue(VARIANT).ordinal();
  }

  @Override
  protected @Nonnull BlockStateContainer createBlockState() {
    return new BlockStateContainer(this, new IProperty[] { VARIANT });
  }

  @Override
  public float getBlockHardness(@Nonnull IBlockState bs, @Nonnull World world, @Nonnull BlockPos pos) {
    return bs.getValue(VARIANT).getHardness();
  }

  @Override
  public float getExplosionResistance(@Nonnull World world, @Nonnull BlockPos pos, @Nullable Entity exploder, @Nonnull Explosion explosion) {
    return getBlockHardness(world.getBlockState(pos), world, pos) * 2.0f; // vanilla default is / 5.0f, this means hardness*2 = resistance
    // TODO 1.9 um, I cannot follow that comment above. Shouldn't this be /5f?
  }

  @Override
  public boolean isBeaconBase(@Nonnull IBlockAccess world, @Nonnull BlockPos pos, @Nonnull BlockPos beacon) {
    return true;
  }

  @Override
  public boolean canBeWrenched() {
    return false;
  }

  @Override
  public void addCommonEntries(@Nonnull ItemStack itemstack, @Nullable EntityPlayer entityplayer, @Nonnull List<String> list, boolean flag) {
    list.add(Lang.BETTER_WITH_BACON.get());
  }

  @Override
  public void addBasicEntries(@Nonnull ItemStack itemstack, @Nullable EntityPlayer entityplayer, @Nonnull List<String> list, boolean flag) {

  }

  @Override
  public void addDetailedEntries(@Nonnull ItemStack itemstack, @Nullable EntityPlayer entityplayer, @Nonnull List<String> list, boolean flag) {
  }

}
