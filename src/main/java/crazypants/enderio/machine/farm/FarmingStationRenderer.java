package crazypants.enderio.machine.farm;

//TODO: 1.8
//public class FarmingStationRenderer extends TechneMachineRenderer<TileFarmStation> {
//
//  public FarmingStationRenderer() {
//    super(EnderIO.blockFarmStation, "models/farm");
//  }
//
//  @Override
//  public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderer) {
//    IIcon override = renderer.overrideBlockTexture;
//
//    if(world != null) {
//      TileEntity te = world.getTileEntity(x, y, z);
//      if(te instanceof TileFarmStation && ((TileFarmStation) te).isActive()) {
//        BoundingBox bb = BoundingBox.UNIT_CUBE.scale(10D / 16D, 0.25, 10D / 16D);
//        bb = bb.scale(1.01, 1, 1.01);
//        bb = bb.translate(0, 5f / 16f, 0);
//        bb = bb.translate(x, y, z);
//        Tessellator.instance.setColorOpaque_F(1, 1, 1);
//        Tessellator.instance.setBrightness(0xF000F0);
//        CubeRenderer.render(bb, override != null ? override : Blocks.portal.getBlockTextureFromSide(1));
//      }
//    }
//
//    return super.renderWorldBlock(world, x, y, z, block, modelId, renderer);
//  }
//}
