package Commands;

import lawsystem.EsaphPipeRunable;
import nitritmodel.Board;
import nitritmodel.ProjectorBoardListe;
import org.dizitart.no2.Nitrite;
import org.dizitart.no2.objects.filters.ObjectFilters;
import org.json.JSONArray;
import org.json.JSONObject;
import request.EsaphRequestHandler;

public class LoadListFromBoard extends EsaphPipeRunable<Nitrite>
{
    public LoadListFromBoard(Nitrite dataBaseClass)
    {
        super(dataBaseClass);
    }

    @Override
    public String getPipeCommand() {
        return "LMLFB";
    }

    @Override
    public void transfusion(EsaphRequestHandler.EsaphServerSession esaphServerSession)
    {
        JSONObject jsonObjectReply = new JSONObject();
        try
        {
            int startFrom = esaphServerSession.getJsonData().getInt("ST");
            long BID = esaphServerSession.getJsonData().getLong("BID");

            ProjectorBoardListe boardDataBase = getDatabase().getRepository(Board.class)
                    .find(ObjectFilters.eq("mBoardId", BID))
                    .project(ProjectorBoardListe.class).firstOrDefault();

            JSONArray jsonArray = new JSONArray();
            if(boardDataBase != null && !boardDataBase.getmBoardListen().isEmpty())
            {
                boardDataBase.setmBoardListen(boardDataBase.getmBoardListen().subList(
                        Math.min(boardDataBase.getmBoardListen().size(), startFrom),
                        Math.min(boardDataBase.getmBoardListen().size(), startFrom + 20)));


                for(int counter = 0; counter < boardDataBase.getmBoardListen().size(); counter++)
                {
                    jsonArray.put(boardDataBase.getmBoardListen().get(counter)
                            .getObjectMapJson(boardDataBase.getmBoardListen().get(counter)));
                }
            }

            jsonObjectReply.put("DATA", jsonArray);
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
