package pl.wf.common;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import pl.wf.common.parameters.EpidemicLayerParameters;
import pl.wf.common.parameters.ParametersRange;
import pl.wf.common.parameters.QVoterParameters;

import java.io.File;
import java.io.IOException;

public class SimulationConfig {
    private int nAgents;
    private int nSteps;
    private int nSaveSteps;
    private int nRuns;
    private double networkP;
    private int networkM;
    private double additionalLinksFraction;
    private boolean isEpidemicLayer;
    private boolean isVirtualLayer;
    private boolean isComorbidities;
    private QVoterParameters qVoterParameters;
    private EpidemicLayerParameters epidemicLayerParameters;
    private double positiveOpinionFraction;
    private double proPisFraction;
    // Correlation between people who vote for PiS and the number of vaccinated at the Powiat level
    private double pisVaccinationCorrelation;
    private double infectedFraction;
    private double fractionIllnessA;
    private double fractionIllnessB;
    private double maxInfectedTimeMean;
    private double maxInfectedTimeStd;
    private String outputFolder;
    private ParametersRange firstParameterRange;
    private ParametersRange secondParameterRange;
    private ParametersRange thirdParameterRange;
    private int nQVoterPerStep;
    private boolean isLinksRemoval;
    private double vaccinationFraction;
    private boolean isNeglectNeighboursPiS;
    private boolean isFilterBubble;
    private boolean isDegreeCorrelated;

    public static SimulationConfig loadConfig(String filePath) throws IOException {
        var objectMapper = new ObjectMapper(new YAMLFactory());
        return objectMapper.readValue(new File(filePath), SimulationConfig.class);
    }

    public SimulationConfig() {
    }

    public SimulationConfig(SimulationConfig other) {
        this.nAgents = other.nAgents;
        this.nSteps = other.nSteps;
        this.nSaveSteps = other.nSaveSteps;
        this.nRuns = other.nRuns;
        this.networkP = other.networkP;
        this.networkM = other.networkM;
        this.additionalLinksFraction = other.additionalLinksFraction;
        this.isEpidemicLayer = other.isEpidemicLayer;
        this.isVirtualLayer = other.isVirtualLayer;
        this.isComorbidities = other.isComorbidities;
        this.qVoterParameters = new QVoterParameters(other.qVoterParameters);
        this.epidemicLayerParameters = new EpidemicLayerParameters(other.epidemicLayerParameters);
        this.positiveOpinionFraction = other.positiveOpinionFraction;
        this.proPisFraction = other.proPisFraction;
        this.pisVaccinationCorrelation = other.pisVaccinationCorrelation;
        this.infectedFraction = other.infectedFraction;
        this.fractionIllnessA = other.fractionIllnessA;
        this.fractionIllnessB = other.fractionIllnessB;
        this.maxInfectedTimeMean = other.maxInfectedTimeMean;
        this.maxInfectedTimeStd = other.maxInfectedTimeStd;
        this.outputFolder = other.outputFolder;
        this.firstParameterRange = other.firstParameterRange;
        this.secondParameterRange = other.secondParameterRange;
        this.thirdParameterRange = other.thirdParameterRange;
        this.nQVoterPerStep = other.nQVoterPerStep;
        this.isLinksRemoval = other.isLinksRemoval;
        this.vaccinationFraction = other.vaccinationFraction;
        this.isNeglectNeighboursPiS = other.isNeglectNeighboursPiS;
        this.isFilterBubble = other.isFilterBubble;
        this.isDegreeCorrelated = other.isDegreeCorrelated;
    }

    public int getnAgents() {
        return nAgents;
    }

    public void setnAgents(int nAgents) {
        this.nAgents = nAgents;
    }

    public boolean isComorbidities() {
        return isComorbidities;
    }

    public void setComorbidities(boolean comorbidities) {
        isComorbidities = comorbidities;
    }

    public QVoterParameters getqVoterParameters() {
        return qVoterParameters;
    }

    public void setqVoterParameters(QVoterParameters qVoterParameters) {
        this.qVoterParameters = qVoterParameters;
    }

    public void setEpidemicLayerParameters(EpidemicLayerParameters epidemicLayerParameters) {
        this.epidemicLayerParameters = epidemicLayerParameters;
    }

    public EpidemicLayerParameters getEpidemicLayerParameters() {
        return epidemicLayerParameters;
    }

    public int getnSteps() {
        return nSteps;
    }

    public void setnSteps(int nSteps) {
        this.nSteps = nSteps;
    }

    public double getInfectedFraction() {
        return infectedFraction;
    }

    public void setInfectedFraction(double infectedFraction) {
        this.infectedFraction = infectedFraction;
    }

    public double getFractionIllnessA() {
        return fractionIllnessA;
    }

    public void setFractionIllnessA(double fractionIllnessA) {
        this.fractionIllnessA = fractionIllnessA;
    }

    public double getFractionIllnessB() {
        return fractionIllnessB;
    }

    public void setFractionIllnessB(double fractionIllnessB) {
        this.fractionIllnessB = fractionIllnessB;
    }

    public double getPositiveOpinionFraction() {
        return positiveOpinionFraction;
    }

    public void setPositiveOpinionFraction(double positiveOpinionFraction) {
        this.positiveOpinionFraction = positiveOpinionFraction;
    }

    public String getOutputFolder() {
        return outputFolder;
    }

    public void setOutputFolder(String outputFolder) {
        this.outputFolder = outputFolder;
    }

    public double getAdditionalLinksFraction() {
        return additionalLinksFraction;
    }

    public void setAdditionalLinksFraction(double additionalLinksFraction) {
        this.additionalLinksFraction = additionalLinksFraction;
    }

