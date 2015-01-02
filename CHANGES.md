CCAutotyper [![Build Status](https://travis-ci.org/Matt529/CCAutotyper.svg)](https://travis-ci.org/Matt529/CCAutotyper)
===========
An Autotyper for ComputerCraft players who use servers that have HTTP Disabled!

###v1.0a to v1.2b
---
####**[Primary]**
- AutoComplete on Location using Ctrl + Space with a history size of 50 locations. Should be plenty.
- Code Confirmation as an optional setting. Brings up a display with syntax highlighting to approve or reject.
- Improved Undo/Redo Ability
- Improved Text Components thanks to RSytanxTextArea and RTextArea as well as AutoComplete

####**[Notable]**
- Information Button now leads to Github Readme Page via Default Browser
- Pre-loads 3 Locations used in testing. 
 * Bubbles! by KingofGamesYami
 * Milkshake GUI Proof of Concept by lednerg
 * Advanced Calculator by Cranium

####**[Minor/Internal]**
- Inclusion of RSyntaxTextArea Library by Fifesoft
- Inclusion of AutoComplete Library by Fifesoft
- Improvements to MemoryUnit.java, adding conversions to and from SI Units and Non-SI Units as well as conversions between SI and Non-SI.
- LocationHandler.URL now does the same HTTP Response check that LocationHandler.PASTE does. 
- System.out is now overridden to ignore any messages that just contain "Yo" which was prompted by RSyntaxTextArea not removing a Test Line. This should not impact Console calls.
- Foundations for a JavaFX GUI Implementation
---

### v0.2b to v1.0a
---
####**[Primary]**
- Initial Swing GUI
- Executables
- Archive Split
- Refined Key Bindings
---

### v0.2a to v0.2b
---
####**[Primary]**
- Downgrade from Java 8 to Java 7 to increase user base
- New Key Bindings to Pasue and Terminate
 * Alt + P to Pause
 * Alt + S to Stop/Terminat
- Alt can be used for a termporary pause
- Inclusion of Native Library (Cross-Platform): JNativeHooks
- Reticulated Splines