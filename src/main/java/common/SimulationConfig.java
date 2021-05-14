package common;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import common.parameters.EpidemicLayerParameters;
import common.parameters.ParametersRange;
import common.parameters.QVoterParameters;

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
    private double infectedFraction;
    private double fractionIllnessA;
    private double fractionIllnessB;
    private String outputFolder;
    private ParametersRange firstParameterRange;

    public SimulationConfig() {
    }

    public static SimulationConfig loadConfig(String filePath) throws IOException {
        var objectMapper = new ObjectMapper(new YAMLFactory());
        return objectMapper.readValue(new File(filePath), SimulationConfig.class);
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
                ", infectedFraction=" + infectedFraction +
                ", fractionIllnessA=" + fractionIllnessA +
                ", fractionIllnessB=" + fractionIllnessB +
                ", outputFolder='" + outputFolder + '\'' +
                ", firstParameterRange=" + firstParameterRange +
                '}';
    }
}
