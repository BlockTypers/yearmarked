package com.blocktyper.yearmarked.monitors;




import org.bukkit.scheduler.BukkitRunnable;

import com.blocktyper.yearmarked.YearmarkedPlugin;
import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;


public class HolographicDisplayMonitor extends BukkitRunnable {

	private YearmarkedPlugin plugin = null;
	

	public HolographicDisplayMonitor(YearmarkedPlugin plugin) {
		super();
		this.plugin = plugin;
	}

	// BEGIN BukkitRunnable
	public void run() {
        // Find the holograms created by your plugin
        for (Hologram hologram : HologramsAPI.getHolograms(plugin)) {
            deleteIfOld(hologram);
        }
    }
	
	private void deleteIfOld(Hologram hologram) {

	    long tenMinutesMillis = 30 * 1000; // 30 sec minute in milliseconds
	    long elapsedMillis = System.currentTimeMillis() - hologram.getCreationTimestamp(); // Milliseconds elapsed from the creation of the hologram

	    if (elapsedMillis > tenMinutesMillis) {
	        hologram.delete();
	    }
	}


}
