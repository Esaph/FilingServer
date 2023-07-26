/*
 * Copyright (c) 2023.
 * Julian Auguscik
 */

package Commands;

import lawsystem.EsaphPipeRunable;
import nitritmodel.*;
import org.dizitart.no2.FindOptions;
import org.dizitart.no2.Nitrite;
import org.dizitart.no2.objects.filters.ObjectFilters;
import org.json.JSONObject;
import request.EsaphRequestHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

public class UpdateAuftrag extends EsaphPipeRunable<Nitrite>
{
    public UpdateAuftrag(Nitrite dataBaseClass)
    {
        super(dataBaseClass);
    }

    @Override
    public String getPipeCommand()
    {
        return "UA";
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
                    .find(ObjectFilters.elemMatch("mBoardListen", ObjectFilters.eq("mBoardListeId", boardListeClient.getmBoardListeId())),
                            FindOptions.limit(0,1))
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
                        ListIterator<Auftrag> listIterator = boardListe.getAuftragKartenListe().listIterator();
                        while(listIterator.hasNext())
                        {
                            Auftrag auftrag = listIterator.next();
                            if(auftrag.getmAuftragsId() == auftragOriginal.getmAuftragsId())
                            {
                                //Updating the card.
                                boardListe.getAuftragKartenListe().set(listIterator.nextIndex()-1, auftragOriginal);
                            }
                        }

                        break;
                    }
                }

                success = getDatabase().getRepository(Board.class).update(boardToUpdate).getAffectedCount();
                esaphServerSession.getLogUtilsEsaph().writeLog("Update Auftrag: " + auftragOriginal.getmAuftragsId());
            }

            if(success > 0)
            {
                FCMMessageCenter.sendBroadCast(getDatabase(), esaphServerSession, "N/A");
                getDatabase().getRepository(EsaphLog.class)
                        .insert(new EsaphLog(boardToUpdate.getmBoardId(),
                                esaphServerSession.getmUserIdDescriptor(),
                                "Hat die Karte \"" + auftragOriginal.getmAufgabe() + "\" geändert.",
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