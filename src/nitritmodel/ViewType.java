/*
 * Copyright (c) 2023.
 * Julian Auguscik
 */

package nitritmodel;

import java.io.Serializable;

public enum ViewType implements Serializable
{
    AUFTRAG_KARTE("auftragKarte"),
    AUFTAG_LISTE("auftragListe");

    private final String displayName;

    ViewType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}