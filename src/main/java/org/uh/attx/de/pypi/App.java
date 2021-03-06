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

    private static Object readInit() {

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

    private static String buildCommand(String REPO, JSONArray dependencies, JSONArray replace, JSONArray forceDependencies) {

        String artifact = "/var/lib/pivy-importer-all.jar";
        String commandLine = "";
        String forcePackageString = "";
        String replaceString = "";

        String dependencyOption = "";
        String forceOption = "";
        String replaceOption = "";

        if (dependencies != null) {
            Iterator i = dependencies.iterator();
            while (i.hasNext()) {
                JSONObject dependency = (JSONObject) i.next();
                String name = (String) dependency.get("name");
                String version = (String) dependency.get("version");
                dependencyOption += name + ":" + version + " ";
            }
        }

        if (replace != null) {
            Iterator j = replace.iterator();
            while (j.hasNext()) {
                JSONObject replacing = (JSONObject) j.next();
                String name = (String) replacing.get("name");
                String oldVersion = (String) replacing.get("oldVersion");
                String newVersion = (String) replacing.get("newVersion");
                replaceString += name + ":" + oldVersion + "=" + name + ":" + newVersion + " ";
            }

            replaceOption = String.format("--replace %s", replaceString);
        }

        if (forceDependencies != null) {
            Iterator m = forceDependencies.iterator();
            while (m.hasNext()) {
                JSONObject forceDependency = (JSONObject) m.next();
                String forceName = (String) forceDependency.get("name");
                String forceVersion = (String) forceDependency.get("version");
                forcePackageString += forceName + ":" + forceVersion + " ";
            }

            forceOption = String.format("--force %s", forcePackageString);
        }

        commandLine = String.format("java -jar %s --repo %s %s %s %s ", artifact, REPO, dependencyOption, forceOption, replaceOption);

        return commandLine;
    }

    public static void main(String[] args) {
        final String REPO = "/data";

        int maxThreads = 6;
        int minThreads = 5;
        int timeOutMillis = 30000;

        port(5639);
        threadPool(maxThreads, minThreads, timeOutMillis);

        post("/requirements", "text/plain", (request, response) -> {
            // upload requirements.txt file

            String result = "";
            response.status(201); // 201 Created
            response.type("application/json");
            return result;
        });

        post("/init", (request, response) -> {
            //init the repository

            JSONObject jsonObject = (JSONObject) readInit();

            JSONArray dependencies = (JSONArray) jsonObject.get("dependencies");
            JSONArray replace = (JSONArray) jsonObject.get("replace");
            JSONArray force = (JSONArray) jsonObject.get("force");

            String theStrings = buildCommand(REPO, dependencies, replace, force);

            try {

                Runtime rt = Runtime.getRuntime();
                Process pr = rt.exec(theStrings);

            } catch (Exception ex) {
                Logger.getLogger(App.class.getName()).log(Level.SEVERE, null, ex);
                TestCase.fail(ex.getMessage());
            }

            response.status(200); // 200 Created
            response.type("application/json");
            String result = String.format("{ " +
                    "\t\"executed\": \"%s\" \n" +
                    "}", theStrings);
            return result;
        });


        post("/add", "application/json", (request, response) -> {
            // add the dependencies to the repository
            String content = request.body();

            JSONParser parser = new JSONParser();
            Object obj = parser.parse(content);
            JSONObject jsonObject = (JSONObject) obj;

            JSONArray dependencies = (JSONArray) jsonObject.get("dependencies");
            JSONArray replace = (JSONArray) jsonObject.get("replace");
            JSONArray force = (JSONArray) jsonObject.get("force");

            String theStrings = buildCommand(REPO, dependencies, replace, force);
            
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
                    "\t\"addedForce\": %s, \n" +
                    "\t\"executed\": \"%s\" \n" +
                    "}", dependencies, replace, force, theStrings);
            response.status(201); // 201 Created
            response.type("application/json");
            return result;
        });
    }


}