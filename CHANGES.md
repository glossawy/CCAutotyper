CCAutotyper [![Build Status](https://travis-ci.org/Matt529/CCAutotyper.svg)](https://travis-ci.org/Matt529/CCAutotyper)
===========
An Autotyper for ComputerCraft players who use servers that have HTTP Disabled!

###v1.2b to v2.0a
---
####**[Primary]**
- Completely new JavaFX GUI Branch. A Much more native looking GUI Toolkit, Swing is now the alternative.
- Re-worked Code Completion. 
- Now uses CodeMirror for Syntax Highlighting. Only if an Internet Connection is available.
- Delegated Swing to an alternative if JavaFX is not available.
- Code Clean Up, new Metadata Objects
- Resolves Issues #1 and #2, Fixes a Fatal NPE Bug

####**[Notable]**
- Further Fragmentation: New x86 and x64 builds for windows, now bundling JRE 8. ZIP now has .exe and .sh
- Modes: Used for syntax highlighting in CodeMirror. See 'modes.properties' file. 

####**[Minor/Internal]**
- Inclusion of JFXRT for 1.7
- New @FXParseable(value=tag), @SwingCompatible, @FXCompatible, and @InDev(since=origVersion, lastUpdate=lastVersionToChange, author=lastUpdater) annotations
 * @FXParseable(value=tag) -- Indicates a NodeParser compatible object, must be a Node with a no-arg constructor that you can implement inline with text.
 * @SwingCompatible -- Indicates that an object can be intertwined with Swing code (Event Dispatcher Thread compatible)
 * @FXCompatible -- Indicates that an object can be intertwined with JavaFX code (JavaFX Application Thread compatible)
 * @Indev(since, lastUpdate, author) -- Indicates an object actively in development and that may be unstable/changing
- Resource Management via Strings.Resources using Strings.Resources.Resource
- Splines Reticulated

---

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
