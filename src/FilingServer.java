import Commands.*;
import com.google.gson.Gson;
import config.FilingConfig;
import lawsystem.EsaphPipeRunable;
import log.EsaphLogUtils;
import main.EsaphServer;
import nitritmodel.Session;
import nitritmodel.User;
import org.dizitart.no2.Nitrite;
import org.dizitart.no2.objects.filters.ObjectFilters;
import request.EsaphRequestHandler;
import serverconfiguration.ServerConfigFile;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class FilingServer extends EsaphServer<Nitrite>
{
    public static final String configFileName = "server-config";
    private Nitrite nitrite;
    private EsaphServerConfig esaphServerConfig;

    public FilingServer() throws Exception
    {
        Runtime.getRuntime().addShutdownHook(new Thread()
        {
            @Override
            public void run()
            {
                System.out.println("Shutdown hook ran!");
                if(nitrite != null && !nitrite.isClosed())
                {
                    nitrite.close();
                }
            }
        });

        EsaphLogUtils.logInConsole = true;
        nitrite = Nitrite.builder()
                .filePath("Filing.db")
                .openOrCreate("woiih2","owhfjnhlakhjiwe");

        esaphServerConfig = new EsaphServerConfig(nitrite,
                100000,
                30,
                30,
                "Filing Server 1.0",
                1002,
                "C:\\Users\\Julian\\IdeaProjects\\FilingServer\\ssl\\server.jks",
                "C:\\Users\\Julian\\IdeaProjects\\FilingServer\\ssl\\servertruststore.jks",
                "jex!sew39",
                "sea43!1s");

        // TODO: 28.01.2020 Uncomment this
        //esaphServerConfig = loadServerConfig();
        startServer();
    }


    private String readFile(File file) throws IOException
    {
        BufferedReader br = new BufferedReader(new FileReader(file));
        try
        {
            StringBuilder sb = new StringBuilder();
            String line = br.readLine();

            while (line != null)
            {
                sb.append(line);
                sb.append(System.lineSeparator());
                line = br.readLine();
            }
            return sb.toString();
        }
        finally
        {
            br.close();
        }
    }

    private EsaphServerConfig loadServerConfig() throws Exception
    {
        Gson gson = new Gson();

        FilingConfig filingConfig = gson.fromJson(readFile(ServerConfigFile.produceConfigFile(configFileName)), FilingConfig.class);

        return new EsaphServerConfig(nitrite,
                filingConfig.getmMaxRequestLength(),
                filingConfig.getmRequestThreadSize(),
                filingConfig.getmRequestSubThreadSize(),
                filingConfig.getmServerName(),
                filingConfig.getmPort(),
                filingConfig.getmSSLKeystoreFilePath(),
                filingConfig.getmSSLTrustStoreFilePath(),
                filingConfig.getmSSLKeystoreFilePassword(),
                filingConfig.getmSSLTrustStoreFilePassword());
    }

    @Override
    public boolean needSession(EsaphRequestHandler.EsaphServerSession esaphServerSession, Nitrite nitrite)
    {
        //The login command dont need a session.
        return !esaphServerSession.getPipe().equals(new LoginWithGoogle(nitrite).getPipeCommand());
    }

    @Override
    public boolean validateSession(EsaphRequestHandler.EsaphServerSession esaphServerSession, Nitrite nitrite)
    {
        try
        {
            Session sessionDataBaseFound = nitrite.getRepository(Session.class)
                    .find(ObjectFilters.eq("mUserId", esaphServerSession.getSession().getmUserId()))
                    .firstOrDefault();

            return esaphServerSession.getSession().getmSession().equals(sessionDataBaseFound.getmSession());
        }
        catch (Exception ec)
        {
            esaphServerSession.getLogUtilsEsaph().writeLog("validateSession() failed: " + ec);
            return false;
        }
    }

    @Override
    public long getDescriptor(long UserId)
    {
        /*
        User user = getDataBase().getRepository(User.class)
                .find(ObjectFilters.eq("Uid", UserId)).firstOrDefault();
        if(user == null || user.getUsername() == null) return -1;
        return user.getUsername();*/

        return UserId; //Maybe we dont need this, because the user id is handelt in EsaphServer lib.
    }

    @Override
    public List<EsaphPipeRunable> getPipes(Nitrite nitrite)
    {
        List<EsaphPipeRunable> list = new ArrayList<>();
        list.add(new LoginWithGoogle(nitrite));
        list.add(new CreateNewBoard(nitrite));
        list.add(new LoadMyBoards(nitrite));
        list.add(new CreateNewList(nitrite));
        list.add(new LoadListFromBoard(nitrite));
        list.add(new LoadAuftraege(nitrite));
        list.add(new CreateNewAuftrag(nitrite));
        list.add(new LoadTMSSystem(nitrite));
        list.add(new LoadLogFromBoard(nitrite));
        list.add(new UpdateAuftrag(nitrite));
        list.add(new DeleteList(nitrite));
        list.add(new DeleteBoard(nitrite));
        list.add(new DeleteAuftrag(nitrite));
        list.add(new TransferAutrag(nitrite));
        list.add(new LoadColorBinding(nitrite));
        list.add(new UpdateFCMToken(nitrite));
        list.add(new UpdateBoard(nitrite));
        list.add(new LoadFilingUsers(nitrite));
        list.add(new ArchiviereAuftrag(nitrite));
        return list;
    }

    @Override
    public Nitrite getDataBase()
    {
        return nitrite;
    }

    @Override
    public EsaphServerConfig obtainServerLaw()
    {
        return esaphServerConfig;
    }
}