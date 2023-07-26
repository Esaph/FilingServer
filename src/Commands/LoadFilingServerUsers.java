package Commands;

import lawsystem.EsaphPipeRunable;
import nitritmodel.User;
import org.dizitart.no2.FindOptions;
import org.dizitart.no2.Nitrite;
import org.json.JSONArray;
import org.json.JSONObject;
import request.EsaphRequestHandler;

import java.util.Iterator;

public class LoadFilingServerUsers extends EsaphPipeRunable<Nitrite>
{
    public LoadFilingServerUsers(Nitrite dataBaseClass) {
        super(dataBaseClass);
    }

    @Override
    public String getPipeCommand() {
        return "LFU";
    }

    @Override
    public void transfusion(EsaphRequestHandler.EsaphServerSession esaphServerSession)
    {
        JSONObject jsonObjectReply = new JSONObject();
        try
        {
            JSONArray jsonArrayBoard = new JSONArray();
            int startFrom = esaphServerSession.getJsonData().getInt("ST");

            Iterator<User> iterator = getDatabase()
                    .getRepository(User.class)
                    .find(FindOptions.limit(startFrom, 20))
                    .iterator();

            do {
                try
                {
                    User user = iterator.next();
                    jsonArrayBoard.put(user.getObjectMapJson(user));
                }
                catch (Exception ec)
                {
                    esaphServerSession.getLogUtilsEsaph().writeLog("Load users from filing server: " + ec);
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
