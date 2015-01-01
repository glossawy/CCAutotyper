CCAutotyper [![Build Status](https://travis-ci.org/Matt529/CCAutotyper.svg)](https://travis-ci.org/Matt529/CCAutotyper)
===========
An Autotyper for ComputerCraft players who use servers that have HTTP Disabled!


### Quick Start/How To
---
To execute CCAutotyper you have several options but the basic structure is as follows:
```
java -jar ccautotyper.jar [file|url|paste|gui] <location> [-wait <val>] [-inDelay <val>]
```
If you don't want to use the Windows Executable or Shell File then you can open the gui using
```
java -jar ccautotyper.jar gui
```
and a basic example of a command line execution would be
```
java -jar ccautotyper.jar paste <pastebin_code>
```

### Parameters
---
| Parameter  | Description |
| ------------- | ------------- |
| **_file_**  | The Location provided exists on the local filesystem  |
| **_url_**   | The Location provided exists at a web location (must be downloaded)  |
| **_paste_** | The Location provided is the pastebin code leading to the patebin file |
| **_gui_** | No Location is provided, simply open the GUI |
| **_location_** | Location to the file you wish to autotype. As a Path (Relative or Absolute) for **_file_**; a Web URL for **_url_**; or pastebin code for _**paste**_|
| **_[-wait]_** | Optional. Set the number of seconds the program should wait before typing. The parameter should be formatted as ```-wait t``` to wait _t_ seconds. **_[Default: 10]_**|
| **_[-inDelay]_** | Optional. Set the number of milliseconds to wait between key strokes. The parameter should be formatted as ```-inDelay t``` to wait _t_ milliseconds **_[Default: 40]_**|

The Required Parameters must be in the order ```[file|url|paste|gui] <location>``` but the optional parameters can be in any order as long as they are formatted correctly and come after the required parameters. i.e. 
```
java -jar ccautotyper.jar [file|url|paste|gui] <location> [Optional Parameters...]
```

### Contributions
---

Contribution is encouraged and welcomed. Pull Requests must be properly formatted and must follow common Java idioms and conventions.  If the PR is not satisfactory, not detailed or superfluous/poor/unreadable then it will be rejected promptly. 

A proper response will be provided to any and all PRs upon closing or approval. Commit history should be clear and detailed while concise. Documentation is recommended.

All of this applies to wiki contributions as well.

---
### Issues? Comments? Questions?
---

Feel free to contact me personally at _matthewcrocco@gmail.com_ or on github. Correspondence should be limited to comments and technical questions. Issues should be reported appropriately on the GitHub Issues page.

If an issue goes unresolved for an extended period of time then you may attempt personal correspondence.