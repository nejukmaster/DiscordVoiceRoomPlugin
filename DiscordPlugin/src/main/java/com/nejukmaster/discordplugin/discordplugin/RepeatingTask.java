package com.nejukmaster.discordplugin.discordplugin;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class RepeatingTask implements Runnable {

        private int taskId;

        public RepeatingTask(JavaPlugin plugin, int arg1, int arg2) {
            taskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, this, arg1, arg2);
        }

        public void canncel() {
            Bukkit.getScheduler().cancelTask(taskId);
        }

		@Override
		public void run() {
			// TODO Auto-generated method stub
			
		}

}
