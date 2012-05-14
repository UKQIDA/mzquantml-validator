/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.liv.mzquantml.validator.utils;

import info.psidev.psi.pi.mzquantml._1_0.*;
import java.util.List;

/**
 *
 * @author Da Qi
 * @institute University of Liverpool
 * @time May 3, 2012 2:48:50 PM
 */
public class AnalysisType {

    public static enum AnalTp {

        LabelFree("LC-MS label-free quantitation analysis"),
        SpectralCounting("spectral counting quantitation analysis"),
        MS1LabelBased("MS1 label-based analysis"),
        MS2TagBased("MS2 tag-based analysis");
        private final String name;

        AnalTp(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    };
    AnalTp at;

    public AnalysisType(ParamListType analysisSummary) {
        List<AbstractParamType> paramGroups = analysisSummary.getParamGroup();
        for (AbstractParamType param : paramGroups) {
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
