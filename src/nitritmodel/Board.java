/*
 * Copyright (c) 2023.
 * Julian Auguscik
 */

package nitritmodel;

import data.PipeData;
import org.dizitart.no2.objects.Id;

import java.io.Serializable;
import java.util.List;

public class Board extends PipeData<Board> implements Serializable
{
    @Id
    private long mBoardId;
    private BoardPolicy boardPolicy;
    private String mBoardName;
    private List<User> mListMitglieder;
    private List<BoardListe> mBoardListen;
    private List<ColorBinder> colorBinders;

    public long getmBoardId() {
        return mBoardId;
    }

    public List<ColorBinder> getColorBinders() {
        return colorBinders;
    }

    public void setColorBinders(List<ColorBinder> colorBinders) {
        this.colorBinders = colorBinders;
    }

    public void setmBoardId(long mBoardId) {
        this.mBoardId = mBoardId;
    }

    public BoardPolicy getBoardPolicy() {
        return boardPolicy;
    }

    public void setBoardPolicy(BoardPolicy boardPolicy) {
        this.boardPolicy = boardPolicy;
    }

    public String getmBoardName() {
        return mBoardName;
    }

    public void setmBoardName(String mBoardName) {
        this.mBoardName = mBoardName;
    }

    public List<User> getmListMitglieder() {
        return mListMitglieder;
    }

    public void setmListMitglieder(List<User> mListMitglieder) {
        this.mListMitglieder = mListMitglieder;
    }

    public List<BoardListe> getmBoardListen() {
        return mBoardListen;
    }

    public void setmBoardListen(List<BoardListe> mBoardListen) {
        this.mBoardListen = mBoardListen;
    }
}
