import os
import pandas as pd
import matplotlib.pyplot as plt
from pyhecdss import *

def plot_x2_limiters(out_dir, out_file, title="X2 Limiters"):
    fpath = os.path.join(out_dir, out_file)

    # Define DSS pathnames for standards and model output
    regular_paths = {
        "roe": ("//X2_STD_ROE_NDAY/////", "//X2_ROE_NDAY_PREV/////"),
        "chs": ("//X2_STD_CHS_NDAY/////", "//X2_CHS_NDAY_PREV/////"),
        "cnf": ("//X2_STD_CNF_NDAY/////", "//X2_CNF_NDAY_PROJ/////")
    }

    fall_paths = {
        "cnf": ("//X2_PRV_KM/////", "//KM_CNF_MOD_/////")
    }

    names = {
        "roe": "Roe Island (Port Chicago)",
        "chs": "Chipps Island (Mallard)",
        "cnf": "Confluence (Collinsville)"
    }

    fig, axes = plt.subplots(4, sharex=True, figsize=(8, 10))

    # Plot regular X2 standards (days)
    for i, loc in enumerate(["roe", "chs", "cnf"]):
        std_path, model_path = regular_paths[loc]
        std = list(pyhecdss.get_ts(fpath, std_path))[0].data
        model = list(pyhecdss.get_ts(fpath, model_path))[0].data
        if "PREV" in model_path: 
            model = model.shift(-1)

        std[std.index.month.isin([8, 9, 10, 11])] = pd.NA
        #model[model.index.month.isin([8, 9, 10, 11])] = pd.NA

        std.plot(ax=axes[i], linewidth=4, color="0.35")
        model.plot(ax=axes[i])

        axes[i].legend(["Standard", "CalSim"])
        axes[i].set_title(names[loc])
        axes[i].set_ylabel("Days")

    # Plot fall X2 standard (km)
    fall_model = list(pyhecdss.get_ts(fpath, fall_paths["cnf"][0]))[0].data.shift(-1)
    fall_std = list(pyhecdss.get_ts(fpath, fall_paths["cnf"][1]))[0].data
    fall_std_filtered = fall_std.copy()
    fall_std_filtered[~fall_std_filtered.index.month.isin([8, 9, 10])] = pd.NA

    fall_std_filtered.plot(ax=axes[3], linewidth=4, color="0.35")
    fall_model.plot(ax=axes[3])
    axes[3].legend(["Standard", "CalSim"])
    axes[3].set_title("Confluence Fall Standard")
    axes[3].set_ylabel("X2 (km)")

    fig.suptitle(title)
    plt.tight_layout()
    plt.show()


if __name__ == "__main__":
    out_dir = "../DSS/output"
    out_file = "DCR2023_DV_9.3.1_Danube_Adj_v1.8.dss"
    plot_x2_limiters(out_dir, out_file, title="X2 Standards Compliance")
