/*
 * Copyright (c) 2023.
 * Julian Auguscik
 */

package Commands;

import lawsystem.EsaphPipeRunable;
import nitritmodel.Board;
import nitritmodel.User;
import org.dizitart.no2.FindOptions;
import org.dizitart.no2.Nitrite;
import org.dizitart.no2.SortOrder;
import org.dizitart.no2.objects.filters.ObjectFilters;
import org.json.JSONArray;
import org.json.JSONObject;
import request.EsaphRequestHandler;

import java.util.Iterator;

public class LoadFilingUsers extends EsaphPipeRunable<Nitrite>
{
    public LoadFilingUsers(Nitrite dataBaseClass)
    {
        super(dataBaseClass);
    }

    @Override
    public String getPipeCommand()
    {
        return "LFU";
    }

    @Override
    public void transfusion(EsaphRequestHandler.EsaphServerSession esaphServerSession)
    {
        JSONObject jsonObjectReply = new JSONObject();
        try
        {
            int offset = esaphServerSession.getJsonData().getInt("ST");
            JSONArray jsonArray = new JSONArray();
            Iterator<User> iteratorUsers = getDatabase()
                    .getRepository(User.class)
                    .find(FindOptions.sort("Username", SortOrder.Descending).thenLimit(offset, 20))
                    .iterator();

            while(iteratorUsers.hasNext())
            {
                User user = iteratorUsers.next();
                if(!user.getUid().equals(esaphServerSession.getSession().getmUserId()))
                {
                    jsonArray.put(user.getObjectMapJson(user));
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
