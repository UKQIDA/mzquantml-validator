/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.liv.mzquantml.validator.cvmapping;

import java.io.*;
import java.util.*;
import org.apache.log4j.Logger;
import psidev.psi.tools.ontology_manager.impl.local.OntologyLoaderException;
import psidev.psi.tools.validator.*;
import psidev.psi.tools.validator.rules.cvmapping.CvRule;
import uk.ac.liv.jmzqml.MzQuantMLElement;
import uk.ac.liv.jmzqml.model.mzqml.*;
import uk.ac.liv.jmzqml.xml.io.MzQuantMLUnmarshaller;

/**
 *
 * @author Da Qi @institute University of Liverpool @time 06-Sep-2012 11:53:19
 */
public class MzQuantMLCvValidator extends Validator {

    private static final Logger logger = Logger.getLogger(MzQuantMLCvValidator.class);
    //private MessageLevel msgL = MessageLevel.WARN;
    private MessageLevel msgL = MessageLevel.INFO;
    private HashMap<String, List<ValidatorMessage>> msgs = null;
    private MzQuantML mzq = null;
    private long uniqId = 0;

    public MzQuantMLCvValidator(InputStream ontoConfig,
            InputStream cvRuleConfig,
            InputStream objectRuleConfig) throws ValidatorException, OntologyLoaderException {
        super(ontoConfig, cvRuleConfig, objectRuleConfig);
        validatorInit();
    }

    public MzQuantMLCvValidator(InputStream ontoConfig,
            InputStream cvRuleConfig) throws ValidatorException, OntologyLoaderException {
        super(ontoConfig, cvRuleConfig);
        validatorInit();
    }

    public MzQuantMLCvValidator(InputStream ontoConfig) throws OntologyLoaderException {
        super(ontoConfig);
    }

    private void validatorInit() throws ValidatorException {

        // Initialized the messages
        msgs = new HashMap<String, List<ValidatorMessage>>();
    }

    public void setMessageReportLevel(MessageLevel level) {
        this.msgL = level;
    }

    public MessageLevel getMessageReportLevel() {
        return this.msgL;
    }

    /**
     * Performs the actual validation, including schema validation (if not
     * turned off), validation against the CV-mapping rules and validation
     * against the registered ObjectRules.
     *
     * @param xmlFile the PRIDE XML file to validate.
     *
     * @return a Collection of ValidatorMessages documenting the validation
     * result.
     */
    public Collection<ValidatorMessage> startValidation(String xmlFile,
            MzQuantML mzq) {
        if (logger.isInfoEnabled()) {
            logger.info("\nStarting new Cv validation, input file: " + new File(xmlFile).getAbsolutePath());
        }

        super.resetCvRuleStatus();

        try {
//            this.unmarshaller = new MzQuantMLUnmarshaller(xmlFile);
//            this.mzq = (MzQuantML) unmarshaller.unmarshall();
            this.mzq = mzq;
            addMessages(this.checkCvMappingRules(), this.msgL);
            // See if the mapping makes sense.
            if (!this.msgs.isEmpty()) {
                System.err.println("\n\nThere were errors procossing the Cv mapping configuration file:\n");
                for (ValidatorMessage lMessage : getMessageCollection()) {
                    System.err.println("\t - " + lMessage);
                }
            }
            System.out.println("number of valid rules: " + this.getCvRuleManager().getCvRules().size());


            // ****************************
            // CHECK MANDATORY ELEMENTS
            // ****************************
            //checkMandatoryElements();


            // Cv mapping rules.
            if (this.mzq != null) {
                applyCvMappingRules();
            }

        } catch (ValidatorException ve) {
            logger.error("Exceptions during validation!", ve);
            ve.printStackTrace();
        }

        //check for terms that were not anticipated with the rules in the Cv mapping file
        for (String xpath : ValidatorCvContext.getInstance().getNotRecognisedXpath()) {

            Set<String> list = ValidatorCvContext.getInstance().getNotRecognisedTerms(xpath);
            if (list != null && !list.isEmpty()) {
                System.out.println("unrecognised terms for xpath '" + xpath + "' : " + list);
            }
        }

        return this.getMessageCollection();
    }

