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

public class CreateNewBoard extends EsaphPipeRunable<Nitrite>
{
    public CreateNewBoard(Nitrite dataBaseClass)
    {
        super(dataBaseClass);
    }

    @Override
    public String getPipeCommand()
    {
        return "CNB";
    }

    @Override
    public void transfusion(EsaphRequestHandler.EsaphServerSession esaphServerSession)
    {
        JSONObject jsonObjectReply = new JSONObject();
        try
        {
            Board boardoriginal = new Board().createSelfFromMap(esaphServerSession.getJsonData().toString());

            User userCreator = getDatabase() //Master Security rule.
                    .getRepository(User.class)
                    .find(ObjectFilters.eq("Uid",esaphServerSession.getSession().getmUserId()))
                    .firstOrDefault();

            boardoriginal.getmListMitglieder().add(userCreator);
            boardoriginal.setmBoardListen(new ArrayList<>());

            if(boardoriginal.getmListMitglieder() == null || boardoriginal.getmListMitglieder().isEmpty()) throw new Exception("Board must have at least one member");

            boardoriginal.setmBoardId(NitriteId.newId().getIdValue());

            WriteResult writeResult = getDatabase().getRepository(Board.class).insert(boardoriginal);
            esaphServerSession.getLogUtilsEsaph().writeLog("Inserted new Board: " + boardoriginal);

            if(writeResult.getAffectedCount() > 0)
            {
                getDatabase().getRepository(EsaphLog.class)
                        .insert(new EsaphLog(boardoriginal.getmBoardId(),
                                esaphServerSession.getmUserIdDescriptor(),
                                "Hat ein neues Board erstellt \"" + boardoriginal.getmBoardName() + "\"",
                                System.currentTimeMillis()));
            }

            jsonObjectReply.put("SUCCESS", writeResult.getAffectedCount() > 0);
            jsonObjectReply.put("DATA", boardoriginal.getObjectMapJson(boardoriginal)); //Sending reply.
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