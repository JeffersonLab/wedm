# wedm [![Java CI with Gradle](https://github.com/JeffersonLab/wedm/actions/workflows/ci.yml/badge.svg)](https://github.com/JeffersonLab/wedm/actions/workflows/ci.yml)  [![Docker](https://img.shields.io/docker/v/jeffersonlab/wedm?sort=semver&label=DockerHub)](https://hub.docker.com/r/jeffersonlab/wedm)
The Web Extensible Display Manager leverages [epics2web](https://github.com/JeffersonLab/epics2web) to view [EDM](https://www.slac.stanford.edu/grp/cd/soft/epics/extensions/edm/edm.html) screens on the web.

![Example](https://github.com/JeffersonLab/wedm/raw/main/doc/img/PhoneExample.png?raw=true "Example")

---
- [Overview](https://github.com/JeffersonLab/wedm#overview)
- [Quick Start with Compose](https://github.com/JeffersonLab/wedm#quick-start-with-compose)
- [Install](https://github.com/JeffersonLab/wedm#install)
- [Configure](https://github.com/JeffersonLab/wedm#configure)
- [Build](https://github.com/JeffersonLab/wedm#build) 
- [Release](https://github.com/JeffersonLab/wedm#release) 
- [See Also](https://github.com/JeffersonLab/wedm#see-also)
---

## Overview

- [WEDM Features](https://github.com/JeffersonLab/wedm/wiki/WEDM-Features)   
- [WEDM Objects](https://github.com/JeffersonLab/wedm/wiki/WEDM-Objects)   

## Quick Start with Compose 
1. Grab project
```
git clone https://github.com/JeffersonLab/wedm
cd wedm
```
2. Launch [Compose](https://github.com/docker/compose)
```
docker compose up
```
3. Navigate to example displays via web browser   

http://localhost:8080/wedm

**See**: [Docker Compose Strategy](https://gist.github.com/slominskir/a7da801e8259f5974c978f9c3091d52c)

## Install
   1. Install [epics2web](https://github.com/JeffersonLab/epics2web)
   1. Download [wedm.war](https://github.com/JeffersonLab/wedm/releases) and drop it into the Tomcat webapps directory
   1. Start Tomcat and navigate your web browser to localhost:8080/wedm
   
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

### Relative path support
EDM versions from 1-12-105J on (ca. June 2021) use this environment variable
to enable support for relative path names:

```
EDMRELATIVEPATHS=yes
```

When relative paths are enabled, the names of embedded displays, images and
links to related displays can be resolved relative to the display which contains them.

By default, WEDM will test access to each relative path and otherwise fall back to
the search path.
These access checks take considerable time.
If a site uses relative path names and no longer relies on a search path,
these checks can be disabled via

```
WEDM_DISABLE_RELATIVEPATHS_CHECK=yes
```

When both `EDMRELATIVEPATHS` and `WEDM_DISABLE_RELATIVEPATHS_CHECK` are set to `yes`,
all file references are assumed to be relative without checking access.

### Context Prefix
When proxying WEDM it is sometimes useful to have multiple instances accessible via the same host via separate context paths.  In order to return correct links to resources an instance proxied with a namespacing prefix needs to be aware of the prefix.  The environment variable **CONTEXT_PREFIX** does this.  For example at Jefferson Lab we use a single proxy server for multiple departments each with their own instance of WEDM, and each configured with a prefix such as "/fel", "/chl", "/itf", and "/srf" ("/ops" uses default/empty prefix).

## Build 
This project is built with [Java 17](https://adoptium.net/) (compiled to Java 8 bytecode), and uses the [Gradle 7](https://gradle.org/) build tool to automatically download dependencies and build the project from source:

```
git clone https://github.com/JeffersonLab/wedm
cd wedm
gradlew build
```
**Note**: If you do not already have Gradle installed, it will be installed automatically by the wrapper script included in the source

**Note for JLab On-Site Users**: Jefferson Lab has an intercepting [proxy](https://gist.github.com/slominskir/92c25a033db93a90184a5994e71d0b78)

**See**: [Docker Development Quick Reference](https://gist.github.com/slominskir/a7da801e8259f5974c978f9c3091d52c#development-quick-reference)

## Release
1. Bump the version number and release date in build.gradle and commit and push to GitHub (using [Semantic Versioning](https://semver.org/)).   
2. Create a new release on the GitHub [Releases](https://github.com/JeffersonLab/wedm/releases) page corresponding to same version in build.gradle (Enumerate changes and link issues).   Run war Gradle build target and attach war to release.
3. Build and push [Docker image](https://gist.github.com/slominskir/a7da801e8259f5974c978f9c3091d52c#8-build-an-image-based-of-github-tag).  DockerHub is configured to do this automatically on git push of semver tag (typically part of GitHub release).  
4. Bump and commit quick start [image version](https://github.com/JeffersonLab/wedm/blob/main/docker-compose.override.yml)

## See Also

  - ["Puddysticks"](https://github.com/JeffersonLab/puddysticks)   
  - [WEDM Technical Notes](https://github.com/JeffersonLab/wedm/wiki/Technical-Notes)      
  - [This work was presented at ICALEPCS 2017](http://icalepcs2017.org/) and the [2017 EPICS workshop](https://indico.esss.lu.se/event/889/session/1/contribution/0)  
