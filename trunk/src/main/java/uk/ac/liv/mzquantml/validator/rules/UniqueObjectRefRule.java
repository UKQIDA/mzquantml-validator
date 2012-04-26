/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.liv.mzquantml.validator.rules;

import info.psidev.psi.pi.mzquantml._1_0.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 *
 * @author Da Qi
 * @time 00:58:46 26-Apr-2012
 * @institution University of Liverpool
 */
public class UniqueObjectRefRule {

    private static List<String> message;

    public UniqueObjectRefRule() {
        message = new ArrayList<String>();
    }

    public void check(QuantLayerType quantLayer) {
        List<RowType> rowList = quantLayer.getDataMatrix().getRow();
        if (!rowList.isEmpty() || (rowList == null)) {
            HashSet objectRefSet = new HashSet();
            for (RowType row : rowList) {
                if (!objectRefSet.add(row.getObjectRef())) {  //need adaptor
                    String msg = "Duplicate object_ref: " + row.getObjectRef().toString();
                    message.add(msg);
                }
            }
        }
    }

    public void check(GlobalQuantLayerType globalQuantLayer) {
        List<RowType> rowList = globalQuantLayer.getDataMatrix().getRow();
        if (!rowList.isEmpty() || (rowList == null)) {
            HashSet objectRefSet = new HashSet();
            for (RowType row : rowList) {
                if (!objectRefSet.add(row.getObjectRef())) {  //need adaptor
                    String msg = "Duplicate object_ref: " + row.getObjectRef().toString() + "\n";
                    message.add(msg);
                }
            }
        }
    }

    public List<String> getMessage() {
        return message;
    }
    
    public void printMessage(){
        for(String s: message){
            System.out.println(s + "\n");
        }
    }
}
