package Commands;

import lawsystem.EsaphPipeRunable;
import nitritmodel.EsaphLog;
import org.dizitart.no2.FindOptions;
import org.dizitart.no2.Nitrite;
import org.dizitart.no2.objects.filters.ObjectFilters;
import org.json.JSONArray;
import org.json.JSONObject;
import request.EsaphRequestHandler;

import java.util.Iterator;

public class LoadLogFromBoard extends EsaphPipeRunable<Nitrite>
{
    public LoadLogFromBoard(Nitrite dataBaseClass)
    {
        super(dataBaseClass);
    }

    @Override
    public String getPipeCommand()
    {
        return "LLFB";
    }

    @Override
    public void transfusion(EsaphRequestHandler.EsaphServerSession esaphServerSession)
    {
        JSONObject jsonObjectReply = new JSONObject();
        try
        {
            long boardId = esaphServerSession.getJsonData().getLong("BID");
            int startFrom = esaphServerSession.getJsonData().getInt("ST");

            JSONArray jsonArrayBoard = new JSONArray();

            Iterator<EsaphLog> iterator = getDatabase().getRepository(EsaphLog.class).find(ObjectFilters.eq("mBoardId", boardId),
                    FindOptions.limit(startFrom, 10)).iterator();
            do {
                try
                {
                    EsaphLog esaphLog = iterator.next();
                    jsonArrayBoard.put(esaphLog.getObjectMapJson(esaphLog));
                }
                catch (Exception ec)
                {
                    esaphServerSession.getLogUtilsEsaph().writeLog("Load my Log from board: " + ec);
                }
            }
            while(iterator.hasNext());


            jsonObjectReply.put("DATA", jsonArrayBoard);
            jsonObjectReply.put("SUCCESS", true);
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
