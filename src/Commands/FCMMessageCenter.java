/*
 * Copyright (c) 2023.
 * Julian Auguscik
 */

package Commands;

import com.google.firebase.messaging.BatchResponse;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.MulticastMessage;
import nitritmodel.User;
import org.dizitart.no2.Nitrite;
import request.EsaphRequestHandler;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class FCMMessageCenter
{
    public static void sendBroadCast(Nitrite nitrite,
                                     EsaphRequestHandler.EsaphServerSession esaphServerSession,
                                     String commandMessage) throws FirebaseMessagingException
    {
        List<String> listFcms = getAllFCMTokens(nitrite, esaphServerSession);
        if(listFcms.isEmpty()) return;

        MulticastMessage message = MulticastMessage.builder()
                .putData("time", System.currentTimeMillis() + "")
                .putData("CMD", commandMessage)
                .addAllTokens(listFcms)
                .build();

        BatchResponse response = FirebaseMessaging.getInstance().sendMulticast(message);
        esaphServerSession.getLogUtilsEsaph().writeLog(response.getSuccessCount() + " messages were sent successfully");
    }

    private static List<String> getAllFCMTokens(Nitrite nitrite, EsaphRequestHandler.EsaphServerSession esaphServerSession)
    {
        Iterator<User> iterator = nitrite.getRepository(User.class)
            .find()
            .iterator();

        List<String> listFCMs = new ArrayList<>();

        while(iterator.hasNext())
        {
            User user = iterator.next();
            if(user != null && !user.getUid().equals(esaphServerSession.getSession().getmUserId()))
            {
                if(user.getFcmToken() != null && !user.getFcmToken().isEmpty())
                {
                    listFCMs.add(user.getFcmToken());
                }
            }
        }

        return listFCMs;
    }
}
