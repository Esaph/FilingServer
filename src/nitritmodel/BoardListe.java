/*
 * Copyright (c) 2023.
 * Julian Auguscik
 */

package nitritmodel;

import data.PipeData;
import org.dizitart.no2.objects.Id;

import java.io.Serializable;
import java.util.List;

public class BoardListe extends PipeData<BoardListe> implements Serializable
{
    @Id
    private long mBoardListeId;
    private int mPriority;
    private String mListenName;
    private List<Auftrag> auftragKartenListe;

    public String getmListenName() {
        return mListenName;
    }

    public void setmListenName(String mListenName) {
        this.mListenName = mListenName;
    }

    public int getmPriority() {
        return mPriority;
    }

    public void setmPriority(int mPriority) {
        this.mPriority = mPriority;
    }

    public long getmBoardListeId() {
        return mBoardListeId;
    }

    public void setmBoardListeId(long mBoardListeId) {
        this.mBoardListeId = mBoardListeId;
    }

    public List<Auftrag> getAuftragKartenListe() {
        return auftragKartenListe;
    }

    public void setAuftragKartenListe(List<Auftrag> auftragKartenListe) {
        this.auftragKartenListe = auftragKartenListe;
    }
}
