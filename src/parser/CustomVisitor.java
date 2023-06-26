package parser;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.*;
import com.github.javaparser.ast.expr.*;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.type.Type;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.MethodCallExpr;
import graph.CustomEdge;
import graph.Graph;
import graph.CustomNode;

import xls.XLSRecord;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import lombok.Getter;

@Getter
public class CustomVisitor extends VoidVisitorAdapter<Void> {

    private XLSRecord record = new XLSRecord(CustomParser.currentRow);
    private CustomNode current_node;
    Graph graph;

    public CustomVisitor( Graph graph){
        this.graph = graph;
    }

    public void visit(ClassOrInterfaceDeclaration n, Void arg) {
        class_analyze(n);
        relationship_analyze(n);
        graph.addNode(current_node); //adding Node after analyzing class and its relationship with other classes (Nodes)
        super.visit(n, arg);
    }

    private void class_analyze(ClassOrInterfaceDeclaration n) {
        /* -------------------------------------------- class name ---------------------------------------------- */
        String className = n.getNameAsString();
        record.setClass_Name(className);
        current_node = graph.newNode(className);

        /* -------------------------------------------- class type ---------------------------------------------- */
        if (n.isInnerClass()) {
            record.setClass_Type(3);
        } else if (n.isInterface()) {
            record.setClass_Type(2);
        } else {
            record.setClass_Type(1);
        }

        /* -------------------------------------------- class visibility ----------------------------------------- */
        String visible = n.getAccessSpecifier().asString();
        switch (visible) {
            case "public" -> record.setClass_Visibility(1);
            case "private" -> record.setClass_Visibility(2);
            case "protected" -> record.setClass_Visibility(3);
        }

        /* -------------------------------------------- class is abstract ---------------------------------------- */
        record.setClass_is_Abstract(n.isAbstract() ? 1 : 0);

        /* -------------------------------------------- class is final ------------------------------------------- */
        record.setClass_is_Final(n.isFinal() ? 1 : 0);

        /* ------------------------------------------- class is static ------------------------------------------- */
        record.setClass_is_Static(n.isStatic() ? 1 : 0);

        /* -------------------------------------------- class is interface --------------------------------------- */
        record.setClass_is_Interface(n.isInterface() ? "true" : "false");

        /* ----------------------------------------------- extends ----------------------------------------------- */
        List<ClassOrInterfaceType> extendsList = n.getExtendedTypes();
        record.setExtends(extendsList.isEmpty() ? "0" : extendsList.toString());

        /* ----------------------------------------------- implements -------------------------------------------- */
        List<ClassOrInterfaceType> implementsList = n.getImplementedTypes();
        record.setImplements(implementsList.isEmpty() ? "0" : implementsList.toString());

        /* ----------------------------------------------- children ---------------------------------------------- */
        List<ClassOrInterfaceDeclaration> children = CustomParser.classDeclarations.stream().filter(classDeclaration -> classDeclaration.getExtendedTypes().stream().map(ClassOrInterfaceType::getNameAsString).anyMatch(name -> name.equals(n.getNameAsString()))).toList();
        record.setChildren(children.stream().map(ClassOrInterfaceDeclaration::getNameAsString).collect(Collectors.joining(" , \n")));

        /* ------------------------------------------- constructor ----------------------------------------------- */
        List<String> constructors = new ArrayList<>();
        for (ConstructorDeclaration item : n.getConstructors()) {
            String str = "";
            str += (item.getNameAsString() + " - type : " + item.getAccessSpecifier() + " - parameters :  " + item.getParameters().toString());
            constructors.add(str);
        }
        record.setConstructor(String.join(" , \n", constructors));

        /* ----------------------------------------------- fields ------------------------------------------------ */
        List<String> fields = new ArrayList<>();
        n.findAll(FieldDeclaration.class).forEach(item -> {
            String str = "";
            boolean isDefault = item.getAccessSpecifier().toString().equals("NONE");
            String type = item.getAccessSpecifier().asString();
            List<VariableDeclarator> variables = item.getVariables();
            for (VariableDeclarator var : variables) {
                str += (var.getNameAsString() + " - type : " + (isDefault ? "default" : type));
                fields.add(str);
            }
        });
        record.setFields(String.join(" , \n", fields));

        /* ----------------------------------------------- methods ----------------------------------------------- */
        /* ----------------------- override  *  has static/final/abstract  method--------------------------------- */
        List<String> methods = new ArrayList<>();
        List<String> overriddenMethods = new ArrayList<>();
        List<String> staticMethods = new ArrayList<>();
        List<String> finalMethods = new ArrayList<>();
        List<String> abstractMethods = new ArrayList<>();
        for (MethodDeclaration method : n.getMethods()) {
            Optional<AnnotationExpr> overrideAnnotation = method.getAnnotationByName("Override");
            methods.add(method.getNameAsString() + " - return type : " + method.getType().asString() + " - parameters : " + method.getParameters().toString());
            if (overrideAnnotation.isPresent()) {
                overriddenMethods.add(method.getNameAsString() + " - parameters : " + method.getParameters().toString() + " - return type : " + method.getType());
            }
            if (method.isAbstract()) {
                abstractMethods.add(method.getNameAsString());
            } else if (method.isFinal()) {
                finalMethods.add(method.getNameAsString());
            } else if (method.isStatic()) {
                staticMethods.add(method.getNameAsString());
            }
            //check api calls for microservice projects
            apiAnalyze(method);
        }
        record.setMethods(String.join(" , \n", methods));
        record.setOverride(String.join(" , \n", overriddenMethods));
        record.setHas_static_method(String.join(" , \n", staticMethods));
        record.setHas_abstract_method(String.join(" , \n", abstractMethods));
        record.setHas_final_method(String.join(" , \n", finalMethods));
    }

