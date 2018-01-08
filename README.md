# wedm
Web Extensible Display Manager

## Introduction
Leverages [epics2web](https://github.com/JeffersonLab/epics2web) to view [EDM](https://ics-web.sns.ornl.gov/edm/eum.html) screens on the web.

![Example](/doc/img/PhoneExample.png?raw=true "Example")


Overview
![Overview](/doc/img/Overview.png?raw=true "Overview")

Browse
![Browse](/doc/img/Browse.png?raw=true "Browse")

## EDM Features / Objects
[WEDM Features](https://github.com/JeffersonLab/wedm/wiki/WEDM-Features)   
[WEDM Objects](https://github.com/JeffersonLab/wedm/wiki/WEDM-Objects)   

## Installation
   1. Install [epics2web](https://github.com/JeffersonLab/epics2web)
   1. Download [wedm.war](https://github.com/JeffersonLab/wedm/releases) and drop it into the Tomcat webapps directory
   1. Start Tomcat and navigate your web browser to localhost:8080/wedm
   
## Configuration
### Screen Files Path
The environment variable **EDL_DIR** must be set to the canonical path to the directory containing your EDL files.  If you want the demo EDL files on the overview page to work you need to download ![the demo files](/data/edl/wedm) and place them inside your *EDL_DIR* directory at the subdirectory *wedm*.  Demo files which require an EPICS monitor will need those PVs to exist (LOC PVs are used as much as possible to limit this).  The demo files are intended to be used with the ![JLab colors.list file](/data/edl/wedm/colors.list).
### Colors File Path
The color palette file is located by searching the following locations in order:
1. **EDMCOLORFILE** environment variable with an absolute path to a file
2. **EDMFILES** environment variable with an absolute path to a directory containing the file "colors.list"
3. Finally the default location of /etc/edm/colors.list

## See Also
[WEDM Technical Notes](https://github.com/JeffersonLab/wedm/wiki/Technical-Notes)      
[This work was presented at ICALEPCS 2017](http://icalepcs2017.org/)     
