package net.pretronic.dkconnect.minecraft.commands;

import net.pretronic.dkconnect.minecraft.config.Messages;
import net.pretronic.libraries.command.sender.CommandSender;
import org.mcnative.runtime.api.player.OnlineMinecraftPlayer;

public class CommandUtil {

    public static boolean isConsole(CommandSender sender){
        if(!(sender instanceof OnlineMinecraftPlayer)) {
            sender.sendMessage(Messages.ERROR_ONLY_PLAYER);
            return true;
        }
        return false;
    }
}