    private void apiAnalyze(MethodDeclaration md) {
        List<String> apiEndpoints = new ArrayList<>();
        /*--------------------------------------------GET--------------------------------------------*/
        if (md.getAnnotationByName("GET").isPresent()) {
            apiEndpoints.add("@GET annotated method: " + md.getNameAsString());
        }
        /*-------------------------------------------POST-------------------------------------------*/
        if (md.getAnnotationByName("POST").isPresent()) {
            apiEndpoints.add("@POST annotated method: " + md.getNameAsString());
        }
        /*--------------------------------------------PUT--------------------------------------------*/
        if (md.getAnnotationByName("PUT").isPresent()) {
            apiEndpoints.add("@PUT annotated method: " + md.getNameAsString());
        }
        /*------------------------------------------DELETE-------------------------------------------*/
        if (md.getAnnotationByName("DELETE").isPresent()) {
            apiEndpoints.add("@DELETE annotated method: " + md.getNameAsString());
        }
        record.setAPIs(apiEndpoints.toString());
    }

    // reminder for later : .class is to specify we only want class Nodes
    private void relationship_analyze(ClassOrInterfaceDeclaration n) {
        List<String> associations = new ArrayList<>();
        List<String> delegations = new ArrayList<>();
        List<String> instantiations = new ArrayList<>();
        List<String> compositions = new ArrayList<>();
        List<String> aggregations = new ArrayList<>();

        for (ClassOrInterfaceDeclaration classDeclaration : n.findAll(ClassOrInterfaceDeclaration.class)) {
            for (FieldDeclaration field : classDeclaration.getFields()) {
                // Association (any relationship)
                if (field.getElementType().isReferenceType()) {
                    associations.add(classDeclaration.getNameAsString() + " -> " + field.getElementType().asReferenceType().toString());
                    current_node.addEdge(new CustomEdge(current_node, graph.newNode(field.getElementType().asReferenceType().toString()), 2, 11, 13));
                }

                // Delegation
                if (field.getElementType() instanceof ClassOrInterfaceType fieldType) {
                    for (MethodDeclaration method : classDeclaration.getMethods()) {
                        for (MethodCallExpr methodCall : method.findAll(MethodCallExpr.class)) {
                            if (methodCall.getScope().isPresent() && isDelegation(fieldType, methodCall.getScope().get())) {
                                // Delegation detected: method in classDeclaration forwards a call to a method in fieldType
                                String str = classDeclaration.getNameAsString() + " -> " + fieldType;
                                if (!delegations.contains(str)) {
                                    delegations.add(str);
                                    current_node.addEdge(new CustomEdge(current_node, graph.newNode(fieldType.getElementType().asReferenceType().toString()), 5));
                                }
                            }
                        }
                        // also check for method calls to inherited classes
                        for (ClassOrInterfaceType superClass : classDeclaration.getExtendedTypes()) {
                            for (MethodCallExpr methodCall : method.findAll(MethodCallExpr.class)) {
                                if (methodCall.getScope().isPresent() && isDelegation(superClass, methodCall.getScope().get())) {
                                    // Delegation detected: method in classDeclaration forwards a call to a method in superClass
                                    String str = classDeclaration.getNameAsString() + " -> " + superClass;
                                    if (!delegations.contains(str)) {
                                        delegations.add(str);
                                        current_node.addEdge(new CustomEdge(current_node, graph.newNode(superClass.getNameAsString()), 5));
                                    }
                                }
                            }
                        }
                    }
                }

                // Instantiation - direct : new keyword
                find_instance_relationship(classDeclaration, instantiations);

                //Composition - Aggregation
                if (!isPrimitive(field.getElementType())) {
                    String otherClassName = field.getElementType().asReferenceType().toString();
                    if (field.getVariables().stream().anyMatch(v -> v.getInitializer().isPresent())) {
                        //If the field type is a non-primitive type and the field is initialized within the class, consider it as Composition.
                        compositions.add(otherClassName);
                        current_node.addEdge(new CustomEdge(current_node, graph.newNode(otherClassName), 3 , 5 , 7));
                    } else {
                        //If the field type is a non-primitive type and the field is not initialized within the class, consider it as Aggregation.
                        aggregations.add(otherClassName);
                        current_node.addEdge(new CustomEdge(current_node, graph.newNode(otherClassName), 7,5,7));
                    }
                }

                // Inheritance -> phase 3 (it was needed for paper 2 proposed method)
                List<ClassOrInterfaceType> extendedTypes = n.getExtendedTypes();
                for (ClassOrInterfaceType extendedType : extendedTypes) {
                    String parentClassName = extendedType.getNameAsString();
                    current_node.addEdge(new CustomEdge( graph.newNode(parentClassName),current_node, 0,2,3));
                }
                List<ClassOrInterfaceType> implementedTypes = n.getImplementedTypes();
                for (ClassOrInterfaceType implementedType : implementedTypes) {
                    String interfaceName = implementedType.getNameAsString();
                    current_node.addEdge(new CustomEdge( graph.newNode(interfaceName),current_node, 0,2,3));
                }
            }
        }

        record.setAggregation(String.join(" , \n", associations));
        record.setDelegation(String.join(" , \n", delegations));
        record.setInstantiation(String.join(" , \n", instantiations));
        record.setAggregation(String.join(" , \n", aggregations));
        record.setComposition(String.join(" , \n", compositions));
    }

    private boolean isDelegation(ClassOrInterfaceType fieldType, Expression scope) {
        if (scope instanceof NameExpr) {
            String scopeName = ((NameExpr) scope).getName().toString();
            String fieldName = fieldType.getName().toString();
            return scopeName.equals(fieldName);
        }
        return false;
    }

    private boolean isPrimitive(Type type) {
        return type.isPrimitiveType() || type.isPrimitiveType() || type.toString().equals("String");
    }

    private void find_instance_relationship(Node node, List<String> relatedClasses) {
        //direct
        if (node instanceof ObjectCreationExpr objectCreationExpr) {
            String className = objectCreationExpr.getType().asString();
            if (!relatedClasses.contains(className)) {
                relatedClasses.add(className);
            }
        }
        //indirect
        for (Node child : node.getChildNodes()) {
            find_instance_relationship(child, relatedClasses);
        }
    }
}
