package com.blocktyper.yearmarked.days.listeners;

import org.bukkit.World;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import com.blocktyper.yearmarked.days.YearmarkedCalendar;

public final class DayChangeEvent extends Event {
	private static final HandlerList handlers = new HandlerList();
	
	private World world;
	private YearmarkedCalendar day;

	public DayChangeEvent(YearmarkedCalendar day, World world) {
		this.day = day;
		this.world = world;
    }

	public YearmarkedCalendar getDay() {
		return day;
	}

	public World getWorld() {
		return world;
	}

	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}
}
