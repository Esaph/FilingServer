/*
 * Copyright (c) 2023.
 * Julian Auguscik
 */

package nitritmodel;

import data.PipeData;
import org.dizitart.no2.objects.Id;

import java.io.Serializable;

// TODO: 03.11.2019 add text index

public class Session extends PipeData<Session> implements Serializable
{
    @Id
    private String mUserId;
    private String mSession;

    public String getmUserId() {
        return mUserId;
    }

    public void setmUserId(String mUserId) {
        this.mUserId = mUserId;
    }

    public String getmSession() {
        return mSession;
    }

    public void setmSession(String mSession) {
        this.mSession = mSession;
    }
}
