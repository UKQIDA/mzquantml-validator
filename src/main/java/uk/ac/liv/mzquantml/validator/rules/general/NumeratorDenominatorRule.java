/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.liv.mzquantml.validator.rules.general;

import info.psidev.psi.pi.mzquantml._1_0.RatioListType;
import info.psidev.psi.pi.mzquantml._1_0.RatioType;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Level;
import uk.ac.liv.mzquantml.validator.utils.Message;

/**
 *
 * @author Da Qi @institute University of Liverpool @time Apr 26, 2012 3:03:40
 * PM
 */
public class NumeratorDenominatorRule {

    ArrayList<Message> msgs = new ArrayList<Message>();
    RatioListType ratioList;

    public NumeratorDenominatorRule() {
        this.ratioList = null;
    }

    public NumeratorDenominatorRule(RatioListType ratioList) {
        this.ratioList = ratioList;
    }

    public void check() {
        List<RatioType> ratioes = this.ratioList.getRatio();
        if (!ratioes.isEmpty()) {
            for (RatioType ratio : ratioes) {
                Class numCls = ratio.getNumeratorRef().getClass();
                Class denCls = ratio.getDenominatorRef().getClass();
                if (!numCls.equals(denCls)) {
                    msgs.add(new Message("In the RatioList, if the numerator is referencing a StudyVariable, "
                            + "the denominator MUST reference a StudyVariable", Level.INFO));
                    msgs.add(new Message("In the RatioList, if the numerator is referencing an Assay, "
                            + "the denominator MUST reference an Assay", Level.INFO));
                    msgs.add(new Message("Numerator and denominator are not referred to the same type in "
                            + "Ratio \"" + ratio.getId() + "\"\n", Level.ERROR));
                }
            }
        }
    }

    public List<Message> getMsgs() {
        return msgs;
    }
}
