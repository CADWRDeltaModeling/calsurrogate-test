#!/usr/bin/env python
# -*- coding: utf-8 -*

import os
import pandas as pd
import matplotlib.pyplot as plt
from pyhecdss import *


def plot_limiters(out_dir, out_file, title="D1641 Limiters"):
    fpath = os.path.join(out_dir, out_file)

    monthly_paths = {
        "emm": "//EM_EC_MONTH/////",
        "jer": "//JP_EC_MONTH/////",
        "rsl": "//RS_EC_MONTH/////",
    }
    std_paths = {
        "emm": "//EM_EC_STD/////",
        "jer": "//JP_EC_STD/////",
        "rsl": "//RS_EC_STD/////",
    }

    names = {"emm": "Emmaton", "jer": "Jersey Point", "rsl": "Rock Slough"}

    fig, axes = plt.subplots(len(names), sharex=True, figsize=(7, 7))

    for ax, name in zip(axes, names.keys()):
        stds = list(pyhecdss.get_ts(fpath, std_paths[name]))
        std = stds[0].data
        std = std.mask(std > 3000.0)
        monthly_ecs = list(pyhecdss.get_ts(fpath, monthly_paths[name]))  
        monthly_ec = monthly_ecs[0].data.shift(-1)
        label = names[name]
        std.plot(ax=ax,linewidth=4,color="0.35")
        monthly_ec.plot(ax=ax)
        ax.legend(["Standard","CalSim"])
        ax.set_title(names[name])
        ax.set_ylabel(r"$\mu S/cm$")

    fig.suptitle(title)
    plt.tight_layout()
    plt.show()


if __name__ == "__main__":
    out_dir = "../DSS/output"
    out_file = "2023DCR_Hist_DV.dss"
    plot_limiters(out_dir, out_file,title="Emmaton Control No Reduced Calls")
