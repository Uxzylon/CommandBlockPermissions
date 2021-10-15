package com.gmail.anthony17j.commandblockpermissions;

import org.bukkit.ChatColor;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;

import java.lang.reflect.Field;
import java.util.logging.Level;

public class CommandBlockListener extends PacketAdapter {
	private final CommandBlockPermissions cbp;
	private final CommandMap bukkitCommandMap;
	private static final PacketType[] LISTENING_PACKETS = new PacketType[] {PacketType.Play.Client.SET_COMMAND_BLOCK, PacketType.Play.Client.SET_COMMAND_MINECART};

	public CommandBlockListener(CommandBlockPermissions cbp) throws NoSuchFieldException, IllegalAccessException {
		super(cbp, LISTENING_PACKETS);
		this.cbp = cbp;

		Field commandMapField = cbp.getServer().getClass().getDeclaredField("commandMap");
		commandMapField.setAccessible(true);
		bukkitCommandMap = (CommandMap) commandMapField.get(cbp.getServer());
	}

	@Override
	public void onPacketReceiving(PacketEvent e) {
		if (!isCorrectPacketType(e.getPacketType())) { // just in case
			cbp.getLogger().warning("Received a wrong packet type: " + e.getPacketType());
			return;
		}
		String fullCommand = e.getPacket().getStrings().read(0);
		if (fullCommand.trim().equals("")) {
			return; // I don't see any reason why blanking a command block shouldn't be allowed
		}
		String commandName = fullCommand.split(" ")[0];
		if (commandName.startsWith("/")) {
			commandName = commandName.substring(1); // remove slash if there is one
		}

		Command command = bukkitCommandMap.getCommand(commandName);
		if (command != null) {
			if (command.getPermission() != null) {
				if (e.getPlayer().hasPermission(command.getPermission())) {
					return;
				}
			} else {
				//plugin.getLogger().warning("Failed to check permissions for command '" + commandName + "'!");
				if (e.getPlayer().hasPermission("cbPerm." + commandName)) {
					return;
				}
			}
		} /*else {
			plugin.getLogger().warning("Failed to check permissions for command '" + commandName + "'!");
		}*/



		e.setCancelled(true);
		e.getPlayer().sendMessage(ChatColor.RED + "Vous ne pouvez pas utiliser des commandes dont vous n'avez pas la permission dans un command block.");
	}
	private boolean isCorrectPacketType(PacketType type) {
		for (PacketType testType : LISTENING_PACKETS) {
			if (type == testType) return true;
		}
		return false;
	}
}
