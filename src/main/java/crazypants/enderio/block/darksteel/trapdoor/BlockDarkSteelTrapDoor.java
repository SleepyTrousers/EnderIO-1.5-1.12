package crazypants.enderio.block.darksteel.trapdoor;

import javax.annotation.Nonnull;

import crazypants.enderio.EnderIOTab;
import crazypants.enderio.IModObject;
import crazypants.enderio.block.darksteel.BlastResistantItemBlock;
import crazypants.enderio.render.IDefaultRenderers;
import net.minecraft.block.BlockTrapDoor;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class BlockDarkSteelTrapDoor extends BlockTrapDoor implements IDefaultRenderers {

  public static BlockDarkSteelTrapDoor create(@Nonnull IModObject modObject) {
    return new BlockDarkSteelTrapDoor(modObject, Material.IRON, true).init(modObject);
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
    setUnlocalizedName(modObject.getUnlocalisedName());
    setRegistryName(modObject.getUnlocalisedName());
    setCreativeTab(EnderIOTab.tabEnderIO);
  }

  protected BlockDarkSteelTrapDoor init(@Nonnull IModObject modObject) {
    GameRegistry.register(this);
    GameRegistry.register(new BlastResistantItemBlock(this, modObject.getUnlocalisedName()));
    return this;
  }

}
