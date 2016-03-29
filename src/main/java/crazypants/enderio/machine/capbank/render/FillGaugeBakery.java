package crazypants.enderio.machine.capbank.render;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.IBlockAccess;

import org.lwjgl.opengl.GL11;

import com.enderio.core.client.render.BoundingBox;
import com.enderio.core.client.render.RenderUtil;
import com.enderio.core.client.render.VertexRotationFacing;
import com.enderio.core.common.vecmath.Vector3d;
import com.enderio.core.common.vecmath.Vector4f;

import crazypants.enderio.EnderIO;
import crazypants.enderio.machine.capbank.BlockCapBank;
import crazypants.enderio.machine.capbank.CapBankType;
import crazypants.enderio.machine.capbank.InfoDisplayType;
import crazypants.enderio.machine.capbank.TileCapBank;
import crazypants.enderio.machine.capbank.network.CapBankClientNetwork;
import crazypants.enderio.machine.capbank.network.ICapBankNetwork;
import crazypants.enderio.render.HalfBakedQuad.HalfBakedList;

public class FillGaugeBakery {

  private static final Double px = 1d / 16d;
  private static final Vector3d CENTER = new Vector3d(8 * px, 8 * px, 8 * px);

  private CapBankType bankType;
  private int height, myOffset;
  private double localFillLevel;
  private boolean connectUp, connectDown;
  private IBlockAccess world;
  private  BlockPos pos;
  private  EnumFacing face;
  private HalfBakedList buffer;

  public FillGaugeBakery(IBlockAccess world, BlockPos pos, EnumFacing face) {
    if (world.getBlockState(pos.offset(face)).getBlock().isSideSolid(world, pos.offset(face), face.getOpposite())) {
      return;
    }
    this.world = world; this.pos=pos;this.face=face;
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

    bake();
  }

  private void bake() {

    VertexRotationFacing rot = new VertexRotationFacing(face);
    rot.setCenter(CENTER);
    rot.setRotation(EnumFacing.NORTH);

    TextureAtlasSprite tex1 = EnderIO.blockCapBank.getGaugeIcon();

    buffer = new HalfBakedList();

    final double upperBound = (connectUp ? 16 : 13) * px, lowerBound = (connectDown ? 0 : 3) * px;
    final double full_out = -.5 * px, half_out = full_out / 2, quarter_out = full_out / 4, bit_in = .01 * px;

    BoundingBox border1 = new BoundingBox(6 * px, lowerBound, full_out, 7 * px, upperBound, bit_in);
    buffer.add(border1, EnumFacing.NORTH, 15.01 * px, 15.99 * px, lowerBound, upperBound, tex1, b);
    buffer.add(border1, EnumFacing.EAST, 15.01 * px, 15.49 * px, lowerBound, upperBound, tex1, b);
    buffer.add(border1, EnumFacing.WEST, 15.99 * px, 15.51 * px, lowerBound, upperBound, tex1, b);

    BoundingBox border2 = new BoundingBox(9 * px, lowerBound, full_out, 10 * px, upperBound, bit_in);
    buffer.add(border2, EnumFacing.NORTH, 12.01 * px, 12.99 * px, lowerBound, upperBound, tex1, b);
    buffer.add(border2, EnumFacing.EAST, 12.01 * px, 12.49 * px, lowerBound, upperBound, tex1, b);
    buffer.add(border2, EnumFacing.WEST, 12.99 * px, 12.51 * px, lowerBound, upperBound, tex1, b);

    if (!connectDown) {
      BoundingBox border3 = new BoundingBox(6 * px, lowerBound - 1 * px, full_out, 10 * px, 3 * px, bit_in);
      buffer.add(border3, EnumFacing.NORTH, 0.005 * px, 3.995 * px, 13.01 * px, 13.99 * px, tex1, b);
      buffer.add(border3, EnumFacing.UP, 0.005 * px, 3.995 * px, 13.5 * px, 13.99 * px, tex1, b);
      buffer.add(border3, EnumFacing.DOWN, 0.005 * px, 3.995 * px, 13.5 * px, 13.99 * px, tex1, b);
      buffer.add(border3, EnumFacing.WEST, 3.01 * px, 3.49 * px, 13.01 * px, 13.99 * px, tex1, b);
      buffer.add(border3, EnumFacing.EAST, 0.99 * px, 0.51 * px, 13.01 * px, 13.99 * px, tex1, b);
    }

    if (!connectUp) {
      BoundingBox border4 = new BoundingBox(6 * px, 13 * px, full_out, 10 * px, upperBound + 1 * px, bit_in);
      buffer.add(border4, EnumFacing.NORTH, 0.005 * px, 3.995 * px, 2.01 * px, 2.99 * px, tex1, b);
      buffer.add(border4, EnumFacing.UP, 0.005 * px, 3.995 * px, 2.01 * px, 2.5 * px, tex1, b);
      buffer.add(border4, EnumFacing.DOWN, 0.005 * px, 3.995 * px, 2.01 * px, 2.5 * px, tex1, b);
      buffer.add(border4, EnumFacing.WEST, 3.01 * px, 3.49 * px, 2.01 * px, 2.99 * px, tex1, b);
      buffer.add(border4, EnumFacing.EAST, 0.99 * px, 0.51 * px, 2.01 * px, 2.99 * px, tex1, b);
    }

    BoundingBox bg = new BoundingBox(6.5 * px, (connectDown ? 0 : 2.5) * px, quarter_out, 9.5 * px, (connectUp ? 16 : 13.5) * px, bit_in);
    buffer.add(bg, EnumFacing.NORTH, 12.5 * px, 15.5 * px, (connectDown ? 0 : 2.5) * px, (connectUp ? 16 : 13.5) * px, tex1, null);

    if (localFillLevel > 0) {
      TextureAtlasSprite tex2 = EnderIO.blockCapBank.getFillBarIcon();
      BoundingBox fg = new BoundingBox(6.5 * px, (connectDown ? 0 : 2.99) * px, half_out, 9.5 * px, localFillLevel * px, bit_in);
      buffer.add(fg, EnumFacing.NORTH, 13.01 * px, 14.99 * px, (connectDown ? 0 : 2.99) * px, localFillLevel * px, tex2, null);
    }

    buffer.transform(rot);
  }

