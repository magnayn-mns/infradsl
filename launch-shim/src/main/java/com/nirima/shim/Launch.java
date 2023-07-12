package com.nirima.shim;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintStream;

public class Launch {
    public static void main(String[] args) {
        try {

            dumpProps();

            //String pathString = "/Users/magnayn/dev/magnayn/untitled/scripting-host/build/distributions/scripting-host/bin/scripting-host";
            String pathString = "/Users/magnayn/dev/magnayn/untitled/mini-host/build/distributions/mini-host/bin/mini-host";

            File path;
            if( args.length > 0 )
                path = new File(args[0]);
            else
                path = new File(pathString);

            System.out.println("Path: " + path.getAbsolutePath());

            var p = new ProcessBuilder(path.getAbsolutePath() );
            //p.directory(path.getParentFile());

            Process process = p.start();
            int code = process.waitFor();
            /*BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            StringBuilder builder = new StringBuilder();
            String line = null;
            while ( (line = reader.readLine()) != null) {
                builder.append(line);
            }
            String result = builder.toString();
            System.out.print(result);
            */

            System.out.println("end of script execution : " + code);
            System.exit(code);

        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    private static void dumpProps() throws FileNotFoundException {
        File f = new File("/tmp/props.txt");
        var fos = new FileOutputStream(f);
        var writer = new PrintStream(fos);
        System.getenv().entrySet().forEach( bi -> {
            writer.print(bi.getKey());
            writer.print("=");
            writer.println(bi.getValue());
        });
        writer.flush();
        writer.close();
    }
}