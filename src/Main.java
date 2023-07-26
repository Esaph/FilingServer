/*
 * Copyright (c) 2023.
 * Julian Auguscik
 */

import com.google.gson.Gson;
import config.FilingConfig;
import serverconfiguration.ServerConfigFile;

import java.io.File;
import java.io.FileWriter;

public class Main
{
    public static void main(String[] args) throws Exception
    {
        if(args.length > 0)
        {
            if(args[0].equals("help"))
            {
                System.out.println("-----help-----");
                System.out.println("1. Konfigurationsdatei erzeugen Parameter: cf");
                System.out.println("2. Filing Server starten: java -jar <Server.jar>");
                System.out.println("-----end-----");
            }
            else if(args[0].equals("cf"))
            {
                File fileConfig = ServerConfigFile.produceConfigFile(FilingServer.configFileName);
                FilingConfig filingConfig = new FilingConfig(100000,
                        30,
                        30,
                        "Filing",
                        1002,
                        "",
                        "",
                        "",
                        "");

                Gson gson = new Gson();
                String configJSON = gson.toJson(filingConfig);

                FileWriter fw = null;
                try
                {
                    fw = new FileWriter(fileConfig);
                    fw.write(configJSON);
                    fw.close();
                }
                catch(Exception e)
                {
                    System.out.println("Konfigurationsdatei konnte nicht erstellt werden: " + e);
                }
                finally
                {
                    if(fw != null)
                    {
                        fw.close();
                    }
                }
            }
        }
        else
        {
            new FilingServer();
        }
    }
}