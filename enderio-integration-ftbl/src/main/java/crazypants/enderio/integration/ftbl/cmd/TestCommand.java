package crazypants.enderio.integration.ftbl.cmd;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.feed_the_beast.ftbl.api.IForgePlayer;
import com.feed_the_beast.ftbl.api.IUniverse;
import com.feed_the_beast.ftbl.api_impl.ForgeTeam;
import com.feed_the_beast.ftbl.api_impl.ForgePlayer;
import com.feed_the_beast.ftbl.api_impl.Universe;
import com.feed_the_beast.ftbl.client.teamsgui.MyTeamData;
import com.feed_the_beast.ftbl.client.teamsgui.MyTeamPlayerData;
import com.feed_the_beast.ftbl.lib.internal.FTBLibIntegrationInternal;
import com.google.common.base.Charsets;

import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;

public class TestCommand implements ICommand {
	private final List<String> aliases;

	protected String fullEntityName;
	protected Entity conjuredEntity;

	public TestCommand() {
		aliases = new ArrayList<String>();
		aliases.add("teamtest");
		aliases.add("ttt");
	}

	@Override
	public int compareTo(ICommand arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "testt";
	}

	@Override
	public String getUsage(ICommandSender sender) {
		// TODO Auto-generated method stub
		return "testt";
	}

	@Override
	public List<String> getAliases() {
		// TODO Auto-generated method stub
		return this.aliases;
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
		// TODO Auto-generated method stub
		
		Universe universe = Universe.INSTANCE;
		  ForgePlayer player = universe.getPlayer(sender);
		  ForgeTeam team = (ForgeTeam) player.getTeam();
		  MyTeamData teamData = new MyTeamData(universe,team,player);
		  
          for(MyTeamPlayerData p : teamData.players)
          {
        	  System.out.println("test" + p.playerName + " "+ p.playerId);
        	  
        	  UUID uuid = UUID.nameUUIDFromBytes(("OfflinePlayer:Player29").getBytes(Charsets.UTF_8));
        	         	  
        	  ForgePlayer fp = universe.getPlayer(uuid);
        	  
        	  ForgeTeam ft = fp.getTeam();
        	  
        	  MyTeamData td = new MyTeamData(universe,ft,fp);
        	  
        	  for(MyTeamPlayerData pp : td.players)
              {
        		  System.out.println("test" + pp.playerName + " "+ pp.playerId);
              }
        	  
        	  
        	  System.out.println(uuid);
          }
     		System.out.println("test" + player.getTeam());
     		
     		//ForgeTeam ft = new ForgeTeam("trato");
     		
     		
	}

	@Override
	public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args,
			BlockPos targetPos) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isUsernameIndex(String[] args, int index) {
		// TODO Auto-generated method stub
		return true;
	}

}