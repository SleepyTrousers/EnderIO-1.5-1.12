package crazypants.enderio.conduit;

import appeng.api.networking.IGridHost;
import cofh.api.transport.IItemDuct;
import com.enderio.core.common.util.BlockCoord;
import cpw.mods.fml.common.Optional.Interface;
import cpw.mods.fml.common.Optional.InterfaceList;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import crazypants.enderio.conduit.facade.ItemConduitFacade.FacadeType;
import crazypants.enderio.conduit.geom.CollidableComponent;
import crazypants.enderio.conduit.geom.Offset;
import crazypants.enderio.power.IInternalPowerHandler;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import li.cil.oc.api.network.Environment;
import li.cil.oc.api.network.SidedEnvironment;
import mekanism.api.gas.IGasHandler;
import mods.immibis.microblocks.api.IMicroblockSupporterTile;
import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.IFluidHandler;

@InterfaceList({
    @Interface(iface = "appeng.api.networking.IGridHost", modid = "appliedenergistics2"),
    @Interface(iface = "mekanism.api.gas.IGasHandler", modid = "MekanismAPI|gas"),
    @Interface(iface = "mods.immibis.microblocks.api.IMicroblockSupporterTile", modid = "ImmibisMicroblocks"),
    @Interface(iface = "li.cil.oc.api.network.Environment", modid = "OpenComputersAPI|Network"),
    @Interface(iface = "li.cil.oc.api.network.SidedEnvironment", modid = "OpenComputersAPI|Network")
})
public interface IConduitBundle
        extends IInternalPowerHandler,
                IFluidHandler,
                IItemDuct,
                IGasHandler,
                IGridHost,
                IMicroblockSupporterTile,
                Environment,
                SidedEnvironment {

    TileEntity getEntity();

    @Override
    BlockCoord getLocation();

    // conduits

    boolean hasType(Class<? extends IConduit> type);

    <T extends IConduit> T getConduit(Class<T> type);

    void addConduit(IConduit conduit);

    void removeConduit(IConduit conduit);

    Collection<IConduit> getConduits();

    Offset getOffset(Class<? extends IConduit> type, ForgeDirection dir);

    // connections

    Set<ForgeDirection> getConnections(Class<? extends IConduit> type);

    boolean containsConnection(Class<? extends IConduit> type, ForgeDirection west);

    Set<ForgeDirection> getAllConnections();

    boolean containsConnection(ForgeDirection dir);

    // geometry

    List<CollidableComponent> getCollidableComponents();

    List<CollidableComponent> getConnectors();

    // events

    void onNeighborBlockChange(Block blockId);

    void onNeighborChange(IBlockAccess world, int x, int y, int z, int tileX, int tileY, int tileZ);

    void onBlockRemoved();

    void dirty();

    // Facade

    enum FacadeRenderState {
        NONE,
        FULL,
        WIRE_FRAME
    }

    @SideOnly(Side.CLIENT)
    FacadeRenderState getFacadeRenderedAs();

    @SideOnly(Side.CLIENT)
    void setFacadeRenderAs(FacadeRenderState state);

    int getLightOpacity();

    void setLightOpacity(int opacity);

    boolean hasFacade();

    void setFacadeId(Block block);

    void setFacadeId(Block block, boolean triggerUpdate);

    void setFacadeMetadata(int meta);

    void setFacadeType(FacadeType type);

    Block getFacadeId();

    int getFacadeMetadata();

    FacadeType getFacadeType();

    World getWorld();

    void setGridNode(Object node);
}
