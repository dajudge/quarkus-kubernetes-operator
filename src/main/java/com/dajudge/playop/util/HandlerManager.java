package com.dajudge.playop.util;

import java.util.ArrayList;
import java.util.Collection;

import static java.util.Collections.synchronizedList;

public class HandlerManager {
    private Collection<HandlerRegistration> handlers = synchronizedList(new ArrayList<>());

    public boolean add(final HandlerRegistration handlerRegistration) {
        return handlers.add(handlerRegistration);
    }

    public void release() {
        handlers.forEach(HandlerRegistration::release);
    }
}
