package crazypants.enderio.enderface.te;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.tileentity.TileEntity;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import crazypants.enderio.Log;

public class MeProxy {

  public static final MeProxy instance = new MeProxy();

  private MeProxy() {
  }

  public boolean isMeAccessTerminal(EntityPlayer player, int x, int y, int z) {
    TileEntity te = player.worldObj.getBlockTileEntity(x, y, z);
    if(te != null && (te.getClass().getName().equals("appeng.me.tile.TileTerminal") || te.getClass().getName().equals("appeng.me.tile.TileCraftingTerminal"))) {
      return true;
    }
    return false;
  }

  private static boolean isCraftingTerminal(EntityPlayer player, int x, int y, int z) {
    TileEntity te = player.worldObj.getBlockTileEntity(x, y, z);
    if(te != null && te.getClass().getName().equals("appeng.me.tile.TileCraftingTerminal")) {
      return true;
    }
    return false;
  }

  public Object createTerminalGui(EntityPlayer player, int x, int y, int z) {
    if(isCraftingTerminal(player, x, y, z)) {
      return createCraftingTerminalGui(player, x, y, z);
    }
    try {
      Class<?> cl = Class.forName("appeng.me.gui.GuiTerminal");
      cl.getDeclaredField("initialSearch").set(null, "");
      cl.getDeclaredField("initialScroll").set(null, 0);

      Container c = MeProxy.createMeTerminalContainer(player, x, y, z, true);
      Constructor<?> constr = cl.getDeclaredConstructor(Container.class);
      return constr.newInstance(c);
    } catch (Exception e) {
      Log.warn("BlockEnderIO: Error occured creating the server element for an ME Terminal " + e);

    }
    return null;
  }

  public Object createCraftingTerminalGui(EntityPlayer player, int x, int y, int z) {
    try {

      Class<?> cl = Class.forName("appeng.me.gui.GuiTerminal");
      cl.getDeclaredField("initialSearch").set(null, "");
      cl.getDeclaredField("initialScroll").set(null, 0);

      cl = Class.forName("appeng.me.gui.GuiCraftingTerminal");
      Constructor<?> constr = cl.getDeclaredConstructor(Class.forName("appeng.me.container.ContainerCraftingTerminal"));

      return constr.newInstance(createMeCraftingTerminalContainer(player, x, y, z, true));
    } catch (Exception e) {
      Log.warn("BlockEnderIO: Error occured creating the client gui for an ME Crafting Terminal " + e);
      e.printStackTrace();

    }
    return null;
  }

  public static Container createMeCraftingTerminalContainer(EntityPlayer player, int x, int y, int z, boolean isClient) throws Exception {
    Enhancer e = new Enhancer();
    e.setCallback(new ContainerTerminalProxy());

    Class<?> baseClass = Class.forName("appeng.me.container.ContainerCraftingTerminal");
    e.setSuperclass(baseClass);
    Class<?> gteClass = Class.forName("appeng.api.me.tiles.IGridTileEntity");
    Class<?>[] argTypes = new Class[] { InventoryPlayer.class, gteClass };
    Object[] args = new Object[] { player.inventory, player.worldObj.getBlockTileEntity(x, y, z) };

    e.setInterceptDuringConstruction(false);

    Container proxifiedObj = (Container) e.create(argTypes, args);
    if(!isClient) {
      proxifiedObj.setPlayerIsPresent(player, true);
    }
    return proxifiedObj;
  }

  public static Container createMeTerminalContainer(EntityPlayer player, int x, int y, int z, boolean isClient) throws Exception {

    if(isCraftingTerminal(player, x, y, z)) {
      return createMeCraftingTerminalContainer(player, x, y, z, isClient);
    }

    Enhancer e = new Enhancer();
    e.setCallback(new ContainerTerminalProxy());

    Class<?> baseClass = Class.forName("appeng.me.container.ContainerTerminal");
    e.setSuperclass(baseClass);
    Class<?> gteClass = Class.forName("appeng.api.me.tiles.IGridTileEntity");
    Class<?>[] argTypes = new Class[] { InventoryPlayer.class, gteClass, boolean.class };
    Object[] args = new Object[] { player.inventory, isClient ? null : player.worldObj.getBlockTileEntity(x, y, z), false };

    e.setInterceptDuringConstruction(false);

    Container proxifiedObj = (Container) e.create(argTypes, args);
    if(!isClient) {
      proxifiedObj.setPlayerIsPresent(player, true);
    }
    return proxifiedObj;
  }

  //  private static void callSetPlayerPresent(EntityPlayer player, Container proxifiedObj) throws ClassNotFoundException, NoSuchMethodException,
  //      IllegalAccessException, InvocationTargetException {
  //    Class<?> baseClass = Class.forName("appeng.me.container.ContainerTerminal");
  //    Method m = null;
  //    try {
  //      m = baseClass.getDeclaredMethod("func_75128_a", EntityPlayer.class, boolean.class);
  //    } catch (Exception ex) {
  //      //ignore, probaly de-obf environemnt
  //    }
  //    if(m == null) {
  //      m = baseClass.getDeclaredMethod("setPlayerIsPresent", EntityPlayer.class, boolean.class);
  //    }
  //    m.invoke(proxifiedObj, player, true);
  //  }

  public static class ContainerTerminalProxy implements MethodInterceptor {

    private ContainerTerminalProxy() {
    }

    @Override
    public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
      if("func_75145_c".equals(method.getName()) || "canInteractWith".equals(method.getName())) {
        return true;
      }
      return methodProxy.invokeSuper(o, objects);
    }

  }

}
