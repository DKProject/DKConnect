package net.pretronic.dkconnect.minecraft.commands;

import net.pretronic.dkconnect.api.player.Verification;
import net.pretronic.dkconnect.api.voiceadapter.VoiceAdapter;
import net.pretronic.dkconnect.api.player.DKConnectPlayer;
import net.pretronic.dkconnect.api.player.PendingVerification;
import net.pretronic.dkconnect.minecraft.config.Messages;
import net.pretronic.libraries.command.command.BasicCommand;
import net.pretronic.libraries.command.command.configuration.CommandConfiguration;
import net.pretronic.libraries.command.sender.CommandSender;
import net.pretronic.libraries.message.bml.variable.VariableSet;
import net.pretronic.libraries.utility.interfaces.ObjectOwner;
import org.mcnative.runtime.api.player.OnlineMinecraftPlayer;

public class VerifyCommand extends BasicCommand {

    private final VoiceAdapter voiceAdapter;

    public VerifyCommand(ObjectOwner owner, CommandConfiguration configuration, VoiceAdapter voiceAdapter) {
        super(owner, configuration);
        this.voiceAdapter = voiceAdapter;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if(!CommandUtil.isConsole(sender)) {
            OnlineMinecraftPlayer onlineMinecraftPlayer = (OnlineMinecraftPlayer) sender;
            DKConnectPlayer player = onlineMinecraftPlayer.getAs(DKConnectPlayer.class);

            PendingVerification pendingVerification = player.getPendingVerification(voiceAdapter);
            if(pendingVerification != null) {
                sender.sendMessage(Messages.ERROR_PLAYER_OPEN_PENDING_VERIFICATION, VariableSet.create()
                        .addDescribed("pendingVerification", pendingVerification));
                return;
            }

            Verification verification = player.getVerification(voiceAdapter);
            if(verification != null) {
                sender.sendMessage(Messages.ERROR_PLAYER_ALREADY_VERIFIED, VariableSet.create().addDescribed("verification", verification));
                return;
            }
            pendingVerification = player.verify(voiceAdapter);
            if(pendingVerification != null) {
                sender.sendMessage(Messages.COMMAND_VERIFY, VariableSet.create().addDescribed("pendingVerification", pendingVerification));
            }
        }
    }
}
