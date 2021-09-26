package com.gmail.anthony17j.commandblockpermissions;

import org.bukkit.command.CommandExecutor;
import org.bukkit.plugin.java.JavaPlugin;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;

public class CommandBlockPermissions extends JavaPlugin implements CommandExecutor {
	@Override
	public void onEnable() {
		ProtocolManager pm = ProtocolLibrary.getProtocolManager();
		try {
			pm.addPacketListener(new CommandBlockListener(this));
		} catch (NoSuchFieldException | IllegalAccessException e) {
			e.printStackTrace();
		}
	}
}
