/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package uk.ac.liv.mzquantml.validator.rules.general;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import javax.xml.bind.JAXBException;
import org.apache.log4j.Level;
import uk.ac.liv.jmzqml.model.mzqml.Assay;
import uk.ac.liv.jmzqml.model.mzqml.Ratio;
import uk.ac.liv.jmzqml.model.mzqml.RatioList;
import uk.ac.liv.jmzqml.model.mzqml.StudyVariable;
import uk.ac.liv.jmzqml.xml.io.MzQuantMLUnmarshaller;
import uk.ac.liv.mzquantml.validator.utils.Message;

/**
 *
 * @author Da Qi
 * @institute University of Liverpool
 * @time Apr 26, 2012 3:03:40 PM
 */
public class NumeratorDenominatorRule {

    ArrayList<Message> msgs = new ArrayList<Message>();
    RatioList ratioList;
    MzQuantMLUnmarshaller unmarshaller;

    public NumeratorDenominatorRule() {
        this.ratioList = null;
    }

    public NumeratorDenominatorRule(RatioList ratioList,
                                    MzQuantMLUnmarshaller um) {
        this.ratioList = ratioList;
        this.unmarshaller = um;
    }

    public void check() {
        boolean numIsAssay;
        boolean numIsStudy;
        boolean denIsAssay;
        boolean denIsStudy;

        List<Ratio> ratioes = this.ratioList.getRatio();
        if (!ratioes.isEmpty()) {

            for (Ratio ratio : ratioes) {
                numIsAssay = false;
                numIsStudy = false;
                denIsAssay = false;
                denIsStudy = false;
                String numRef = ratio.getNumeratorRef();
                String denRef = ratio.getDenominatorRef();

                // try unmarshall numerator to Assay
                try {
                    Assay numAssay = this.unmarshaller.unmarshal(uk.ac.liv.jmzqml.model.mzqml.Assay.class, numRef);

                    if (numAssay != null) {
                        numIsAssay = true;
                    }

                }
                catch (JAXBException ex) {
                    Logger.getLogger(NumeratorDenominatorRule.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
                }
                catch (IllegalArgumentException ilArEx) {
                    //msgs.add(new Message(ilArEx.getMessage(), Level.ERROR));
                }

                // try unmarshall denominator to Assay
                try {
                    Assay denAssay = this.unmarshaller.unmarshal(uk.ac.liv.jmzqml.model.mzqml.Assay.class, denRef);

                    if (denAssay != null) {
                        denIsAssay = true;
                    }
                }
                catch (JAXBException ex) {
                    Logger.getLogger(NumeratorDenominatorRule.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
                }
                catch (IllegalArgumentException ilArEx) {
                    //msgs.add(new Message(ilArEx.getMessage(), Level.ERROR));
                }


                // try unmarshall numerator to StudyVariable
                try {
                    StudyVariable numStudy = this.unmarshaller.unmarshal(uk.ac.liv.jmzqml.model.mzqml.StudyVariable.class, numRef);

                    if (numStudy != null) {
                        numIsStudy = true;
                    }
                }
                catch (JAXBException ex) {
                    Logger.getLogger(NumeratorDenominatorRule.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
                }
                catch (IllegalArgumentException ilArEx) {
                    //msgs.add(new Message(ilArEx.getMessage(), Level.ERROR));
                }

                // try unmarshall denominator to StudyVariable
                try {
                    StudyVariable denStudy = this.unmarshaller.unmarshal(uk.ac.liv.jmzqml.model.mzqml.StudyVariable.class, denRef);

                    if (denStudy != null) {
                        denIsStudy = true;
                    }
                }
                catch (JAXBException ex) {
                    Logger.getLogger(NumeratorDenominatorRule.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
                }
                catch (IllegalArgumentException ilArEx) {
                    //msgs.add(new Message(ilArEx.getMessage(), Level.ERROR));
                }

                if (!numIsAssay && !numIsStudy) {
                    msgs.add(new Message("Numerator in Ratio \""
                            + ratio.getId() + "\" are not either StudyVariable or Assay\n", Level.ERROR));
                }

                if (!denIsAssay && !denIsStudy) {
                    msgs.add(new Message("Demoninator in Ratio \""
                            + ratio.getId() + "\" are not either StudyVariable or Assay\n", Level.ERROR));
                }

                if (numIsAssay && denIsStudy) {
                    msgs.add(new Message("In the RatioList, if the numerator is referencing a StudyVariable, "
                            + "the denominator MUST reference a StudyVariable.\n", Level.INFO));
                    msgs.add(new Message("In the RatioList, if the numerator is referencing an Assay, "
                            + "the denominator MUST reference an Assay.\n", Level.INFO));
                    msgs.add(new Message("In Ratio \"" + ratio.getId() + "\", the numerator is referencing an Assay but "
                            + "the denominator is referencing a StudyVariable.", Level.ERROR));
                }

                if (numIsStudy && denIsAssay) {
                    msgs.add(new Message("In the RatioList, if the numerator is referencing a StudyVariable, "
                            + "the denominator MUST reference a StudyVariable.\n", Level.INFO));
                    msgs.add(new Message("In the RatioList, if the numerator is referencing an Assay, "
                            + "the denominator MUST reference an Assay.\n", Level.INFO));
                    msgs.add(new Message("In Ratio \"" + ratio.getId() + "\", the numerator is referencing a StudyVariable but "
                            + "the denominator is referencing an Assay.", Level.ERROR));
                }
            }
        }
    }

    public List<Message> getMsgs() {
        return msgs;
    }

}
