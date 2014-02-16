package crazypants.enderio.teleport;

import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Icon;
import net.minecraft.world.World;
import cpw.mods.fml.common.registry.GameRegistry;
import crazypants.enderio.Config;
import crazypants.enderio.EnderIOTab;
import crazypants.enderio.ModObject;
import crazypants.enderio.PacketHandler;

public class BlockTravelAnchor extends Block implements ITileEntityProvider {

  public static BlockTravelAnchor create() {
    TravelPacketHandler pp = new TravelPacketHandler();
    PacketHandler.instance.addPacketProcessor(pp);

    BlockTravelAnchor result = new BlockTravelAnchor();
    result.init();
    return result;
  }

  Icon selectedOverlayIcon;
  Icon highlightOverlayIcon;

  private BlockTravelAnchor() {
    super(ModObject.blockTravelPlatform.id, Material.rock);
    setHardness(0.5F);
    setStepSound(Block.soundStoneFootstep);
    setUnlocalizedName("enderio." + ModObject.blockTravelPlatform);
    if(Config.travelAnchorEnabled) {
      setCreativeTab(EnderIOTab.tabEnderIO);
    } else {
      setCreativeTab(null);
    }
  }

  private void init() {
    GameRegistry.registerBlock(this, ModObject.blockTravelPlatform.unlocalisedName);
    GameRegistry.registerTileEntity(TileTravelAnchor.class, ModObject.blockTravelPlatform.unlocalisedName + "TileEntity");
  }

  @Override
  public void registerIcons(IconRegister iconRegister) {
    blockIcon = iconRegister.registerIcon("enderio:blockTravelAnchor");
    highlightOverlayIcon = iconRegister.registerIcon("enderio:blockTravelAnchorHighlight");
    selectedOverlayIcon = iconRegister.registerIcon("enderio:blockTravelAnchorSelected");
  }

  @Override
  public TileEntity createNewTileEntity(World world) {
    return new TileTravelAnchor();
  }

}
