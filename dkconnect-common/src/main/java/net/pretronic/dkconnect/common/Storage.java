package net.pretronic.dkconnect.common;

import net.pretronic.databasequery.api.Database;
import net.pretronic.databasequery.api.collection.DatabaseCollection;
import net.pretronic.databasequery.api.collection.field.FieldOption;
import net.pretronic.databasequery.api.datatype.DataType;

public class Storage {

    private final DatabaseCollection playerVerifications;
    private final DatabaseCollection playerPendingVerifications;
    private final DatabaseCollection voiceAdapterStaticMessages;

    public Storage(Database database) {
        this.playerVerifications = database.createCollection("dkconnect_player_verifications")
                .field("PlayerId", DataType.UUID, FieldOption.NOT_NULL)
                .field("VoiceAdapterName", DataType.STRING, FieldOption.NOT_NULL)
                .field("UserId", DataType.STRING, FieldOption.NOT_NULL)
                .field("Username", DataType.STRING, FieldOption.NOT_NULL)
                .field("Time", DataType.LONG, FieldOption.NOT_NULL)
                .create();

        this.playerPendingVerifications = database.createCollection("dkconnect_player_pending_verifications")
                .field("PlayerId", DataType.UUID, FieldOption.NOT_NULL)
                .field("VoiceAdapterName", DataType.STRING, FieldOption.NOT_NULL)
                .field("Code", DataType.STRING, FieldOption.NOT_NULL)
                .field("Time", DataType.LONG, FieldOption.NOT_NULL)
                .create();

        this.voiceAdapterStaticMessages = database.createCollection("dkconnect_voiceadapter_static_messages")
                .field("VoiceAdapterName", DataType.STRING, FieldOption.NOT_NULL)
                .field("Name", DataType.STRING, FieldOption.PRIMARY_KEY)
                .field("ChannelId", DataType.STRING, FieldOption.NOT_NULL)
                .field("MessageId", DataType.STRING, FieldOption.NOT_NULL)
                .create();
    }

    public DatabaseCollection getPlayerVerifications() {
        return playerVerifications;
    }

    public DatabaseCollection getPlayerPendingVerifications() {
        return playerPendingVerifications;
    }

    public DatabaseCollection getVoiceAdapterStaticMessages() {
        return voiceAdapterStaticMessages;
    }
}
