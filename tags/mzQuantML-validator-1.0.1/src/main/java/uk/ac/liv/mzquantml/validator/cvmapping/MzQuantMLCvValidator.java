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
 * @author Da Qi
 * @institute University of Liverpool
 * @time 06-Sep-2012 11:53:19
 */
public class MzQuantMLCvValidator extends Validator {

    private static final Logger logger = Logger.getLogger(MzQuantMLCvValidator.class);
    private MessageLevel msgL = MessageLevel.WARN;
    //private MessageLevel msgL = MessageLevel.INFO;
    private HashMap<String, List<ValidatorMessage>> msgs = null;
    private MzQuantMLUnmarshaller unmarshaller;
    //private MzQuantML mzq = null;
    private long uniqId = 0;

    public MzQuantMLCvValidator(InputStream ontoConfig,
                                InputStream cvRuleConfig,
                                InputStream objectRuleConfig)
            throws ValidatorException, OntologyLoaderException {
        super(ontoConfig, cvRuleConfig, objectRuleConfig);
        validatorInit();
    }

    public MzQuantMLCvValidator(InputStream ontoConfig,
                                InputStream cvRuleConfig)
            throws ValidatorException, OntologyLoaderException {
        super(ontoConfig, cvRuleConfig);
        validatorInit();
    }

    public MzQuantMLCvValidator(InputStream ontoConfig)
            throws OntologyLoaderException {
        super(ontoConfig);
    }

    private void validatorInit()
            throws ValidatorException {

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
     * @param xmlFile the mzq file to validate.
     *
     * @return a Collection of ValidatorMessages documenting the validation
     *         result.
     */
    public Collection<ValidatorMessage> startValidation(String xmlFile) {
        if (logger.isInfoEnabled()) {
            logger.info("\nStarting new Cv validation, input file: " + new File(xmlFile).getAbsolutePath());
        }

        super.resetCvRuleStatus();

        try {
            this.unmarshaller = new MzQuantMLUnmarshaller(new File(xmlFile));
//            this.mzq = (MzQuantML) unmarshaller.unmarshall();
            //this.mzq = mzq;
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
            //if (this.mzq != null) {
            applyCvMappingRules();
            //}

        }
        catch (ValidatorException ve) {
            ValidatorMessage veMsg = new ValidatorMessage("Exceptions during cv validation: " + ve.getMessage(), MessageLevel.FATAL);
            List<ValidatorMessage> vMsgs = msgs.get("Exceptions");
            if (vMsgs == null) {
                vMsgs = new ArrayList<ValidatorMessage>();
            }
            vMsgs.add(veMsg);
            msgs.put("Exceptions", vMsgs);

        }
        catch (Exception e) {
            ValidatorMessage veMsg = new ValidatorMessage("Exceptions during cv validation: " + e.getMessage(), MessageLevel.FATAL);
            List<ValidatorMessage> vMsgs = msgs.get("Exceptions");
            if (vMsgs == null) {
                vMsgs = new ArrayList<ValidatorMessage>();
            }
            vMsgs.add(veMsg);
            msgs.put("Exceptions", vMsgs);
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
                }
                else {
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
            }
            else {
                List<ValidatorMessage> list = new ArrayList<ValidatorMessage>();
                list.add(validatorMessage);
                this.msgs.put(ruleId, list);
            }
        }
    }

