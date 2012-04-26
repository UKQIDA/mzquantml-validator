/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.liv.mzquantml.validator.rules;

import info.psidev.psi.pi.mzquantml._1_0.RatioListType;
import info.psidev.psi.pi.mzquantml._1_0.RatioType;
import java.util.List;

/**
 *
 * @author Da Qi
 * @institute University of Liverpool
 * @time Apr 26, 2012 3:03:40 PM
 */
public class NumeratorDenominatorRule {

    private boolean valid;
    
    public NumeratorDenominatorRule() {
        this.valid = true;
    }
    
    public void check(RatioListType ratioList){
        List<RatioType> ratioes = ratioList.getRatio();
        for (RatioType ratio: ratioes){
            Class numCls = ratio.getNumeratorRef().getClass();
            Class denCls = ratio.getDenominatorRef().getClass();
            if (!numCls.equals(denCls)){
                this.valid = false;
            }
        }
    }
    
    public boolean isValid(){
        return this.valid;
    }
}
