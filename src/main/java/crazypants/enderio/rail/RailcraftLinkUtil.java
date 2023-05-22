package crazypants.enderio.rail;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import com.enderio.core.common.vecmath.Vector3d;

import mods.railcraft.api.carts.CartTools;
import mods.railcraft.api.carts.ILinkageManager;

public class RailcraftLinkUtil implements ICartLinkUtil {

    private Class<?> directionalClass;

    public RailcraftLinkUtil() throws Exception {
        // Make sure the class we actually want to use exists, just in case the API changes
        Class.forName("mods.railcraft.api.carts.CartTools");

        try {
            directionalClass = Class.forName("mods.railcraft.common.carts.IDirectionalCart");
        } catch (Exception e) {}
    }

    @Override
    public void setCartDirection(EntityMinecart cart, ForgeDirection dir) {

        if (!isDirectional(cart)) {
            CartLinkUtil.defaultInstance.setCartDirection(cart, dir);
            return;
        }

        ForgeDirection oldDir = getCurrentDir(cart);
        CartLinkUtil.defaultInstance.setCartDirection(cart, dir);
        ForgeDirection newDir = getCurrentDir(cart);

        if (oldDir.getOpposite() == newDir) {
            cart.rotationYaw += 180;
        }
    }

    private boolean isDirectional(EntityMinecart cart) {
        if (directionalClass == null) {
            return false;
        }
        return directionalClass.isAssignableFrom(cart.getClass());
    }

    private ForgeDirection getCurrentDir(EntityMinecart cart) {
        boolean isX = true;
        boolean isNeg = cart.motionX < 0;
        if (Math.abs(cart.motionZ) > Math.abs(cart.motionX)) {
            isX = false;
            isNeg = cart.motionZ < 0;
        }
        if (isX) {
            return isNeg ? ForgeDirection.WEST : ForgeDirection.EAST;
        }
        return isNeg ? ForgeDirection.NORTH : ForgeDirection.SOUTH;
    }

    @Override
    public void breakLinks(World world, Entity cart) {
        if (cart instanceof EntityMinecart) {
            breakLinks(world, (EntityMinecart) cart);
        }
    }

    public void breakLinks(World world, EntityMinecart cart) {
        if (world == null || cart == null) {
            return;
        }
        ILinkageManager linkMan = CartTools.getLinkageManager(world);
        if (linkMan == null) {
            return;
        }
        linkMan.breakLinks(cart);
    }

    public void recreateLink(EntityMinecart existingCart, EntityMinecart newCart) {
        if (existingCart == null || newCart == null) {
            return;
        }
        ILinkageManager linkMan = CartTools.getLinkageManager(existingCart.worldObj);
        if (linkMan == null) {
            return;
        }
        UUID linkA = getLinkA(newCart);
        if (linkA != null && linkA.equals(existingCart.getPersistentID())) {
            if (!linkMan.areLinked(existingCart, newCart)) {
                boolean res = linkMan.createLink(existingCart, newCart);
            }
            return;
        }
        UUID linkB = getLinkB(newCart);
        if (linkB != null && linkB.equals(existingCart.getPersistentID())) {
            if (!linkMan.areLinked(existingCart, newCart)) {
                boolean res = linkMan.createLink(existingCart, newCart);
            }
            return;
        }
    }

    @Override
    public void updateCartLinks(World world, EntityMinecart cart) {
        ILinkageManager linkMan = CartTools.getLinkageManager(cart.worldObj);
        if (linkMan == null || linkMan.countCartsInTrain(cart) <= 1) {
            return;
        }
        Iterable<EntityMinecart> allCarts = linkMan.getCartsInTrain(cart);
        for (EntityMinecart aCart : allCarts) {
            if (aCart != null) {
                updateLink("a", aCart, linkMan.getLinkedCartA(aCart));
                updateLink("b", aCart, linkMan.getLinkedCartB(aCart));
            }
        }
    }

    private void updateLink(String prefix, EntityMinecart cart, EntityMinecart linkedCart) {
        NBTTagCompound data = cart.getEntityData();
        long lastUpdateTime = -1;
        String timeKey = prefix + "UpdateTime";
        if (data.hasKey(timeKey)) {
            lastUpdateTime = data.getLong(timeKey);
        }
        long curTime = cart.worldObj.getTotalWorldTime();
        if (lastUpdateTime > 0 && curTime - lastUpdateTime < 100) {
            return;
        }
        data.setLong(timeKey, curTime);
        data.setString(prefix + "Link", linkedCart == null ? "null" : linkedCart.getPersistentID().toString());
    }

    public UUID getLinkA(EntityMinecart cart) {
        return getLink("a", cart);
    }

    public UUID getLinkB(EntityMinecart cart) {
        return getLink("b", cart);
    }

    private UUID getLink(String prefix, EntityMinecart cart) {
        NBTTagCompound data = cart.getEntityData();
        String uuidStr = data.getString(prefix + "Link");
        if (uuidStr == null || uuidStr.trim().isEmpty() || "null".equals(uuidStr)) {
            return null;
        }
        return UUID.fromString(uuidStr);
    }

    @Override
    public int getNumberOfCartsInTrain(EntityMinecart cart) {
        if (cart == null) {
            return 0;
        }
        ILinkageManager linkMan = CartTools.getLinkageManager(cart.worldObj);
        if (linkMan == null) {
            return 1;
        }
        return linkMan.countCartsInTrain(cart);
    }

    @Override
    public List<EntityMinecart> getCartsInTrain(EntityMinecart cart) {
        if (cart == null) {
            Collections.emptyList();
        }
        List<EntityMinecart> result = new ArrayList<EntityMinecart>();
        ILinkageManager linkMan = CartTools.getLinkageManager(cart.worldObj);
        if (linkMan == null) {
            result.add(cart);
            return result;
        }

        Iterable<EntityMinecart> iter = linkMan.getCartsInTrain(cart);
        for (EntityMinecart cartInTrain : iter) {
            if (cartInTrain != null) {
                result.add(cartInTrain);
            }
        }
        Collections.sort(result, new TrainOrderComparator(cart));
        return result;
    }

    @Override
    public void recreateLinks(EntityMinecart cart) {
        ILinkageManager linkMan = CartTools.getLinkageManager(cart.worldObj);
        if (linkMan == null) {
            return;
        }
        recreateLink(cart, linkMan, getLinkA(cart));
        recreateLink(cart, linkMan, getLinkB(cart));
    }

    private void recreateLink(EntityMinecart cart, ILinkageManager linkMan, UUID uuid) {
        if (uuid != null) {
            EntityMinecart linkTo = linkMan.getCartFromUUID(uuid);
            if (!linkMan.areLinked(cart, linkTo)) {
                linkMan.createLink(cart, linkTo);
            }
        }
    }

    private static class TrainOrderComparator implements Comparator<EntityMinecart> {

        private final Vector3d refPoint = new Vector3d();
        private final Vector3d loc1 = new Vector3d();
        private final Vector3d loc2 = new Vector3d();
        private double d1;
        private double d2;

        TrainOrderComparator(EntityMinecart head) {
            set(refPoint, head);
        }

        @Override
        public int compare(EntityMinecart o1, EntityMinecart o2) {
            set(loc1, o1);
            set(loc2, o2);
            d1 = refPoint.distanceSquared(loc1);
            d2 = refPoint.distanceSquared(loc2);
            return Double.compare(d1, d2);
        }

        private void set(Vector3d point, EntityMinecart cart) {
            point.set(cart.posX, cart.posY, cart.posZ);
        }
    }
}
