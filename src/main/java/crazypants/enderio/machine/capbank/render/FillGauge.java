package crazypants.enderio.machine.capbank.render;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourceManagerReloadListener;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import com.enderio.core.client.render.BoundingBox;
import com.enderio.core.client.render.RenderUtil;
import com.enderio.core.common.util.BlockCoord;
import com.enderio.core.common.util.ForgeDirectionOffsets;
import com.enderio.core.common.vecmath.Vector3d;
import com.enderio.core.common.vecmath.Vector4f;
import com.enderio.core.common.vecmath.Vertex;

import crazypants.enderio.EnderIO;
import crazypants.enderio.machine.capbank.CapBankType;
import crazypants.enderio.machine.capbank.InfoDisplayType;
import crazypants.enderio.machine.capbank.TileCapBank;
import crazypants.enderio.machine.capbank.network.CapBankClientNetwork;

public class FillGauge implements IInfoRenderer, IResourceManagerReloadListener {

    private static final double HEIGHT = 0.75;
    private static final double VERT_BORDER = (1 - HEIGHT) / 2;
    private static final double WIDTH = 0.25;

    enum Type {
        SINGLE,
        TOP,
        BOTTOM,
        MIDDLE
    }

    private IIcon barIcon;
    private IIcon gaugeIcon;

    private float barHeightV;

    private Map<GaugeKey, List<Vertex>> gaugeVertexCache;
    private Map<GaugeKey, List<Vertex>> levelVertexCache;
    private float barMinV;

    FillGauge() {
        RenderUtil.registerReloadListener(this);
    }

    @Override
    public void render(TileCapBank cb, ForgeDirection dir, double x, double y, double z, float partialTick) {

        CapBankClientNetwork nw = null;
        if (cb.getNetwork() != null) {
            nw = (CapBankClientNetwork) cb.getNetwork();
            nw.requestPowerUpdate(cb, 20);
        }

        int brightness = cb.getWorldObj().getLightBrightnessForSkyBlocks(
                cb.xCoord + dir.offsetX,
                cb.yCoord + dir.offsetY,
                cb.zCoord + dir.offsetZ,
                0);
        GaugeInfo info = getGaugeInfo(cb, dir);
        GaugeKey key = new GaugeKey(dir, info.type);
        doRender(nw, brightness, info, key);
    }

    public void doRender(CapBankClientNetwork nw, int brightness, GaugeInfo info, GaugeKey key) {
        if (gaugeVertexCache == null) {
            createVertexCache();
        }
        RenderUtil.bindBlockTexture();
        Tessellator tes = Tessellator.instance;
        tes.startDrawingQuads();
        tes.setBrightness(brightness);
        tes.setColorOpaque_F(1, 1, 1);
        List<Vertex> verts = gaugeVertexCache.get(key);
        RenderUtil.addVerticesToTessellator(verts, Tessellator.instance);
        renderFillBar(key, nw, info);
        tes.draw();
    }

    private void renderFillBar(GaugeKey key, CapBankClientNetwork nw, GaugeInfo info) {
        double ratio = 0;
        if (nw != null) {
            ratio = nw.getEnergyStoredRatio();
        }
        if (ratio <= 0) {
            return;
        }
        double maxY = ratio * info.height;
        if (maxY <= info.yPosition) {
            // empty
            return;
        }

        Vector3d offset = ForgeDirectionOffsets.offsetScaled(key.dir, 0.005);
        Tessellator.instance.addTranslation((float) offset.x, (float) offset.y, (float) offset.z);

        List<Vertex> verts = levelVertexCache.get(key);
        if (maxY >= info.yPosition + 1) {
            // full bar
            RenderUtil.addVerticesToTessellator(verts, Tessellator.instance);

        } else {
            // need to render partial bar
            double myMaxY = maxY - info.yPosition;

            if (info.type == Type.BOTTOM || info.type == Type.SINGLE) {
                // If we have some power and we are the bottom bit of the display,
                // always show at least a little bit in the bar
                myMaxY = Math.max(0.2, myMaxY);
            }
            List<Vertex> newVerts = new ArrayList<Vertex>();
            for (Vertex v : verts) {
                v = new Vertex(v);
                newVerts.add(v);
                if (v.y() > myMaxY) {
                    v.setXYZ(v.x(), myMaxY, v.z());
                    v.setUV(v.u(), barMinV + (float) (myMaxY * barHeightV));
                }
            }
            RenderUtil.addVerticesToTessellator(newVerts, Tessellator.instance);
        }
        offset.scale(-1);
        Tessellator.instance.addTranslation((float) offset.x, (float) offset.y, (float) offset.z);
    }

