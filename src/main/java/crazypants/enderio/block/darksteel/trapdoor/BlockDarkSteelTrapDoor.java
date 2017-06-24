package crazypants.enderio.block.darksteel.trapdoor;

import javax.annotation.Nonnull;

import crazypants.enderio.EnderIOTab;
import crazypants.enderio.block.darksteel.BlastResistantItemBlock;
import crazypants.enderio.init.IModObject;
import crazypants.enderio.render.IDefaultRenderers;
import net.minecraft.block.BlockTrapDoor;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.item.Item;

public class BlockDarkSteelTrapDoor extends BlockTrapDoor implements IDefaultRenderers, IModObject.WithBlockItem {

  public static BlockDarkSteelTrapDoor create(@Nonnull IModObject modObject) {
    return new BlockDarkSteelTrapDoor(modObject, Material.IRON, true);
  }

  public BlockDarkSteelTrapDoor(@Nonnull IModObject modObject, Material materialIn, boolean isBlastResistant) {
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
    return modObject.apply(new BlastResistantItemBlock(this));
  }

}