    public boolean isEpidemicLayer() {
        return isEpidemicLayer;
    }

    public void setEpidemicLayer(boolean epidemicLayer) {
        isEpidemicLayer = epidemicLayer;
    }

    public boolean isVirtualLayer() {
        return isVirtualLayer;
    }

    public void setVirtualLayer(boolean virtualLayer) {
        isVirtualLayer = virtualLayer;
    }

    public int getnSaveSteps() {
        return nSaveSteps;
    }

    public void setnSaveSteps(int nSaveSteps) {
        this.nSaveSteps = nSaveSteps;
    }

    public int getnRuns() {
        return nRuns;
    }

    public void setnRuns(int nRuns) {
        this.nRuns = nRuns;
    }

    public ParametersRange getFirstParameterRange() {
        return firstParameterRange;
    }

    public void setFirstParameterRange(ParametersRange firstParameterRange) {
        this.firstParameterRange = firstParameterRange;
    }

    public double getNetworkP() {
        return networkP;
    }

    public void setNetworkP(double networkP) {
        this.networkP = networkP;
    }

    public int getNetworkM() {
        return networkM;
    }

    public void setNetworkM(int networkM) {
        this.networkM = networkM;
    }

    public double getMaxInfectedTimeMean() {
        return maxInfectedTimeMean;
    }

    public void setMaxInfectedTimeMean(double maxInfectedTimeMean) {
        this.maxInfectedTimeMean = maxInfectedTimeMean;
    }

    public double getMaxInfectedTimeStd() {
        return maxInfectedTimeStd;
    }

    public void setMaxInfectedTimeStd(double maxInfectedTimeStd) {
        this.maxInfectedTimeStd = maxInfectedTimeStd;
    }

    public ParametersRange getSecondParameterRange() {
        return secondParameterRange;
    }

    public void setSecondParameterRange(ParametersRange secondParameterRange) {
        this.secondParameterRange = secondParameterRange;
    }

    public int getnQVoterPerStep() {
        return nQVoterPerStep;
    }

    public void setnQVoterPerStep(int nQVoterPerStep) {
        this.nQVoterPerStep = nQVoterPerStep;
    }

    public boolean isLinksRemoval() {
        return isLinksRemoval;
    }

    public void setLinksRemoval(boolean linksRemoval) {
        isLinksRemoval = linksRemoval;
    }

    public double getProPisFraction() {
        return proPisFraction;
    }

    public void setProPisFraction(double proPisFraction) {
        this.proPisFraction = proPisFraction;
    }

    public double getPisVaccinationCorrelation() {
        return pisVaccinationCorrelation;
    }

    public void setPisVaccinationCorrelation(double pisVaccinationCorrelation) {
        this.pisVaccinationCorrelation = pisVaccinationCorrelation;
    }

    public double getVaccinationFraction() {
        return vaccinationFraction;
    }

    public void setVaccinationFraction(double vaccinationFraction) {
        this.vaccinationFraction = vaccinationFraction;
    }

    public boolean isNeglectNeighboursPiS() {
        return isNeglectNeighboursPiS;
    }

    public void setNeglectNeighboursPiS(boolean neglectNeighboursPiS) {
        isNeglectNeighboursPiS = neglectNeighboursPiS;
    }

    public ParametersRange getThirdParameterRange() {
        return thirdParameterRange;
    }

    public void setThirdParameterRange(ParametersRange thirdParameterRange) {
        this.thirdParameterRange = thirdParameterRange;
    }

    public boolean isFilterBubble() {
        return isFilterBubble;
    }

    public void setFilterBubble(boolean filterBubble) {
        isFilterBubble = filterBubble;
    }

    public boolean isDegreeCorrelated() {
        return isDegreeCorrelated;
    }

    public void setDegreeCorrelated(boolean degreeCorrelated) {
        isDegreeCorrelated = degreeCorrelated;
    }

    @Override
    public String toString() {
        return "SimulationConfig{" +
                "nAgents=" + nAgents +
                ", nSteps=" + nSteps +
                ", nSaveSteps=" + nSaveSteps +
                ", nRuns=" + nRuns +
                ", networkP=" + networkP +
                ", networkM=" + networkM +
                ", additionalLinksFraction=" + additionalLinksFraction +
                ", isEpidemicLayer=" + isEpidemicLayer +
                ", isVirtualLayer=" + isVirtualLayer +
                ", isComorbidities=" + isComorbidities +
                ", qVoterParameters=" + qVoterParameters +
                ", epidemicLayerParameters=" + epidemicLayerParameters +
                ", positiveOpinionFraction=" + positiveOpinionFraction +
                ", proPisFraction=" + proPisFraction +
                ", pisVaccinationCorrelation=" + pisVaccinationCorrelation +
                ", infectedFraction=" + infectedFraction +
                ", fractionIllnessA=" + fractionIllnessA +
                ", fractionIllnessB=" + fractionIllnessB +
                ", maxInfectedTimeMean=" + maxInfectedTimeMean +
                ", maxInfectedTimeStd=" + maxInfectedTimeStd +
                ", outputFolder='" + outputFolder + '\'' +
                ", firstParameterRange=" + firstParameterRange +
                ", secondParameterRange=" + secondParameterRange +
                ", thirdParameterRange=" + thirdParameterRange +
                ", nQVoterPerStep=" + nQVoterPerStep +
                ", isLinksRemoval=" + isLinksRemoval +
                ", vaccinationFraction=" + vaccinationFraction +
                ", isNeglectNeighboursPiS=" + isNeglectNeighboursPiS +
                ", isFilterBubble=" + isFilterBubble +
                ", isDegreeCorrelated=" + isDegreeCorrelated +
                '}';
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return (SimulationConfig) super.clone();
    }
}
