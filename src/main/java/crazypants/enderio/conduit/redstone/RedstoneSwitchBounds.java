package crazypants.enderio.conduit.redstone;

import com.enderio.core.api.client.render.VertexTransform;
import com.enderio.core.client.render.BoundingBox;
import com.enderio.core.client.render.VertexRotation;
import com.enderio.core.common.vecmath.Vector3d;
import crazypants.enderio.config.Config;

public class RedstoneSwitchBounds {

    private static RedstoneSwitchBounds instance;

    public static final RedstoneSwitchBounds getInstance() {
        if (instance == null) {
            instance = new RedstoneSwitchBounds();
        }
        return instance;
    }

    final VertexTransform[] xForms;
    final BoundingBox switchBounds;
    final BoundingBox connectorBounds;

    private final BoundingBox[] aabb;

    RedstoneSwitchBounds() {

        float conduitScale = (float) Config.conduitScale;

        float size = Math.max(0.2f, conduitScale * 0.5f);
        float halfWidth = size / 3;
        float halfHeight = size / 2;

        // float DEPTH = 0.05f;
        float halfDepth = 0.025f;
        float distance = Math.max(0.25f, conduitScale * 0.3f);

        BoundingBox bb = new BoundingBox(
                0.5 - halfWidth, 0.5 - halfHeight, 0.5 - halfDepth, 0.5 + halfWidth, 0.5 + halfHeight, 0.5 + halfDepth);
        switchBounds = bb.translate(0, 0, distance);

        float connectorHalfWidth = (float) Math.max(0.015, conduitScale * 0.05);
        connectorBounds = new BoundingBox(
                0.5 - connectorHalfWidth,
                0.5 - connectorHalfWidth,
                0.5 - connectorHalfWidth,
                0.5 + connectorHalfWidth,
                0.5 + connectorHalfWidth,
                0.5 + distance);

        Vector3d axis = new Vector3d(0, 1, 0);
        Vector3d p = new Vector3d(0.5, 0.5, 0.5);
        xForms = new VertexTransform[4];
        double angle = Math.toRadians(45);
        for (int i = 0; i < xForms.length; i++) {
            xForms[i] = new VertexRotation(angle, axis, p);
            angle += Math.toRadians(90);
        }

        aabb = new BoundingBox[xForms.length];
        for (int i = 0; i < xForms.length; i++) {
            aabb[i] = switchBounds.transform(xForms[i]);
        }
    }

    public BoundingBox[] getAABB() {
        return aabb;
    }
}