    private void applyCvMappingRules()
            throws ValidatorException {
        logger.debug("Validating against the Cv mapping Rules...");

        //check ProviderContactRole_rule
        //Provider provider = this.mzq.getProvider();  //jmzquantml 1.0.0-rc3-1.0.5
        Provider provider = unmarshaller.unmarshal(MzQuantMLElement.Provider);

        Collection<ValidatorMessage> cvMappingResult = new ArrayList<ValidatorMessage>();
        if (provider != null) {
            cvMappingResult = this.checkCvMapping(provider, MzQuantMLElement.Provider.getXpath());
            addMessages(cvMappingResult, this.msgL);
        }

        //check AuditCollectionPerson_rule and AuditCollectionOrganization_rule
        //AuditCollection auditCollection = this.mzq.getAuditCollection();
        AuditCollection auditCollection = unmarshaller.unmarshal(MzQuantMLElement.AuditCollection);
        if (auditCollection != null) {
            List<Person> persons = auditCollection.getPerson();
            if (persons != null) {
                for (Person person : persons) {
                    cvMappingResult = this.checkCvMapping(person, MzQuantMLElement.Person.getXpath());
                    addMessages(cvMappingResult, this.msgL);
                }
            }

            List<Organization> orgs = auditCollection.getOrganization();
            if (orgs != null) {
                for (Organization org : orgs) {
                    cvMappingResult = this.checkCvMapping(org, MzQuantMLElement.Organization.getXpath());
                    addMessages(cvMappingResult, this.msgL);
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
        //InputFiles inputFiles = this.mzq.getInputFiles();
        InputFiles inputFiles = unmarshaller.unmarshal(MzQuantMLElement.InputFiles);
//        Collection toValidate = new ArrayList();
//        if (inputFiles != null) {
//            toValidate.add(inputFiles);
//            cvMappingResult = this.checkCvMapping(toValidate, "/MzQuantML/InputFiles");
//            addMessages(cvMappingResult, this.msgL);
//        }

        List<RawFilesGroup> rawFileGroupList = inputFiles.getRawFilesGroup();

        if (rawFileGroupList != null) {
            for (RawFilesGroup rawFilesGroup : rawFileGroupList) {

                // check RawFilesGroup_rule
                cvMappingResult = this.checkCvMapping(rawFilesGroup, "/MzQuantML/InputFiles/rawFilesGroup");
                addMessages(cvMappingResult, this.msgL);

                List<RawFile> rawFileList = rawFilesGroup.getRawFile();

                if (rawFileList != null) {
                    for (RawFile rawFile : rawFileList) {

                        // check RawFile_rule
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

        if (inputFiles.getIdentificationFiles() != null) {
            List<IdentificationFile> idFiles = inputFiles.getIdentificationFiles().getIdentificationFile();
            for (IdentificationFile idFile : idFiles) {
                // check IdentificationFile_rule
                cvMappingResult = this.checkCvMapping(idFile, "/MzQuantML/InputFiles/IdentificationFiles/identificationFile");
                addMessages(cvMappingResult, this.msgL);
                FileFormat idFF = idFile.getFileFormat();
                if (idFF != null) {
                    // check IdentificationFileFormat_rule
                    cvMappingResult = this.checkCvMapping(idFF, "/MzQuantML/InputFiles/IdentificationFiles/IdentificationFile/fileFormat");
                    addMessages(cvMappingResult, this.msgL);
                }
            }
        }

        if (inputFiles.getMethodFiles() != null) {
            List<MethodFile> metFiles = inputFiles.getMethodFiles().getMethodFile();
            for (MethodFile metFile : metFiles) {
                // check MethodFileFormat_rule
                FileFormat metFF = metFile.getFileFormat();
                if (metFF != null) {
                    cvMappingResult = this.checkCvMapping(metFF, "/MzQuantML/InputFiles/MethodFiles/MethodFile/fileFormat");
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


        //AssayList assayList = this.mzq.getAssayList(); //jmzquantml 1.0.0-rc3-1.0.5
        AssayList assayList = unmarshaller.unmarshal(MzQuantMLElement.AssayList);
        if (assayList != null) {
            List<Assay> assays = assayList.getAssay();
            for (Assay assay : assays) {
                // check Assay_rule
                if (assay != null) {
                    cvMappingResult = this.checkCvMapping(assay, "/MzQuantML/AssayList/assay");
                    addMessages(cvMappingResult, this.msgL);
                    Label label = assay.getLabel();
                    if (label != null) {
                        // check AssayLabelModification_rule
                        List<ModParam> modParams = label.getModification();
                        if (modParams != null || !modParams.isEmpty()) {
                            for (ModParam modParam : modParams) {
                                cvMappingResult = this.checkCvMapping(modParam, "/MzQuantML/AssayList/Assay/Label/Modification");
                                addMessages(cvMappingResult, this.msgL);
                            }
                        }
                    }
                }
            }
        }

        // check StudyVariable_rule
        //StudyVariableList studyVariables = this.mzq.getStudyVariableList(); //jmzquantml 1.0.0-rc3-1.0.5
        StudyVariableList studyVariables = unmarshaller.unmarshal(MzQuantMLElement.StudyVariableList);
        if (studyVariables != null) {
            List<StudyVariable> studyVariableList = studyVariables.getStudyVariable();
            if (studyVariableList != null) {
                for (StudyVariable studyVariable : studyVariableList) {
                    cvMappingResult = this.checkCvMapping(studyVariable, "/MzQuantML/StudyVariableList/studyVariable");
                    addMessages(cvMappingResult, this.msgL);
                }
            }
        }

        // check AnalysisSummary_rule
        // TODO: already hardcoded, but may need to move back in future
//        ParamList analysisSummary = this.mzq.getAnalysisSummary();
//        if (analysisSummary != null) {
//            cvMappingResult = this.checkCvMapping(analysisSummary, "/MzQuantML/AnalysisSummary");
//            addMessages(cvMappingResult, this.msgL);
//        }

        /**
         * *************************
         *
         * check RatioList
         *
         * ************************
         */
        //RatioList ratioList = this.mzq.getRatioList(); //jmzquantml 1.0.0-rc3-1.0.5
        RatioList ratioList = unmarshaller.unmarshal(MzQuantMLElement.RatioList);
        if (ratioList != null) {
            List<Ratio> ratios = ratioList.getRatio();
            if (!ratios.isEmpty()) {
                for (Ratio ratio : ratios) {
                    // check RatioCalculation_rule
                    ParamList ratioCalculation = ratio.getRatioCalculation();
                    if (ratioCalculation != null) {
                        cvMappingResult = this.checkCvMapping(ratioCalculation, "/MzQuantML/RatioList/Ratio/ratioCalculation");
                        addMessages(cvMappingResult, this.msgL);
                    }

                    // check RatioNumeratorDataType_rule
                    CvParamRef numDT = ratio.getNumeratorDataType();
                    if (numDT != null) {
                        cvMappingResult = this.checkCvMapping(numDT, "/MzQuantML/RatioList/Ratio/NumeratorDataType");
                        addMessages(cvMappingResult, this.msgL);
                    }

                    //check RatioDenominatorDataType_rule
                    CvParamRef denDT = ratio.getDenominatorDataType();
                    if (denDT != null) {
                        cvMappingResult = this.checkCvMapping(denDT, "/MzQuantML/RatioList/Ratio/DenominatorDataType");
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
        //ProteinGroupList pgList = this.mzq.getProteinGroupList(); //jmzquantml 1.0.0-rc3-1.0.5
        ProteinGroupList pgList = unmarshaller.unmarshal(MzQuantMLElement.ProteinGroupList);
        if (pgList != null) {
            //check ProteinGroupList_rule
            cvMappingResult = this.checkCvMapping(pgList, "/MzQuantML/proteinGroupList");
            addMessages(cvMappingResult, this.msgL);

            //check ProteinGroup_rule
            List<ProteinGroup> proteinGroups = pgList.getProteinGroup();
            if (proteinGroups != null) {
                for (ProteinGroup pg : proteinGroups) {
                    cvMappingResult = this.checkCvMapping(pg, "/MzQuantML/ProteinGroupList/proteinGroup");
                    addMessages(cvMappingResult, this.msgL);

                    // check ProteinGroupProteinRef_rule
                    List<ProteinRef> protRefs = pg.getProteinRef();
                    if (protRefs != null) {
                        for (ProteinRef protRef : protRefs) {
                            cvMappingResult = this.checkCvMapping(protRef, "/MzQuantML/ProteinGroupList/ProteinGroup/ProteinRef");
                            addMessages(cvMappingResult, this.msgL);
                        }
                    }
                }
            }

            // check ProteinGroupGlobalQuantLayer_rule
            List<GlobalQuantLayer> pgGlobalQuantLayers = pgList.getGlobalQuantLayer();
            if (pgGlobalQuantLayers != null) {
                for (GlobalQuantLayer pgGQL : pgGlobalQuantLayers) {
                    if (pgGQL.getColumnDefinition() != null) {
                        List<Column> columns = pgGQL.getColumnDefinition().getColumn();
                        if (columns != null) {
                            for (Column col : columns) {
                                CvParamRef cvParamRef = col.getDataType();
                                cvMappingResult = this.checkCvMapping(cvParamRef, "/MzQuantML/ProteinGroupList/GlobalQuantLayer/ColumnDefinition/Column/dataType");
                                addMessages(cvMappingResult, this.msgL);
                            }
                        }
                    }
                }
            }

            // check ProteinGroupAssayQuantLayer_rule
            List<QuantLayer<IdOnly>> pgAssayQuantLayers = pgList.getAssayQuantLayer();
            if (pgAssayQuantLayers != null) {
                for (QuantLayer pgAQL : pgAssayQuantLayers) {
                    CvParamRef cvParamRef = pgAQL.getDataType();
                    cvMappingResult = this.checkCvMapping(cvParamRef, "/MzQuantML/ProteinGroupList/AssayQuantLayer/dataType");
                    addMessages(cvMappingResult, this.msgL);
                }
            }

            // check ProteinGroupStudyVariableQuantLayer_rule
            List<QuantLayer<IdOnly>> pgStudyVariableQuantLayers = pgList.getStudyVariableQuantLayer();
            if (pgStudyVariableQuantLayers != null) {
                for (QuantLayer pgSVQL : pgStudyVariableQuantLayers) {
                    CvParamRef cvParamRef = pgSVQL.getDataType();
                    cvMappingResult = this.checkCvMapping(cvParamRef, "/MzQuantML/ProteinGroupList/StudyVariableQuantLayer/dataType");
                    addMessages(cvMappingResult, this.msgL);
                }
            }
        }

        /**
         * ************************
         *
         * ProteinList
         *
         * *************************
         */
        //ProteinList protList = this.mzq.getProteinList(); //jmzquantml 1.0.0-rc3-1.0.5
        ProteinList protList = unmarshaller.unmarshal(MzQuantMLElement.ProteinList);
        if (protList != null) {
            // check ProteinList_rule
            cvMappingResult = this.checkCvMapping(protList, "/MzQuantML/proteinList");
            addMessages(cvMappingResult, this.msgL);

            // check Protein_rule
            List<Protein> proteins = protList.getProtein();
            if (proteins != null) {
                for (Protein protein : proteins) {
                    cvMappingResult = this.checkCvMapping(protein, "/MzQuantML/ProteinList/protein");
                    addMessages(cvMappingResult, this.msgL);
                }
            }

            List<GlobalQuantLayer> protGlobalQuantLayers = protList.getGlobalQuantLayer();
            if (protGlobalQuantLayers != null) {
                for (GlobalQuantLayer protGQL : protGlobalQuantLayers) {
                    // check ProteinGlobalQuantLayer_rule
                    if (protGQL.getColumnDefinition() != null) {
                        List<Column> columns = protGQL.getColumnDefinition().getColumn();
                        if (columns != null) {
                            for (Column col : columns) {
                                CvParamRef cvParamRef = col.getDataType();
                                cvMappingResult = this.checkCvMapping(cvParamRef, "/MzQuantML/ProteinList/GlobalQuantLayer/ColumnDefinition/Column/dataType");
                                addMessages(cvMappingResult, this.msgL);
                            }
                        }
                    }
                }
            }

            // check ProteinAssayQuantLayer_rule
            List<QuantLayer<IdOnly>> protAssayQuantLayers = protList.getAssayQuantLayer();
            if (protAssayQuantLayers != null) {
                for (QuantLayer protAQL : protAssayQuantLayers) {
                    CvParamRef cvParamRef = protAQL.getDataType();
                    cvMappingResult = this.checkCvMapping(cvParamRef, "/MzQuantML/ProteinList/AssayQuantLayer/dataType");
                    addMessages(cvMappingResult, this.msgL);
                }
            }

            // check ProteinStudyVariableQuantLayer_rule
            List<QuantLayer<IdOnly>> protStudyVariableQuantLayers = protList.getStudyVariableQuantLayer();
            if (protStudyVariableQuantLayers != null) {
                for (QuantLayer protSVQL : protStudyVariableQuantLayers) {
                    CvParamRef cvParamRef = protSVQL.getDataType();
                    cvMappingResult = this.checkCvMapping(cvParamRef, "/MzQuantML/ProteinList/StudyVariableQuantLayer/dataType");
                    addMessages(cvMappingResult, this.msgL);
                }
            }
        }

        /**
         * ******************************
         *
         * PeptideConsensusList
         *
         * ******************************
         */
        //List<PeptideConsensusList> pepLists = this.mzq.getPeptideConsensusList(); //jmzquantml 1.0.0-rc3-1.0.5
        Iterator<PeptideConsensusList> pepLists = unmarshaller.unmarshalCollectionFromXpath(MzQuantMLElement.PeptideConsensusList);
        if (pepLists != null) {
            //for (PeptideConsensusList pepList : pepLists) { //jmzquantml 1.0.0-rc3-1.0.5
            while (pepLists.hasNext()) {
                PeptideConsensusList pepList = pepLists.next();
                if (pepList != null) {
                    // check PeptideConsensusList_rule
                    cvMappingResult = this.checkCvMapping(pepList, "/MzQuantML/peptideConsensusList");
                    addMessages(cvMappingResult, this.msgL);

                    // check PeptideConsensus_rule
                    List<PeptideConsensus> peptides = pepList.getPeptideConsensus();
                    if (peptides != null) {
                        for (PeptideConsensus peptide : peptides) {
                            cvMappingResult = this.checkCvMapping(peptide, "/MzQuantML/PeptideConsensusList/peptideConsensus");
                            addMessages(cvMappingResult, this.msgL);
                        }
                    }

                    // check PeptideConsensusGlobalQuantLayer_rule
                    List<GlobalQuantLayer> pepGlobalQuantLayers = pepList.getGlobalQuantLayer();
                    if (pepGlobalQuantLayers != null) {
                        for (GlobalQuantLayer pepGQL : pepGlobalQuantLayers) {
                            if (pepGQL.getColumnDefinition() != null) {
                                List<Column> columns = pepGQL.getColumnDefinition().getColumn();
                                if (columns != null) {
                                    for (Column col : columns) {
                                        CvParamRef cvParamRef = col.getDataType();
                                        cvMappingResult = this.checkCvMapping(cvParamRef, "/MzQuantML/PeptideConsensusList/GlobalQuantLayer/ColumnDefinition/Column/dataType");
                                        addMessages(cvMappingResult, this.msgL);
                                    }
                                }
                            }
                        }
                    }

                    // check PeptideConsensusAssayQuantLayer_rule
                    List<QuantLayer<IdOnly>> pepAssayQuantLayers = pepList.getAssayQuantLayer();
                    if (pepAssayQuantLayers != null) {
                        for (QuantLayer pepAQL : pepAssayQuantLayers) {
                            CvParamRef cvParamRef = pepAQL.getDataType();
                            cvMappingResult = this.checkCvMapping(cvParamRef, "/MzQuantML/PeptideConsensusList/AssayQuantLayer/dataType");
                            addMessages(cvMappingResult, this.msgL);
                        }
                    }

                    // check PeptideConsensusStudyVariableQuantLayer_rule
                    List<QuantLayer<IdOnly>> pepStudyVariableQuantLayers = pepList.getStudyVariableQuantLayer();
                    if (pepStudyVariableQuantLayers != null) {
                        for (QuantLayer pepSVQL : pepStudyVariableQuantLayers) {
                            CvParamRef cvParamRef = pepSVQL.getDataType();
                            cvMappingResult = this.checkCvMapping(cvParamRef, "/MzQuantML/PeptideConsensusList/StudyVariableQuantLayer/dataType");
                            addMessages(cvMappingResult, this.msgL);
                        }
                    }

                    // check PeptideConsensusModification_rule
                    if (pepList.getPeptideConsensus() != null) {
                        for (PeptideConsensus pep : pepList.getPeptideConsensus()) {
                            List<Modification> mods = pep.getModification();
                            if (mods != null) {
                                cvMappingResult = this.checkCvMapping(mods, "/MzQuantML/PeptideConsensusList/PeptideConsensus/modification");
                                addMessages(cvMappingResult, this.msgL);
                            }
                        }
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
        //SmallMoleculeList smList = this.mzq.getSmallMoleculeList(); //jmzquantml 1.0.0-rc3-1.0.5
        SmallMoleculeList smList = unmarshaller.unmarshal(MzQuantMLElement.SmallMoleculeList);
        if (smList != null) {
            // check SmallMoleculeList_rule
            cvMappingResult = this.checkCvMapping(smList, "/MzQuantML/smallMoleculeList");
            addMessages(cvMappingResult, this.msgL);

            // check SmallMolecule_rule
            List<SmallMolecule> smallMols = smList.getSmallMolecule();
            if (smallMols != null) {
                for (SmallMolecule smallMol : smallMols) {
                    cvMappingResult = this.checkCvMapping(smallMol, "/MzQuantML/SmallMoleculeList/smallMolecule");
                    addMessages(cvMappingResult, this.msgL);

                    // check SmallMoleculeModification_rule
                    List<SmallMolModification> smallMolMods = smallMol.getModification();
                    if (smallMolMods != null) {
                        cvMappingResult = this.checkCvMapping(smallMolMods, "/MzQuantML/SmallMoleculeList/SmallMolecule/Modification");
                        addMessages(cvMappingResult, this.msgL);
                    }
                }
            }

            List<GlobalQuantLayer> smGlobalQuantLayers = smList.getGlobalQuantLayer();
            if (smGlobalQuantLayers != null) {
                // check SmallMoleculeGlobalQuantLayer_rule
                for (GlobalQuantLayer smGQL : smGlobalQuantLayers) {
                    if (smGQL.getColumnDefinition() != null) {
                        List<Column> columns = smGQL.getColumnDefinition().getColumn();
                        if (columns != null) {
                            for (Column col : columns) {
                                CvParamRef cvParamRef = col.getDataType();
                                cvMappingResult = this.checkCvMapping(cvParamRef, "/MzQuantML/SmallMoleculeList/GlobalQuantLayer/ColumnDefinition/Column/dataType");
                                addMessages(cvMappingResult, this.msgL);
                            }
                        }
                    }
                }
            }

            // check SmallMoleculeAssayQuantLayer_rule
            List<QuantLayer<IdOnly>> smAssayQuantLayers = smList.getAssayQuantLayer();
            if (smAssayQuantLayers != null) {
                for (QuantLayer smAQL : smAssayQuantLayers) {
                    CvParamRef cvParamRef = smAQL.getDataType();
                    cvMappingResult = this.checkCvMapping(cvParamRef, "/MzQuantML/SmallMoleculeList/AssayQuantLayer/dataType");
                    addMessages(cvMappingResult, this.msgL);
                }
            }

            // check SmallMoleculeStudyVariableQuantLayer_rule
            List<QuantLayer<IdOnly>> smStudyVariableQuantLayers = smList.getStudyVariableQuantLayer();
            if (smStudyVariableQuantLayers != null) {
                for (QuantLayer smSVQL : smStudyVariableQuantLayers) {
                    CvParamRef cvParamRef = smSVQL.getDataType();
                    cvMappingResult = this.checkCvMapping(cvParamRef, "/MzQuantML/SmallMoleculeList/StudyVariableQuantLayer/dataType");
                    addMessages(cvMappingResult, this.msgL);
                }
            }
        }

        /**
         * ******************************
         *
         * FeatureList
         *
         * ******************************
         */
        //List<FeatureList> featureLists = this.mzq.getFeatureList(); //jmzquantml 1.0.0-rc3-1.0.5
        Iterator<FeatureList> featureLists = unmarshaller.unmarshalCollectionFromXpath(MzQuantMLElement.FeatureList);
        if (featureLists != null) {
            //for (FeatureList ftList : featureLists) { //jmzquantml 1.0.0-rc3-1.0.5
            while (featureLists.hasNext()) {
                FeatureList ftList = featureLists.next();
                if (ftList != null) {
                    // check FeatureList_rule
                    cvMappingResult = this.checkCvMapping(ftList, "/MzQuantML/featureList");
                    addMessages(cvMappingResult, this.msgL);

                    // check Feature_rule
                    List<Feature> features = ftList.getFeature();
                    if (features != null) {
                        for (Feature feature : features) {
                            cvMappingResult = this.checkCvMapping(feature, "/MzQuantML/FeatureList/feature");
                            addMessages(cvMappingResult, this.msgL);
                        }
                    }

                    // check FeatureListFeatureQuantLayer_rule
                    List<GlobalQuantLayer> ftGlobalQuantLayers = ftList.getFeatureQuantLayer();
                    if (ftGlobalQuantLayers != null) {
                        for (GlobalQuantLayer ftGQL : ftGlobalQuantLayers) {
                            if (ftGQL.getColumnDefinition() != null) {
                                List<Column> columns = ftGQL.getColumnDefinition().getColumn();
                                if (columns != null) {
                                    for (Column col : columns) {
                                        CvParamRef cvParamRef = col.getDataType();
                                        cvMappingResult = this.checkCvMapping(cvParamRef, "/MzQuantML/FeatureList/FeatureQuantLayer/ColumnDefinition/Column/dataType");
                                        addMessages(cvMappingResult, this.msgL);
                                    }
                                }
                            }
                        }
                    }

                    // check FeatureListMS2AssayQuantLayer_rule
                    List<QuantLayer<IdOnly>> ftAssayQuantLayers = ftList.getMS2AssayQuantLayer();
                    if (ftAssayQuantLayers != null) {
                        for (QuantLayer ftAQL : ftAssayQuantLayers) {
                            CvParamRef cvParamRef = ftAQL.getDataType();
                            cvMappingResult = this.checkCvMapping(cvParamRef, "/MzQuantML/FeatureList/MS2AssayQuantLayer/dataType");
                            addMessages(cvMappingResult, this.msgL);
                        }
                    }

                    // check FeatureListMS2StudyVariableQuantLayer_rule
                    List<QuantLayer<IdOnly>> ftStudyVariableQuantLayers = ftList.getMS2StudyVariableQuantLayer();
                    if (ftStudyVariableQuantLayers != null) {
                        for (QuantLayer ftSVQL : ftStudyVariableQuantLayers) {
                            CvParamRef cvParamRef = ftSVQL.getDataType();
                            cvMappingResult = this.checkCvMapping(cvParamRef, "/MzQuantML/FeatureList/MS2StudyVariableQuantLayer/dataType");
                            addMessages(cvMappingResult, this.msgL);
                        }
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

        // check Software_rule_1 and Software_rule_2
        //SoftwareList softwareList = this.mzq.getSoftwareList(); //jmzquantml 1.0.0-rc3-1.0.5
        SoftwareList softwareList = unmarshaller.unmarshal(MzQuantMLElement.SoftwareList);
        if (softwareList != null) {
            List<Software> softwares = softwareList.getSoftware();
            if (softwares != null) {
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

        // check DataProcessing_rule
        //DataProcessingList dpList = this.mzq.getDataProcessingList(); //jmzquantml 1.0.0-rc3-1.0.5
        DataProcessingList dpList = unmarshaller.unmarshal(MzQuantMLElement.DataProcessingList);
        if (dpList != null) {
            List<DataProcessing> dps = dpList.getDataProcessing();
            if (dps != null) {
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
     *         of the given input stream.
     *
     * @throws IOException if an IO error occur.
     */
    private File storeAsTemporaryFile(InputStream is)
            throws IOException {

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
                                                       String xPath)
            throws ValidatorException {
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
        }
        else {
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
