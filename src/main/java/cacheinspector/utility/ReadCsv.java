package cacheinspector.utility;

import cacheinspector.core.InspectorCsv;
import cacheinspector.entity.EntityCsv;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.*;
import java.util.stream.Stream;

public class ReadCsv {

    private static final char DEFAULT_SEPARATOR = ',';
    private static final char DOUBLE_QUOTES = '"';
    private static final char DEFAULT_QUOTE_CHAR = DOUBLE_QUOTES;
    private static final String NEW_LINE = "\n";

    private boolean isMultiLine = false;
    private String pendingField = "";
    private String[] pendingFieldLine = new String[]{};

    public Map<String,List<EntityCsv>> readFile(File csvFile) throws Exception {
        return readFile(csvFile, 0);
    }

    public Map<String,List<EntityCsv>> readFile(File csvFile, int skipLine)
            throws Exception {

        Map<String,List<EntityCsv>> result = null;
        int indexLine = 0;
        String previousCck = null;

        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
            String line;

            while ((line = br.readLine()) != null) {

                if (indexLine++ <= skipLine) {
                    continue;
                }

                String[] csvLineInArray = parseLine(line);

                if (isMultiLine) {
                    pendingFieldLine = joinArrays(pendingFieldLine, csvLineInArray);
                } else {

                    if (pendingFieldLine != null && pendingFieldLine.length > 0) {
                        // joins all fields and add to list
                        //result.add(joinArrays(pendingFieldLine, csvLineInArray));
                        pendingFieldLine = new String[]{};
                    } else {
                        // if dun want to support multiline, only this line is required.
                        String cck = csvLineInArray[1];
                        if(previousCck==null){
                            result = new HashMap<>();
                            previousCck = cck;
                        }
                        String response = csvLineInArray[2];
                        int sequence = (int)Double.parseDouble(csvLineInArray[3]);
                        if(result.containsKey(cck)){
                            List<EntityCsv> listEntity = result.get(cck);
                            boolean find = false;
                            for(EntityCsv entity:listEntity){
                                if (entity.getSequence() == sequence) {
                                    find = true;
                                    System.out.println("Duplicate records cck: " + cck);
                                    break;
                                }
                            }
                            if(!find) {
                                EntityCsv entityCsv = new EntityCsv();
                                entityCsv.setResponse(response);
                                entityCsv.setSequence(sequence);
                                listEntity.add(entityCsv);
                                result.put(cck, listEntity);
                            }
                        }else{
                            if(!result.isEmpty()) {
                                List<EntityCsv> entityCsvs = result.get(previousCck);
                                try {
                                    String json = InspectorCsv.getBasket(entityCsvs);
                                    ObjectMapper mapper = new ObjectMapper();
                                    Map<String, Object> root = mapper.readValue(json, Map.class);
                                    InspectorCsv.readJson(previousCck, root);
                                } catch (Exception e) {
                                    System.out.println(e.getMessage() + " for this cck: " + previousCck);
                                }
                                result = new HashMap<>();
                                previousCck = cck;
                            }
                            List<EntityCsv> listEntity = new ArrayList<>();
                            EntityCsv entityCsv = new EntityCsv();
                            entityCsv.setResponse(response);
                            entityCsv.setSequence(sequence);
                            listEntity.add(entityCsv);
                            result.put(cck,listEntity);
                        }
                    }
                }
            }
        }

        return result;
    }

    public String[] parseLine(String line) throws Exception {
        return parseLine(line, DEFAULT_SEPARATOR);
    }

    public String[] parseLine(String line, char separator) throws Exception {
        return parse(line, separator, DEFAULT_QUOTE_CHAR).toArray(String[]::new);
    }

    private List<String> parse(String line, char separator, char quoteChar)
            throws Exception {
        List<String> result = new ArrayList<>();


        boolean inQuotes = false;
        boolean isFieldWithEmbeddedDoubleQuotes = false;

        StringBuilder field = new StringBuilder();

        for (char c : line.toCharArray()) {

            if (c == DOUBLE_QUOTES) {               // handle embedded double quotes ""
                if (isFieldWithEmbeddedDoubleQuotes) {

                    //if (field.length() > 0) {       // handle for empty field like "",""
                        field.append(DOUBLE_QUOTES);
                        isFieldWithEmbeddedDoubleQuotes = false;
                    //}

                } else {
                    isFieldWithEmbeddedDoubleQuotes = true;
                }
            } else {
                isFieldWithEmbeddedDoubleQuotes = false;
            }

            if (isMultiLine) {                      // multiline, add pending from the previous field
                field.append(pendingField).append(NEW_LINE);
                pendingField = "";
                inQuotes = true;
                isMultiLine = false;
            }

            if (c == quoteChar) {
                inQuotes = !inQuotes;
            } else {
                if (c == separator && !inQuotes) {  // if find separator and not in quotes, add field to the list
                    result.add(field.toString());
                    field.setLength(0);             // empty the field and ready for the next
                } else {
                    field.append(c);                // else append the char into a field
                }
            }

        }

        //line done, what to do next?
        if (inQuotes) {
            pendingField = field.toString();        // multiline
            isMultiLine = true;
        } else {
            result.add(field.toString());           // this is the last field
        }

        return result;

    }

    private String[] joinArrays(String[] array1, String[] array2) {
        return Stream.concat(Arrays.stream(array1), Arrays.stream(array2))
                .toArray(String[]::new);
    }
}
