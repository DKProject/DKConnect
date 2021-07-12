package net.pretronic.dkconnect.minecraft;

import net.pretronic.dkconnect.common.player.DefaultDKConnectPlayer;
import net.pretronic.dkconnect.common.player.DefaultPendingVerification;
import net.pretronic.dkconnect.common.player.DefaultVerification;
import net.pretronic.libraries.message.bml.variable.describer.VariableDescriber;
import net.pretronic.libraries.message.bml.variable.describer.VariableDescriberRegistry;
import org.mcnative.runtime.api.McNative;

public class DescriberRegistrar {

    public static void register() {
        VariableDescriber<DefaultDKConnectPlayer> playerDescriber = VariableDescriberRegistry.registerDescriber(DefaultDKConnectPlayer.class);
        playerDescriber.setForwardFunction(player -> McNative.getInstance().getPlayerManager().getPlayer(player.getId()));

        VariableDescriberRegistry.registerDescriber(DefaultPendingVerification.class);
        VariableDescriberRegistry.registerDescriber(DefaultVerification.class);
    }
}
