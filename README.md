# wedm
Web Extensible Display Manager

## Introduction
Leverages [epics2web](https://github.com/JeffersonLab/epics2web) to view [EDM](https://ics-web.sns.ornl.gov/edm/eum.html) screens on the web.

Overview
![Overview](/doc/img/Overview.png?raw=true "Overview")

Browse
![Browse](/doc/img/Browse.png?raw=true "Browse")

## EDM Features / Objects
[WEDM Objects](https://github.com/JeffersonLab/wedm/wiki/WEDM-Objects)   
[WEDM Features](https://github.com/JeffersonLab/wedm/wiki/WEDM-Features)

## Installation
   1. Install [epics2web](https://github.com/JeffersonLab/epics2web)
   1. Download wedm.war and drop it into the Tomcat webapps directory
   1. Start Tomcat and navigate your web browser to localhost:8080/wedm
   
## Configuration
The environment variable **EDL_DIR** must be set to the canonical path to the directory containing your EDL files.  If you want the demo EDL files on the overview page to work you need to download ![the demo files](/data/edl/wedm) and place them inside your *EDL_DIR* directory at the subdirectory *wedm*.  Demo files which require an EPICS monitor will need those PVs to exist.
