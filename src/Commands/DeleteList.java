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
import org.dizitart.no2.objects.filters.ObjectFilters;
import org.json.JSONObject;
import request.EsaphRequestHandler;

import java.util.ListIterator;

public class DeleteList extends EsaphPipeRunable<Nitrite>
{
    public DeleteList(Nitrite dataBaseClass)
    {
        super(dataBaseClass);
    }

    @Override
    public String getPipeCommand()
    {
        return "DL";
    }

    @Override
    public void transfusion(EsaphRequestHandler.EsaphServerSession esaphServerSession)
    {
        JSONObject jsonObjectReply = new JSONObject();
        try
        {
            long BID = esaphServerSession.getJsonData().getLong("BID");

            Board boardToUpdate = getDatabase().getRepository(Board.class)
                    .find(ObjectFilters.elemMatch("mBoardListen", ObjectFilters.eq("mBoardListeId", BID)))
                    .firstOrDefault();

            BoardListe boardListeDataBase = null;

            int success = 0;
            if(boardToUpdate != null
                    && boardToUpdate.getmBoardListen() != null
                    && !boardToUpdate.getmBoardListen().isEmpty())
            {
                ListIterator<BoardListe> boardListeIterator = boardToUpdate.getmBoardListen().listIterator();
                while(boardListeIterator.hasNext())
                {
                    BoardListe boardListeCurrentIterating = boardListeIterator.next();
                    if(boardListeCurrentIterating.getmBoardListeId() == BID)
                    {
                        boardListeDataBase = boardListeCurrentIterating;
                        boardListeIterator.remove();
                    }
                }

                success = getDatabase().getRepository(Board.class).update(boardToUpdate).getAffectedCount();
                if(boardListeDataBase != null)
                {
                    esaphServerSession.getLogUtilsEsaph().writeLog("List deleted: " + boardListeDataBase.getmListenName());
                }
            }

            if(success > 0 && boardListeDataBase != null)
            {
                getDatabase().getRepository(EsaphLog.class)
                        .insert(new EsaphLog(boardToUpdate.getmBoardId(),
                                esaphServerSession.getmUserIdDescriptor(),
                                "Hat die Liste \"" + boardListeDataBase.getmListenName() + "\" gelöscht.",
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