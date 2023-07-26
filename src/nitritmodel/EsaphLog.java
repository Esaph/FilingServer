/*
 * Copyright (c) 2023.
 * Julian Auguscik
 */

package nitritmodel;

import data.PipeData;
import java.io.Serializable;

public class EsaphLog extends PipeData<EsaphLog> implements Serializable
{
    private long mBoardId;
    private long mId;
    private String log;
    private long logTime;

    public EsaphLog()
    {
    }

    public EsaphLog(long mBoardId, long id, String log, long logTime) {
        this.mBoardId = mBoardId;
        mId = id;
        this.log = log;
        this.logTime = logTime;
    }

    public long getmBoardId() {
        return mBoardId;
    }

    public void setmBoardId(long mBoardId) {
        this.mBoardId = mBoardId;
    }

    public long getmId() {
        return mId;
    }

    public void setmId(long mId) {
        this.mId = mId;
    }

    public long getLogTime() {
        return logTime;
    }

    public void setLogTime(long logTime) {
        this.logTime = logTime;
    }

    public String getLog() {
        return log;
    }

    public void setLog(String log) {
        this.log = log;
    }
}
