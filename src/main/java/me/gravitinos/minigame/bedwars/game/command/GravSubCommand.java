package me.gravitinos.minigame.bedwars.game.command;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;

public abstract class GravSubCommand implements GravCommandPermissionable{
	private ArrayList<GravSubCommand> subCommands = new ArrayList<>();
	public abstract String getPermission();
	public abstract String getDescription();
	public abstract String getAlias();

	private GravCommandPermissionable parent;

	private String cmdPath;

	public GravSubCommand(GravCommandPermissionable parentCommand, String cmdPath){
		this.cmdPath = cmdPath;
		this.parent = parentCommand;
	}

	public GravCommandPermissionable getParentCommand(){
		return this.parent;
	}

	public abstract boolean onCommand(CommandSender sender, Command cmd, String label, String[] args, Object... passedArgs);

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
	public GravSubCommand getSubCommand(String alias) {
		for(GravSubCommand cmds : this.subCommands) {
			if(cmds.getAlias().equalsIgnoreCase(alias)) {
				return cmds;
			}
		}
		return null;
	}

	protected boolean sendErrorMessage(CommandSender sender, String msg){
	    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', msg));
	    return true;
    }

    protected void setCmdPath(String path){
		this.cmdPath = path;
	}

	public String getCmdPath(){
		return this.cmdPath;
	}

	protected String getSubCommandCmdPath(){ //The path to a subcommand of this command
		return this.cmdPath + this.getAlias() + (this.getArgumentString() != null && this.getArgumentString().length() > 0 ? " " + this.getArgumentString() : "") + " ";
	}

	/**
	 * Gets help messages from a format with placeholders <cmd_name> <cmd_description> and <cmd_permission>
	 * @param format
	 * @param page
	 * @return
	 */
	public ArrayList<String> getHelpMessages(String helpFormat, int page){ // Get help messages from immediate sub commands
		ArrayList<String> helpMessages = new ArrayList<>();
		for(GravSubCommand subCommand : this.getSubCommands()){
			helpMessages.add(ChatColor.translateAlternateColorCodes('&', helpFormat.replace("<cmd_name>", subCommand.getCmdPath() + subCommand.getAlias() + " " + subCommand.getArgumentString())
					.replace("<cmd_description>", subCommand.getDescription()).replace("<cmd_permission>", subCommand.getPermission())));
		}
		return helpMessages;
	}

	public ArrayList<String> getEndingHelpMessages(String helpFormat, int page){ // Get help messages from the sub commands at the ends of the command tree (The ones that dont have any more sub commands after them)
		ArrayList<String> helpMessages = new ArrayList<>();
		for(GravSubCommand subCommand : this.getEndingSubCommands()){
			helpMessages.add(ChatColor.translateAlternateColorCodes('&', helpFormat.replace("<cmd_name>", subCommand.getCmdPath() + subCommand.getAlias() + " " + subCommand.getArgumentString())
					.replace("<cmd_description>", subCommand.getDescription()).replace("<cmd_permission>", subCommand.getPermission())));
		}
		return helpMessages;
	}

    public boolean checkPermission(CommandSender sender, String noPermissionMessage){
    	if(this.getPermission() == null) {
    		return true;
		}
		if(!sender.hasPermission(this.getPermission())){
			sender.sendMessage(ChatColor.translateAlternateColorCodes('&', noPermissionMessage));
			return false;
		}
		return true;
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
	public void addSubCommand(GravSubCommand cmd) {
		this.subCommands.add(cmd);
	}
	public ArrayList<GravSubCommand> getSubCommands(){
		return this.subCommands;
	}
}
