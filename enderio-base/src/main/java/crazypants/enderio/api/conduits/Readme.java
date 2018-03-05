package crazypants.enderio.api.conduits;

import crazypants.enderio.base.conduit.IConduit;
import crazypants.enderio.base.conduit.IConduitNetwork;
import crazypants.enderio.base.conduit.IConduitRenderer;
import crazypants.enderio.base.conduit.registry.ConduitBuilder;
import crazypants.enderio.base.conduit.registry.ConduitRegistry;
import net.minecraftforge.fml.common.Optional;

/**
 * Sorry, the conduit API is too involved to be put into the API jar.
 * <p>
 * Ok, it would be possible to do, but it would be much work and addons that implement conduits have no reason not to compile against Ender IO proper, anyway.
 * <p>
 * So, to get started:
 * <ol>
 * <li>Write an interface defining your conduit that extends {@link IConduit}.
 * <li>Write one or more classes implementing that.
 * <li>Add an {@link IConduitNetwork} if your want.
 * <li>Add an {@link IConduitRenderer}.
 * <li>Write your item class.
 * <li>Use the {@link ConduitBuilder} to define your conduit (Note: You can leave out the offsets to get assigned the first free offset).
 * <li>Register the definition with the {@link ConduitRegistry}.
 * <li>Register your renderer with the ConduitBundleRenderManager (that's in the conduits module at the moment...)
 * </ol>
 * Oh, and if your conduit doesn't use Capabilities but needs an interface implemented on the TileEntity: Your problem.
 * <p>
 * Really, I'm tired of having a ConduitBundle TileEntity that has countless {@link Optional}-ed interfaces on it. Just switch over to a Capability-based
 * API---it isn't that hard.
 * <hr>
 * Notes:
 * <p>
 * The Offset system will hopefully go away soon and be replaced with a dynamic conduit positioning. This will be a breaking change for the rendering and
 * collision system, but it will become simpler, not harder. If you use the default renderer (like all built-in conduits but the 2 lower-tier liquid conduits
 * do), you'll probably won't have to do much at all.
 * <p>
 * Please subscribe to the Ender IO github issue tracker and handle tickets about your conduits there. Because if your conduits cause too many tickets there I
 * may be tempted to blacklist them.
 * 
 * @author Henry Loenwind
 *
 */
public class Readme {

}
