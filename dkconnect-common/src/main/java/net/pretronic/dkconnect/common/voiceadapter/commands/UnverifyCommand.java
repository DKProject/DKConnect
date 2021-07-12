package net.pretronic.dkconnect.common.voiceadapter.commands;

import net.pretronic.dkconnect.api.player.DKConnectPlayer;
import net.pretronic.dkconnect.api.voiceadapter.VoiceAdapter;
import net.pretronic.dkconnect.common.voiceadapter.VoiceAdapterCommandSender;
import net.pretronic.dkconnect.common.voiceadapter.VoiceAdapterMessages;
import net.pretronic.libraries.command.command.BasicCommand;
import net.pretronic.libraries.command.command.configuration.CommandConfiguration;
import net.pretronic.libraries.command.sender.CommandSender;

public class UnverifyCommand extends BasicCommand {

    private final VoiceAdapter voiceAdapter;

    public UnverifyCommand(VoiceAdapter voiceAdapter, CommandConfiguration configuration) {
        super(voiceAdapter, configuration);
        this.voiceAdapter = voiceAdapter;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if(!(sender instanceof VoiceAdapterCommandSender)) {
            return;
        }
        VoiceAdapterCommandSender<?> commandSender = (VoiceAdapterCommandSender<?>) sender;
        DKConnectPlayer player = this.voiceAdapter.getDKConnect().getPlayerManager().getPlayerByVerificationUserId(voiceAdapter, commandSender.getId());
        if(player != null) {
            if(player.getVerification(voiceAdapter).unverify()) {
                ((VoiceAdapterCommandSender<?>) sender).sendMessageKey(VoiceAdapterMessages.COMMAND_UNVERIFY_COMPLETED);
            }
        } else {
            ((VoiceAdapterCommandSender<?>) sender).sendMessageKey(VoiceAdapterMessages.COMMAND_UNVERIFY_NOT_VERIFIED);
        }
    }
}
