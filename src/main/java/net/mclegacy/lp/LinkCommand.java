package net.mclegacy.lp;

import net.mclegacy.lp.ex.LinkException;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class LinkCommand extends Command
{
    protected LinkCommand()
    {
        super("MCLink");
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args)
    {
        if (label.equalsIgnoreCase("mclink"))
        {
            if (!(sender instanceof Player))
            {
                sender.sendMessage(ChatColor.RED + "Only players can use this command.");
                return true;
            }

            if (args.length != 1)
            {
                sender.sendMessage(ChatColor.RED + "Insufficient arguments. Usage: /link <code>");
                return true;
            }

            Bukkit.getScheduler().scheduleAsyncDelayedTask(LoginPass.getInstance(), () ->
            {
                try
                {
                    String name = sender.getName();
                    int code = Integer.parseInt(args[0]);

                    try
                    {
                        UpstreamAPI.validateLinkCode(name, code);

                        // if the above code does throw an exception, this should not run

                    } catch (LinkException ex) {
                        sender.sendMessage(ChatColor.RED + ex.getMessage());
                    }
                } catch (NumberFormatException ex) {
                    sender.sendMessage(ChatColor.RED + "Link code must contain integers only, ex: 1234");
                }
            }, 0L);

            return true;
        }

        return true;
    }
}
