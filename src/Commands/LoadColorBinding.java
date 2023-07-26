package Commands;

import lawsystem.EsaphPipeRunable;
import nitritmodel.Board;
import nitritmodel.ColorBinder;
import org.dizitart.no2.Nitrite;
import org.dizitart.no2.objects.filters.ObjectFilters;
import org.json.JSONArray;
import org.json.JSONObject;
import request.EsaphRequestHandler;

import java.util.ArrayList;
import java.util.List;

public class LoadColorBinding extends EsaphPipeRunable<Nitrite>
{
    public LoadColorBinding(Nitrite dataBaseClass)
    {
        super(dataBaseClass);
    }

    @Override
    public String getPipeCommand()
    {
        return "LMCB";
    }

    private boolean success = false;
    @Override
    public void transfusion(EsaphRequestHandler.EsaphServerSession esaphServerSession)
    {
        JSONObject jsonObjectReply = new JSONObject();
        try
        {
            long boardId = esaphServerSession.getJsonData().getLong("BID");

            Board boardDatabase = getDatabase().getRepository(Board.class)
                    .find(ObjectFilters.eq("mBoardId", boardId))
                    .firstOrDefault();

            JSONArray jsonArray = new JSONArray();
            List<ColorBinder> list = boardDatabase.getColorBinders();
            if(list == null) list = new ArrayList<>();

            for (ColorBinder c : list)
            {
                jsonArray.put(c.getObjectMapJson(c));
            }

            success = true;
            jsonObjectReply.put("DATA", jsonArray);
            jsonObjectReply.put("SUCCESS", success);
        }
        catch (Exception ec)
        {
            esaphServerSession.getLogUtilsEsaph().writeLog("Failed: " + ec);
            jsonObjectReply.put("SUCCESS", false);
        }
        finally
        {
            esaphServerSession.getWriter().println(jsonObjectReply.toString());
            esaphServerSession.getWriter().flush();
            getDatabase().commit();
        }
    }
}
