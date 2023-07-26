/*
 * Copyright (c) 2023.
 * Julian Auguscik
 */

package Commands;

import lawsystem.EsaphPipeRunable;
import nitritmodel.Board;
import org.dizitart.no2.FindOptions;
import org.dizitart.no2.Nitrite;
import org.dizitart.no2.objects.filters.ObjectFilters;
import org.json.JSONArray;
import org.json.JSONObject;
import request.EsaphRequestHandler;

import java.util.Iterator;

public class LoadMyBoards extends EsaphPipeRunable<Nitrite>
{
    public LoadMyBoards(Nitrite dataBaseClass)
    {
        super(dataBaseClass);
    }

    @Override
    public String getPipeCommand()
    {
        return "LMB";
    }

    @Override
    public void transfusion(EsaphRequestHandler.EsaphServerSession esaphServerSession)
    {
        JSONObject jsonObjectReply = new JSONObject();
        try
        {
            long userId = esaphServerSession.getSession().getmUserId();
            JSONArray jsonArrayBoard = new JSONArray();
            int startFrom = esaphServerSession.getJsonData().getInt("ST");

            Iterator<Board> iterator = getDatabase().getRepository(Board.class).find(ObjectFilters.elemMatch("mListMitglieder",
                    ObjectFilters.eq("Uid", userId)), FindOptions.limit(startFrom, 20)).iterator();
            do {
                try
                {
                    Board board = iterator.next();
                    if(board != null)
                    {
                        jsonArrayBoard.put(board.getObjectMapJson(board));
                    }
                }
                catch (Exception ec)
                {
                    esaphServerSession.getLogUtilsEsaph().writeLog("Load my boards: " + ec);
                }
            }
            while(iterator.hasNext());


            jsonObjectReply.put("DATA", jsonArrayBoard);
            jsonObjectReply.put("SUCCESS", true);
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
