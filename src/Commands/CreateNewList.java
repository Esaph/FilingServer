/*
 * Copyright (c) 2023.
 * Julian Auguscik
 */

package Commands;

import lawsystem.EsaphPipeRunable;
import nitritmodel.Board;
import nitritmodel.BoardListe;
import nitritmodel.EsaphLog;
import org.dizitart.no2.Nitrite;
import org.dizitart.no2.NitriteId;
import org.dizitart.no2.objects.filters.ObjectFilters;
import org.json.JSONObject;
import request.EsaphRequestHandler;

import java.util.ArrayList;

public class CreateNewList extends EsaphPipeRunable<Nitrite>
{
    public CreateNewList(Nitrite dataBaseClass)
    {
        super(dataBaseClass);
    }

    @Override
    public String getPipeCommand()
    {
        return "CNL";
    }

    // TODO: 05.11.2019 should be same list names valid ?

    @Override
    public void transfusion(EsaphRequestHandler.EsaphServerSession esaphServerSession)
    {
        JSONObject jsonObjectReply = new JSONObject();
        try
        {
            Board board = new Board().createSelfFromMap(esaphServerSession.getJsonData().getJSONObject("BOARD").toString());
            BoardListe boardListe = new BoardListe().createSelfFromMap(esaphServerSession.getJsonData().getJSONObject("BLIST").toString());
            boardListe.setmBoardListeId(NitriteId.newId().getIdValue());
            boardListe.setAuftragKartenListe(new ArrayList<>());

            Board boardDatabase = getDatabase().getRepository(Board.class)
                    .find(ObjectFilters.eq("mBoardId", board.getmBoardId())).firstOrDefault();
            esaphServerSession.getLogUtilsEsaph().writeLog("Adding new List into " + boardDatabase.getmBoardName());
            if(boardDatabase.getmBoardListen() == null) boardDatabase.setmBoardListen(new ArrayList<>());

            boardDatabase.getmBoardListen().add(boardListe);

            int updated = getDatabase().getRepository(Board.class).update(boardDatabase).getAffectedCount();

            esaphServerSession.getLogUtilsEsaph().writeLog("Inserted new List: " + boardListe);

            if(updated > 0)
            {
                getDatabase().getRepository(EsaphLog.class)
                        .insert(new EsaphLog(boardDatabase.getmBoardId(),
                                esaphServerSession.getmUserIdDescriptor(),
                                "Hat eine neue Liste \"" + boardListe.getmListenName() + "\" erstellt.",
                                System.currentTimeMillis()));

                FCMMessageCenter.sendBroadCast(getDatabase(), esaphServerSession, "N/A");
            }

            jsonObjectReply.put("SUCCESS", updated > 0);
            jsonObjectReply.put("DATA", boardListe.getObjectMapJson(boardListe));
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