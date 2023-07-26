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

import java.util.*;

public class LoadAuftraege extends EsaphPipeRunable<Nitrite>
{
    public LoadAuftraege(Nitrite dataBaseClass)
    {
        super(dataBaseClass);
    }

    @Override
    public String getPipeCommand()
    {
        return "LA";
    }

    public class CompSortByDate implements Comparator<Auftrag>
    {
        @Override
        public int compare(Auftrag o1, Auftrag o2)
        {
            Date millisFirst = new Date(o1.getmAblaufUhrzeit());
            Date millisSecond = new Date(o2.getmAblaufUhrzeit());

            return millisFirst.compareTo(millisSecond);
        }
    }

    @Override
    public void transfusion(EsaphRequestHandler.EsaphServerSession esaphServerSession)
    {
        JSONObject jsonObjectReply = new JSONObject();
        try
        {
            int startFrom = esaphServerSession.getJsonData().getInt("ST");
            long LID = esaphServerSession.getJsonData().getLong("LID");

            ProjectorAuftraege projectorAuftraege = getDatabase().getRepository(Board.class)
                    .find(ObjectFilters.elemMatch("mBoardListen", ObjectFilters.eq("mBoardListeId", LID)))
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
                    if(b.getmBoardListeId() == LID)
                    {
                        listAuftraege = b.getAuftragKartenListe();
                        break;
                    }
                }

                if(!listAuftraege.isEmpty())
                {
                    listAuftraege = (listAuftraege.subList(
                            Math.min(listAuftraege.size(), startFrom),
                            Math.min(listAuftraege.size(), startFrom + 20)));

                    listAuftraege.sort(new CompSortByDate());

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