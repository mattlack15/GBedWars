package me.gravitinos.minigame.bedwars.game.command;
import me.gravitinos.minigame.gamecore.CoreHandler;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;

public abstract class GravCommand implements CommandExecutor, GravCommandPermissionable {
	private ArrayList<GravSubCommand> subCommands = new ArrayList<GravSubCommand>();
	private String cmdPath = "";

	public static final String DEFAULT_HELP_FORMAT = "&7/&e<cmd_name> &f- &7<cmd_description>";

	public GravCommand() {
		for(String a : this.getAliases()) {
			CoreHandler.main.getCommand(a).setExecutor(this);
		}
	}
	public void addSubCommand(GravSubCommand cmd) {
		this.subCommands.add(cmd);
	}
	public boolean callSubCommand(GravSubCommand cmd, CommandSender sender, Command cmd1, String label, String[] args, Object... passedArgs){
		return this.callSubCommand(cmd, 0, sender, cmd1, label, args, passedArgs);
	}
	public boolean callSubCommand(GravSubCommand cmd, int usedArgs, CommandSender sender, Command cmd1, String label, String[] args, Object... passedArgs) {
		String[] args1 = new String[args.length - usedArgs - 1];
		for(int i = usedArgs+1; i < args.length; i++) {
			args1[i-usedArgs-1] = args[i];
		}
		return cmd.onCommand(sender, cmd1, label, args1, passedArgs);
	}

	protected boolean sendErrorMessage(CommandSender sender, String msg){
		sender.sendMessage(ChatColor.translateAlternateColorCodes('&', msg));
		return true;
	}

	public boolean isAlias(String alias) {
		for(String a : this.getAliases()) {
			if(a.equalsIgnoreCase(alias)) {
				return true;
			}
		}
		return false;
	}
	public boolean checkPermission(CommandSender sender, String noPermissionMessage){
		if(!sender.hasPermission(this.getPermission())){
			sender.sendMessage(ChatColor.translateAlternateColorCodes('&', noPermissionMessage));
			return false;
		}
		return true;
	}
	public GravSubCommand getSubCommand(String alias) {
		for(GravSubCommand cmds : this.subCommands) {
			if(cmds.getAlias().equalsIgnoreCase(alias)) {
				return cmds;
			}
		}
		return null;
	}

	public String getCmdPath(){
		return this.cmdPath;
	}

	protected String getSubCommandCmdPath(){
		return this.cmdPath + (this.getAliases().size() > 0 ? this.getAliases().get(0) : "") + (this.getArgumentString() != null && this.getArgumentString().length() > 0 ? " " + this.getArgumentString() : "") + " ";
	}

	/**
	 * Gets help messages from a format with placeholders <cmd_name> <cmd_description> and <cmd_permission>
	 * @param format
	 * @param page
	 * @return
	 */
	public ArrayList<String> getHelpMessages(String helpFormat, int page){
		ArrayList<String> helpMessages = new ArrayList<>();
		for(GravSubCommand subCommand : this.getSubCommands()){
			helpMessages.add(ChatColor.translateAlternateColorCodes('&', helpFormat.replace("<cmd_name>", subCommand.getCmdPath() + subCommand.getAlias() + " " + subCommand.getArgumentString())
					.replace("<cmd_description>", subCommand.getDescription()).replace("<cmd_permission>", subCommand.getPermission())));
		}
		return helpMessages;
	}

	public ArrayList<String> getEndingHelpMessages(String helpFormat, int page){
		ArrayList<String> helpMessages = new ArrayList<>();
		for(GravSubCommand subCommand : this.getEndingSubCommands()){
			helpMessages.add(ChatColor.translateAlternateColorCodes('&', helpFormat.replace("<cmd_name>", subCommand.getCmdPath() + subCommand.getAlias() + " " + subCommand.getArgumentString())
					.replace("<cmd_description>", subCommand.getDescription()).replace("<cmd_permission>", subCommand.getPermission())));
		}
		return helpMessages;
	}

	public String getArgumentString(){
		return "";
	}

	public ArrayList<GravSubCommand> getEndingSubCommands(){
		ArrayList<GravSubCommand> subCommands = new ArrayList<>();
		for(GravSubCommand cmds : this.getSubCommands()){
			if(cmds.getSubCommands().size() == 0){
				subCommands.add(cmds);
			} else {
				subCommands.addAll(cmds.getEndingSubCommands());
			}
		}
		return subCommands;
	}

	public ArrayList<GravSubCommand> getSubCommands(){
		return this.subCommands;
	}
	public abstract String getDescription();
	public abstract ArrayList<String> getAliases();
	public abstract String getPermission();
}
