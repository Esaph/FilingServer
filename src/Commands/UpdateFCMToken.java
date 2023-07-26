/*
 * Copyright (c) 2023.
 * Julian Auguscik
 */

package Commands;

import lawsystem.EsaphPipeRunable;
import nitritmodel.*;
import org.dizitart.no2.Nitrite;
import org.dizitart.no2.WriteResult;
import org.dizitart.no2.objects.filters.ObjectFilters;
import org.json.JSONObject;
import request.EsaphRequestHandler;

public class UpdateFCMToken extends EsaphPipeRunable<Nitrite>
{
    public UpdateFCMToken(Nitrite dataBaseClass)
    {
        super(dataBaseClass);
    }

    @Override
    public String getPipeCommand()
    {
        return "UFT";
    }

    @Override
    public void transfusion(EsaphRequestHandler.EsaphServerSession esaphServerSession)
    {
        JSONObject jsonObjectReply = new JSONObject();
        try
        {
            String fcmToken = esaphServerSession.getJsonData().getString("FT");
            String Uid = esaphServerSession.getJsonData().getString("FT");

            User user = getDatabase().getRepository(User.class).find(ObjectFilters.eq("Uid", Uid)).firstOrDefault();
            user.setFcmToken(fcmToken);
            WriteResult writeResult = getDatabase().getRepository(User.class).update(user);

            esaphServerSession.getLogUtilsEsaph().writeLog("New Fcm token registered.");

            jsonObjectReply.put("SUCCESS", writeResult.getAffectedCount() > 0);
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