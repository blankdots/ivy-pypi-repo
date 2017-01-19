package org.uh.attx.de.pypi;

import static spark.Spark.*;

import junit.framework.TestCase;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

public class App {

    private static Object readFile() {

        String fileName = "init.json";
        Object obj = new Object();

        JSONParser parser = new JSONParser();
        try {
            InputStream in = App.class.getClassLoader().getResourceAsStream(fileName);
            obj = parser.parse(new InputStreamReader(in, "UTF-8"));

        } catch (Exception ex) {
            Logger.getLogger(App.class.getName()).log(Level.SEVERE, null, ex);
            TestCase.fail(ex.getMessage());
        }
        return obj;
    }

    private static String readDependencies(String VERSION, String REPO, JSONArray dependencies, JSONArray replace) {

        String commandLine = "";
        String packageString = "";
        String replaceString = "";

        if (dependencies != null) {
            Iterator i = dependencies.iterator();
            while (i.hasNext()) {
                JSONObject dependency = (JSONObject) i.next();
                String name = (String) dependency.get("name");
                String version = (String) dependency.get("version");
                packageString += name + ":" + version + " ";
            }

            commandLine = String.format("java -jar /var/lib/pivy-importer-%s-all.jar --repo %s %s", VERSION, REPO, packageString);

            if (replace != null) {
                Iterator j = replace.iterator();

                while (j.hasNext()) {
                    JSONObject replacing = (JSONObject) j.next();
                    String name = (String) replacing.get("name");
                    String oldVersion = (String) replacing.get("oldVersion");
                    String newVersion = (String) replacing.get("newVersion");
                    replaceString += name + ":" + oldVersion + "=" + name + ":" + newVersion + " ";
                }

                commandLine = String.format("java -jar /var/lib/pivy-importer-%s-all.jar --repo %s %s--replace %s", VERSION, REPO, packageString, replaceString);

            }
        }
        return commandLine;
    }

    public static void main(String[] args) {
        final String REPO = "/data";
        final String VERSION = "0.3.39";

        int maxThreads = 4;
        int minThreads = 2;
        int timeOutMillis = 30000;

        port(5639);
        threadPool(maxThreads, minThreads, timeOutMillis);

        post("/init", (request, response) -> {
            //init the repository

            JSONObject jsonObject = (JSONObject) readFile();

            JSONArray dependencies = (JSONArray) jsonObject.get("dependencies");
            JSONArray replace = (JSONArray) jsonObject.get("replace");

            String theStrings = readDependencies(VERSION, REPO, dependencies, replace);

            try {

                Runtime rt = Runtime.getRuntime();
                Process pr = rt.exec(theStrings);

            } catch (Exception ex) {
                Logger.getLogger(App.class.getName()).log(Level.SEVERE, null, ex);
                TestCase.fail(ex.getMessage());
            }

            response.status(201); // 201 Created
            response.type("application/json");
            String result = String.format("{ " +
                    "\t\"executed\": \"%s\" \n" +
                    "}", theStrings);
            return result;
        });


        post("/add", "application/json", (request, response) -> {
            String content = request.body();

            JSONParser parser = new JSONParser();
            Object obj = parser.parse(content);
            JSONObject jsonObject = (JSONObject) obj;

            JSONArray dependencies = (JSONArray) jsonObject.get("dependencies");
            JSONArray replace = (JSONArray) jsonObject.get("replace");
            String theStrings = readDependencies(VERSION, REPO, dependencies, replace);
            try {
                Runtime rt = Runtime.getRuntime();
                Process pr = rt.exec(theStrings);
            } catch (Exception ex) {
                Logger.getLogger(App.class.getName()).log(Level.SEVERE, null, ex);
                TestCase.fail(ex.getMessage());
            }
            String result = String.format("{ \n" +
                    "\t\"addedDependencies\": %s, \n" +
                    "\t\"addedReplace\": %s, \n" +
                    "\t\"executed\": \"%s\" \n" +
                    "}", dependencies, replace, theStrings);
            response.status(201); // 201 Created
            response.type("application/json");
            return result;
        });
    }


}