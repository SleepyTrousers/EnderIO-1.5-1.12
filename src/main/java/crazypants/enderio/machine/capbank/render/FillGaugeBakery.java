package crazypants.enderio.machine.capbank.render;

import com.enderio.core.client.render.BoundingBox;
import com.enderio.core.client.render.VertexRotationFacing;
import com.enderio.core.common.BlockEnder;
import com.enderio.core.common.vecmath.Vector3d;
import crazypants.enderio.machine.capbank.BlockCapBank;
import crazypants.enderio.machine.capbank.CapBankType;
import crazypants.enderio.machine.capbank.InfoDisplayType;
import crazypants.enderio.machine.capbank.TileCapBank;
import crazypants.enderio.machine.capbank.network.CapBankClientNetwork;
import crazypants.enderio.machine.capbank.network.ICapBankNetwork;
import crazypants.enderio.render.util.HalfBakedQuad.HalfBakedList;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import org.lwjgl.opengl.GL11;

import java.util.List;

public class FillGaugeBakery {

  private static final Double px = 1d / 16d;
  private static final Vector3d CENTER = new Vector3d(8 * px, 8 * px, 8 * px);

  private CapBankType bankType;
  private int height, myOffset;
  private double localFillLevel;
  private boolean connectUp, connectDown;
  private final IBlockAccess world;
  private final BlockPos pos;
  private final EnumFacing face;
  private final TextureAtlasSprite tex;
  private HalfBakedList buffer, litBuffer;

  public FillGaugeBakery(TextureAtlasSprite tex, double fillLevel) {
    this(null, null, EnumFacing.NORTH, tex, fillLevel);
  }

  public FillGaugeBakery(IBlockAccess world, BlockPos pos, EnumFacing face, TextureAtlasSprite tex, double fillLevel) {
    this.world = world;
    this.pos = pos;
    this.face = face;
    this.tex = tex;
    localFillLevel = fillLevel * 10 + 3;
    connectUp = connectDown = false;
    mkQuads();
  }

  public FillGaugeBakery(IBlockAccess world, BlockPos pos, EnumFacing face, TextureAtlasSprite tex) {
    this.world = world;
    this.pos = pos;
    this.face = face;
    this.tex = tex;
    IBlockState bs = world.getBlockState(pos.offset(face));
    if (bs.getBlock().isSideSolid(bs, world, pos.offset(face), face.getOpposite())) {
      return;
    }
    IBlockState state = world.getBlockState(pos);
    if (!(state.getBlock() instanceof BlockCapBank)) {
      return;
    }
    bankType = state.getValue(CapBankType.KIND);
    if (!bankType.isMultiblock()) {
      // no connection possible
      height = 1;
      myOffset = 0;
      connectUp = connectDown = false;
    } else {
      // determine connections
      countNeighbors();
    }

    if (bankType.isCreative()) {
      localFillLevel = 8; // px
    } else {
      calculateFillLevel();
    }

    mkQuads();
  }

  private void mkQuads() {

    VertexRotationFacing rot = new VertexRotationFacing(face);
    rot.setCenter(CENTER);
    rot.setRotation(EnumFacing.NORTH);

    buffer = new HalfBakedList();

    final double upperBound = (connectUp ? 16 : 13) * px, lowerBound = (connectDown ? 0 : 3) * px;
    final double full_out = -.5 * px, half_out = full_out / 2, quarter_out = full_out / 4, bit_in = .01 * px;

    BoundingBox border1 = new BoundingBox(6 * px, lowerBound, full_out, 7 * px, upperBound, bit_in);
    buffer.add(border1, EnumFacing.NORTH, 15.01 * px, 15.99 * px, lowerBound, upperBound, tex, null);
    buffer.add(border1, EnumFacing.EAST, 15.01 * px, 15.49 * px, lowerBound, upperBound, tex, null);
    buffer.add(border1, EnumFacing.WEST, 15.99 * px, 15.51 * px, lowerBound, upperBound, tex, null);

    BoundingBox border2 = new BoundingBox(9 * px, lowerBound, full_out, 10 * px, upperBound, bit_in);
    buffer.add(border2, EnumFacing.NORTH, 12.01 * px, 12.99 * px, lowerBound, upperBound, tex, null);
    buffer.add(border2, EnumFacing.EAST, 12.01 * px, 12.49 * px, lowerBound, upperBound, tex, null);
    buffer.add(border2, EnumFacing.WEST, 12.99 * px, 12.51 * px, lowerBound, upperBound, tex, null);

    BoundingBox back = new BoundingBox(6 * px, (connectDown ? 0 : 2) * px, full_out, 10 * px, (connectUp ? 16 : 14) * px, bit_in);
    buffer.add(back, EnumFacing.SOUTH, 4.01 * px, 7.99 * px, (connectDown ? 0 : 2) * px, (connectUp ? 16 : 14) * px, tex, null);

    if (!connectDown) {
      BoundingBox border3 = new BoundingBox(6 * px, lowerBound - 1 * px, full_out, 10 * px, 3 * px, bit_in);
      buffer.add(border3, EnumFacing.NORTH, 0.005 * px, 3.995 * px, 13.01 * px, 13.99 * px, tex, null);
      buffer.add(border3, EnumFacing.UP, 0.005 * px, 3.995 * px, 13.5 * px, 13.99 * px, tex, null);
      buffer.add(border3, EnumFacing.DOWN, 0.005 * px, 3.995 * px, 13.5 * px, 13.99 * px, tex, null);
      buffer.add(border3, EnumFacing.WEST, 3.01 * px, 3.49 * px, 13.01 * px, 13.99 * px, tex, null);
      buffer.add(border3, EnumFacing.EAST, 0.99 * px, 0.51 * px, 13.01 * px, 13.99 * px, tex, null);
    }

    if (!connectUp) {
      BoundingBox border4 = new BoundingBox(6 * px, 13 * px, full_out, 10 * px, upperBound + 1 * px, bit_in);
      buffer.add(border4, EnumFacing.NORTH, 0.005 * px, 3.995 * px, 2.01 * px, 2.99 * px, tex, null);
      buffer.add(border4, EnumFacing.UP, 0.005 * px, 3.995 * px, 2.01 * px, 2.5 * px, tex, null);
      buffer.add(border4, EnumFacing.DOWN, 0.005 * px, 3.995 * px, 2.01 * px, 2.5 * px, tex, null);
      buffer.add(border4, EnumFacing.WEST, 3.01 * px, 3.49 * px, 2.01 * px, 2.99 * px, tex, null);
      buffer.add(border4, EnumFacing.EAST, 0.99 * px, 0.51 * px, 2.01 * px, 2.99 * px, tex, null);
    }

    BoundingBox bg = new BoundingBox(6.5 * px, (connectDown ? 0 : 2.5) * px, quarter_out, 9.5 * px, (connectUp ? 16 : 13.5) * px, bit_in);
    buffer.add(bg, EnumFacing.NORTH, 12.5 * px, 15.5 * px, (connectDown ? 0 : 2.5) * px, (connectUp ? 16 : 13.5) * px, tex, null);
    buffer.transform(rot);

    if (localFillLevel > 0.001) {
      BoundingBox fg = new BoundingBox(6.5 * px, (connectDown ? 0 : 2.99) * px, half_out, 9.5 * px, localFillLevel * px, bit_in);
      litBuffer = new HalfBakedList();
      litBuffer.add(fg, EnumFacing.NORTH, 8.55 * px, 11.45 * px, (connectDown ? 0 : 2.99) * px, localFillLevel * px, tex, null);
      litBuffer.transform(rot);
    }

  }

