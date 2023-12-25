package org.chorser.service;

import org.javacord.api.event.message.MessageCreateEvent;

public abstract class IFunctionService {

    public abstract String response(String input,MessageCreateEvent event);
}
