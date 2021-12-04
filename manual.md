### Installation

The project is built using Maven tool. Make sure to install `mvn` (see: https://maven.apache.org/install.html).

Tested version:
```shell
✦ ➜ mvn --version            Apache Maven 3.8.2 (ea98e05a04480131370aa0c110b8c54cf726c06f)
Maven home: /home/rob/software/maven/apache-maven-3.8.2
Java version: 16.0.2, vendor: Oracle Corporation, runtime: /usr/lib/jvm/java-16-oracle
Default locale: en_US, platform encoding: UTF-8
OS name: "linux", version: "5.4.0-91-generic", arch: "amd64", family: "unix"
```
It is recommended to have Java 16 installed.

### How to run the simulations?

1. Copy config below and modify to your needs (see explanations below)
2. Run `run.sh` script, e.g.,
   ```shell
    ./run.sh config/example_config_model_2.yaml ex1
   ```
   with the name of the config file as the first argument and the prefix of the each file.
3. Alternatively one could run multiple simulations in the following way.
    ```shell
    python scripts/run_modify_config.py config_file mainParameter:secondParameter=val1,val2,val3
    ```   
    - first parameter -- config file
    - second parameter -- a list of parameters, see `script/run_modif_config.py` for some examples

### Description of the config file

```yaml
nAgents: 10000 # number of agents/nodes
nSteps: 100    # number of steps 
nSaveSteps: 1  # save metrics per nSaveSteps
nRuns: 10      # each simulation is run nRuns
networkP: 0.8  # probability of triad formation, see Holme and Kim paper
networkM: 10   # see Barabasi Albert model, mean degree (<k>) = 2 * m
additionalLinksFraction: 0.01 # additional fraction of links in the opinion layer 
virtualLayer: true  # turn on/off opinion layer
epidemicLayer: true # turn on/off epidemic layer
comorbidities: false # turn on/off comorbidities (not implemented yet)
qVoterParameters:
  p: 0.01 # probability of independence
  q: 6    # size of q-lobby
epidemicLayerParameters:
  beta: 0.9   # probability of S -> I
  alpha: 0.0  # effectiveness of the vaccination 
  zeta: 0.0   # attitude towards vaccination
  gamma: 0.5  # probability of I -> Q
  mu: 0.9     # probability of I/Q -> R
  kappa: 0.1  # probability of I/Q -> D
maxInfectedTimeMean: 10 # time in infected state is drawn from normal distribution ... 
maxInfectedTimeStd: 5   # ... t_i ~ N(maxInfectedTimeMean, maxInfectedTimeStd^2)
positiveOpinionFraction: 1.0 # initial fraction of positive agents 
infectedFraction: 0.1  # initial fraction of infected agents
fractionIllnessA: 0.0  # fraction of agents with illness A
fractionIllnessB: 0.0  # fraction of agents with illness B
proPisFraction: 0.0 # fraction of people supporting PiS
pisVaccinationCorrelation: 0.0 # correlation between vaccination rate and PiS support (assumption: negative)
outputFolder: simulations/experiment1 # path to output files 
firstParameterRange: # see below
  start: 0.0
  end: 0.5
  count: 51
  vals: null
  type: p
secondParameterRange:
  start: 0.0
  end: 1.0
  count: 21
  vals: [ 0.01, 0.02, 0.05, 0.1, 0.5 ]
  type: beta
thirdParameterRange:
  start: 0.0
  end: 0.0
  count: 0
  vals: [ 1, 5, 10, 20 ]
  type: nQVoterPerStep
linksRemoval: false # remove links in quarantine state (not used in paper)
nQVoterPerStep: 1 # number of q-voter updates per one epidemic update 
```

Currently, one could run multiple simulations for a given parameter range by using `firstParameterRange`,
`secondParameterRange` and `thirdParameterRange`. Example:

```yaml
firstParameterRange:
  start: 0.0 # initial value 
  end: 0.5   # final value
  count: 51  # number of values
  vals: null
  type: p    # parameter type
```

or

```yaml
firstParameterRange:
  start: 0.0
  end: 0.5
  count: 51
  vals: [ 0.1, 0.5, 0.9 ] # list of values
  type: p    # parameter type
```

If `vals` field is not null then a list of values is constructed. Otherwise `vals` list is used in the simulations. If
you do not need to run simulations for many parameters you can turn off some of them, e.g.,
set `secondParameterRange: null`.

### How to add a new parameter in `....ParameterRange`?

Let say that you want to run simulations for different value of initial infection fraction (`infectedFraction`). Right
now it is not supported, but here I show you how it could be easily incorporated.

1. Go to `parameters/ParameterType.jave` file and add `infectedFraction` to the enum class
2. Go to `simulation.Simulation.java` file and find `updateParameter` function. You need to add a switch statement for
   that parameters, e.g.,
    ```java
    switch(type) {
    ...
    case infectedFraction -> config.setInfectionFraction(x);
    }
    ```
   Make sure that in `SimulationConfig.java` file that parameter is already added with setter and getter methods.
3. Voila, now you can run simulations for different values of `infectedFraction`
   ```yaml
   ...
   firstParameterRange:
   start: 0.0
   end: 0.5
   count: 51
   vals: [0.1, 0.5, 0.9]
   type: infectedFraction
   ...
   ```

### How to add new parameter?

The `SimulationConfig.java` file is responsible for reading the config file. When one wants to add new parameter to the
simulations, one needs to add it into that file. Alongside, the private field one has to define getter and setter method
and add that parameter in copy constructor. Once you have it, that parameter is easily accessible in
the `Simulation.java`.


### Info about source files

```
├── common
│   ├── agent
│   │   ├── Agent.java # intristic parameters of each agent 
│   │   ├── AgentMetrics.java # calculate main metrics of population of the agents
│   │   └── AgentState.java # epidemic states of the agents
│   ├── AgeStatistics.java # loader for age statistics of population in Poland
│   ├── network
│   │   ├── Initializer.java # initialize a list of agents with a given parameters
│   │   ├── Layer.java # basic graph class
│   │   ├── Network.java # graph generators, Barabasi Albert model, create bilayer network
│   │   └── PowerlawClusterGraphGenerator.java # Holme and Kim model (powerlawClusterGraph)
│   ├── parameters
│   │   ├── EpidemicLayerParameters.java # parameters of epidemic, the same for all agents
│   │   ├── ParametersRange.java # class to handle running multiple simulations
│   │   ├── ParameterType.java # parameter type used in ...ParametersRange section in config file
│   │   └── QVoterParameters.java # parameters of q-voter model
│   └── SimulationConfig.java # file 
├── Main.java # main class, better to run `run.sh` script
├── simulation
│   └── Simulation.java # the core of the simulations
└── utils
    ├── ImprovedRandomSpliterator.java
    ├── RandomCollectors.java
    └── Utils.java
```


