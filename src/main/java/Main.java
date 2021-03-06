import fr.curie.cd2sbgnml.Cd2SbgnmlScript;
import fr.curie.cd2sbgnml.Sbgnml2CdScript;

import java.io.*;

import static spark.Spark.*;

public class Main {
    public static void main(String[] args) {

        System.setProperty("org.eclipse.jetty.util.log.class", "org.eclipse.jetty.util.log.StdErrLog");
        System.setProperty("org.eclipse.jetty.LEVEL", "OFF");

        if (args.length != 0)
            port(Integer.parseInt(args[0]));

        options("/*",
                (request, response) -> {

                    String accessControlRequestHeaders = request
                            .headers("Access-Control-Request-Headers");
                    if (accessControlRequestHeaders != null) {
                        response.header("Access-Control-Allow-Headers",
                                accessControlRequestHeaders);
                    }

                    String accessControlRequestMethod = request
                            .headers("Access-Control-Request-Method");
                    if (accessControlRequestMethod != null) {
                        response.header("Access-Control-Allow-Methods",
                                accessControlRequestMethod);
                    }

                    return "OK";
                });

        before((request, response) -> response.header("Access-Control-Allow-Origin", "*"));

        post("/cd2sbgnml", "application/xml", (req, res) -> {
            String input = req.body();

            PrintWriter out = new PrintWriter("input.xml");
            out.println(input);
            out.close();
            Cd2SbgnmlScript.convert("input.xml", "output.sbgn");

            String output = readFile("output.sbgn");
            File inputFile = new File("input.xml");
            inputFile.delete();
            File outputFile = new File("output.sbgn");
            outputFile.delete();

            return output;
        });

        post("/sbgnml2cd", "application/xml", (req, res) -> {
            String inputString = req.body();

            PrintWriter out = new PrintWriter("input.sbgn");
            out.println(inputString);
            out.close();
            Sbgnml2CdScript.convert("input.sbgn", "output.xml");

            String output = readFile("output.xml");
            File inputFile = new File("input.sbgn");
            inputFile.delete();
            File outputFile = new File("output.xml");
            outputFile.delete();

            return output;
        });
    }

    private static String readFile(String file) throws IOException {
        String line;
        StringBuilder stringBuilder = new StringBuilder();
        String ls = System.getProperty("line.separator");

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);
                stringBuilder.append(ls);
            }

            return stringBuilder.toString();
        }
    }
}
