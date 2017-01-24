package com.blocktyper.yearmarked.commands;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.blocktyper.yearmarked.LocalizedMessage;
import com.blocktyper.yearmarked.YearmarkedPlugin;
import com.blocktyper.yearmarked.days.YearmarkedCalendar;
import com.blocktyper.yearmarked.days.listeners.SendDayInfoListener;

public class YmCommand implements CommandExecutor {

	private YearmarkedPlugin plugin;

	private static final List<String> TIMELORD_PERMISSIONS = Arrays.asList(Permission.TIMELORD.getName());

	Map<String, Map<String, Long>> playerWorldReturnMap;

	public YmCommand(YearmarkedPlugin plugin) {
		this.plugin = plugin;
		playerWorldReturnMap = new HashMap<String, Map<String, Long>>();
	}

	private boolean playerCanDoAction(Player player, boolean sendMessage, List<String> permissions) {
		if (player.isOp() || permissions == null || permissions.isEmpty()) {
			return true;
		}

		for (String permission : permissions) {
			if (player.hasPermission(permission)) {
				return true;
			}
		}

		if (sendMessage) {
			String message = plugin.getLocalizedMessage(LocalizedMessage.NO_PERMISSION.getKey(), player);
			player.sendMessage(ChatColor.RED
					+ new MessageFormat(message).format(new Object[] { StringUtils.join(permissions, ",") }));
		}

		return false;
	}

	private boolean playerCanDoAction(Player player, List<String> permissions) {
		return playerCanDoAction(player, true, permissions);

	}

	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

