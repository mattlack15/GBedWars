package me.gravitinos.bedwars.gamecore.party.messaging.agents.listeners;

import me.gravitinos.bedwars.gamecore.CoreHandler;
import me.gravitinos.bedwars.gamecore.channel.MessageCallback;
import me.gravitinos.bedwars.gamecore.party.BaseParty;
import me.gravitinos.bedwars.gamecore.party.BungeeParty;
import me.gravitinos.bedwars.gamecore.party.BungeePartyFactory;
import me.gravitinos.bedwars.gamecore.party.messaging.PartyMessageIdentifier;
import me.gravitinos.bedwars.gamecore.util.ComponentUtil;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.UUID;

public class PartyMessageInviteListener implements MessageCallback {

    @Override
    public String getSubChannel() {
        return PartyMessageIdentifier.SUB_INVITE;
    }

    @Override
    public boolean test(Player receiver, DataInputStream input) throws IOException {
        return true;
    }

    @Override
    public boolean accept(Player receiver, DataInputStream input) throws IOException {

        CoreHandler.instance.getAsyncExecutor().execute(() -> {
            try {
                UUID partyId = UUID.fromString(input.readUTF());
                UUID player = UUID.fromString(input.readUTF());
                String inviter = input.readUTF();
                String sourceServer = input.readUTF();

                BungeeParty p = BungeePartyFactory.getRegisteredParty(partyId);

                if (p == null) return;

                p.invitePlayerLocal(player);

                Player invited = Bukkit.getPlayer(player);
                if (invited != null) {
                    TextComponent c = ComponentUtil.toComponent(BungeePartyFactory.PREFIX);
                    c.addExtra(ComponentUtil.getClickHoverComponent("&a&lACCEPT", "&7Click to &aaccept the invitation\\n&cYou will be moved to &e" + sourceServer, ClickEvent.Action.RUN_COMMAND, "/party " + inviter));
                    c.addExtra(ComponentUtil.toComponent("&7 - "));
                    c.addExtra(ComponentUtil.getClickHoverComponent("&c&lDECLINE", "&7Click to &cdecline the invitation\\n&b&lNOTE: &7This doesn't actually do anything", ClickEvent.Action.RUN_COMMAND, ""));
                    c.addExtra(ComponentUtil.toComponent(" &a" + inviter + "&7 has invited you to a party in &e" + sourceServer));
                    invited.spigot().sendMessage(c);
                }
            } catch(Exception e){
                e.printStackTrace();
            }
        });

        return false;
    }
}
