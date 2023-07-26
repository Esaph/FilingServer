/*
 * Copyright (c) 2023.
 * Julian Auguscik
 */

package Commands;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import esaphsession.EsaphServerSessionManager;
import lawsystem.EsaphPipeRunable;
import log.EsaphLogUtils;
import nitritmodel.Session;
import nitritmodel.User;
import org.dizitart.no2.Nitrite;
import org.dizitart.no2.objects.ObjectRepository;
import org.dizitart.no2.objects.filters.ObjectFilters;
import org.json.JSONObject;
import request.EsaphRequestHandler;
import java.util.Collections;

import static org.dizitart.no2.objects.filters.ObjectFilters.eq;

public class LoginWithGoogle extends EsaphPipeRunable<Nitrite>
{
    public LoginWithGoogle(Nitrite dataBaseClass) {
        super(dataBaseClass);
    }

    @Override
    public String getPipeCommand()
    {
        return "LWG";
    }


    private GoogleIdToken.Payload authNewClient(String CLIENT_ID,
                                                String idTokenString,
                                                EsaphLogUtils esaphLogUtils) throws Exception
    {
        esaphLogUtils.writeLog("ID Token validating - newClient");

        GoogleIdTokenVerifier verifierForNewAndroidClients = new GoogleIdTokenVerifier.Builder(GoogleNetHttpTransport.newTrustedTransport(),
                JacksonFactory.getDefaultInstance())
                .setAudience(Collections.singletonList(CLIENT_ID))
                .setIssuer("https://accounts.google.com")
                .build();


        GoogleIdToken idToken = verifierForNewAndroidClients.verify(idTokenString);

        if (idToken != null)
        {
            esaphLogUtils.writeLog("ID Token valid - new client");
            return idToken.getPayload();
        } else {
            esaphLogUtils.writeLog("Invalid ID token - new client");
        }
        return null;
    }


    @Override
    public void transfusion(EsaphRequestHandler.EsaphServerSession esaphServerSession)
    {
        JSONObject jsonObjectReply = new JSONObject();


        try
        {
            String CLIENT_ID = esaphServerSession.getJsonData().getString("CLID");
            String idTokenString = esaphServerSession.getJsonData().getString("idTS");

            GoogleIdToken.Payload payload = authNewClient(CLIENT_ID, idTokenString, esaphServerSession.getLogUtilsEsaph());

            String id = payload.getSubject();
            String email = payload.getEmail();
            String name = (String) payload.get("name");
            String familyName = (String) payload.get("family_name");
            String givenName = (String) payload.get("given_name");

            User user = new User();
            user.setUid(id);
            user.setEmail(email);
            user.setFamilyName(familyName);
            user.setGivenName(givenName);
            user.setUsername(name);


            ObjectRepository<User> repository = getDatabase().getRepository(User.class);

            User userLoaded = repository.find(eq("Uid", user.getUid()))
                    .firstOrDefault();

            if(userLoaded == null)
            {
                esaphServerSession.getLogUtilsEsaph().writeLog("Registring user: " + user + " creating session.");
                repository.insert(user);
            }
            else
            {
                esaphServerSession.getLogUtilsEsaph().writeLog("User registered: " + userLoaded + " creating session.");
            }

            String SID = new EsaphServerSessionManager().generateSessionToken();
            Session session = new Session();
            session.setmUserId(user.getUid());
            session.setmSession(SID);

            //Session created.

            int affected = getDatabase()
                    .getRepository(Session.class)
                    .update(ObjectFilters.eq("mUserId", user.getUid()), session, true).getAffectedCount();
            jsonObjectReply.put("DATA", session.getObjectMapJson(session));
        }
        catch (Exception ec)
        {
            esaphServerSession.getLogUtilsEsaph().writeLog("Failed:" + ec);
        }
        finally
        {
            esaphServerSession.getWriter().println(jsonObjectReply.toString());
            esaphServerSession.getWriter().flush();
            getDatabase().commit();
        }
    }
}