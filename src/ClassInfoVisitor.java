import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.AnnotationExpr;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import com.github.javaparser.resolution.declarations.ResolvedMethodDeclaration;
import jxl.Workbook;
import jxl.write.Label;
import lombok.Getter;
import lombok.Setter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Getter
@Setter
public class ClassInfoVisitor extends VoidVisitorAdapter<Void> {

    XLSRecord record = new XLSRecord(CustomParser.currentRow);

    public void visit(ClassOrInterfaceDeclaration n, Void arg) {
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
        if (visible == "public") {
            record.setClass_Visibility(1);
        } else if (visible == "private") {
            record.setClass_Visibility(2);
        } else if (visible == "protected") {
            record.setClass_Visibility(3);
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
        record.setChildren(children.isEmpty() ? "0" : String.join(" , ",children));

        /* ------------------------------------------- constructor ----------------------------------------------- */
        List<String> constructors = new ArrayList<>();
        for (ConstructorDeclaration item : n.getConstructors()) {
            String str = "";
            str += (item.getClass().getName() + " - type : " + item.getAccessSpecifier() + " - parameters :  " + item.getParameters().toString());
            constructors.add(str);
        }
        record.setConstructor(String.join(" , ",constructors));

        /* ----------------------------------------------- fields ------------------------------------------------ */
        List<String> fields = new ArrayList<>();
        for (FieldDeclaration item : n.getFields()) {
            String str = "";
            boolean isDefault = item.getAccessSpecifier().equals("NONE");
            str += (item.getClass().getName() + " - type : " + (isDefault ? "DEFAULT" : item.getAccessSpecifier()));
            fields.add(str);
        }
        record.setFields(String.join(" , ",fields));

        /* ----------------------------------------------- methods ----------------------------------------------- */
        List<String> methods = new ArrayList<>();
        for (MethodDeclaration item : n.getMethods()) {
            String str = "";
            str += (item.getName().asString() + " - return type : " + item.getType().asString() + " - parameters : " + item.getParameters().toString());
            methods.add(str);
        }
        record.setMethods(String.join(" , ",methods));

        /* ----------------------- override  *  has static/final/abstract  method-------------------------------- */
        List<String> overriddenMethods = new ArrayList<>();
        List<String> staticMethods = new ArrayList<>();
        List<String> finalMethods = new ArrayList<>();
        List<String> abstractMethods = new ArrayList<>();
        for (MethodDeclaration method : n.getMethods()) {
            Optional<AnnotationExpr> overrideAnnotation = method.getAnnotationByName("Override");
            if (overrideAnnotation.isPresent()) {
                overriddenMethods.add( method.getNameAsString() + " - parameters : " + method.getParameters().toString() + " - return type : " + method.getType());
            }
            if (method.isAbstract()){
                abstractMethods.add(method.getNameAsString());
            }
            else if(method.isFinal()){
                finalMethods.add(method.getNameAsString());
            }
            else if(method.isStatic()){
                staticMethods.add(method.getNameAsString());
            }
        }
        record.setOverride(String.join(" , ",overriddenMethods));
        record.setHas_static_method(String.join(" , ",staticMethods));
        record.setHas_abstract_method(String.join(" , ",abstractMethods));
        record.setHas_final_method(String.join(" , ",finalMethods));

        super.visit(n, arg);

    }

}
