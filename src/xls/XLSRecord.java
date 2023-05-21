package xls;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class XLSRecord {
    final private int row;
    private String Project_Name = ""; // col: 0
    private String Package_Name = "";
    private String Class_Name = "";
    private int Class_Type = 1;
    private int Class_Visibility = 1;
    private int Class_is_Abstract = 0;
    private int Class_is_Static = 0;
    private int Class_is_Final = 0;
    private String Class_is_Interface = "false";
    private String Extends = "0";
    private String Implements = "0";
    private String Children = "0";
    private String Constructor = "";
    private String Fields = "";
    private String Methods = "";
    private String Override = "";
    private String has_static_method = "";
    private String has_final_method = "";
    private String has_abstract_method = "";
    private String association="";
    private String aggregation ="";
    private String delegation = "";
    private String composition = "";
    private String instantiation = "";
    private String APIs = "";

    public XLSRecord(int row) {
        this.row = row;
    }
}
