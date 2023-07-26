/*
 * Copyright (c) 2023.
 * Julian Auguscik
 */

package Commands;

import lawsystem.EsaphPipeRunable;
import nitritmodel.Board;
import nitritmodel.EsaphLog;
import nitritmodel.User;
import org.dizitart.no2.Nitrite;
import org.dizitart.no2.NitriteId;
import org.dizitart.no2.WriteResult;
import org.dizitart.no2.objects.filters.ObjectFilters;
import org.json.JSONObject;
import request.EsaphRequestHandler;

import java.util.ArrayList;

public class UpdateBoard extends EsaphPipeRunable<Nitrite>
{
    public UpdateBoard(Nitrite dataBaseClass)
    {
        super(dataBaseClass);
    }

    @Override
    public String getPipeCommand()
    {
        return "UCB";
    }

    @Override
    public void transfusion(EsaphRequestHandler.EsaphServerSession esaphServerSession)
    {
        JSONObject jsonObjectReply = new JSONObject();
        try
        {
            Board boardoriginal = new Board().createSelfFromMap(esaphServerSession.getJsonData().toString());

            int affectedCount = getDatabase().getRepository(Board.class)
                    .update(boardoriginal).getAffectedCount();

            if(affectedCount > 0)
            {
                esaphServerSession.getLogUtilsEsaph().writeLog("Board wurde erfolgreich aktualisiert: " + boardoriginal.getmBoardName());
                getDatabase().getRepository(EsaphLog.class)
                        .insert(new EsaphLog(boardoriginal.getmBoardId(),
                                esaphServerSession.getmUserIdDescriptor(),
                                "Hat das Board \"" + boardoriginal.getmBoardName() + "\" bearbeitet.",
                                System.currentTimeMillis()));
            }

            jsonObjectReply.put("SUCCESS", affectedCount > 0);
            jsonObjectReply.put("DATA", ""); //No need to transmitt any data.
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