package crazypants.enderio.base.block.darksteel.door;

import java.util.Random;

import javax.annotation.Nonnull;

import crazypants.enderio.base.EnderIOTab;
import crazypants.enderio.base.init.IModObject;
import crazypants.enderio.base.init.ModObject;
import crazypants.enderio.base.item.darksteel.ItemDarkSteelDoor;
import crazypants.enderio.base.render.IDefaultRenderers;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

public class BlockDarkSteelDoor extends BlockDoor implements IDefaultRenderers, IModObject.WithBlockItem {

  public static BlockDarkSteelDoor create(@Nonnull IModObject modObject) {
    return new BlockDarkSteelDoor(modObject, Material.IRON, true);
  }

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
  }

  @Override
  public Item createBlockItem(@Nonnull IModObject modObject) {
    return modObject.apply(new ItemDarkSteelDoor(this));
  }

  @Override
  public @Nonnull ItemStack getPickBlock(@Nonnull IBlockState bs, @Nonnull RayTraceResult target, @Nonnull World world, @Nonnull BlockPos pos,
      @Nonnull EntityPlayer player) {
    // TODO Item.getItemFromBlock
    return new ItemStack(ModObject.blockDarkSteelDoor.getItem(), 1, this.damageDropped(bs));
  }

  @Override
  public Item getItemDropped(IBlockState state, Random rand, int fortune) {
    // TODO Item.getItemFromBlock
    return ModObject.blockDarkSteelDoor.getItem();
  }
}
