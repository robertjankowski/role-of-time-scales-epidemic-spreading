import yaml
import os
import subprocess
import sys
import tempfile

def run_model(prefix, config_file):
    command = f"""
        nohup mvn compile exec:java -Dexec.mainClass="pl.wf.Main" -Dexec.args="{config_file} {prefix} &"
    """.strip()
    print(command)
    os.system(command)


def read_config(path: str):
    with open(path, "r") as f:
        data = yaml.load(f, yaml.FullLoader)
        return data


def extract_params(params):
    """
    Format:
    > basename:name=x,y,z

    if name is not present:
    > :name=x,y,z,w

    Example:
    > qVoterParameters:q=1,2,3,4
    > :linksRemoval=true,false
    """
    name, values = params.split("=")
    values = values.split(",")
    names = name.split(":")
    return names, values


def run_grid_search_with_params(config, params, prefix):
    name, values = extract_params(params)
    for v in values:
        if name[0]:
            config[name[0]][name[1]] = v
        else:
            config[name[1]] = v

        tmp_config = tempfile.mktemp()
        with open(tmp_config, 'w') as f:
            yaml.dump(config, f)
        run_model(prefix, tmp_config)


if __name__ == "__main__":
    if len(sys.argv) != 4:
        raise ValueError("Error. Provide config file and params list in format e.g.: 'qVoterParameters:q=1,2,3,4,5,6,7'")
    config = read_config(sys.argv[1])
    params = sys.argv[2]
    prefix = sys.argv[3]
    run_grid_search_with_params(config, params, prefix)
