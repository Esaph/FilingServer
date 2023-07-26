/*
 * Copyright (c) 2023.
 * Julian Auguscik
 */

package Commands;

import lawsystem.EsaphPipeRunable;
import nitritmodel.Board;
import nitritmodel.BoardNameProjection;
import nitritmodel.EsaphLog;
import org.dizitart.no2.Nitrite;
import org.dizitart.no2.objects.filters.ObjectFilters;
import org.json.JSONObject;
import request.EsaphRequestHandler;

public class DeleteBoard extends EsaphPipeRunable<Nitrite>
{
    public DeleteBoard(Nitrite dataBaseClass)
    {
        super(dataBaseClass);
    }

    @Override
    public String getPipeCommand()
    {
        return "DB";
    }

    @Override
    public void transfusion(EsaphRequestHandler.EsaphServerSession esaphServerSession)
    {
        JSONObject jsonObjectReply = new JSONObject();
        try
        {
            long BOARDID = esaphServerSession.getJsonData().getLong("BID");

            BoardNameProjection boardNameProjection =
                    getDatabase().getRepository(Board.class)
                    .find(ObjectFilters.eq("mBoardId", BOARDID)).project(BoardNameProjection.class).firstOrDefault();

            int affectedCount = getDatabase().getRepository(Board.class)
                    .remove(ObjectFilters.eq("mBoardId", BOARDID)).getAffectedCount();

            if(affectedCount > 0)
            {
                getDatabase().getRepository(EsaphLog.class)
                        .insert(new EsaphLog(BOARDID,
                                esaphServerSession.getmUserIdDescriptor(),
                                "Hat das Board \"" + boardNameProjection.getmBoardName() + "\" gelöscht.",
                                System.currentTimeMillis()));
            }

            jsonObjectReply.put("SUCCESS", affectedCount > 0);
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