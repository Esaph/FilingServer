/*
 * Copyright (c) 2023.
 * Julian Auguscik
 */

package Commands;

import lawsystem.EsaphPipeRunable;
import nitritmodel.Auftrag;
import nitritmodel.Board;
import nitritmodel.BoardListe;
import nitritmodel.ProjectorAuftraege;
import org.dizitart.no2.Nitrite;
import org.dizitart.no2.objects.filters.ObjectFilters;
import org.json.JSONArray;
import org.json.JSONObject;
import request.EsaphRequestHandler;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class LoadTMSSystem extends EsaphPipeRunable<Nitrite>
{
    public LoadTMSSystem(Nitrite dataBaseClass)
    {
        super(dataBaseClass);
    }

    @Override
    public String getPipeCommand()
    {
        return "LRTMS";
    }

    @Override
    public void transfusion(EsaphRequestHandler.EsaphServerSession esaphServerSession)
    {
        JSONObject jsonObjectReply = new JSONObject();
        try
        {
            int startFrom = esaphServerSession.getJsonData().getInt("ST");
            int colorCode = 0;
            boolean hasColorCode = esaphServerSession.getJsonData().has("CC");
            if(hasColorCode)
            {
                colorCode = esaphServerSession.getJsonData().getInt("CC");
            }


            long BID = esaphServerSession.getJsonData().getLong("BID");

            ProjectorAuftraege projectorAuftraege = getDatabase().getRepository(Board.class)
                    .find(ObjectFilters.elemMatch("mBoardListen", ObjectFilters.eq("mBoardListeId", BID)))
                    .project(ProjectorAuftraege.class).firstOrDefault();

            JSONArray jsonArray = new JSONArray();

            if(projectorAuftraege != null
                    && projectorAuftraege.getmBoardListen() != null
                    && !projectorAuftraege.getmBoardListen().isEmpty())
            {
                List<Auftrag> listAuftraege = new ArrayList<>();
                for (BoardListe b :
                        projectorAuftraege.getmBoardListen())
                {
                    if(b.getmBoardListeId() == BID)
                    {
                        listAuftraege = b.getAuftragKartenListe();
                        break;
                    }
                }

                if(!listAuftraege.isEmpty())
                {
                    Iterator<Auftrag> iterator = listAuftraege.iterator();

                    while(iterator.hasNext())
                    {
                        Auftrag auftrag = iterator.next();
                        if(auftrag.getContactDTO() == null) //Putting only cards in list, which have a Contact with an Adress.
                        {
                            iterator.remove();
                        }
                        else
                        {
                            if(auftrag.getmColorCode() != colorCode && hasColorCode)
                            {
                                iterator.remove();
                            }
                        }
                    }

                    listAuftraege = (listAuftraege.subList(
                            Math.min(listAuftraege.size(), startFrom),
                            Math.min(listAuftraege.size(), startFrom + 20)));

                    for(int counter = 0; counter < listAuftraege.size(); counter++)
                    {
                        jsonArray.put(listAuftraege
                                .get(counter).getObjectMapJson(listAuftraege.get(counter)));
                    }
                }
            }

            jsonObjectReply.put("DATA", jsonArray);
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