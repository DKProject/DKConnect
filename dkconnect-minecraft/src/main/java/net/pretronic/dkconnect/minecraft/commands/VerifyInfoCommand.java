package net.pretronic.dkconnect.minecraft.commands;

import net.pretronic.dkconnect.api.player.DKConnectPlayer;
import net.pretronic.dkconnect.minecraft.config.Messages;
import net.pretronic.libraries.command.command.BasicCommand;
import net.pretronic.libraries.command.command.configuration.CommandConfiguration;
import net.pretronic.libraries.command.sender.CommandSender;
import net.pretronic.libraries.message.bml.variable.VariableSet;
import net.pretronic.libraries.utility.interfaces.ObjectOwner;
import org.mcnative.runtime.api.McNative;
import org.mcnative.runtime.api.player.MinecraftPlayer;

public class VerifyInfoCommand extends BasicCommand {

    public VerifyInfoCommand(ObjectOwner owner, CommandConfiguration configuration) {
        super(owner, configuration);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        DKConnectPlayer player;
        if(args.length == 1) {
            String playerName = args[0];
            MinecraftPlayer minecraftPlayer = McNative.getInstance().getPlayerManager().getPlayer(playerName);
            if(minecraftPlayer == null) {
                sender.sendMessage(Messages.ERROR_PLAYER_NOT_FOUND, VariableSet.create().add("name", playerName));
                return;
            }
            player = minecraftPlayer.getAs(DKConnectPlayer.class);
        } else {
            if(CommandUtil.isConsole(sender)) return;
            player = ((MinecraftPlayer)sender).getAs(DKConnectPlayer.class);
        }
        sender.sendMessage(Messages.COMMAND_VERIFYINFO, VariableSet.create().addDescribed("player", player));
    }
}
