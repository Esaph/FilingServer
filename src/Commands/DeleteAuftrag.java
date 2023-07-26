/*
 * Copyright (c) 2023.
 * Julian Auguscik
 */

package Commands;

import lawsystem.EsaphPipeRunable;
import nitritmodel.*;
import org.dizitart.no2.Nitrite;
import org.dizitart.no2.objects.filters.ObjectFilters;
import org.json.JSONObject;
import request.EsaphRequestHandler;

import java.util.ArrayList;
import java.util.List;

public class DeleteAuftrag extends EsaphPipeRunable<Nitrite>
{
    public DeleteAuftrag(Nitrite dataBaseClass)
    {
        super(dataBaseClass);
    }

    @Override
    public String getPipeCommand()
    {
        return "DA";
    }

    private void removeCard(List<Auftrag> list, Auftrag auftrag)
    {
        for (int count = 0; count < list.size(); count++)
        {
            if(list.get(count).getmAuftragsId() == auftrag.getmAuftragsId())
            {
                list.remove(count);
                break;
            }
        }

        for (Auftrag a :
                list)
        {
            a.setmPrio(a.getmPrio()-1);
        }
    }

    @Override
    public void transfusion(EsaphRequestHandler.EsaphServerSession esaphServerSession)
    {
        JSONObject jsonObjectReply = new JSONObject();
        try
        {
            Auftrag auftragOriginal = new Auftrag().createSelfFromMap(esaphServerSession.getJsonData().getJSONObject("AT").toString());
            BoardListe boardListeClient = new BoardListe().createSelfFromMap(esaphServerSession.getJsonData().getJSONObject("BOARDL").toString());

            Board boardToUpdate = getDatabase().getRepository(Board.class)
                    .find(ObjectFilters.elemMatch("mBoardListen", ObjectFilters.eq("mBoardListeId", boardListeClient.getmBoardListeId())))
                    .firstOrDefault();

            int success = 0;
            if(boardToUpdate != null
                    && boardToUpdate.getmBoardListen() != null
                    && !boardToUpdate.getmBoardListen().isEmpty())
            {
                List<BoardListe> boardListeList = boardToUpdate.getmBoardListen();
                for (BoardListe boardListe :
                        boardListeList)
                {
                    if(boardListe.getmBoardListeId() == boardListeClient.getmBoardListeId())
                    {
                        removeCard(boardListe.getAuftragKartenListe(), auftragOriginal);
                        break;
                    }
                }

                success = getDatabase().getRepository(Board.class).update(boardToUpdate).getAffectedCount();
                esaphServerSession.getLogUtilsEsaph().writeLog("Auftrag wurde gelöscht: " + auftragOriginal.getmAufgabe());
            }

            if(success > 0)
            {
                FCMMessageCenter.sendBroadCast(getDatabase(), esaphServerSession, "N/A");
                getDatabase().getRepository(EsaphLog.class)
                        .insert(new EsaphLog(boardToUpdate.getmBoardId(),
                                esaphServerSession.getmUserIdDescriptor(),
                                "Hat den Auftrag \"" + auftragOriginal.getmAufgabe() + "\" gelöscht.",
                                System.currentTimeMillis()));
            }

            jsonObjectReply.put("SUCCESS", success > 0);
            jsonObjectReply.put("DATA", auftragOriginal.getObjectMapJson(auftragOriginal)); //Sending reply.
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