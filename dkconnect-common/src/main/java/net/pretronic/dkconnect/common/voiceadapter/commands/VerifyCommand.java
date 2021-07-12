package net.pretronic.dkconnect.common.voiceadapter.commands;

import net.pretronic.dkconnect.api.player.DKConnectPlayer;
import net.pretronic.dkconnect.api.player.PendingVerification;
import net.pretronic.dkconnect.api.player.Verification;
import net.pretronic.dkconnect.api.voiceadapter.VoiceAdapter;
import net.pretronic.dkconnect.common.voiceadapter.VoiceAdapterCommandSender;
import net.pretronic.dkconnect.common.voiceadapter.VoiceAdapterMessages;
import net.pretronic.libraries.command.command.BasicCommand;
import net.pretronic.libraries.command.command.configuration.CommandConfiguration;
import net.pretronic.libraries.command.sender.CommandSender;
import net.pretronic.libraries.message.bml.variable.VariableSet;

public class VerifyCommand extends BasicCommand {

    private final VoiceAdapter voiceAdapter;

    public VerifyCommand(VoiceAdapter voiceAdapter, CommandConfiguration configuration) {
        super(voiceAdapter, configuration);
        this.voiceAdapter = voiceAdapter;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if(!(sender instanceof VoiceAdapterCommandSender)) {
            return;
        }
        if(args.length == 0) {
            sender.sendMessage(voiceAdapter.getMessage(VoiceAdapterMessages.COMMAND_VERIFY_USAGE));
            return;
        }
        VoiceAdapterCommandSender<?> commandSender = (VoiceAdapterCommandSender<?>) sender;
        String code = args[0];
        DKConnectPlayer player = this.voiceAdapter.getDKConnect().getPlayerManager().getPlayerByPendingVerificationCode(voiceAdapter, code);
        if(player != null) {
            PendingVerification pendingVerification = player.getPendingVerification(voiceAdapter);
            Verification verification = pendingVerification.complete(commandSender.getId(), commandSender.getName());
            commandSender.sendMessageKey(VoiceAdapterMessages.COMMAND_VERIFY_COMPLETED, VariableSet.create().addDescribed("verification", verification));
        } else {
            commandSender.sendMessageKey(VoiceAdapterMessages.COMMAND_VERIFY_NO_PENDING_VERIFICATION, VariableSet.create().add("code", code));
        }
    }
}
