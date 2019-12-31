# ProteinNetworkAnalysis

Create local jre env
```shell script
> jlink --module-path $PATH_TO_FX_MODS:mods/production --add-modules ProteinNetworkAnalysis --output jre
```

Get into the root folder of the project
```shell script
> jre/bin/java -m ProteinNetworkAnalysis/home.Main
```