package net.pretronic.dkconnect.minecraft.config;

import org.mcnative.runtime.api.text.Text;
import org.mcnative.runtime.api.text.components.MessageComponent;

public class Messages {

    public static final MessageComponent<?> ERROR_ONLY_PLAYER = Text.ofMessageKey("dkconnect.error.onlyPlayer");
    public static final MessageComponent<?> ERROR_PLAYER_ALREADY_VERIFIED = Text.ofMessageKey("dkconnect.error.player.alreadyVerified");
    public static final MessageComponent<?> ERROR_PLAYER_OPEN_PENDING_VERIFICATION = Text.ofMessageKey("dkconnect.error.player.openPendingVerification");
    public static final MessageComponent<?> ERROR_PLAYER_NOT_VERIFIED = Text.ofMessageKey("dkconnect.error.player.notVerified");
    public static final MessageComponent<?> ERROR_PLAYER_NOT_FOUND = Text.ofMessageKey("dkconnect.error.player.notFound");

    public static final MessageComponent<?> COMMAND_VERIFY = Text.ofMessageKey("dkconnect.command.verify");
    public static final MessageComponent<?> COMMAND_UNVERIFY = Text.ofMessageKey("dkconnect.command.unverify");
    public static final MessageComponent<?> COMMAND_VERIFYINFO = Text.ofMessageKey("dkconnect.command.verifyInfo");
}
