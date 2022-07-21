package crazypants.enderio.gui;

import com.enderio.core.client.render.BoundingBox;
import com.enderio.core.client.render.ColorUtil;
import com.enderio.core.client.render.RenderUtil;
import com.enderio.core.common.util.BlockCoord;
import com.enderio.core.common.util.IBlockAccessWrapper;
import com.enderio.core.common.vecmath.Camera;
import com.enderio.core.common.vecmath.Matrix4d;
import com.enderio.core.common.vecmath.VecmathUtil;
import com.enderio.core.common.vecmath.Vector3d;
import com.enderio.core.common.vecmath.Vector4f;
import com.enderio.core.common.vecmath.Vertex;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import crazypants.enderio.EnderIO;
import crazypants.enderio.machine.IIoConfigurable;
import crazypants.enderio.machine.IoMode;
import crazypants.enderio.machine.PacketIoMode;
import crazypants.enderio.network.PacketHandler;
import crazypants.enderio.teleport.TravelController;
import crazypants.util.RenderPassHelper;
import java.awt.Color;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GL14;

public class IoConfigRenderer {

    protected static final RenderBlocks RB = new RenderBlocks();

    private boolean dragging = false;
    private float pitch = 0;
    private float yaw = 0;
    private double distance;
    private long initTime;

    private Minecraft mc = Minecraft.getMinecraft();
    private World world = mc.thePlayer.worldObj;

    private final Vector3d origin = new Vector3d();
    private final Vector3d eye = new Vector3d();
    private final Camera camera = new Camera();
    private final Matrix4d pitchRot = new Matrix4d();
    private final Matrix4d yawRot = new Matrix4d();

    public BlockCoord originBC;

    private List<BlockCoord> configurables = new ArrayList<BlockCoord>();
    private List<BlockCoord> neighbours = new ArrayList<BlockCoord>();

    private SelectedFace selection;

    private boolean renderNeighbours = true;
    private boolean inNeigButBounds = false;

    public IoConfigRenderer(IIoConfigurable configuarble) {
        this(Collections.singletonList(configuarble.getLocation()));
    }

    public IoConfigRenderer(List<BlockCoord> configurables) {
        this.configurables.addAll(configurables);

        Vector3d c;
        Vector3d size;
        if (configurables.size() == 1) {
            BlockCoord bc = configurables.get(0);
            c = new Vector3d(bc.x + 0.5, bc.y + 0.5, bc.z + 0.5);
            size = new Vector3d(1, 1, 1);
        } else {
            Vector3d min = new Vector3d(Double.MAX_VALUE, Double.MAX_VALUE, Double.MAX_VALUE);
            Vector3d max = new Vector3d(-Double.MAX_VALUE, -Double.MAX_VALUE, -Double.MAX_VALUE);
            for (BlockCoord bc : configurables) {
                min.set(Math.min(bc.x, min.x), Math.min(bc.y, min.y), Math.min(bc.z, min.z));
                max.set(Math.max(bc.x, max.x), Math.max(bc.y, max.y), Math.max(bc.z, max.z));
            }
            size = new Vector3d(max);
            size.sub(min);
            size.scale(0.5);
            c = new Vector3d(min.x + size.x, min.y + size.y, min.z + size.z);
            size.scale(2);
        }

        originBC = new BlockCoord((int) c.x, (int) c.y, (int) c.z);
        origin.set(c);
        pitchRot.setIdentity();
        yawRot.setIdentity();

        pitch = -mc.thePlayer.rotationPitch;
        yaw = 180 - mc.thePlayer.rotationYaw;

        distance = Math.max(Math.max(size.x, size.y), size.z) + 4;

        for (BlockCoord bc : configurables) {
            for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
                BlockCoord loc = bc.getLocation(dir);
                if (!configurables.contains(loc)) {
                    neighbours.add(loc);
                }
            }
        }

