package com.jakeconley.provo.functions.mail;

import java.util.Date;
import java.util.UUID;

public class Mail
{
    private final long Timestamp;
    private final UUID Sender;
    private final String Message;
    public long getTimestamp(){ return Timestamp; }
    public UUID getSenderUUID(){ return Sender; }
    public String getMessage(){ return Message; }
    
    public Mail(long _Timestamp, UUID _Sender, String _Message)
    {
        Timestamp = _Timestamp;
        Sender = _Sender;
        Message = _Message;
    }
    public Mail(UUID _Sender, String _Message){ this((new Date()).getTime(), _Sender, _Message); }
}
