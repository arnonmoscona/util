package com.moscona.util;

import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;
import com.moscona.exceptions.InvalidArgumentException;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
// FIXME Missing unit test

/**
 * Created by Arnon on 5/6/2014.
 */
public class CsvHelper {
    public List<String[]> load(String path) throws IOException {
        return load(path,false);
    }

    public List<String[]> load(String path, boolean optional) throws IOException {
        if (optional && ! new File(path).exists()) {
            return new ArrayList<String[]>();
        }

        List<String[]> entries;
        FileReader fileReader = new FileReader(path);

        try {
            CSVReader reader = new CSVReader(fileReader);
            entries = reader.readAll();
        } finally {
            fileReader.close();
        }
        return entries;
    }

    /**
     * Takes a two column CSV, discards the headers and returns is as a map where the key is the first column and the
     * value is the second.
     * @param path
     * @param optional
     * @return
     * @throws IOException
     * @throws com.moscona.exceptions.InvalidArgumentException
     */
    public TreeMap<String,String> loadAsMap(String path, boolean optional, String[] expectedHeaders) throws IOException, InvalidArgumentException {
        List<String[]> contents = load(path,optional);
        return toTreeMap(path, contents, expectedHeaders);
    }

    public TreeMap<String,String> loadAsMap(String path, boolean optional) throws IOException, InvalidArgumentException {
        return loadAsMap(path,optional,null);
    }

    private TreeMap<String, String> toTreeMap(String path, List<String[]> contents) throws InvalidArgumentException {
        return toTreeMap(path,contents,null);
    }

    private TreeMap<String, String> toTreeMap(String path, List<String[]> contents, String[] expectedHeaders) throws InvalidArgumentException {
        if (contents==null||contents.size()==0) {
            return null;
        }
        String[] headers = contents.get(0);
        int startingLine = 0;
        if (headers.length != 2) {
            throw new InvalidArgumentException("Cannot load this file as map. There must be exactly two columns. " +
                    "Instead the header line length is "+headers.length+". Aborting loading of "+path);
        }
        if (expectedHeaders != null) {
            if (expectedHeaders.length != 2) {
                throw new InvalidArgumentException("Expected headers, when supplied, must be exactly 2 strings");
            }
            boolean allMatch = headers[0].trim().equals(expectedHeaders[0].trim()) && headers[1].trim().equals(expectedHeaders[1].trim());
            if (allMatch) {
                startingLine = 1;
            }
        }


        TreeMap<String,String> retval = new TreeMap<String,String>();
        for (int i=startingLine;i<contents.size();i++) {
            String[] line = contents.get(i);
            if (line==null||line.length==0) {
                continue;
            }
            if (line.length!=2) {
                throw new InvalidArgumentException("Cannot load this file as map. There must be exactly two columns. " +
                    "Instead encountered a line whose length is "+line.length+". Aborting loading of "+path);
            }
            retval.put(line[0],line[1]);
        }

        return retval;
    }

    public TreeMap<String,String> loadAsMap(String path) throws IOException, InvalidArgumentException {
        List<String[]> contents = load(path,false);
        return toTreeMap(path, contents);
    }

    public void validateHeaders(String[] headers, String[] expected, String fileTypeName) throws InvalidArgumentException {
        if (headers.length != expected.length) {
            throw new InvalidArgumentException("Invalid "+fileTypeName+". Wrong number of headers. Expected "+expected.length+" but got "+headers.length);
        }

        String errors = "";

        for (int i=0; i<headers.length; i++) {
            if (! headers[i].equals(expected[i])) {
                errors += "Wrong header in column "+(i+1)+" expected \""+expected[i]+"\" but got \"" + headers[i] + "\". ";
            }
        }

        if (! errors.equals("")) {
            throw new InvalidArgumentException("Invalid CSV headers. " + errors);
        }
    }

    public void writeAll(String path, String[] csvHeaders, List<String[]> output) throws IOException {
        CSVWriter writer = makeCsvWriter(path);
        try {
            writer.writeNext(csvHeaders);
            for (String[] line: output) {
                writer.writeNext(line);
            }
            writer.flush();
        }
        finally {
            writer.close();
            //fileWriter.close();
        }
    }

    @SuppressWarnings({"IOResourceOpenedButNotSafelyClosed"})
    private CSVWriter makeCsvWriter(String path) throws IOException {
        FileWriter fileWriter = new FileWriter(path);
        return new CSVWriter(fileWriter,',',CSVWriter.NO_QUOTE_CHARACTER,"\r\n");
    }

    @SuppressWarnings({"unchecked"})
    public void writeAll(String path, String header1, String header2, TreeMap output) throws IOException {
        CSVWriter writer = makeCsvWriter(path);
        try {
            writer.writeNext(new String[] {header1,header2});
            Map.Entry entry = output.firstEntry();
            while (entry!=null) {
                String key = entry.getKey().toString();
                writer.writeNext(new String[]{key, entry.getValue().toString()});
                entry = output.higherEntry(key); // unchecked
            }
        }
        finally {
            writer.close();
        }
    }

    public String cleanString(String str) {
        return str.replaceAll(","," ").replaceAll("\"", "'").trim();
    }

    public boolean isBlank(String[] line) {
        return line.length==0 || line.length==1 && StringUtils.isBlank(line[0]);
    }
}
