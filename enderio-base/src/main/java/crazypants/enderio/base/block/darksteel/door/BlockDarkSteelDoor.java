package crazypants.enderio.base.block.darksteel.door;

import java.util.Random;

import javax.annotation.Nonnull;

import crazypants.enderio.api.IModObject;
import crazypants.enderio.base.EnderIOTab;
import crazypants.enderio.base.render.IDefaultRenderers;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

public class BlockDarkSteelDoor extends BlockDoor implements IDefaultRenderers, IModObject.WithBlockItem {

  public static BlockDarkSteelDoor create(@Nonnull IModObject modObject) {
    return new BlockDarkSteelDoor(modObject, Material.IRON, true);
  }

  private final @Nonnull IModObject modobject;

  public BlockDarkSteelDoor(@Nonnull IModObject modObject, Material materialIn, boolean isBlastResistant) {
    super(materialIn);
    if (isBlastResistant) {
      setResistance(2000.0F); // TNT Proof
    }
    if (materialIn == Material.IRON) {
      setHardness(5.0F);
      setSoundType(SoundType.METAL);
    } else {
      setHardness(3.0F);
      setSoundType(SoundType.WOOD);
    }
    disableStats();
    modObject.apply(this);
    setCreativeTab(EnderIOTab.tabEnderIO);
    this.modobject = modObject;
  }

  @Override
  public Item createBlockItem(@Nonnull IModObject modObject) {
    return modObject.apply(new BlockItemDarkSteelDoor(this));
  }

  @Override
  public @Nonnull ItemStack getPickBlock(@Nonnull IBlockState bs, @Nonnull RayTraceResult target, @Nonnull World world, @Nonnull BlockPos pos,
      @Nonnull EntityPlayer player) {
    return new ItemStack(getItem(), 1, this.damageDropped(bs));
  }

  @Override
  public @Nonnull ItemStack getItem(@Nonnull World worldIn, @Nonnull BlockPos pos, @Nonnull IBlockState state) {
    return new ItemStack(getItem());
  }

  private @Nonnull Item getItem() {
    return modobject.getItemNN();
  }

  @Override
  public @Nonnull Item getItemDropped(@Nonnull IBlockState state, @Nonnull Random rand, int fortune) {
    return state.getValue(HALF) == BlockDoor.EnumDoorHalf.UPPER ? Items.AIR : this.getItem();
  }
}
