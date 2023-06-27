package parser;

import com.github.javaparser.ParserConfiguration;
import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import graph.Graph;
import graph.GraphVisualizer;
import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import pattern_detection.MainBodyGraphs;
import xls.DirExplorer;
import xls.XLSRecord;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.apache.batik.anim.timing.Trace.exit;


public class CustomParser {

    static WritableWorkbook workbook;// Create a new writable workbook
    static WritableSheet sheet;// Create a new sheet in the workbook
    public static int currentRow = 1;
    public static List<ClassOrInterfaceDeclaration> classDeclarations = new ArrayList<>();

    public static void main(String[] args) throws IOException {
        // Set up JavaParser configuration
        ParserConfiguration config = new ParserConfiguration();
        JavaParser javaParser = new JavaParser(config);

        //Define the root directory of project
        //1
//        String projectName = "QuickUML 2001";
//        String filePath = "projects/1 - QuickUML 2001";

        //2
//        String filePath = "projects/2 - Lexi v0.1.1 alpha";
//        String projectName = "Lexi v0.1.1 alpha";

        //3
//        String filePath = "projects/3 - JRefactory v2.6.24";
//        String projectName = " JRefactory";

        //4
//        String filePath = "projects/4 - Netbeans v1.0.x (1)";
//        String projectName = "Netbeans";

        //5
//        String filePath = "projects/5 - JUnit v3.7";
//        String projectName = "JUnit";

        //6
//        String filePath = "projects/6 - JHotDraw v5.1";
//        String projectName = "JHotDraw";

        //7
//        String filePath = "projects/7 - MapperXML v1.9.7";
//        String projectName = "MapperXML";

        //8
//        String filePath = "projects/8 - Nutch v0.4";
//        String projectName = "Nutch";

        //9
//        String filePath = "projects/9 - PMD v1.8";
//        String projectName = "PMD";

        //10 - micro
//        String filePath = "micro_projects/axon-server-se-master";
//        String projectName = "Axon";

        //11 - micro
//        String filePath = "micro_projects/dropwizard-release-2.1.x";
//        String projectName = "dropwizard";

        //12
//        String filePath = "micro_projects/axon-server-se-master";
//        String projectName = "axon-server-se-master";

        //13 --> has no java file :?
//        String filePath = "micro_projects/generator-jhipster-main";
//        String projectName = "generator-jhipster-main";

        //14
//        String filePath = "micro_projects/hazelcast-master";
//        String projectName = "hazelcast-master";

        //15
//        String filePath = "micro_projects/playframework-main";
//        String projectName = "playframework-main";

        //16
//        String filePath = "micro_projects/quarkus-main";
//        String projectName = "quarkus-main";

        //17
//        String filePath = "micro_projects/servicecomb-java-chassis-master";
//        String projectName = "servicecomb-java-chassis-master";

        //18
//        String filePath = "micro_projects/spring-cloud-netflix-main";
//        String projectName = "spring-cloud-netflix-main";

        //19
//        String filePath = "micro_projects/spring-cloud-sleuth-3.1.x";
//        String projectName = "spring-cloud-sleuth-3.1.x";

        //20
        String filePath = "micro_projects/vert.x-master";
        String projectName = "vert.x-master";


        File projectRoot = new File(filePath);
        Graph graph = new Graph();

        try {
            workbook = Workbook.createWorkbook(new File("outputs/" + projectName + ".xls"));
            sheet = workbook.createSheet("sheet", 0);
            initializeSheet();

            // Use xls.DirExplorer to process all Java files in the directory
            new DirExplorer((level, path, file) -> path.endsWith(".java"), (level, path, file) -> {
                System.out.println("Parsing: " + path);
                try {
                    // Parse the Java file and obtain the CompilationUnit
                    CompilationUnit cu;
                    if (javaParser.parse(file).getResult().isPresent()) {
                        cu = javaParser.parse(file).getResult().get();

                        // Process the CompilationUnit (e.g., analyze, modify, or generate code)
                        // Write the extracted information to an .xlsx file
                        CustomVisitor customVisitor = new CustomVisitor(graph);
                        customVisitor.visit(cu, null); //class related columns
                        customVisitor.getRecord().setPackage_Name(cu.getPackageDeclaration().isPresent() ? cu.getPackageDeclaration().get().getName().asString() : ""); //package name
                        customVisitor.getRecord().setProject_Name(projectName);
                        writeToXlsx(customVisitor.getRecord());
                        // Find all ClassOrInterfaceDeclaration objects --> used for finding children of classes
                        classDeclarations.addAll(cu.findAll(ClassOrInterfaceDeclaration.class));
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }).explore(projectRoot);
            // Write and close the workbook
            workbook.write();
            workbook.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // graph visualization -> phase 2 -> is commented to get better performance while checking phase 3
//        GraphVisualizer graphVisualizer = new GraphVisualizer(projectName,graph);
//        graphVisualizer.visualize();

        //pattern detection
        // TODO enrich the graph
        MainBodyGraphs bodies = new MainBodyGraphs();
        if(graph.findSubgraph(bodies.mediator)){
            System.out.println("\n\nFound Mediator\n\n");;
        }


        //forcing application to end
        System.exit(0);
    }

    private static void writeToXlsx(XLSRecord record) throws IOException {
        ArrayList<String> contents = new ArrayList<>(Arrays.asList(record.getProject_Name(), record.getPackage_Name(), record.getClass_Name(), String.valueOf(record.getClass_Type()), String.valueOf(record.getClass_Visibility()), String.valueOf(record.getClass_is_Abstract()), String.valueOf(record.getClass_is_Static()), String.valueOf(record.getClass_is_Final()), record.getClass_is_Interface(), record.getExtends(), record.getImplements(), record.getChildren(), record.getConstructor(), record.getFields(), record.getMethods(), record.getOverride(), record.getHas_static_method(), record.getHas_final_method(), record.getHas_abstract_method(), record.getAssociation(), record.getAggregation(), record.getDelegation(), record.getComposition(), record.getInstantiation(), record.getAPIs()));
        for (int i = 0; i < contents.size(); i++) {
            Label label = new Label(i, currentRow, contents.get(i));
            try {
                sheet.addCell(label);
            } catch (WriteException e) {
                throw new RuntimeException(e);
            }
        }
        currentRow++;
    }

    private static void initializeSheet() {
        ArrayList<String> contents = new ArrayList<>(Arrays.asList("Project_Name", "Package_Name", "Class_Name", "Class_Type", "Class_Visibility", "Class_is_Abstract", "Class_is_Static", "Class_is_Final", "Class_is_Interface", "Extends", "Implements", "Children", "Constructor", "Fields", "Methods", "Override", "has_static_method", "has_final_method", "has_abstract_method", "association", "aggregation", "delegation", "composition", "instantiation", "APIs"));
        for (int i = 0; i < contents.size(); i++) {
            Label label = new Label(i, 0, contents.get(i));
            try {
                sheet.addCell(label);
            } catch (WriteException e) {
                throw new RuntimeException(e);
            }
        }
    }
}

