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
public class CvValidator extends Validator {

    private static final Logger logger = Logger.getLogger(CvValidator.class);
    private MessageLevel msgL = MessageLevel.WARN;
    private HashMap<String, List<ValidatorMessage>> msgs = null;
    private MzQuantMLUnmarshaller unmarshaller = null;
    private MzQuantML mzq = null;
    private long uniqId = 0;

    public CvValidator(InputStream ontoConfig,
                       InputStream cvRuleConfig,
                       InputStream objectRuleConfig) throws ValidatorException, OntologyLoaderException {
        super(ontoConfig, cvRuleConfig, objectRuleConfig);
        validatorInit();
    }

    public CvValidator(InputStream ontoConfig,
                       InputStream cvRuleConfig) throws ValidatorException, OntologyLoaderException {
        super(ontoConfig, cvRuleConfig);
        validatorInit();
    }

    public CvValidator(InputStream ontoConfig) throws OntologyLoaderException {
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
    public Collection<ValidatorMessage> startValidation(String xmlFile) {
        if (logger.isInfoEnabled()) {
            logger.info("\nStarting new Cv validation, input file: " + new File(xmlFile).getAbsolutePath());
        }

        super.resetCvRuleStatus();

        try {
            this.unmarshaller = new MzQuantMLUnmarshaller(xmlFile);
            this.mzq = (MzQuantML) unmarshaller.unmarshall();

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

        //checkElementCvMapping(MzQuantMLElement.Provider);
        Provider provider = this.mzq.getProvider();

        Collection<ValidatorMessage> cvMappingResult;
        if (provider != null) {
            cvMappingResult = this.checkCvMapping(provider, MzQuantMLElement.Provider.getXpath());
            addMessages(cvMappingResult, this.msgL);
        }


//        ParamList analysisSummary = this.mzq.getAnalysisSummary();
//        cvMappingResult = this.checkCvMapping(analysisSummary, "/MzQuantML/AnalysisSummary");
//
//        addMessages(cvMappingResult, this.msgL);

        //checkElementCvMapping(MzQuantMLElement.AuditCollection);
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

        //////////////////////////
        // check InputFiles  //
        //////////////////////////
        InputFiles inputFiles = this.mzq.getInputFiles();
        //cvMappingResult = this.checkCvMapping(inputFiles, MzQuantMLElement.InputFiles.getXpath());

        // addMessages(cvMappingResult, this.msgL);

        // check IdentificationFileFormat_rule
        if (inputFiles.getIdentificationFiles() != null) {
            List<IdentificationFile> idFiles = inputFiles.getIdentificationFiles().getIdentificationFile();
            for (IdentificationFile idFile : idFiles) {
                FileFormat idFF = idFile.getFileFormat();
                if (idFF != null) {
                    cvMappingResult = this.checkCvMapping(idFF, "/MzQuantML/InputFiles/identificationFiles/identificationFile/fileFormat");
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
                    cvMappingResult = this.checkCvMapping(sFF, "/MzQuantML/InputFiles/sourceFile/fileFormat");
                    addMessages(cvMappingResult, this.msgL);
                }
            }
        }

        // check SearchDatabaseFileFormat_rule
        if (inputFiles.getSearchDatabase() != null) {
            List<SearchDatabase> searchDBs = inputFiles.getSearchDatabase();
            for (SearchDatabase sDB : searchDBs) {
                FileFormat sDBFF = sDB.getFileFormat();
                if (sDBFF != null) {
                    cvMappingResult = this.checkCvMapping(sDBFF, "/MzQuantML/InputFiles/searchDatabase/fileFormat");
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
                    cvMappingResult = this.checkCvMapping(modParam, "/MzQuantML/AssayList/assay/label/modification");
                    addMessages(cvMappingResult, this.msgL);
                }
            }
        }

        ///////////////////////////
        // ProteinGroupList      //
        ///////////////////////////

        // check ProteinGroupGlobalQuantLayer_rule
        ProteinGroupList pgList = this.mzq.getProteinGroupList();
        if (pgList != null) {
            List<GlobalQuantLayer> pgGlobalQuantLayers = pgList.getGlobalQuantLayer();
            for (GlobalQuantLayer pgGQL : pgGlobalQuantLayers) {
                List<Column> columns = pgGQL.getColumnDefinition().getColumn();
                for (Column col : columns) {
                    CvParamRef cvParamRef = col.getDataType();
                    cvMappingResult = this.checkCvMapping(cvParamRef, "/MzQuantML/ProteinGroupList/globalQuantLayer/columnDefinition/column/dataType");
                    addMessages(cvMappingResult, this.msgL);
                }
            }

            // check ProteinGroupAssayQuantLayer_rule
            List<QuantLayer> pgAssayQuantLayers = pgList.getAssayQuantLayer();
            for (QuantLayer pgAQL : pgAssayQuantLayers) {
                CvParamRef cvParamRef = pgAQL.getDataType();
                cvMappingResult = this.checkCvMapping(cvParamRef, "/MzQuantML/ProteinGroupList/assayQuantLayer/dataType");
                addMessages(cvMappingResult, this.msgL);
            }

            // check ProteinGroupStudyVariableQuantLayer_rule
            List<QuantLayer> pgStudyVariableQuantLayers = pgList.getStudyVariableQuantLayer();
            for (QuantLayer pgSVQL : pgStudyVariableQuantLayers) {
                CvParamRef cvParamRef = pgSVQL.getDataType();
                cvMappingResult = this.checkCvMapping(cvParamRef, "/MzQuantML/ProteinGroupList/studyVariableQuantLayer/dataType");
                addMessages(cvMappingResult, this.msgL);
            }

            // check ProteinGroupRatioQuantLayer_rule
            QuantLayer pgRQL = pgList.getRatioQuantLayer();
            if (pgRQL != null) {
                CvParamRef pgcvParamRef = pgRQL.getDataType();
                cvMappingResult = this.checkCvMapping(pgcvParamRef, "/MzQuantML/ProteinGroupList/ratioQuantLayer/dataType");
                addMessages(cvMappingResult, this.msgL);
            }
        }

        ///////////////////////////
        // ProteinList           //
        ///////////////////////////

        // check ProteinGlobalQuantLayer_rule
        ProteinList protList = this.mzq.getProteinList();
        if (protList != null) {
            List<GlobalQuantLayer> protGlobalQuantLayers = protList.getGlobalQuantLayer();
            for (GlobalQuantLayer protGQL : protGlobalQuantLayers) {
                List<Column> columns = protGQL.getColumnDefinition().getColumn();
                for (Column col : columns) {
                    CvParamRef cvParamRef = col.getDataType();
                    cvMappingResult = this.checkCvMapping(cvParamRef, "/MzQuantML/ProteinList/globalQuantLayer/columnDefinition/column/dataType");
                    addMessages(cvMappingResult, this.msgL);
                }
            }

            // check ProteinAssayQuantLayer_rule
            List<QuantLayer> protAssayQuantLayers = protList.getAssayQuantLayer();
            for (QuantLayer protAQL : protAssayQuantLayers) {
                CvParamRef cvParamRef = protAQL.getDataType();
                cvMappingResult = this.checkCvMapping(cvParamRef, "/MzQuantML/ProteinList/assayQuantLayer/dataType");
                addMessages(cvMappingResult, this.msgL);
            }

            // check ProteinStudyVariableQuantLayer_rule
            List<QuantLayer> protStudyVariableQuantLayers = protList.getStudyVariableQuantLayer();
            for (QuantLayer protSVQL : protStudyVariableQuantLayers) {
                CvParamRef cvParamRef = protSVQL.getDataType();
                cvMappingResult = this.checkCvMapping(cvParamRef, "/MzQuantML/ProteinList/studyVariableQuantLayer/dataType");
                addMessages(cvMappingResult, this.msgL);
            }

            // check ProteinRatioQuantLayer_rule
            QuantLayer protRQL = protList.getRatioQuantLayer();
            if (protRQL != null) {
                CvParamRef protcvParamRef = protRQL.getDataType();
                cvMappingResult = this.checkCvMapping(protcvParamRef, "/MzQuantML/ProteinList/ratioQuantLayer/dataType");
                addMessages(cvMappingResult, this.msgL);
            }
        }

        /////////////////////////////////
        // PeptideConsensusList        //
        /////////////////////////////////


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
                            cvMappingResult = this.checkCvMapping(cvParamRef, "/MzQuantML/PeptideConsensusList/globalQuantLayer/columnDefinition/column/dataType");
                            addMessages(cvMappingResult, this.msgL);
                        }
                    }

                    // check PeptideConsensusAssayQuantLayer_rule
                    List<QuantLayer> pepAssayQuantLayers = pepList.getAssayQuantLayer();
                    for (QuantLayer pepAQL : pepAssayQuantLayers) {
                        CvParamRef cvParamRef = pepAQL.getDataType();
                        cvMappingResult = this.checkCvMapping(cvParamRef, "/MzQuantML/PeptideConsensusList/assayQuantLayer/dataType");
                        addMessages(cvMappingResult, this.msgL);
                    }

                    // check PeptideConsensusStudyVariableQuantLayer_rule
                    List<QuantLayer> pepStudyVariableQuantLayers = pepList.getStudyVariableQuantLayer();
                    for (QuantLayer pepSVQL : pepStudyVariableQuantLayers) {
                        CvParamRef cvParamRef = pepSVQL.getDataType();
                        cvMappingResult = this.checkCvMapping(cvParamRef, "/MzQuantML/PeptideConsensusList/studyVariableQuantLayer/dataType");
                        addMessages(cvMappingResult, this.msgL);
                    }

                    // check PeptideConsensusRatioQuantLayer_rule
                    QuantLayer pepRQL = pepList.getRatioQuantLayer();
                    if (pepRQL != null) {
                        CvParamRef pepcvParamRef = pepRQL.getDataType();
                        cvMappingResult = this.checkCvMapping(pepcvParamRef, "/MzQuantML/PeptideConsensusList/ratioQuantLayer/dataType");
                        addMessages(cvMappingResult, this.msgL);
                    }

                    // check PeptideConsensusModification_rule
                    for (PeptideConsensus pep : pepList.getPeptideConsensus()) {
                        List<Modification> mods = pep.getModification();
                        cvMappingResult = this.checkCvMapping(mods, "/MzQuantML/PeptideConsensusList/peptideConsensus/modification");
                        addMessages(cvMappingResult, this.msgL);
                    }
                }
            }
        }

        ////////////////////////////////
        // SmallMoleculeList          //
        ////////////////////////////////

        // check SmallMoleculeGlobalQuantLayer_rule
        SmallMoleculeList smList = this.mzq.getSmallMoleculeList();
        if (smList != null) {
            List<GlobalQuantLayer> smGlobalQuantLayers = smList.getGlobalQuantLayer();
            for (GlobalQuantLayer smGQL : smGlobalQuantLayers) {
                List<Column> columns = smGQL.getColumnDefinition().getColumn();
                for (Column col : columns) {
                    CvParamRef cvParamRef = col.getDataType();
                    cvMappingResult = this.checkCvMapping(cvParamRef, "/MzQuantML/SmallMoleculeList/globalQuantLayer/columnDefinition/column/dataType");
                    addMessages(cvMappingResult, this.msgL);
                }
            }

            // check SmallMoleculeAssayQuantLayer_rule
            List<QuantLayer> smAssayQuantLayers = smList.getAssayQuantLayer();
            for (QuantLayer smAQL : smAssayQuantLayers) {
                CvParamRef cvParamRef = smAQL.getDataType();
                cvMappingResult = this.checkCvMapping(cvParamRef, "/MzQuantML/SmallMoleculeList/assayQuantLayer/dataType");
                addMessages(cvMappingResult, this.msgL);
            }

            // check SmallMoleculeStudyVariableQuantLayer_rule
            List<QuantLayer> smStudyVariableQuantLayers = smList.getStudyVariableQuantLayer();
            for (QuantLayer smSVQL : smStudyVariableQuantLayers) {
                CvParamRef cvParamRef = smSVQL.getDataType();
                cvMappingResult = this.checkCvMapping(cvParamRef, "/MzQuantML/SmallMoleculeList/studyVariableQuantLayer/dataType");
                addMessages(cvMappingResult, this.msgL);
            }

            // check SmallMoleculeRatioQuantLayer_rule
            QuantLayer smRQL = smList.getRatioQuantLayer();
            if (smRQL != null) {
                CvParamRef smcvParamRef = smRQL.getDataType();
                cvMappingResult = this.checkCvMapping(smcvParamRef, "/MzQuantML/SmallMoleculeList/ratioQuantLayer/dataType");
                addMessages(cvMappingResult, this.msgL);
            }
        }

        ////////////////////////////
        // FeatureList            //
        ////////////////////////////

        List<FeatureList> featureLists = this.mzq.getFeatureList();
        if (featureLists != null) {
            for (FeatureList ftList : featureLists) {

                if (ftList != null) {
                    // check FeatureListFeatureQuantLayer_rule
                    List<GlobalQuantLayer> ftGlobalQuantLayers = ftList.getFeatureQuantLayer();
                    for (GlobalQuantLayer ftGQL : ftGlobalQuantLayers) {
                        List<Column> columns = ftGQL.getColumnDefinition().getColumn();
                        for (Column col : columns) {
                            CvParamRef cvParamRef = col.getDataType();
                            cvMappingResult = this.checkCvMapping(cvParamRef, "/MzQuantML/FeatureList/featureQuantLayer/columnDefinition/column/dataType");
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
    }

    public Collection<ValidatorMessage> validate(InputStream aInputStream) throws ValidatorException {

        File tempFile;
        try {
            tempFile = storeAsTemporaryFile(aInputStream);
            return this.startValidation(tempFile.getAbsolutePath());
        } catch (Exception e) {
            e.printStackTrace();
            throw new ValidatorException("Unable to process input stream", e);
        }
    }

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
