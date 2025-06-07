import os
import pandas as pd
import matplotlib.pyplot as plt
from pyhecdss import *

def plot_mtz_beldon_smscg(primary_path, reference_path, title="Martinez & Beldon's EC Comparison"):
    paths = {
        "mtz_ec": "//MTZ_EC_7DAY/////",
        "bd_ec": "//BD_EC_MONTH/////",
        "smscg": "//SMSCG_OP/////"
    }

    # Read time series from both files
    mtz_primary = next(get_ts(primary_path, paths["mtz_ec"])).data
    mtz_reference = next(get_ts(reference_path, paths["mtz_ec"])).data

    bd_primary = next(get_ts(primary_path, paths["bd_ec"])).data
    bd_reference = next(get_ts(reference_path, paths["bd_ec"])).data

    smscg_primary = next(get_ts(primary_path, paths["smscg"])).data
    smscg_reference = next(get_ts(reference_path, paths["smscg"])).data + 0.1

    fig, axes = plt.subplots(3, sharex=True, figsize=(10, 8))

    # Martinez EC
    axes[0].plot(mtz_primary.index.to_timestamp(how="start"), mtz_primary.values, label="New ANN")
    axes[0].plot(mtz_reference.index.to_timestamp(how="start"), mtz_reference.values, label="DCR Reference")
    axes[0].set_title("Martinez EC (7-day Avg)")
    axes[0].set_ylabel(r"$\mu S/cm$")
    axes[0].legend()

    # Beldon's Landing EC
    axes[1].plot(bd_primary.index.to_timestamp(how="start"), bd_primary.values, label="New ANN")
    axes[1].plot(bd_reference.index.to_timestamp(how="start"), bd_reference.values, label="DCR Reference")
    axes[1].set_title("Beldon's Landing EC (Monthly Avg)")
    axes[1].set_ylabel(r"$\mu S/cm$")
    axes[1].legend()

    # Suisun Marsh Salinity Control Gate Operation
    axes[2].step(smscg_primary.index.to_timestamp(how="start"), smscg_primary.values, where="post", label="New ANN")
    axes[2].step(smscg_reference.index.to_timestamp(how="start"), smscg_reference.values, where="post", label="DCR Reference")
    axes[2].set_title("Suisun Marsh Salinity Control Gate Operation")
    axes[2].set_ylabel("Gate Status (0/1)")
    axes[2].legend()

    fig.suptitle(title)
    plt.tight_layout()
    plt.show()

if __name__ == "__main__":
    primary_dss = os.path.abspath("../DSS/output/DCR2023_DV_9.3.1_Danube_Adj_v1.8.dss")
    reference_dss = os.path.abspath("../../9.3.1_danube_adj_ref/DSS/output/DCR2023_DV_9.3.1_v2a_Danube_Adj_v1.8.dss")
    plot_mtz_beldon_smscg(primary_dss, reference_dss)
