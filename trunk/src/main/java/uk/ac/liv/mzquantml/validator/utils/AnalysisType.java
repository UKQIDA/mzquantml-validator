/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.liv.mzquantml.validator.utils;

import java.util.List;
import uk.ac.liv.jmzqml.model.mzqml.AbstractParam;
import uk.ac.liv.jmzqml.model.mzqml.ParamList;

/**
 *
 * @author Da Qi
 * @institute University of Liverpool
 * @time May 3, 2012 2:48:50 PM
 */
public class AnalysisType {

    public static enum AnalTp {

        LabelFree("LC-MS label-free quantitation analysis", "MS:1001834"),
        SpectralCounting("spectral counting quantitation analysis", "MS:1001836"),
        MS1LabelBased("MS1 label-based analysis", "MS:1002018"),
        MS2TagBased("MS2 tag-based analysis", "MS:1002023");
        private final String name;
        private final String assession;
        
        AnalTp(String name, String assession) {
            this.name = name;
            this.assession = assession;
        }

        public String getName() {
            return name;
        }
        
        public String getAccession(){
            return assession;
        }
    };
    AnalTp at;

    public AnalysisType(ParamList analysisSummary) {
        List<AbstractParam> paramGroups = analysisSummary.getParamGroup();
        for (AbstractParam param : paramGroups) {
            //Technique tech = new Technique(param.getName());
            if (param.getName().equals(AnalTp.LabelFree.getName())) {
                at = AnalTp.LabelFree;
            } else if (param.getName().equals(AnalTp.SpectralCounting.getName())) {
                at = AnalTp.SpectralCounting;
            } else if (param.getName().equals(AnalTp.MS1LabelBased.getName())) {
                at = AnalTp.MS1LabelBased;
            } else if (param.getName().equals(AnalTp.MS2TagBased.getName())) {
                at = AnalTp.MS2TagBased;
            } else {
                at = null;
            }
            if (at != null) {
                break;
            }
        }
    }

    public AnalysisType() {
        at = null;
    }

    public AnalTp getAnalysisType() {
        return this.at;
    }
}