    private void addMessages(Collection<ValidatorMessage> aNewMessages,
            MessageLevel aLevel) {
        for (ValidatorMessage aNewMessage : aNewMessages) {
            if (aNewMessage.getLevel().isHigher(aLevel) || aNewMessage.getLevel().isSame(aLevel)) {
                if (aNewMessage.getRule() != null) {
                    addValidatorMessage(aNewMessage.getRule().getId(), aNewMessage, this.msgL);
                } else {
                    addValidatorMessage("unknown", aNewMessage, this.msgL);
                }
            }
        }
    }

    private void addValidatorMessage(String ruleId,
            ValidatorMessage validatorMessage,
            MessageLevel msgLevel) {
        if (validatorMessage.getLevel().isHigher(msgLevel) || validatorMessage.getLevel().isSame(msgLevel)) {
            if (this.msgs.containsKey(ruleId)) {
                this.msgs.get(ruleId).add(validatorMessage);
            } else {
                List<ValidatorMessage> list = new ArrayList<ValidatorMessage>();
                list.add(validatorMessage);
                this.msgs.put(ruleId, list);
            }
        }
    }

    private void applyCvMappingRules() throws ValidatorException {
        logger.debug("Validating against the Cv mapping Rules...");

        //check ProviderContactRole_rule
        Provider provider = this.mzq.getProvider();

        Collection<ValidatorMessage> cvMappingResult;
        if (provider != null) {
            cvMappingResult = this.checkCvMapping(provider, MzQuantMLElement.Provider.getXpath());
            addMessages(cvMappingResult, this.msgL);
        }

        //check AuditCollectionPerson_rule and AuditCollectionOrganization_rule
        AuditCollection auditCollection = this.mzq.getAuditCollection();
        if (auditCollection != null) {
            List<AbstractContact> personOrOrg = auditCollection.getPersonOrOrganization();
            if (personOrOrg != null) {
                for (AbstractContact ac : personOrOrg) {
                    if (ac instanceof Person) {
                        cvMappingResult = this.checkCvMapping(ac, MzQuantMLElement.Person.getXpath());
                        addMessages(cvMappingResult, this.msgL);
                    } else if (ac instanceof Organization) {
                        cvMappingResult = this.checkCvMapping(ac, MzQuantMLElement.Organization.getXpath());
                        addMessages(cvMappingResult, this.msgL);
                    }
                }
            }
        }

        /**
         * *************************
         *
         * check InputFiles
         *
         * ************************
         */
        InputFiles inputFiles = this.mzq.getInputFiles();

        List<RawFilesGroup> rawFileGroupList = inputFiles.getRawFilesGroup();

        if (rawFileGroupList != null) {
            for (RawFilesGroup rawFilesGroup : rawFileGroupList) {

                // check RawFilesGroup_rule
                cvMappingResult = this.checkCvMapping(rawFilesGroup, "/MzQuantML/InputFiles/rawFilesGroup");
                addMessages(cvMappingResult, this.msgL);

                List<RawFile> rawFileList = rawFilesGroup.getRawFile();

                if (rawFileList != null) {
                    for (RawFile rawFile : rawFileList) {

                        // check RawFilesGroup_rule
                        cvMappingResult = this.checkCvMapping(rawFile, "/MzQuantML/InputFiles/RawFilesGroup/rawFile");
                        addMessages(cvMappingResult, this.msgL);

                        // check RawFileFormat_rule
                        FileFormat rFF = rawFile.getFileFormat();
                        if (rFF != null) {
                            cvMappingResult = this.checkCvMapping(rFF, "/MzQuantML/InputFiles/RawFilesGroup/RawFile/fileFormat");
                            addMessages(cvMappingResult, this.msgL);
                        }
                    }
                }
            }
        }


        // check IdentificationFileFormat_rule
        if (inputFiles.getIdentificationFiles() != null) {
            List<IdentificationFile> idFiles = inputFiles.getIdentificationFiles().getIdentificationFile();
            for (IdentificationFile idFile : idFiles) {
                FileFormat idFF = idFile.getFileFormat();
                if (idFF != null) {
                    cvMappingResult = this.checkCvMapping(idFF, "/MzQuantML/InputFiles/IdentificationFiles/IdentificationFile/fileFormat");
                    addMessages(cvMappingResult, this.msgL);
                }
            }
        }

        // check SourceFileFormat_rule
        if (inputFiles.getSourceFile() != null) {
            List<SourceFile> sourceFiles = inputFiles.getSourceFile();
            for (SourceFile sFile : sourceFiles) {
                FileFormat sFF = sFile.getFileFormat();
                if (sFF != null) {
                    cvMappingResult = this.checkCvMapping(sFF, "/MzQuantML/InputFiles/SourceFile/fileFormat");
                    addMessages(cvMappingResult, this.msgL);
                }
            }
        }


        if (inputFiles.getSearchDatabase() != null) {
            List<SearchDatabase> searchDBs = inputFiles.getSearchDatabase();
            for (SearchDatabase sDB : searchDBs) {

                // check SearchDatabase_rule
                List<CvParam> sDBCvParams = sDB.getCvParam();
                if (!sDBCvParams.isEmpty()) {
                    cvMappingResult = this.checkCvMapping(searchDBs, "/MzQuantML/InputFiles/searchDatabase");
                    addMessages(cvMappingResult, this.msgL);
                }

                // check SearchDatabaseName_rule
                Param DBName = sDB.getDatabaseName();
                if (DBName != null) {
                    cvMappingResult = this.checkCvMapping(DBName, "/MzQuantML/InputFiles/SearchDatabase/databaseName");
                    addMessages(cvMappingResult, this.msgL);
                }

                // check SearchDatabaseFileFormat_rule
                FileFormat sDBFF = sDB.getFileFormat();
                if (sDBFF != null) {
                    cvMappingResult = this.checkCvMapping(sDBFF, "/MzQuantML/InputFiles/SearchDatabase/fileFormat");
                    addMessages(cvMappingResult, this.msgL);
                }
            }
        }

        // check AssayLabelModification_rule
        AssayList assayList = this.mzq.getAssayList();
        if (assayList != null) {
            List<Assay> assays = assayList.getAssay();
            for (Assay assay : assays) {
                List<ModParam> modParams = assay.getLabel().getModification();
                for (ModParam modParam : modParams) {
                    cvMappingResult = this.checkCvMapping(modParam, "/MzQuantML/AssayList/Assay/Label/Modification");
                    addMessages(cvMappingResult, this.msgL);
                }
            }
        }

        // check StudyVariable_rule
        StudyVariableList studyVariables = this.mzq.getStudyVariableList();
        if (studyVariables != null) {
            for (StudyVariable studyVariable : studyVariables.getStudyVariable()) {
                cvMappingResult = this.checkCvMapping(studyVariable, "/MzQuantML/StudyVariableList/studyVariable");
                addMessages(cvMappingResult, this.msgL);
            }
        }

        // check AnalysisSummary_rule
        ParamList analysisSummary = this.mzq.getAnalysisSummary();
        if (analysisSummary != null) {
            cvMappingResult = this.checkCvMapping(analysisSummary, "/MzQuantML/AnalysisSummary");
            addMessages(cvMappingResult, this.msgL);
        }

        // check RatioCalculation_rule
        RatioList ratioList = this.mzq.getRatioList();
        if (ratioList != null) {
            List<Ratio> ratios = ratioList.getRatio();
            if (!ratios.isEmpty()) {
                for (Ratio ratio : ratios) {
                    ParamList ratioCalculation = ratio.getRatioCalculation();
                    if (ratioCalculation != null) {
                        cvMappingResult = this.checkCvMapping(ratioCalculation, "/MzQuantML/RatioList/Ratio/ratioCalculation");
                        addMessages(cvMappingResult, this.msgL);
                    }
                }
            }
        }

        /**
         * **********************
         *
         * ProteinGroupList
         *
         * ***********************
         */
        // check ProteinGroupGlobalQuantLayer_rule
        ProteinGroupList pgList = this.mzq.getProteinGroupList();
        if (pgList != null) {
            List<GlobalQuantLayer> pgGlobalQuantLayers = pgList.getGlobalQuantLayer();
            for (GlobalQuantLayer pgGQL : pgGlobalQuantLayers) {
                List<Column> columns = pgGQL.getColumnDefinition().getColumn();
                for (Column col : columns) {
                    CvParamRef cvParamRef = col.getDataType();
                    cvMappingResult = this.checkCvMapping(cvParamRef, "/MzQuantML/ProteinGroupList/GlobalQuantLayer/ColumnDefinition/Column/dataType");
                    addMessages(cvMappingResult, this.msgL);
                }
            }

            // check ProteinGroupAssayQuantLayer_rule
            List<QuantLayer> pgAssayQuantLayers = pgList.getAssayQuantLayer();
            for (QuantLayer pgAQL : pgAssayQuantLayers) {
                CvParamRef cvParamRef = pgAQL.getDataType();
                cvMappingResult = this.checkCvMapping(cvParamRef, "/MzQuantML/ProteinGroupList/AssayQuantLayer/dataType");
                addMessages(cvMappingResult, this.msgL);
            }

            // check ProteinGroupStudyVariableQuantLayer_rule
            List<QuantLayer> pgStudyVariableQuantLayers = pgList.getStudyVariableQuantLayer();
            for (QuantLayer pgSVQL : pgStudyVariableQuantLayers) {
                CvParamRef cvParamRef = pgSVQL.getDataType();
                cvMappingResult = this.checkCvMapping(cvParamRef, "/MzQuantML/ProteinGroupList/StudyVariableQuantLayer/dataType");
                addMessages(cvMappingResult, this.msgL);
            }

            // check ProteinGroupRatioQuantLayer_rule
            QuantLayer pgRQL = pgList.getRatioQuantLayer();
            if (pgRQL != null) {
                CvParamRef pgcvParamRef = pgRQL.getDataType();
                cvMappingResult = this.checkCvMapping(pgcvParamRef, "/MzQuantML/ProteinGroupList/RatioQuantLayer/dataType");
                addMessages(cvMappingResult, this.msgL);
            }
        }

        /**
         * ************************
         *
         * ProteinList
         *
         * *************************
         */
        // check ProteinGlobalQuantLayer_rule
        ProteinList protList = this.mzq.getProteinList();
        if (protList != null) {
            List<GlobalQuantLayer> protGlobalQuantLayers = protList.getGlobalQuantLayer();
            for (GlobalQuantLayer protGQL : protGlobalQuantLayers) {
                List<Column> columns = protGQL.getColumnDefinition().getColumn();
                for (Column col : columns) {
                    CvParamRef cvParamRef = col.getDataType();
                    cvMappingResult = this.checkCvMapping(cvParamRef, "/MzQuantML/ProteinList/GlobalQuantLayer/ColumnDefinition/Column/dataType");
                    addMessages(cvMappingResult, this.msgL);
                }
            }

            // check ProteinAssayQuantLayer_rule
            List<QuantLayer> protAssayQuantLayers = protList.getAssayQuantLayer();
            for (QuantLayer protAQL : protAssayQuantLayers) {
                CvParamRef cvParamRef = protAQL.getDataType();
                cvMappingResult = this.checkCvMapping(cvParamRef, "/MzQuantML/ProteinList/AssayQuantLayer/dataType");
                addMessages(cvMappingResult, this.msgL);
            }

            // check ProteinStudyVariableQuantLayer_rule
            List<QuantLayer> protStudyVariableQuantLayers = protList.getStudyVariableQuantLayer();
            for (QuantLayer protSVQL : protStudyVariableQuantLayers) {
                CvParamRef cvParamRef = protSVQL.getDataType();
                cvMappingResult = this.checkCvMapping(cvParamRef, "/MzQuantML/ProteinList/StudyVariableQuantLayer/dataType");
                addMessages(cvMappingResult, this.msgL);
            }

            // check ProteinRatioQuantLayer_rule
            QuantLayer protRQL = protList.getRatioQuantLayer();
            if (protRQL != null) {
                CvParamRef protcvParamRef = protRQL.getDataType();
                cvMappingResult = this.checkCvMapping(protcvParamRef, "/MzQuantML/ProteinList/RatioQuantLayer/dataType");
                addMessages(cvMappingResult, this.msgL);
            }
        }

        /**
         * ******************************
         *
         * PeptideConsensusList
         *
         * ******************************
         */
        List<PeptideConsensusList> pepLists = this.mzq.getPeptideConsensusList();
        if (pepLists != null) {
            for (PeptideConsensusList pepList : pepLists) {

                if (pepList != null) {
                    // check PeptideConsensusGlobalQuantLayer_rule
                    List<GlobalQuantLayer> pepGlobalQuantLayers = pepList.getGlobalQuantLayer();
                    for (GlobalQuantLayer pepGQL : pepGlobalQuantLayers) {
                        List<Column> columns = pepGQL.getColumnDefinition().getColumn();
                        for (Column col : columns) {
                            CvParamRef cvParamRef = col.getDataType();
                            cvMappingResult = this.checkCvMapping(cvParamRef, "/MzQuantML/PeptideConsensusList/GlobalQuantLayer/ColumnDefinition/Column/dataType");
                            addMessages(cvMappingResult, this.msgL);
                        }
                    }

                    // check PeptideConsensusAssayQuantLayer_rule
                    List<QuantLayer> pepAssayQuantLayers = pepList.getAssayQuantLayer();
                    for (QuantLayer pepAQL : pepAssayQuantLayers) {
                        CvParamRef cvParamRef = pepAQL.getDataType();
                        cvMappingResult = this.checkCvMapping(cvParamRef, "/MzQuantML/PeptideConsensusList/AssayQuantLayer/dataType");
                        addMessages(cvMappingResult, this.msgL);
                    }

                    // check PeptideConsensusStudyVariableQuantLayer_rule
                    List<QuantLayer> pepStudyVariableQuantLayers = pepList.getStudyVariableQuantLayer();
                    for (QuantLayer pepSVQL : pepStudyVariableQuantLayers) {
                        CvParamRef cvParamRef = pepSVQL.getDataType();
                        cvMappingResult = this.checkCvMapping(cvParamRef, "/MzQuantML/PeptideConsensusList/StudyVariableQuantLayer/dataType");
                        addMessages(cvMappingResult, this.msgL);
                    }

                    // check PeptideConsensusRatioQuantLayer_rule
                    QuantLayer pepRQL = pepList.getRatioQuantLayer();
                    if (pepRQL != null) {
                        CvParamRef pepcvParamRef = pepRQL.getDataType();
                        cvMappingResult = this.checkCvMapping(pepcvParamRef, "/MzQuantML/PeptideConsensusList/RatioQuantLayer/dataType");
                        addMessages(cvMappingResult, this.msgL);
                    }

                    // check PeptideConsensusModification_rule
                    for (PeptideConsensus pep : pepList.getPeptideConsensus()) {
                        List<Modification> mods = pep.getModification();
                        cvMappingResult = this.checkCvMapping(mods, "/MzQuantML/PeptideConsensusList/PeptideConsensus/modification");
                        addMessages(cvMappingResult, this.msgL);
                    }
                }
            }
        }

        /**
         * ******************************
         *
         * SmallMoleculeList
         *
         * ******************************
         */
        // check SmallMoleculeGlobalQuantLayer_rule
        SmallMoleculeList smList = this.mzq.getSmallMoleculeList();
        if (smList != null) {
            List<GlobalQuantLayer> smGlobalQuantLayers = smList.getGlobalQuantLayer();
            for (GlobalQuantLayer smGQL : smGlobalQuantLayers) {
                List<Column> columns = smGQL.getColumnDefinition().getColumn();
                for (Column col : columns) {
                    CvParamRef cvParamRef = col.getDataType();
                    cvMappingResult = this.checkCvMapping(cvParamRef, "/MzQuantML/SmallMoleculeList/GlobalQuantLayer/ColumnDefinition/Column/dataType");
                    addMessages(cvMappingResult, this.msgL);
                }
            }

            // check SmallMoleculeAssayQuantLayer_rule
            List<QuantLayer> smAssayQuantLayers = smList.getAssayQuantLayer();
            for (QuantLayer smAQL : smAssayQuantLayers) {
                CvParamRef cvParamRef = smAQL.getDataType();
                cvMappingResult = this.checkCvMapping(cvParamRef, "/MzQuantML/SmallMoleculeList/AssayQuantLayer/dataType");
                addMessages(cvMappingResult, this.msgL);
            }

            // check SmallMoleculeStudyVariableQuantLayer_rule
            List<QuantLayer> smStudyVariableQuantLayers = smList.getStudyVariableQuantLayer();
            for (QuantLayer smSVQL : smStudyVariableQuantLayers) {
                CvParamRef cvParamRef = smSVQL.getDataType();
                cvMappingResult = this.checkCvMapping(cvParamRef, "/MzQuantML/SmallMoleculeList/StudyVariableQuantLayer/dataType");
                addMessages(cvMappingResult, this.msgL);
            }

            // check SmallMoleculeRatioQuantLayer_rule
            QuantLayer smRQL = smList.getRatioQuantLayer();
            if (smRQL != null) {
                CvParamRef smcvParamRef = smRQL.getDataType();
                cvMappingResult = this.checkCvMapping(smcvParamRef, "/MzQuantML/SmallMoleculeList/RatioQuantLayer/dataType");
                addMessages(cvMappingResult, this.msgL);
            }
        }

        /**
         * ******************************
         *
         * FeatureList
         *
         * ******************************
         */
        List<FeatureList> featureLists = this.mzq.getFeatureList();
        if (featureLists != null) {
            for (FeatureList ftList : featureLists) {

                if (ftList != null) {

                    // check FeatureList_rule
                    cvMappingResult = this.checkCvMapping(ftList, "/MzQuantML/featureList");
                    addMessages(cvMappingResult, this.msgL);

                    // check Feature_rule
                    List<Feature> features = ftList.getFeature();
                    if ((features != null) && (!features.isEmpty())) {
                        for (Feature feature : features) {
                            cvMappingResult = this.checkCvMapping(feature, "/MzQuantML/FeatureList/Feature");
                            addMessages(cvMappingResult, this.msgL);
                        }
                    }

                    // check FeatureListFeatureQuantLayer_rule
                    List<GlobalQuantLayer> ftGlobalQuantLayers = ftList.getFeatureQuantLayer();
                    for (GlobalQuantLayer ftGQL : ftGlobalQuantLayers) {
                        List<Column> columns = ftGQL.getColumnDefinition().getColumn();
                        for (Column col : columns) {
                            CvParamRef cvParamRef = col.getDataType();
                            cvMappingResult = this.checkCvMapping(cvParamRef, "/MzQuantML/FeatureList/FeatureQuantLayer/ColumnDefinition/Column/dataType");
                            addMessages(cvMappingResult, this.msgL);
                        }
                    }

                    // check FeatureListMS2AssayQuantLayer_rule
                    List<QuantLayer> ftAssayQuantLayers = ftList.getMS2AssayQuantLayer();
                    for (QuantLayer ftAQL : ftAssayQuantLayers) {
                        CvParamRef cvParamRef = ftAQL.getDataType();
                        cvMappingResult = this.checkCvMapping(cvParamRef, "/MzQuantML/FeatureList/MS2AssayQuantLayer/dataType");
                        addMessages(cvMappingResult, this.msgL);
                    }

                    // check FeatureListMS2StudyVariableQuantLayer_rule
                    List<QuantLayer> ftStudyVariableQuantLayers = ftList.getMS2StudyVariableQuantLayer();
                    for (QuantLayer ftSVQL : ftStudyVariableQuantLayers) {
                        CvParamRef cvParamRef = ftSVQL.getDataType();
                        cvMappingResult = this.checkCvMapping(cvParamRef, "/MzQuantML/FeatureList/MS2StudyVariableQuantLayer/dataType");
                        addMessages(cvMappingResult, this.msgL);
                    }

                    // check FeatureListMS2RatioQuantLayer_rule
                    QuantLayer ftRQL = ftList.getMS2RatioQuantLayer();
                    if (ftRQL != null) {
                        CvParamRef ftcvParamRef = ftRQL.getDataType();
                        cvMappingResult = this.checkCvMapping(ftcvParamRef, "/MzQuantML/FeatureList/MS2RatioQuantLayer/dataType");
                        addMessages(cvMappingResult, this.msgL);
                    }
                }
            }
        }

        /*
         * ********************************
         *
         * SoftwareList
         *
         * ********************************
         */

        SoftwareList softwareList = this.mzq.getSoftwareList();
        if (softwareList != null) {
            List<Software> softwares = softwareList.getSoftware();
            if ((softwares != null) && (!softwares.isEmpty())) {
                for (Software software : softwares) {
                    cvMappingResult = this.checkCvMapping(software, "/MzQuantML/SoftwareList/Software");
                    addMessages(cvMappingResult, this.msgL);
                }
            }
        }

        /*
         * *****************************
         *
         * DataProcessingList
         *
         * *****************************
         */

        DataProcessingList dpList = this.mzq.getDataProcessingList();
        if (dpList != null) {
            List<DataProcessing> dps = dpList.getDataProcessing();
            if ((dps != null) && (!dps.isEmpty())) {
                for (DataProcessing dp : dps) {
                    List<ProcessingMethod> pms = dp.getProcessingMethod();
                    if (pms != null) {
                        cvMappingResult = this.checkCvMapping(pms, "/MzQuantML/DataProcessingList/DataProcessing/ProcessingMethod");
                        addMessages(cvMappingResult, this.msgL);
                    }
                }
            }
        }
    }

//    public Collection<ValidatorMessage> validate(InputStream aInputStream) throws ValidatorException {
//
//        File tempFile;
//        try {
//            tempFile = storeAsTemporaryFile(aInputStream);
//            return this.startValidation(tempFile.getAbsolutePath());
//        } catch (Exception e) {
//            e.printStackTrace();
//            throw new ValidatorException("Unable to process input stream", e);
//        }
//    }
    /**
     * Store the content of the given input stream into a temporary file and
     * return its descriptor.
     *
     * @param is the input stream to store.
     *
     * @return a File descriptor describing a temporary file storing the content
     * of the given input stream.
     * @throws IOException if an IO error occur.
     */
    private File storeAsTemporaryFile(InputStream is) throws IOException {

        if (is == null) {
            throw new IllegalArgumentException("You must give a non null InputStream");
        }

        BufferedReader in = new BufferedReader(new InputStreamReader(is));

        // Create a temp file and write URL content in it.
        File tempDirectory = new File(System.getProperty("java.io.tmpdir", "tmp"));
        if (!tempDirectory.exists()) {
            if (!tempDirectory.mkdirs()) {
                throw new IOException("Cannot create temp directory: "
                        + tempDirectory.getAbsolutePath());
            }
        }

        long id = getUniqueId();

        File tempFile = File.createTempFile("validator." + id, ".xml", tempDirectory);

        log.info("The file is temporary store as: " + tempFile.getAbsolutePath());

        BufferedWriter out = new BufferedWriter(new FileWriter(tempFile));

        String line;
        while ((line = in.readLine()) != null) {
            out.write(line);
        }

        in.close();

        out.flush();
        out.close();

        return tempFile;
    }

