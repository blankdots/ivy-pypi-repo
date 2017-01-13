package org.uh.attx.de.pypi;

import static spark.Spark.*;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.util.Iterator;

public class App {

    public static void main(String[] args) {
        final String REPO = "/data";
        final String VERSION = "0.3.38";

        port(5639);

        post("/add", "application/json", (request, response) -> {
            String content = request.body();

            JSONParser parser = new JSONParser();
            Object obj = parser.parse(content);
            JSONObject jsonObject = (JSONObject) obj;

            JSONArray dependencies = (JSONArray) jsonObject.get("dependencies");
            JSONArray replace = (JSONArray) jsonObject.get("replace");
            String artifactVersion = (String) jsonObject.get("artifactVersion");
            String commandLine = "";
            String packageString = "";
            String replaceString = "";

            if (dependencies != null) {
                try {
                    if (artifactVersion == null) {
                        artifactVersion = VERSION;
                    }

                    Iterator i = dependencies.iterator();
                    while (i.hasNext()) {
                        JSONObject dependency = (JSONObject) i.next();
                        String name = (String) dependency.get("name");
                        String version = (String) dependency.get("version");
                        packageString += name + ":" + version + " ";
                    }

                    commandLine = String.format("java -jar /var/lib/pivy-importer-%s-all.jar --repo %s %s", artifactVersion, REPO, packageString);

                    if (replace != null) {
                        Iterator j = replace.iterator();

                        while (j.hasNext()) {
                            JSONObject replacing = (JSONObject) j.next();
                            String name = (String) replacing.get("name");
                            String oldVersion = (String) replacing.get("oldVersion");
                            String newVersion = (String) replacing.get("newVersion");
                            replaceString += name + ":" + oldVersion + "=" + name + ":" + newVersion + " ";
                        }

                        commandLine = String.format("java -jar /var/lib/pivy-importer-%s-all.jar --repo %s %s--replace %s", artifactVersion, REPO, packageString, replaceString);

                    }

                    Runtime rt = Runtime.getRuntime();
                    Process pr = rt.exec(commandLine);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            String result = String.format("{ \n" +
                    "\t\"addedDependencies\": %s, \n" +
                    "\t\"addedReplace\": %s, \n" +
                    "\t\"commandLine\": \"%s\" \n" +
                    "}", dependencies, replace, commandLine);
            response.status(201); // 201 Created
            response.type("application/json");
            return result;
        });

    }
}