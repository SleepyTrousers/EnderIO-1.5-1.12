package crazypants.enderio.machine.gui;

import java.lang.reflect.Constructor;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.tileentity.TileEntity;
import crazypants.enderio.Log;

/**
 * A proxy class to create Container and GUI classes. This allows a superclass
 * to create matching classes for its subclasses, using only the class
 * references.
 *
 */
public class GuiClassMaker {
  private final Class<?> targetClass;
  private final Constructor constructor;
  private final GuiClassMaker.CallType callType;
  private final String caseName;

  /**
   * The different patterns of parameter for the constructor that are supported:
   * <ul>
   * <li>{@link #NONE}: No constructor found (yet).
   * <li>{@link #TE}: A TileEntity of the given type.
   * <li>{@link #INV}: An InventoryPlayer or IInventory.
   * <li>{@link #INV_TE}: An InventoryPlayer and a TileEntity.
   * <li>{@link #PL_INV_TE}: A player, an InventoryPlayer and a TileEntity.
   * <li>{@link #TE_CONT}: A TileEntity and a Container class.
   * <li>{@link #DEFAULT}: The parameterless default constructor.
   * </ul>
   * There are way to many different constructors...
   */
  private static enum CallType {
    NONE, TE, INV, INV_TE, PL_INV_TE, DEFAULT, TE_CONT;
  }

  private GuiClassMaker(Class<?> targetClass, Constructor constructor, GuiClassMaker.CallType callType, String caseName) {
    this.targetClass = targetClass;
    this.constructor = constructor;
    this.callType = callType;
    this.caseName = caseName;
  }

  /**
   * Finds out which constructor of the given targetClass can be used to create
   * it for the given teClass and optionally (for GUI classes) the given
   * additional class (usually a Container class).
   * 
   * @param targetClass
   *          The class that should be created. Can be null.
   * @param teClass
   *          The TileEntity the targetClass relates to.
   * @param scondaryClass
   *          A secondary class that may be an additional parameter of the
   *          constructor.
   * @param caseName
   *          A name to be used in the logfile warning if no matching
   *          constructor is found.
   * @return A GuiClassMaker object that can be used to create an object of
   *         class targetClass or null if either no targetClass was given or no
   *         constructor could be be found.
   */
  @Nullable
  public static GuiClassMaker getClassMaker(@Nullable Class<?> targetClass, Class<?> teClass, @Nullable Class<?> scondaryClass,
      @Nonnull String caseName) {
    Constructor tmpConstructor = null;
    GuiClassMaker.CallType tmpCallType = CallType.NONE;
    if (targetClass != null) {
        try {
        tmpConstructor = targetClass.getDeclaredConstructor(new Class[] { teClass });
        tmpCallType = CallType.TE;
      } catch (Exception e) {
      }
      if (tmpCallType == CallType.NONE) {
        try {
          tmpConstructor = targetClass.getDeclaredConstructor(new Class[] { InventoryPlayer.class });
          tmpCallType = CallType.INV;
        } catch (Exception e) {
        }
      }
      if (tmpCallType == CallType.NONE) {
        try {
          tmpConstructor = targetClass.getDeclaredConstructor(new Class[] { IInventory.class });
          tmpCallType = CallType.INV;
        } catch (Exception e) {
        }
      }
      if (tmpCallType == CallType.NONE) {
        try {
          tmpConstructor = targetClass.getDeclaredConstructor(new Class[] { InventoryPlayer.class, teClass });
          tmpCallType = CallType.INV_TE;
        } catch (Exception e) {
        }
      }
      if (tmpCallType == CallType.NONE) {
        try {
          tmpConstructor = targetClass.getDeclaredConstructor(new Class[] { EntityPlayer.class, InventoryPlayer.class, teClass });
          tmpCallType = CallType.PL_INV_TE;
        } catch (Exception e) {
        }
      }
      if (tmpCallType == CallType.NONE) {
        try {
          tmpConstructor = targetClass.getDeclaredConstructor(new Class[] {});
          tmpCallType = CallType.DEFAULT;
        } catch (Exception e) {
        }
      }
      if (tmpCallType == CallType.NONE) {
        try {
          if (scondaryClass != null) {
            tmpConstructor = targetClass.getDeclaredConstructor(new Class[] { teClass, scondaryClass });
            tmpCallType = CallType.TE_CONT;
          }
        } catch (Exception e) {
          }
        }
      if (tmpCallType == CallType.NONE) {
        Log.warn("Failed to find constructor for " + caseName + " '" + targetClass.getName() + "' of TileEntity '" + teClass.getName() + "'");
        return null;
      } else {
        return new GuiClassMaker(targetClass, tmpConstructor, tmpCallType, caseName);
      }
    }
    return null;
  }

  /**
   * Creates and returns a new object of the targetClass this GuiClassMaker was
   * configured for.
   * 
   * @param player
   *          An EntityPlayer object.
   * @param te
   *          A TileEntity of the same class this GuiClassMaker was configured
   *          for.
   * @param secondary
   *          An optional GuiClassMaker object to provide another parameter
   *          object.
   * @return A new object or null if no new object can be created.
   */
  @Nullable
  public Object makeClass(@Nonnull EntityPlayer player, @Nonnull TileEntity te, @Nullable GuiClassMaker secondary) {
    try {
      switch (callType) {
      case NONE:
        return null;
      case DEFAULT:
        return constructor.newInstance(new Object[] {});
      case TE:
        return constructor.newInstance(new Object[] { te });
      case INV:
        return constructor.newInstance(new Object[] { player.inventory });
      case INV_TE:
        return constructor.newInstance(new Object[] { player.inventory, te });
      case PL_INV_TE:
        return constructor.newInstance(new Object[] { player, player.inventory, te });
      case TE_CONT:
        if(secondary != null) {
          return constructor.newInstance(new Object[] { te, secondary.makeClass(player, te, null) });
        } else {
          throw new Exception("ContainerClass missing but required");
        }
      }
    } catch (Exception e) {
      Log.warn("Failed to call constructor for " + caseName + " '" + targetClass.getName() + "' of TileEntity '" + te.getClass().getName() + "': " + e);
    }
    return null;
  }
  
}