    /**
     * Return a unique ID.
     *
     * @return a unique id.
     */
    synchronized private long getUniqueId() {
        return ++uniqId;
    }

//    private void checkElementCvMapping(MzQuantMLElement element) throws ValidatorException {
//
//        Iterator<MzQuantMLObject> mzqIter;
//        mzqIter = this.unmarshaller.unmarshalCollectionFromXpath(element);
//        Collection toValidate = new ArrayList();
//        while (mzqIter.hasNext()) {
//            final Object next = mzqIter.next();
//            toValidate.add(next);
//        }
//
//        final Collection<ValidatorMessage> cvMappingResult = this.checkCvMapping(toValidate, element.getXpath());
//        addMessages(cvMappingResult, this.msgL);
//    }
    @Override
    public Collection<ValidatorMessage> checkCvMapping(Collection<?> collection,
            String xPath) throws ValidatorException {
        Collection messages = new ArrayList();

        if (this.getCvRuleManager() != null) {
            for (CvRule rule : this.getCvRuleManager().getCvRules()) {
                for (Object o : collection) {
                    if (rule.canCheck(xPath)) {
                        final Collection<ValidatorMessage> resultCheck = rule.check(o, xPath);
                        messages.addAll(resultCheck);
                    }
                }
            }
        } else {
            logger.error("The CvRuleManager has not been set up yet.");
        }
        return messages;
    }