        world = mc.thePlayer.worldObj;
        RB.blockAccess = new InnerBA();
    }

    public void init() {
        initTime = System.currentTimeMillis();
    }

    public SelectedFace getSelection() {
        return selection;
    }

    public void handleMouseInput() {

        if (Mouse.getEventButton() == 0) {
            dragging = Mouse.getEventButtonState();
        }

        if (dragging) {

            double dx = (Mouse.getEventDX() / (double) mc.displayWidth);
            double dy = (Mouse.getEventDY() / (double) mc.displayHeight);
            if (Keyboard.isKeyDown(Keyboard.KEY_LCONTROL) || Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) {
                distance -= dy * 15;
            } else {
                yaw -= 4 * dx * 180;
                pitch += 2 * dy * 180;
                pitch = (float) VecmathUtil.clamp(pitch, -80, 80);
            }
        }

        distance -= Mouse.getEventDWheel() * 0.01;
        distance = VecmathUtil.clamp(distance, 0.01, 200);

        long elapsed = System.currentTimeMillis() - initTime;

        int x = Mouse.getEventX();
        int y = Mouse.getEventY();
        Vector3d start = new Vector3d();
        Vector3d end = new Vector3d();
        if (camera.getRayForPixel(x, y, start, end)) {
            end.scale(distance * 2);
            end.add(start);
            updateSelection(start, end);
        }

        if (!Mouse.getEventButtonState() && camera.isValid() && elapsed > 500) {
            if (Mouse.getEventButton() == 1) {
                if (selection != null) {
                    selection.config.toggleIoModeForFace(selection.face);
                    PacketHandler.INSTANCE.sendToServer(new PacketIoMode(selection.config, selection.face));
                }
            } else if (Mouse.getEventButton() == 0 && inNeigButBounds) {
                renderNeighbours = !renderNeighbours;
            }
        }
    }

    private void updateSelection(Vector3d start, Vector3d end) {
        start.add(origin);
        end.add(origin);
        List<MovingObjectPosition> hits = new ArrayList<MovingObjectPosition>();

        for (BlockCoord bc : configurables) {
            Block block = world.getBlock(bc.x, bc.y, bc.z);
            if (block != null) {
                MovingObjectPosition hit = block.collisionRayTrace(
                        world,
                        bc.x,
                        bc.y,
                        bc.z,
                        Vec3.createVectorHelper(start.x, start.y, start.z),
                        Vec3.createVectorHelper(end.x, end.y, end.z));
                if (hit != null) {
                    hits.add(hit);
                }
            }
        }
        selection = null;
        MovingObjectPosition hit = getClosestHit(Vec3.createVectorHelper(start.x, start.y, start.z), hits);
        if (hit != null) {
            TileEntity te = world.getTileEntity(hit.blockX, hit.blockY, hit.blockZ);
            if (te instanceof IIoConfigurable) {
                IIoConfigurable configuarble = (IIoConfigurable) te;
                ForgeDirection face = ForgeDirection.getOrientation(hit.sideHit);
                selection = new SelectedFace(configuarble, face);
            }
        }
    }

    public static MovingObjectPosition getClosestHit(Vec3 origin, Collection<MovingObjectPosition> candidates) {
        double minLengthSquared = Double.POSITIVE_INFINITY;
        MovingObjectPosition closest = null;

        for (MovingObjectPosition hit : candidates) {
            if (hit != null) {
                double lengthSquared = hit.hitVec.squareDistanceTo(origin);
                if (lengthSquared < minLengthSquared) {
                    minLengthSquared = lengthSquared;
                    closest = hit;
                }
            }
        }
        return closest;
    }

    public void drawScreen(int par1, int par2, float partialTick, Rectangle vp, Rectangle parentBounds) {

        if (!updateCamera(partialTick, vp.x, vp.y, vp.width, vp.height)) {
            return;
        }
        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
        applyCamera(partialTick);
        TravelController.instance.setSelectionEnabled(false);
        renderScene();
        TravelController.instance.setSelectionEnabled(true);
        renderSelection();

        renderOverlay(par1, par2);
        GL11.glPopAttrib();
    }

    private void renderSelection() {
        if (selection == null) {
            return;
        }

        BoundingBox bb = new BoundingBox(selection.config.getLocation());

        IIcon icon = EnderIO.blockAlloySmelter.selectedFaceIcon;
        List<Vertex> corners = bb.getCornersWithUvForFace(
                selection.face, icon.getMinU(), icon.getMaxU(), icon.getMinV(), icon.getMaxV());

        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glDisable(GL11.GL_LIGHTING);
        RenderUtil.bindBlockTexture();
        GL11.glColor3f(1, 1, 1);
        Tessellator.instance.startDrawingQuads();
        Tessellator.instance.setColorOpaque_F(1, 1, 1);
        Vector3d trans = new Vector3d((-origin.x) + eye.x, (-origin.y) + eye.y, (-origin.z) + eye.z);
        Tessellator.instance.setTranslation(trans.x, trans.y, trans.z);
        RenderUtil.addVerticesToTesselator(corners);
        Tessellator.instance.draw();
        Tessellator.instance.setTranslation(0, 0, 0);
    }

    private void renderOverlay(int mx, int my) {
        Rectangle vp = camera.getViewport();
        ScaledResolution scaledresolution = new ScaledResolution(mc, mc.displayWidth, mc.displayHeight);

        int vpx = vp.x / scaledresolution.getScaleFactor();
        int vph = vp.height / scaledresolution.getScaleFactor();
        int vpw = vp.width / scaledresolution.getScaleFactor();
        int vpy = (int) ((float) (vp.y + vp.height - 4) / (float) scaledresolution.getScaleFactor());

        GL11.glViewport(0, 0, mc.displayWidth, mc.displayHeight);
        GL11.glMatrixMode(GL11.GL_PROJECTION);
        GL11.glLoadIdentity();
        GL11.glOrtho(
                0.0D,
                scaledresolution.getScaledWidth_double(),
                scaledresolution.getScaledHeight_double(),
                0.0D,
                1000.0D,
                3000.0D);
        GL11.glMatrixMode(GL11.GL_MODELVIEW);
        GL11.glLoadIdentity();
        GL11.glTranslatef(vpx, vpy, -2000.0F);

        GL11.glDisable(GL11.GL_LIGHTING);

        int x = vpw - 16;
        int y = vph - 16;

        mx -= vpx;
        my -= vpy;

        if (mx >= x && mx <= x + IconEIO.IO_WHATSIT.width && my >= y && my <= y + IconEIO.IO_WHATSIT.height) {
            RenderUtil.renderQuad2D(
                    x, y, 0, IconEIO.IO_WHATSIT.width, IconEIO.IO_WHATSIT.height, new Vector4f(0.4f, 0.4f, 0.4f, 0.6f));
            inNeigButBounds = true;
        } else {
            inNeigButBounds = false;
        }

        GL11.glColor3f(1, 1, 1);
        IconEIO.map.render(IconEIO.IO_WHATSIT, x, y, true);

        if (selection != null) {
            IconEIO ioIcon = null;
            //    INPUT
            IoMode mode = selection.config.getIoMode(selection.face);
            if (mode == IoMode.PULL) {
                ioIcon = IconEIO.INPUT;
            } else if (mode == IoMode.PUSH) {
                ioIcon = IconEIO.OUTPUT;
            } else if (mode == IoMode.PUSH_PULL) {
                ioIcon = IconEIO.INPUT_OUTPUT;
            } else if (mode == IoMode.DISABLED) {
                ioIcon = IconEIO.DISABLED;
            }

            y = vph - mc.fontRenderer.FONT_HEIGHT - 2;
            mc.fontRenderer.drawString(getLabelForMode(mode), 4, y, ColorUtil.getRGB(Color.white));
            if (ioIcon != null) {
                int w = mc.fontRenderer.getStringWidth(mode.getLocalisedName());
                double xd = (w - ioIcon.width) / 2;
                xd = Math.max(0, w);
                xd /= 2;
                xd += 4;
                xd /= scaledresolution.getScaleFactor();
                ioIcon.getMap().render(ioIcon, xd, y - mc.fontRenderer.FONT_HEIGHT - 2, true);
            }
        }
    }

    protected String getLabelForMode(IoMode mode) {
        return mode.getLocalisedName();
    }

    private void renderScene() {
        GL11.glEnable(GL11.GL_CULL_FACE);
        GL11.glEnable(GL12.GL_RESCALE_NORMAL);

        RenderHelper.disableStandardItemLighting();
        mc.entityRenderer.disableLightmap(0);
        RenderUtil.bindBlockTexture();
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_ALPHA_TEST);

        Vector3d trans = new Vector3d((-origin.x) + eye.x, (-origin.y) + eye.y, (-origin.z) + eye.z);
        for (int pass = 0; pass < 1; pass++) {
            setGlStateForPass(pass, false);
            doWorldRenderPass(trans, configurables, pass);
            if (renderNeighbours) {
                setGlStateForPass(pass, true);
                doWorldRenderPass(trans, neighbours, pass);
            }
        }

        RenderHelper.enableStandardItemLighting();
        GL11.glEnable(GL11.GL_LIGHTING);
        TileEntityRendererDispatcher.instance.field_147558_l = origin.x - eye.x;
        TileEntityRendererDispatcher.instance.field_147560_j = origin.y - eye.y;
        TileEntityRendererDispatcher.instance.field_147561_k = origin.z - eye.z;
        TileEntityRendererDispatcher.staticPlayerX = origin.x - eye.x;
        TileEntityRendererDispatcher.staticPlayerY = origin.y - eye.y;
        TileEntityRendererDispatcher.staticPlayerZ = origin.z - eye.z;

        for (int pass = 0; pass < 2; pass++) {
            setGlStateForPass(pass, false);
            doTileEntityRenderPass(configurables, pass);
            if (renderNeighbours) {
                setGlStateForPass(pass, true);
                doTileEntityRenderPass(neighbours, pass);
            }
        }
        setGlStateForPass(0, false);
    }

    private void doTileEntityRenderPass(List<BlockCoord> blocks, int pass) {
        RenderPassHelper.setEntityRenderPass(pass);
        for (BlockCoord bc : blocks) {
            TileEntity tile = world.getTileEntity(bc.x, bc.y, bc.z);
            if (tile != null) {
                Vector3d at = new Vector3d(eye.x, eye.y, eye.z);
                at.x += bc.x - origin.x;
                at.y += bc.y - origin.y;
                at.z += bc.z - origin.z;
                GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
                TileEntityRendererDispatcher.instance.renderTileEntityAt(tile, at.x, at.y, at.z, 0);
                GL11.glPopAttrib();
            }
        }
        RenderPassHelper.clearEntityRenderPass();
    }

    private void doWorldRenderPass(Vector3d trans, List<BlockCoord> blocks, int pass) {
        RenderPassHelper.setBlockRenderPass(pass);

        Tessellator.instance.startDrawingQuads();
        Tessellator.instance.setTranslation(trans.x, trans.y, trans.z);
        Tessellator.instance.setBrightness(15 << 20 | 15 << 4);

        for (BlockCoord bc : blocks) {
            Block block = world.getBlock(bc.x, bc.y, bc.z);
            if (block != null) {
                if (block.canRenderInPass(pass)) {
                    RB.renderAllFaces = true;
                    RB.setRenderAllFaces(true);
                    RB.setRenderBounds(0, 0, 0, 1, 1, 1);
                    try {
                        RB.renderBlockByRenderType(block, bc.x, bc.y, bc.z);
                    } catch (Exception e) {
                        // Ignore, things might blow up in rendering due to the modified block access
                        // but this is about as good as we can do
                    }
                }
            }
        }

        Tessellator.instance.draw();
        Tessellator.instance.setTranslation(0, 0, 0);
        RenderPassHelper.clearBlockRenderPass();
    }

    private void setGlStateForPass(int pass, boolean isNeighbour) {

        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        if (isNeighbour) {

            float alpha = 0.8f;
            if (pass == 0) {
                GL11.glEnable(GL11.GL_DEPTH_TEST);
                GL11.glEnable(GL11.GL_BLEND);
                GL11.glEnable(GL11.GL_CULL_FACE);
                GL11.glBlendFunc(GL11.GL_CONSTANT_ALPHA, GL11.GL_CONSTANT_COLOR);
                GL14.glBlendColor(1.0f, 1.0f, 1.0f, alpha);
                GL11.glDepthMask(true);
            } else {
                GL11.glEnable(GL11.GL_BLEND);
                GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_CONSTANT_COLOR);
                GL14.glBlendColor(1.0f, 1.0f, 1.0f, 0.8f);
                GL14.glBlendColor(1.0f, 1.0f, 1.0f, alpha);
                GL11.glDepthMask(false);
            }
            return;
        }

        if (pass == 0) {
            GL11.glEnable(GL11.GL_DEPTH_TEST);
            GL11.glDisable(GL11.GL_BLEND);
            GL11.glDepthMask(true);
        } else {
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            GL11.glDepthMask(false);
        }
    }

    private boolean updateCamera(float partialTick, int vpx, int vpy, int vpw, int vph) {
        if (vpw <= 0 || vph <= 0) {
            return false;
        }
        camera.setViewport(vpx, vpy, vpw, vph);
        camera.setProjectionMatrixAsPerspective(30, 0.05, 50, vpw, vph);
        eye.set(0, 0, distance);
        pitchRot.makeRotationX(Math.toRadians(pitch));
        yawRot.makeRotationY(Math.toRadians(yaw));
        pitchRot.transform(eye);
        yawRot.transform(eye);
        camera.setViewMatrixAsLookAt(eye, RenderUtil.ZERO_V, RenderUtil.UP_V);
        return camera.isValid();
    }

    private void applyCamera(float partialTick) {
        Rectangle vp = camera.getViewport();
        GL11.glViewport(vp.x, vp.y, vp.width, vp.height);
        GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT);
        GL11.glMatrixMode(GL11.GL_PROJECTION);
        RenderUtil.loadMatrix(camera.getTransposeProjectionMatrix());
        GL11.glMatrixMode(GL11.GL_MODELVIEW);
        RenderUtil.loadMatrix(camera.getTransposeViewMatrix());
        GL11.glTranslatef(-(float) eye.x, -(float) eye.y, -(float) eye.z);
    }

    public static class SelectedFace {

        public IIoConfigurable config;
        public ForgeDirection face;

        public SelectedFace(IIoConfigurable config, ForgeDirection face) {
            super();
            this.config = config;
            this.face = face;
        }

        @Override
        public String toString() {
            return "SelectedFace [config=" + config + ", face=" + face + "]";
        }
    }

    private class InnerBA extends IBlockAccessWrapper {

        InnerBA() {
            super(world);
        }

        @Override
        public boolean isSideSolid(int x, int y, int z, ForgeDirection side, boolean _default) {
            return false;
        }

        @Override
        public boolean isAirBlock(int var1, int var2, int var3) {
            if (!configurables.contains(new BlockCoord(var1, var2, var3))) {
                return false;
            }
            return super.isAirBlock(var1, var2, var3);
        }

        @Override
        @SideOnly(Side.CLIENT)
        public int getLightBrightnessForSkyBlocks(int var1, int var2, int var3, int var4) {
            return 15 << 20 | 15 << 4;
        }

        @Override
        public Block getBlock(int var1, int var2, int var3) {
            if (!configurables.contains(new BlockCoord(var1, var2, var3))) {
                return Blocks.air;
            }
            return super.getBlock(var1, var2, var3);
        }
    }
}
