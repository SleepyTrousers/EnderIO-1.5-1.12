package crazypants.enderio.block.darksteel.bars;

import javax.annotation.Nonnull;

import crazypants.enderio.EnderIOTab;
import crazypants.enderio.block.darksteel.BlastResistantItemBlock;
import crazypants.enderio.init.IModObject;
import crazypants.enderio.render.IDefaultRenderers;
import net.minecraft.block.BlockPane;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class BlockDarkIronBars extends BlockPane implements IDefaultRenderers {

  public static BlockDarkIronBars create(@Nonnull IModObject modObject) {
    return new BlockDarkIronBars(modObject).init(modObject);
  }

  protected BlockDarkIronBars(@Nonnull IModObject modObject) {
    super(Material.IRON, true);
    setResistance(2000.0F); // TNT Proof
    setHardness(5.0F);
    setSoundType(SoundType.METAL);
    setUnlocalizedName(modObject.getUnlocalisedName());
    setRegistryName(modObject.getUnlocalisedName());
    setCreativeTab(EnderIOTab.tabEnderIO);
  }

  protected BlockDarkIronBars init(@Nonnull IModObject modObject) {
    GameRegistry.register(this);
    GameRegistry.register(new BlastResistantItemBlock(this, modObject.getUnlocalisedName()));
    return this;
  }

}
