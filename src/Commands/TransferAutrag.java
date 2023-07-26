package Commands;

import lawsystem.EsaphPipeRunable;
import nitritmodel.*;
import org.dizitart.no2.Nitrite;
import org.dizitart.no2.objects.filters.ObjectFilters;
import org.json.JSONObject;
import request.EsaphRequestHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

public class TransferAutrag extends EsaphPipeRunable<Nitrite>
{
    public TransferAutrag(Nitrite dataBaseClass)
    {
        super(dataBaseClass);
    }

    @Override
    public String getPipeCommand()
    {
        return "TA";
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
            String kartenName = "";
            String listenameTransferFrom = "";
            String listenameTransgerTo = "";
            long boardId = -1;

            int success = 0;

            long auftragID = esaphServerSession.getJsonData().getLong("AID");
            long listenIDTransferFrom = esaphServerSession.getJsonData().getLong("BIDC");
            long listenIDTransferTo = esaphServerSession.getJsonData().getLong("BIDT");

            Board boardWorkingOn = getDatabase().getRepository(Board.class)
                    .find(ObjectFilters.elemMatch("mBoardListen",
                            ObjectFilters.eq("mBoardListeId", listenIDTransferFrom)))
                    .firstOrDefault();
            boardId = boardWorkingOn.getmBoardId();

            ListIterator<BoardListe> boardListeListIterator = boardWorkingOn.getmBoardListen().listIterator();
            while(boardListeListIterator.hasNext())
            {
                //KARTE FINDEN


                BoardListe boardListe = boardListeListIterator.next();
                if(boardListe.getmBoardListeId() == listenIDTransferFrom)
                {
                    listenameTransferFrom = boardListe.getmListenName();
                    ListIterator<Auftrag> listIteratorCardIsFROM = boardListe.getAuftragKartenListe().listIterator();
                    while(listIteratorCardIsFROM.hasNext())
                    {
                        Auftrag auftrag = listIteratorCardIsFROM.next();
                        if(auftrag.getmAuftragsId() == auftragID)//KARTE GEFUNDEN.
                        {
                            //Zweite list Suchen.
                            ListIterator<BoardListe> boardListeListIteratorSecondList = boardWorkingOn.getmBoardListen().listIterator();
                            while(boardListeListIteratorSecondList.hasNext())
                            {
                                BoardListe boardListeSecondList = boardListeListIteratorSecondList.next();
                                if(boardListeSecondList.getmBoardListeId() == listenIDTransferTo)
                                {
                                    //Zweite liste gefunden.
                                    applyCardPriority(boardListeSecondList.getAuftragKartenListe(), auftrag); //Karte hinzufügen.
                                    listIteratorCardIsFROM.remove(); //Karte aus der alten liste entfernen.
                                    success = getDatabase().getRepository(Board.class)
                                            .update(boardWorkingOn).getAffectedCount();
                                }
                            }
                            break;
                        }
                    }
                    break;
                }
            }

            if(success > 0)
            {
                FCMMessageCenter.sendBroadCast(getDatabase(), esaphServerSession, "N/A");
                getDatabase().getRepository(EsaphLog.class)
                        .insert(new EsaphLog(boardId,
                                esaphServerSession.getmUserIdDescriptor(),
                                "Hat die Karte \"" + kartenName + "\" von der Liste \"" + listenameTransferFrom + "\""
                                + " in die Liste \"" + listenameTransgerTo + "\" verschoben.",
                                System.currentTimeMillis()));
            }

            jsonObjectReply.put("SUCCESS", success > 0);
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