package nitritmodel;

import data.PipeData;
import org.dizitart.no2.objects.Id;

import java.io.Serializable;
import java.util.List;

public class BoardNameProjection extends PipeData<BoardNameProjection> implements Serializable
{
    private String mBoardName;

    public String getmBoardName() {
        return mBoardName;
    }

    public void setmBoardName(String mBoardName) {
        this.mBoardName = mBoardName;
    }
}