    private GaugeInfo getGaugeInfo(TileCapBank cb, ForgeDirection dir) {

        if (!cb.getType().isMultiblock()) {
            return new GaugeInfo(1, 0);
        }

        int height = 1;
        int yPos = 0;
        BlockCoord loc = cb.getLocation();
        boolean found = true;
        while (found) {
            loc = loc.getLocation(ForgeDirection.UP);
            if (isGaugeType(cb.getWorldObj(), loc, dir, cb.getType())) {
                height++;
            } else {
                found = false;
            }
        }

        loc = cb.getLocation();
        found = true;
        while (found) {
            loc = loc.getLocation(ForgeDirection.DOWN);
            if (isGaugeType(cb.getWorldObj(), loc, dir, cb.getType())) {
                height++;
                yPos++;
            } else {
                found = false;
            }
        }

        return new GaugeInfo(height, yPos);
    }

    private boolean isGaugeType(World worldObj, BlockCoord bc, ForgeDirection face, CapBankType type) {
        TileEntity te = worldObj.getTileEntity(bc.x, bc.y, bc.z);
        if (te instanceof TileCapBank) {
            TileCapBank cb = (TileCapBank) te;
            return type == cb.getType() && cb.getDisplayType(face) == InfoDisplayType.LEVEL_BAR;
        }
        return false;
    }

    @Override
    public void onResourceManagerReload(IResourceManager p_110549_1_) {
        createVertexCache();
    }

    private void createVertexCache() {
        barIcon = EnderIO.blockCapBank.getFillBarIcon();
        barMinV = barIcon.getMinV();
        barHeightV = barIcon.getMaxV() - barIcon.getMinV();
        gaugeIcon = EnderIO.blockCapBank.getGaugeIcon();

        gaugeVertexCache = new HashMap<FillGauge.GaugeKey, List<Vertex>>();
        levelVertexCache = new HashMap<FillGauge.GaugeKey, List<Vertex>>();
        for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
            if (dir.offsetY == 0) {
                for (Type type : Type.values()) {
                    GaugeKey key = new GaugeKey(dir, type);
                    gaugeVertexCache.put(key, createGaugeBoundForFace(key, gaugeIcon));
                    levelVertexCache.put(key, createGaugeBoundForFace(key, barIcon));
                }
            }
        }
    }

    protected List<Vertex> createGaugeBoundForFace(GaugeKey key, IIcon icon) {

        ForgeDirection dir = key.dir;
        Type type = key.type;

        double widthScale = 0.25;
        double heightScale = 0.8;

        double xScale = dir.offsetX == 0 ? widthScale : 1;
        double yScale = 1;
        double zScale = dir.offsetZ == 0 ? widthScale : 1;

        BoundingBox bb = BoundingBox.UNIT_CUBE;
        Vector3d off = ForgeDirectionOffsets.forDirCopy(dir);
        off.scale(-1);
        bb = bb.translate(off);
        bb = bb.scale(xScale, yScale, zScale);
        off.scale(-1);
        bb = bb.translate(off);

        Vector4f uv = getUvForType(key.type, icon);

        List<Vertex> result = bb.getCornersWithUvForFace(dir, uv.x, uv.y, uv.z, uv.w);
        return result;
    }

    private Vector4f getUvForType(Type type, IIcon icon) {
        double uWidth = (icon.getMaxU() - icon.getMinU()) / 4;

        Vector4f res = new Vector4f();
        res.x = (float) (icon.getMinU() + type.ordinal() * uWidth);
        res.y = (float) (res.x + uWidth);

        res.z = icon.getMinV();
        res.w = icon.getMaxV();
        return res;
    }

    static class GaugeInfo {

        int height;
        int yPosition;
        Type type;

        GaugeInfo(int height, int position) {
            this.height = height;
            yPosition = position;
            type = calcType();
        }

        Type calcType() {
            if (height == 1) {
                return Type.SINGLE;
            }
            if (yPosition == 0) {
                return Type.BOTTOM;
            }
            if (yPosition == height - 1) {
                return Type.TOP;
            }
            return Type.MIDDLE;
        }
    }

    static class GaugeKey {

        ForgeDirection dir;
        Type type;

        GaugeKey(ForgeDirection dir, Type type) {
            this.dir = dir;
            this.type = type;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((dir == null) ? 0 : dir.hashCode());
            result = prime * result + ((type == null) ? 0 : type.hashCode());
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            GaugeKey other = (GaugeKey) obj;
            if (dir != other.dir) {
                return false;
            }
            if (type != other.type) {
                return false;
            }
            return true;
        }
    }
}
