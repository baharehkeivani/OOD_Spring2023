import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.*;
import com.github.javaparser.ast.expr.AnnotationExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.expr.SimpleName;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.type.Type;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Getter
@Setter
public class ClassVisitor extends VoidVisitorAdapter<Void> {

    XLSRecord record = new XLSRecord(CustomParser.currentRow);

    public void visit(ClassOrInterfaceDeclaration n, Void arg) {
        class_analyze(n);
        relationship_analyze(n);
        super.visit(n, arg);
    }

    private void class_analyze(ClassOrInterfaceDeclaration n) {
        /* -------------------------------------------- class name ---------------------------------------------- */
        record.setClass_Name(n.getClass().getName());

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
        List<String> children = new ArrayList<>();
        for (Node item : n.getChildNodes())
            children.add(item.getClass().getName());
        record.setChildren(children.isEmpty() ? "0" : String.join(" , \n", children));

        /* ------------------------------------------- constructor ----------------------------------------------- */
        List<String> constructors = new ArrayList<>();
        for (ConstructorDeclaration item : n.getConstructors()) {
            String str = "";
            str += (item.getClass().getName() + " - type : " + item.getAccessSpecifier() + " - parameters :  " + item.getParameters().toString());
            constructors.add(str);
        }
        record.setConstructor(String.join(" , \n", constructors));

        /* ----------------------------------------------- fields ------------------------------------------------ */
        List<String> fields = new ArrayList<>();
        for (FieldDeclaration item : n.getFields()) {
            String str = "";
            boolean isDefault = item.getAccessSpecifier().toString().equals("NONE");
            str += (item.getClass().getName() + " - type : " + (isDefault ? "DEFAULT" : item.getAccessSpecifier()));
            fields.add(str);
        }
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
            methods.add(method.getName().asString() + " - return type : " + method.getType().asString() + " - parameters : " + method.getParameters().toString());
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
        }
        record.setMethods(String.join(" , \n", methods));
        record.setOverride(String.join(" , \n", overriddenMethods));
        record.setHas_static_method(String.join(" , \n", staticMethods));
        record.setHas_abstract_method(String.join(" , \n", abstractMethods));
        record.setHas_final_method(String.join(" , \n", finalMethods));
    }

    private void relationship_analyze(ClassOrInterfaceDeclaration n) {
        List<String> associations = new ArrayList<>();
        List<String> delegations = new ArrayList<>();
        List<String> instantiations = new ArrayList<>();
        List<String> compositions = new ArrayList<>();
        List<String> aggregations = new ArrayList<>();
        // reminder for later : .class is to specify we only want class Nodes
        for (ClassOrInterfaceDeclaration classDeclaration : n.findAll(ClassOrInterfaceDeclaration.class)) {
            for (FieldDeclaration field : classDeclaration.getFields()) {
                // Association (any relationship)
                if (field.getElementType().isReferenceType()) {
                    associations.add( classDeclaration.getNameAsString() + " -> " + field.getElementType().asReferenceType().toString());
                }
                // Delegation
                if (field.getElementType() instanceof ClassOrInterfaceType fieldType) {
                    SimpleName fieldName = field.getVariable(0).getName();
                    for (MethodDeclaration method : classDeclaration.getMethods()) {
                        for (MethodCallExpr methodCall : method.findAll(MethodCallExpr.class)) {
                            if (methodCall.getScope().isPresent() && methodCall.getScope().get().toString().equals(fieldName.toString())) {
                                // Delegation detected: method in classDeclaration forwards a call to a method in fieldType
                                String str = classDeclaration.getName() + " -> " + fieldType;
                                if (!delegations.contains(str)) {
                                    delegations.add(str);
                                }
                            }
                        }
                    }
                }
                // Instantiation - direct : new keyword
                find_instance_relationship(classDeclaration, instantiations);
                //Composition - Aggregation
                if (!isPrimitive(field.getElementType())) {
                    if (field.getVariables().stream().anyMatch(v -> v.getInitializer().isPresent())) {
                        //If the field type is a non-primitive type and the field is initialized within the class, consider it as Composition.
                        compositions.add(field.toString());
                    } else {
                        //If the field type is a non-primitive type and the field is not initialized within the class, consider it as Aggregation.
                        aggregations.add(field.toString());
                    }
                }
            }
        }
        record.setAggregation(String.join(" , \n",associations));
        record.setDelegation(String.join(" , \n",delegations));
        record.setInstantiation(String.join(" , \n",instantiations));
        record.setAggregation(String.join(" , \n",aggregations));
        record.setComposition(String.join(" , \n",compositions));
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