    private Collection<ValidatorMessage> getMessageCollection() {
        Collection<ValidatorMessage> ret = new HashSet<ValidatorMessage>();
        for (String key : this.msgs.keySet()) {
            final List<ValidatorMessage> list = this.msgs.get(key);
            if (list != null) {
                ret.addAll(list);
            }
        }
        return ret;
    }

    /**
     * Check for the presence of all mandatory elements required at this
     * validation type
     */
//    private void checkMandatoryElements() {
//        List<ValidatorMessage> ret = new ArrayList<ValidatorMessage>();
//        if (ruleFilterManager != null) {
//            final List<String> mandatoryElements = ruleFilterManager.getMandatoryElements();
//            for (String elementName : mandatoryElements) {
//                MzQuantMLElement mzQuantMLElement = getMzQuantMLElement(elementName);
//                // check if that element is present on the file
//                final MzQuantMLObject mzQuantMLObject = unmarshaller.unmarshal(mzQuantMLElement);
//                if (mzQuantMLObject == null) {
//                    final MandatoryElementsObjectRule mandatoryObjectRule = new MandatoryElementsObjectRule(
//                            ontologyMngr);
//                    final ValidatorMessage validatorMessage = new ValidatorMessage(
//                            "The element on xPath:'" + mzQuantMLElement.getXpath()
//                            + "' is required for the current type of validation.",
//                            MessageLevel.ERROR, new Context(mzQuantMLElement.getXpath()),
//                            mandatoryObjectRule);
//                    // extendedReport.objectRuleExecuted(mandatoryObjectRule,
//                    // validatorMessage);
//                    // this.addObjectRule(mandatoryObjectRule);
//                    addValidatorMessage(validatorMessage.getRule().getId(), validatorMessage,
//                            this.msgL);
//                }
//            }
//        }
//    }
    private MzQuantMLElement getMzQuantMLElement(String elementName) {
        for (MzQuantMLElement element : MzQuantMLElement.values()) {
            if (element.name().equals(elementName)) {
                return element;
            }
        }
        return null;
    }
}
