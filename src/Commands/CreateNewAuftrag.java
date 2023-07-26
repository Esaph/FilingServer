package Commands;

import lawsystem.EsaphPipeRunable;
import nitritmodel.*;
import org.dizitart.no2.Nitrite;
import org.dizitart.no2.NitriteId;
import org.dizitart.no2.objects.filters.ObjectFilters;
import org.json.JSONObject;
import request.EsaphRequestHandler;

import java.util.List;

public class CreateNewAuftrag extends EsaphPipeRunable<Nitrite>
{
    public CreateNewAuftrag(Nitrite dataBaseClass)
    {
        super(dataBaseClass);
    }

    @Override
    public String getPipeCommand()
    {
        return "CNA";
    }

    private void applyCardPriority(List<Auftrag> list, Auftrag auftrag)
    {
        auftrag.setmPrio(list.size());
        list.add(auftrag);
    }

    @Override
    public void transfusion(EsaphRequestHandler.EsaphServerSession esaphServerSession)
    {
        JSONObject jsonObjectReply = new JSONObject();
        try
        {
            Auftrag auftragOriginal = new Auftrag().createSelfFromMap(esaphServerSession.getJsonData().getJSONObject("AT").toString());
            BoardListe boardListeClient = new BoardListe().createSelfFromMap(esaphServerSession.getJsonData().getJSONObject("BOARDL").toString());
            auftragOriginal.setmAuftragsId(NitriteId.newId().getIdValue());
            auftragOriginal.setmErstelltID(esaphServerSession.getSession().getmUserId());

            Board boardToUpdate = getDatabase().getRepository(Board.class)
                    .find(ObjectFilters.elemMatch("mBoardListen",
                            ObjectFilters.eq("mBoardListeId", boardListeClient.getmBoardListeId())))
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
                        applyCardPriority(boardListe.getAuftragKartenListe(), auftragOriginal);
                        break;
                    }
                }

                boardToUpdate.setmBoardListen(boardListeList);
                success = getDatabase().getRepository(Board.class).update(boardToUpdate).getAffectedCount();
                esaphServerSession.getLogUtilsEsaph().writeLog("Inserted new Auftrag: " + auftragOriginal);
            }

            if(success > 0)
            {
                FCMMessageCenter.sendBroadCast(getDatabase(), esaphServerSession, "N/A");
                getDatabase().getRepository(EsaphLog.class)
                        .insert(new EsaphLog(boardToUpdate.getmBoardId(),
                                esaphServerSession.getmUserIdDescriptor(),
                                "Hat dem Board \"" + boardToUpdate.getmBoardName() + "\" eine neue Karte hinzugefügt \"" + auftragOriginal.getmAufgabe() + "\"",
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