		// the default mode is to treat the first argument as a number or a
		// number surrounded by parenthesis
		// control flow is passed to handleAlternateFirstArgument the first
		// argument is not a number or
		// a number surrounded by parenthesis
		try {
			if (!(sender instanceof Player)) {
				return false;
			}

			Player player = (Player) sender;

			if (!plugin.worldEnabled(player.getWorld().getName())) {
				plugin.debugInfo("no time commands. world not enabled.");
				String message = plugin.getLocalizedMessage(LocalizedMessage.WORLD_NOT_ENABLED.getKey(), player);
				player.sendMessage(new MessageFormat(message).format(new Object[] { player.getWorld().getName() }));
				return false;
			}

			double daysToProgress = 1.0;
			boolean isRelative = false;

			if (args != null && args.length > 0) {

				String firstArg = args[0];

				if (firstArg != null) {

					if (firstArg.startsWith("(") && firstArg.endsWith(")")) {
						isRelative = true;
						firstArg = firstArg.substring(1, firstArg.length() - 1);
					}

					try {
						daysToProgress = Double.parseDouble(firstArg);
					} catch (Exception e) {
						return handleAlternateFirstArgument(args, player, label);
					}

				}
			} else {
				return handleNoArgs(player);
			}

			if (!playerCanDoAction(player, TIMELORD_PERMISSIONS)) {
				return false;
			}

			if (Math.abs(daysToProgress) > 100000) {
				player.sendMessage(ChatColor.RED
						+ "The absolute value of the number of days must be less than 100,000 (because of reasons)");
				return false;
			}

			Double valueToProgress = daysToProgress * YearmarkedCalendar.TICKS_IN_A_DAY;

			if (isRelative) {

				if (player.getWorld().getFullTime() < YearmarkedCalendar.TICKS_IN_A_DAY) {
					if (valueToProgress < 0) {
						valueToProgress = player.getWorld().getFullTime() * -1.0;
					} else {
						valueToProgress = valueToProgress - player.getWorld().getFullTime();
					}
				} else {
					double remainder = player.getWorld().getFullTime() % YearmarkedCalendar.TICKS_IN_A_DAY;
					valueToProgress = valueToProgress - remainder;
				}
			}

			Long fullTime = player.getWorld().getFullTime() + Long.valueOf(valueToProgress.longValue());

			if (fullTime < 0) {
				fullTime = 0L;
			}

			player.getWorld().setFullTime(fullTime);

			player.sendMessage("/time set " + fullTime);

			return true;
		} catch (Exception e) {
			plugin.warning("error during " + label + " command");
			return false;
		}

	}

	private boolean handleNoArgs(Player player) {
		YearmarkedCalendar cal = new YearmarkedCalendar(player.getWorld().getFullTime());
		SendDayInfoListener.sendDayInfo(plugin, cal, Arrays.asList(player));
		player.sendMessage(ChatColor.GREEN + "/ym help ");
		return true;
	}

	private boolean handleAlternateFirstArgument(String[] args, Player player, String label) {
		if (args == null || args.length < 1 || args[0] == null) {
			plugin.debugInfo("Null or empty args");
			return false;
		}

		if (args[0].equals("day")) {

			if (!playerCanDoAction(player, TIMELORD_PERMISSIONS)) {
				return false;
			}

			plugin.debugInfo("'day' 1st arg");
			return handleDayArgument(args, player);
		} else if (args[0].equals("return")) {

			if (!playerCanDoAction(player, TIMELORD_PERMISSIONS)) {
				return false;
			}

			plugin.debugInfo("'return' 1st arg");
			return handleReturnArgument(args, player);
		} else if (args[0].equals("help")) {

			plugin.debugInfo("'help' 1st arg");
			return handleHelpArgument(player, label);
		} else {

			player.sendMessage(ChatColor.RED + "argument not recognized.  See help:");
			handleHelpArgument(player, label);
			return false;
		}
	}

	private boolean validateSecondArg(String[] args, Player player) {
		if (args.length < 2 || args[1] == null) {
			plugin.debugInfo("no 2nd arg");
			return false;
		}

		return true;
	}

	private boolean handleHelpArgument(Player player, String label) {
		player.sendMessage(ChatColor.GREEN + "command examples: ");

		if (playerCanDoAction(player, true, TIMELORD_PERMISSIONS)) {
			player.sendMessage("  - " + ChatColor.GREEN + "/" + label + " 3 " + ChatColor.WHITE
					+ "Moves world's fulltime forward exactly " + YearmarkedCalendar.TICKS_IN_A_DAY + "x3 units");
			player.sendMessage("  - " + ChatColor.GREEN + "/" + label + " -1 " + ChatColor.WHITE
					+ "Moves world's fulltime backwards exactly " + YearmarkedCalendar.TICKS_IN_A_DAY + "x1 units");
			player.sendMessage("  - " + ChatColor.GREEN + "/" + label + " (5) " + ChatColor.WHITE
					+ "Moves world's fulltime forward to the exact start of the day 5 from *now");
			player.sendMessage("  - " + ChatColor.GREEN + "/" + label + " (-2) " + ChatColor.WHITE
					+ "Moves world's fulltime backwards to the exact start of the day 2 *ago");
			player.sendMessage("  - " + ChatColor.GREEN + "/" + label + " day 1 " + ChatColor.WHITE
					+ "Moves to day 1 through 7. And snapshots your current time for use by the '" + label
					+ " return' command.");
			player.sendMessage("  - " + ChatColor.GREEN + "/" + label + " return " + ChatColor.WHITE
					+ "Moves the world's full time to where the user was before they ran the '" + label
					+ " day' command.");
		} else {
			player.sendMessage("#You only have permssions to run the '/ym command'");
		}

		return true;
	}

	private boolean handleDayArgument(String[] args, Player player) {

		if (!validateSecondArg(args, player)) {
			player.sendMessage(ChatColor.RED + "supply a number. it must be 1-7");
			return false;
		}

		try {
			int dayNumber = Integer.parseInt(args[1]);

			if (dayNumber < 1 || dayNumber > 7) {
				player.sendMessage(ChatColor.RED + "number must be 1-7");
				return false;
			} else {
				setReturnValueForPlayer(player);
				int newFulltime = (dayNumber - 1) * YearmarkedCalendar.TICKS_IN_A_DAY;
				player.sendMessage("/time set " + newFulltime);
				player.getWorld().setFullTime(newFulltime);
				return true;
			}

		} catch (NumberFormatException e) {
			player.sendMessage(ChatColor.RED + "error while parsing [" + args[1] + "] as an integer");
			plugin.debugInfo("issue parsing user input [" + args[1] + "] as integer: " + e.getMessage());
			return false;
		}
	}

	private boolean handleReturnArgument(String[] args, Player player) {
		Long returnValue = getReturnValueForPlayer(player);
		if (returnValue != null) {
			player.getWorld().setFullTime(returnValue);
			player.sendMessage("/time set " + returnValue);
			setReturnValueForPlayer(player, null);
			return true;
		}
		player.sendMessage("no return value is presently set");
		return false;
	}

	private void setReturnValueForPlayer(Player player, Long value) {
		if (!playerWorldReturnMap.containsKey(player.getName()) || playerWorldReturnMap.get(player.getName()) == null) {
			playerWorldReturnMap.put(player.getName(), new HashMap<String, Long>());
		}
		playerWorldReturnMap.get(player.getName()).put(player.getWorld().getName(), value);
	}

	private void setReturnValueForPlayer(Player player) {
		player.sendMessage(ChatColor.ITALIC + "#" + player.getWorld().getFullTime());
		player.getWorld().playSound(player.getLocation(), Sound.BLOCK_WOODEN_TRAPDOOR_CLOSE, 1, 1);
		setReturnValueForPlayer(player, player.getWorld().getFullTime());
	}

	private Long getReturnValueForPlayer(Player player) {
		if (!playerWorldReturnMap.containsKey(player.getName()) || playerWorldReturnMap.get(player.getName()) == null) {
			playerWorldReturnMap.put(player.getName(), new HashMap<String, Long>());
		}
		return playerWorldReturnMap.get(player.getName()).get(player.getWorld().getName());
	}

}
