package me.gravitinos.minigame.bedwars.game.command.common;

import me.gravitinos.minigame.bedwars.game.command.GravCommand;
import me.gravitinos.minigame.gamecore.CoreHandler;
import me.gravitinos.minigame.gamecore.channel.ProxyComm;
import me.gravitinos.minigame.gamecore.party.BungeeParty;
import me.gravitinos.minigame.gamecore.party.BungeePartyFactory;
import me.gravitinos.minigame.gamecore.party.PartyComm;
import me.gravitinos.minigame.gamecore.util.ComponentUtil;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

public class CommandParty extends GravCommand {
    public static long INVITATION_EXPIRATION_MILLIS = 300000; //This is 5 minutes

    @Override
    public String getDescription() {
        return "Party main command";
    }

    @Override
    public ArrayList<String> getAliases() {
        return new ArrayList<String>() {{
            add("party");
        }};
    }

    @Override
    public String getPermission() {
        return "party.use";
    }

    //Sub Commands Planned:
    //Party <player>
    //Party leave
    //Party join <player>

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        //Purposefully don't check permission, everyone should have this command

        CoreHandler.instance.getAsyncExecutor().execute(() -> {

            //TODO Also Notify proxy when party invite is created
            //TODO Notifying proxy should be in roughly the form of notifier.updateParty(party)

            if (!(sender instanceof Player)) {
                this.sendErrorMessage(sender, BungeePartyFactory.PREFIX + "You must be a player to execute this command!");
                return;
            }

            Player p = (Player) sender;

            if (args.length < 1) {
                BungeeParty party = BungeePartyFactory.getPartyOf(p.getUniqueId());
                if(party == null || party.isOnePersonParty()) {
                    if(party == null) {
                        party = BungeePartyFactory.createNewParty(p.getUniqueId());
                    }
                    this.sendErrorMessage(p, BungeePartyFactory.PREFIX + "You are not currently in a party, use /party <player> to make one");
                    return;
                }
                StringBuilder members = new StringBuilder();
                party.getMembers().forEach(m -> {
                    Player mem = Bukkit.getPlayer(m);
                    if(mem == null) {
                        members.append(m).append(", ");
                    } else {
                        members.append(mem.getName()).append(", ");
                    }
                });
                if(members.length() > 1){
                    members.deleteCharAt(members.length()-1);
                    members.deleteCharAt(members.length()-1);
                }
                this.sendErrorMessage(p, BungeePartyFactory.PREFIX + "You are in a party with " + ChatColor.YELLOW + members.toString());
                return;
            }

            BungeeParty party = BungeePartyFactory.getPartyOf(p.getUniqueId());
            if (party == null) {
                party = BungeePartyFactory.createNewParty(p.getUniqueId());
            }

            if (args[0].equalsIgnoreCase("leave")) {
                if (party.isOnePersonParty()) {
                    this.sendErrorMessage(sender, BungeePartyFactory.PREFIX + "You are not in a party!");
                    return;
                }
                party.removeMember(p.getUniqueId());
                this.sendErrorMessage(p, BungeePartyFactory.PREFIX + "You left the party!");
                return;
            }

            if (!party.getLeader().equals(p.getUniqueId())) {
                this.sendErrorMessage(sender, BungeePartyFactory.PREFIX + "You must be the leader of this party to invite others!");
                return;
            }

            Player invited = Bukkit.getPlayer(args[0]);
            UUID invitedID = null;
            String invitedName = "";
            if (invited != null) {
                invitedID = invited.getUniqueId();
                invitedName = invited.getName();
            } else {
                ArrayList<String> players = new ArrayList<>();
                try {
                    players = ProxyComm.instance.getOnlinePlayers("ALL").get();
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
                if (players.contains(args[0])) {
                    try {
                        invitedID = ProxyComm.instance.getUUIDOther(args[0]).get();
                    } catch (InterruptedException | ExecutionException e) {
                        e.printStackTrace();
                    }
                    invitedName = args[0];
                }
            }
            if (invitedID != null) {
                BungeeParty partyInvited = BungeePartyFactory.getPartyOf(invitedID);
                if (partyInvited != null && partyInvited.isPlayerInvited(p.getUniqueId())) { //If the player is invited to the "invited" person's party, then they are trying to join their party
                    if (System.currentTimeMillis() - partyInvited.getInvitationTime(p.getUniqueId()) < INVITATION_EXPIRATION_MILLIS) {
                        party.removeMember(p.getUniqueId());
                        if (party.getMembers().size() == 0) {
                            BungeePartyFactory.getRegisteredParties().remove(party);
                        }
                        partyInvited.addMember(p.getUniqueId());
                        partyInvited.removeInvite(p.getUniqueId());
                        this.sendErrorMessage(sender, BungeePartyFactory.PREFIX + "You joined the party!");
                        partyInvited.getMembers().forEach(m -> {
                            Player p1 = Bukkit.getPlayer(m);
                            if (p1 != null)
                                p1.sendMessage(BungeePartyFactory.PREFIX + ChatColor.GREEN + p.getName() + ChatColor.GRAY + " joined the party!");
                        });
                        return;
                    } else {
                        this.sendErrorMessage(sender, BungeePartyFactory.PREFIX + "Your invite has expired!");
                        return;
                    }
                }

                if (party.getMembers().contains(invitedID)) {
                    this.sendErrorMessage(sender, BungeePartyFactory.PREFIX + "Player is already in your party!");
                    return;
                }
                if (party.isPlayerInvited(invitedID) && System.currentTimeMillis() - party.getInvitationTime(invitedID) < INVITATION_EXPIRATION_MILLIS) {
                    this.sendErrorMessage(sender, BungeePartyFactory.PREFIX + "Player has already been invited!");
                    return;
                }
                party.invitePlayer(invitedID, p.getName());
                this.sendErrorMessage(sender, ChatColor.GOLD + "Party > " + ChatColor.GRAY + "You invited " + ChatColor.GOLD + invitedName + ChatColor.GRAY + " to the party!");
                if (invited != null) {
                    this.sendErrorMessage(invited, ChatColor.GOLD + "Party > " + ChatColor.GRAY + "You have been invited to a party by " + ChatColor.GREEN + p.getName());

                    TextComponent c = ComponentUtil.toComponent(BungeePartyFactory.PREFIX);
                    c.addExtra(ComponentUtil.getClickHoverComponent("&a&lACCEPT", "&7Click to &aaccept the invitation", ClickEvent.Action.RUN_COMMAND, "/party " + p.getName()));
                    c.addExtra(ComponentUtil.toComponent("&7 - "));
                    c.addExtra(ComponentUtil.getClickHoverComponent("&c&lDECLINE", "&7Click to &cdecline the invitation\\n&b&lNOTE: &7This doesn't actually do anything", ClickEvent.Action.RUN_COMMAND, ""));
                    c.addExtra(ComponentUtil.toComponent(" &a" + p.getName() + "&7 has invited you to a party"));
                    invited.spigot().sendMessage(c);
                }
            } else {
                this.sendErrorMessage(sender, ChatColor.GOLD + "Party > " + ChatColor.RED + args[0] + ChatColor.GRAY + " is not online!");
            }
        });
        return true;
    }
}
