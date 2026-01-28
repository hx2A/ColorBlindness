from collections import defaultdict
from pathlib import Path

import jpype
import jpype.imports
from pandas import DataFrame

jpype.startJVM(
    classpath=[
        "/home/jim/INSTALL/processing-4.4.10/lib/app/core-4.4.10-a15ddaffbbd75eb4394dbaa07893d8c9.jar",
        "./build/libs/ColorBlindness.jar",
    ]
)

from colorblind import Deficiency
from colorblind.generators import ColorDeficiencySimulator

OUTPUT_DIR = Path("luts")
OUTPUT_DIR.mkdir(exist_ok=True)

deficiency = Deficiency.BLUE_CONE_MONOCHROMACY

generator = ColorDeficiencySimulator(deficiency)
lut = generator.getColorMap()

data = defaultdict(list)

for c in range(lut.length):
    s = lut[c]

    r, g, b = (c >> 16) & 0xFF, (c >> 8) & 0xFF, c & 0xFF
    sr, sg, sb = (s >> 16) & 0xFF, (s >> 8) & 0xFF, s & 0xFF

    data["r"].append(r)
    data["g"].append(g)
    data["b"].append(b)
    data["simulated_r"].append(sr)
    data["simulated_g"].append(sg)
    data["simulated_b"].append(sb)

df = DataFrame(data)

df.to_parquet(
    OUTPUT_DIR / f"{str(deficiency).lower()}.parquet", index=False, compression="gzip"
)

jpype.shutdownJVM()
