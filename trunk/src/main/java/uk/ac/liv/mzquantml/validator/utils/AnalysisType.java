/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package uk.ac.liv.mzquantml.validator.utils;

import java.util.List;
import uk.ac.liv.jmzqml.model.mzqml.AnalysisSummary;
import uk.ac.liv.jmzqml.model.mzqml.CvParam;

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
        MS2TagBased("MS2 tag-based analysis", "MS:1002023"),
        SRM("SRM quantitation analysis", "MS:1001838"),
        InvalidAnalysisType("invalid name", "invalid accession");
        private final String name;
        private final String accession;

        AnalTp(String name, String accession) {
            this.name = name;
            this.accession = accession;
        }

        public String getName() {
            return name;
        }

        public String getAccession() {
            return accession;
        }

        @Override
        public String toString() {
            return (this.getAccession() + ": " + this.getName());
        }

    };

    AnalTp at;

    public AnalysisType(AnalysisSummary analysisSummary) {
        /*
         * This can not deal with the situation when two valid cv terms are present.
         * In reality, one experiment can only be from one techniqe.
         * The type of techinque for mzq file will be decided by the first valid
         * cv term.
         */
        List<CvParam> paramGroups = analysisSummary.getCvParam();

        for (CvParam cvParam : paramGroups) {

            if (cvParam.getAccession().equals(AnalTp.LabelFree.getAccession())) {
                at = AnalTp.LabelFree;
            }
            else if (cvParam.getAccession().equals(AnalTp.SpectralCounting.getAccession())) {
                at = AnalTp.SpectralCounting;
            }
            else if (cvParam.getAccession().equals(AnalTp.MS1LabelBased.getAccession())) {
                at = AnalTp.MS1LabelBased;
            }
            else if (cvParam.getAccession().equals(AnalTp.MS2TagBased.getAccession())) {
                at = AnalTp.MS2TagBased;
            }
            else if (cvParam.getAccession().equals(AnalTp.SRM.getAccession())) {
                at = AnalTp.SRM;
            }
            else {
                at = AnalTp.InvalidAnalysisType;
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
