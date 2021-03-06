# wedm
The Web Extensible Display Manager leverages [epics2web](https://github.com/JeffersonLab/epics2web) to view [EDM](https://www.slac.stanford.edu/grp/cd/soft/epics/extensions/edm/edm.html) screens on the web.

---
- [Install](https://github.com/JeffersonLab/wedm#install)
- [Build](https://github.com/JeffersonLab/wedm#build)
- [Configure](https://github.com/JeffersonLab/wedm#configure)
- [Docker](https://github.com/JeffersonLab/wedm#docker)
- [See Also](https://github.com/JeffersonLab/wedm#see-also)
---

![Example](https://github.com/JeffersonLab/wedm/raw/master/doc/img/PhoneExample.png?raw=true "Example")


**Overview**   
![Overview](https://github.com/JeffersonLab/wedm/raw/master/doc/img/Overview.png?raw=true "Overview")

**Browse**        
![Browse](https://github.com/JeffersonLab/wedm/raw/master/doc/img/Browse.png?raw=true "Browse")


- [WEDM Features](https://github.com/JeffersonLab/wedm/wiki/WEDM-Features)   
- [WEDM Objects](https://github.com/JeffersonLab/wedm/wiki/WEDM-Objects)   

## Install
   1. Install [epics2web](https://github.com/JeffersonLab/epics2web)
   1. Download [wedm.war](https://github.com/JeffersonLab/wedm/releases) and drop it into the Tomcat webapps directory
   1. Start Tomcat and navigate your web browser to localhost:8080/wedm
   
## Build 
```
git clone https://github.com/JeffersonLab/wedm
cd wedm
gradlew war
```
   
## Configure

### Web Socket Gateway
Use the environment varaible **EPICS_2_WEB_HOST** to specify the hostname (and optionally :port) of the epics2web server.   If undefined, the same host as WEDM is assumed.

### Screen Files Path
The environment variable **EDL_DIR** must be set to the canonical path to the directory containing your EDL files.  This directory (and subdirectories) will be browsable in WEDM.  If you want the demo EDL files on the overview page to work you need to download [the demo files](https://github.com/JeffersonLab/wedm/blob/master/examples/edl) and place them inside your *EDL_DIR* directory at the subdirectory *wedm*.  Demo files which require an EPICS monitor will need those PVs to exist (LOC PVs are used as much as possible to limit this).  The demo files are intended to be used with the JLab [colors.list](https://github.com/JeffersonLab/wedm/blob/master/examples/edl/colors.list).

### Colors File Path
The color palette file is located by searching the following locations in order:
1. **EDMCOLORFILE** environment variable with an absolute path to a file
2. **EDMFILES** environment variable with an absolute path to a directory containing the file "colors.list"
3. Finally the default location of /etc/edm/colors.list

### Screen File Search Path
Similar to EDM, the environment variable **EDMDATAFILES** may be set to a colon-separated list of search paths.
For example, setting `EDMDATAFILES=/main/sub1:/main/sub2` will result in searches for display files in the two
provided folders.  When relative paths are encountered in an EDL file, the **EDL_DIR** path is searched first, then any additional paths specified in **EDMDATAFILES** are searched.

### Accessing Screen Files on Web Server
Some EDM installations share files across a site via a web server.
That way, clients running EDM do not need local or NFS-based file access,
but can access all `*.edl` files from a web server.  WEDM will fetch edl files specified with an absolute HTTP or HTTPS URL, and alternatively can search for files specified with a relative path to a remote server.
Similar to EDM, the environment variable **EDMHTTPDOCROOT** allows WEDM to locate relatively-specified remote files via a web address.  It has to be used in combination with an **EDMDATAFILES** search path, which might have only one `/` entry.

For example, assume `EDMHTTPDOCROOT=http://www.webserver.com/edlfiles` and
`EDMDATAFILES=/main/sub1:/main/sub2`.
Whenever WEDM is now trying to open a file `x.edl`, it will attempt to open  
`http://www.webserver.com/edlfiles/main/sub1/x.edl`
followed by 
`http://www.webserver.com/edlfiles/main/sub2/x.edl`,
using the order provided in the search path,
until it succeeds to find the file.

When `EDMHTTPDOCROOT` is defined, all complete URLs passed to WEDM via `...?edl=http:/...`
must in fact start with the `EDMHTTPDOCROOT`. Other URLs will be rejected to prevent
network attacks which try to use the WEDM host to probe URL access.

Often it is convenient to ignore self-signed certificates.  This can be done by defining the environment variable **WEDM_DISABLE_CERTIFICATE_CHECK** to any value.

### Context Prefix
When proxying WEDM it is sometimes useful to have multiple instances accessible via the same host via separate context paths.  In order to return correct links to resources an instance proxied with a namespacing prefix needs to be aware of the prefix.  The environment variable **CONTEXT_PREFIX** does this.  For example at Jefferson Lab we use a single proxy server for multiple departments each with their own instance of WEDM, and each configured with a prefix such as "/fel", "/chl", "/itf", and "/srf" ("/ops" uses default/empty prefix).


## Docker
```
gradlew build
docker-compose up
```
Image hosted on [DockerHub](https://hub.docker.com/r/slominskir/wedm)

Now navigate to http://localhost:8080/wedm/

**Note:** The build is required because the docker-compose.yml file is setup for development.  If you remove the volume _./build/libs:/user/local/tomcat/webapps_ then you can skip the _gradlew build_ step and the pre-built version of WEDM inside the container will be used, but then you'll not be able to make changes on the fly on your localhost. 

**Note:** If you're running the Docker build inside an organization network with an intercepting SSL proxy you will likely need to insert the organization custom CA certificate.  For example on the Jefferson Lab network builds are run with:
```
docker build -t wedm . --build-arg CUSTOM_CRT_URL=http://pki.jlab.org/JLabCA.crt --no-cache
```


## See Also

  - ["Puddysticks"](https://github.com/JeffersonLab/puddysticks)   
  - [WEDM Technical Notes](https://github.com/JeffersonLab/wedm/wiki/Technical-Notes)      
  - [This work was presented at ICALEPCS 2017](http://icalepcs2017.org/) and the [2017 EPICS workshop](https://indico.esss.lu.se/event/889/session/1/contribution/0)  
