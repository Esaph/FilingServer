package Commands;

import lawsystem.EsaphPipeRunable;
import nitritmodel.*;
import org.dizitart.no2.Nitrite;
import org.dizitart.no2.objects.filters.ObjectFilters;
import org.json.JSONObject;
import request.EsaphRequestHandler;

import java.util.ListIterator;

public class ArchiviereAuftrag extends EsaphPipeRunable<Nitrite>
{
    public ArchiviereAuftrag(Nitrite dataBaseClass)
    {
        super(dataBaseClass);
    }

    @Override
    public String getPipeCommand()
    {
        return "AA";
    }

    @Override
    public void transfusion(EsaphRequestHandler.EsaphServerSession esaphServerSession)
    {
        JSONObject jsonObjectReply = new JSONObject();
        try
        {
            int affectedCount = 0;
            long BID = esaphServerSession.getJsonData().getLong("BID");
            long AID = esaphServerSession.getJsonData().getLong("AID");
            long LID = esaphServerSession.getJsonData().getLong("LID");

            Board board = getDatabase().getRepository(Board.class)
                    .find(ObjectFilters.eq("mBoardId", BID)).firstOrDefault();
            if(board != null)
            {
                ListIterator<BoardListe> listIterator = board.getmBoardListen().listIterator();
                while(listIterator.hasNext())
                {
                    BoardListe boardListe = listIterator.next();
                    if(boardListe.getmBoardListeId() == LID)
                    {
                        ListIterator<Auftrag> auftragListIterator = boardListe.getAuftragKartenListe().listIterator();
                        while(auftragListIterator.hasNext())
                        {
                            Auftrag auftrag = auftragListIterator.next();
                            if(auftrag.getmAuftragsId() == AID)
                            {
                                AuftragArchiv auftragArchiv = new AuftragArchiv(auftrag.getmAuftragsId(),
                                        auftrag.getmErstelltID(),
                                        auftrag.getmAblaufUhrzeit(),
                                        auftrag.getmColorCode(),
                                        auftrag.getmPrio(),
                                        auftrag.getmBeschreibung(),
                                        auftrag.getmAufgabe(),
                                        auftrag.getContactDTO(),
                                        auftrag.getViewType());

                                int affectedCountArchiv = getDatabase().getRepository(AuftragArchiv.class)
                                        .insert(auftragArchiv).getAffectedCount();

                                if(affectedCountArchiv > 0)
                                {
                                    auftragListIterator.remove();
                                    affectedCount = getDatabase().getRepository(Board.class)
                                            .update(ObjectFilters.eq("mBoardId", BID), board)
                                            .getAffectedCount();

                                    if(affectedCount > 0)
                                    {
                                        esaphServerSession.getLogUtilsEsaph().writeLog("Auftrag wurde erfolgreich archiviert.");
                                        getDatabase().getRepository(EsaphLog.class)
                                                .insert(new EsaphLog(board.getmBoardId(),
                                                        esaphServerSession.getmUserIdDescriptor(),
                                                        "Hat den Auftrag \"" + auftrag.getmAufgabe() + "\" archiviert.",
                                                        System.currentTimeMillis()));
                                    }
                                }
                            }
                        }
                    }
                }
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