  private void calculateFillLevel() {
    TileEntity tileEntity = this.world.getTileEntity(this.pos);
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
    localFillLevel = // Math.min(Math.max(0, ratio * (height * 16 - 6) - myOffset * 16), 13) + Math.signum(myOffset) * 3;
    Math.max(0, Math.min(ratio * (height * 16 - 6) - myOffset * 16, 13) + 3);

    // System.out.println("CapBank at " + pos + ": ratio=" + ratio + " localFillLevel=" + localFillLevel + " height=" + height + " myOffset=" + myOffset +
    // " raw="
    // + (ratio * (height * 16 - 6) - (myOffset * 16 - Math.signum(myOffset) * 3)));
  }

  private void countNeighbors() {
    height=1; myOffset=0;
    
    BlockPos other = pos;
    while (true) {
      other = other.up();
      IBlockState state = world.getBlockState(other);
      if (!(state.getBlock() instanceof BlockCapBank) || state.getValue(CapBankType.KIND) != bankType
          || world.getBlockState(other.offset(face)).getBlock().isSideSolid(world, other.offset(face), face.getOpposite())) {
        // world.getBlockState(other.offset(face)).getBlock().isOpaqueCube()) {
        break;
      }
      TileEntity tileEntity = world.getTileEntity(other);
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
      if (!(state.getBlock() instanceof BlockCapBank) || state.getValue(CapBankType.KIND) != bankType
          || world.getBlockState(other.offset(face)).getBlock().isOpaqueCube()) {
        break;
      }
      TileEntity tileEntity = world.getTileEntity(other);
      if (!(tileEntity instanceof TileCapBank) || ((TileCapBank) tileEntity).getDisplayType(face) != InfoDisplayType.LEVEL_BAR) {
        break;
      }
      height++;
      myOffset++;
      connectDown = true;
    }
  }
  
  Vector4f b;
  public void render() {
    if (buffer != null) {
      int i = world.getCombinedLight(pos.offset(face), 0);
      int j = i % 65536;
      int k = i / 65536;
      OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, j / 1.0F, k / 1.0F);
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

      RenderUtil.bindBlockTexture();
      WorldRenderer tes = Tessellator.getInstance().getWorldRenderer();
      tes.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);
      buffer.render(tes);
      Tessellator.getInstance().draw();
    }
  }

  public boolean canRender() {
    return buffer != null;
  }

}