  private void calculateFillLevel() {
    TileEntity tileEntity = BlockEnder.getAnyTileEntitySafe(world, pos);
    if (!(tileEntity instanceof TileCapBank)) {
      localFillLevel = 0;
      return;
    }
    ICapBankNetwork network = ((TileCapBank) tileEntity).getNetwork();
    if (!(network instanceof CapBankClientNetwork)) {
      localFillLevel = 0;
      return;
    }
    ((CapBankClientNetwork) network).requestPowerUpdate(((TileCapBank) tileEntity), 20);
    double ratio = Math.min(((CapBankClientNetwork) network).getEnergyStoredRatio(), 1);
    localFillLevel = Math.max(0, Math.min(ratio * (height * 16 - 6) - myOffset * 16, 13) + 3);
  }

  private void countNeighbors() {
    height=1; myOffset=0;
    
    BlockPos other = pos;
    while (true) {
      other = other.up();
      IBlockState state = world.getBlockState(other);
      if (!(state.getBlock() instanceof BlockCapBank) || state.getValue(CapBankType.KIND) != bankType) {
        break;
      }      
      IBlockState infrontOfOther = world.getBlockState(other.offset(face));
      boolean isCovered = infrontOfOther.isSideSolid(world, other.offset(face), face.getOpposite());      
      if(isCovered) {
        break;
      }
      TileEntity tileEntity = BlockEnder.getAnyTileEntitySafe(world, other);
      if (!(tileEntity instanceof TileCapBank) || ((TileCapBank)tileEntity).getDisplayType(face) != InfoDisplayType.LEVEL_BAR) {
        break;
      }
      height++;
      connectUp = true;
    }

    other = pos;
    while (true) {
      other = other.down();
      IBlockState state = world.getBlockState(other);
      if (!(state.getBlock() instanceof BlockCapBank) || state.getValue(CapBankType.KIND) != bankType) {
        break;
      }      
      IBlockState infrontOfOther = world.getBlockState(other.offset(face));
      boolean isCovered = infrontOfOther.isSideSolid(world, other.offset(face), face.getOpposite());      
      if(isCovered) {
        break;
      }      
      TileEntity tileEntity = BlockEnder.getAnyTileEntitySafe(world, other);
      if (!(tileEntity instanceof TileCapBank) || ((TileCapBank) tileEntity).getDisplayType(face) != InfoDisplayType.LEVEL_BAR) {
        break;
      }
      height++;
      myOffset++;
      connectDown = true;
    }
  }
  
  public void render() {
    if (canRender()) {
      int i = world.getCombinedLight(pos.offset(face), 0);
      int j = i % 65536;
      int k = i / 65536;
      OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, j / 1.0F, k / 1.0F);

      VertexBuffer tes = Tessellator.getInstance().getBuffer();
      tes.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR_NORMAL);
      buffer.render(tes);
      if (litBuffer != null) {
        litBuffer.render(tes);
      }
      Tessellator.getInstance().draw();
    }
  }

  public boolean canRender() {
    return buffer != null;
  }

  public void bake(List<BakedQuad> quads) {
    buffer.bake(quads);
    if (litBuffer != null) {
      litBuffer.bake(quads);
    }
  }

}
