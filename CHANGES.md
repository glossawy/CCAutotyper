CCAutotyper [![Build Status](https://travis-ci.org/Matt529/CCAutotyper.svg)](https://travis-ci.org/Matt529/CCAutotyper)
===========
An Autotyper for ComputerCraft players who use servers that have HTTP Disabled!

###v2.1.1 to v3.0.0 -- Minification and Polish Update
---
####**[Primary]**
- New 'scripts' directory in which any non-java scripts will be stored for use later on
 * Primarily included as of this update to allow for use of luamin
- Minification of Lua Code is now possible (to reduce character count and speed up the typing process) using luamin through nashorn
- Files downloaded from Pastebin or some URL are now stored in a '.cctyper-cache' hidden directory (Up to 20 files are stored)
 * This likely warrants an uninstall script to handle removing any hidden directories/files
- Fixes for autotyping in which Multi-line comments and in-line comments were not handled correctly
 * This was likely due to API restructuring and not an ages old bug
- Small fix to try and ensure that icons appear in Ubuntu-based OS' on load, not on show
- Multiple URL (non-pastebin) files are now cached, moved away from calling them all "cc-auto-file" and now uses a random crc32c based naming system

####**[Notable]**
- Cleaned up certain text snafus
- Added a minification checkbox which has it's state saved between each instance
- Added tooltips to certain things of importance such as the minification check box, location field, menubar buttons (top left)
- Code Preview now loads Minified code instead of original code so that the user may deem if the Minification was worth it or too dangerous
- New links to the Github Page and the Developer Website on the Copyright dialog

####**[Minor/Internal]**
- New 'minify' package where any minification APIs will reside, currently this is all centralized in a single 'Minifier' class that uses luamin to minify Lua Code using the Nashorn Scripting Engine. 
- Moved setAppIcons to Resources instead of FXAutotyperWindow
- Further documentation effort... hopefully some day....
- Reticulated splines
- New TimedTooltip that allows modificaiton of the Open Time (time between hover and showing the tooltip) using Reflection to create a TooltipBehavior
- New WebLink that puts the basic function of a Hyperlink in one call. Creates a Hyperlink with a URL location to go to using HostServices or something similar
- More debug information as well as more readable documentation (i.e. Loading and Saving Preferences)
- FXGuiUtils.buildLongAlert now allows you to specify a list of Nodes to include at the bottom of the dialog
- GuiAccessors must now provide a openSite method that opens a URL in a web browser
- TypingMethodology fixed to handle spaces, comments and multi-line comments correctly
- Console.empty() now prints an empty line
- Web Files (non-pastebin) are now stored in the cache using a Random filename that is dervied from the result of CRC32C and then turned into a hex string
 * Much better than just "cc-auto-file"
- Cleaned up legacy stuff, especially in the console interface. Swing needs to be cleaned.
- Updated libraries as necessary and ensured compatibility

---

###v2.1.0 to v2.1.1 (too few visible changes to warrant a minor update)
---
####**[Noticeable]**
- General improvements to AutoCompletion (All those things stated in last update now actually work)
- Alt, Alt-P and Alt-S were broken at some stage in my last cleanup, now they work as expected
- Updating to New Versions now cause all Preferences to be overwritten to avoid incompatible KV Pairs
 * This means that the new version will not remember your previous settings at all
- AutoCompletion no longer does a prefix lookup, now it searches for any occurrence at any point in the result.
 * i.e. Typing 'ste' will return all 'Pastebin' results since 'ste' appears in the middle of Pastebin

####**[Codebase]**
- Continued Cleanup and Conversion to clean Java 8 Functional Idioms
- The 'caused' flag no longer does anything in AutoCompleteTextField, that was replaced with a Key EventHandler and Mouse EventHandler attached to the ListView
- AutoCompleteTextField is just generally nicer to read since more procedures have been moved to their own methods
- SwingKeyboard and FXKeyboard now implement a Queue to allow interaction with the JNativeHooks Dispatch Thread
- More To-Do's as clean-up continues. Might repalce AutoCompletion with ControlsFX. Why re-invent the wheel?
- AutoCompletion requires a re-structuring or removal, it is not very plug-and-play. (too few interfaces)
- Continued attempts to remove Static Variables and the horrible Singleton Pattern... 
- Removed useless @InDev annotation, Renamed InformedOutcome to Outcome, Other Renamings (see commit)
- Moved all popup.show() calls to AutoCompleteTextField.showPopup(), same for popup.hide() and hidePopup()
- All calls in FXAutotyperWindow from AutoCompeleteTextField.addData are filtered through saveToHistory now
 * Prevents Duplicate Entries in AutoCompleteTextField
- Checking if a location already is saved is now done through Google Commons' Hashing Functions
- Continued Documentation Effort


---

###v2.0a to v2.1.0
---
####**[Primary]**
- New Versioning. MAJOR.MINOR.REVISION instead of MAJOR.MINOR[buildCharacter]
- Cleaner, likely more expected, way of handling typing tasks. Uses JavaFX Concurrency tools and hides the non-responsive window.
- New Scheme for Easier Sorting and Identification of Locations, only used for Locations in Location History
 * New "Web URL:" tag
 * New "Local File:" tag
 * New "Pastebin:" tag
- Auto-Completion Results should no longer return empty lists
- Now Deploying as a total Java 1.8 Application. Windows Users need not worry since the JRE is bundled for them.

####**[Notable]**
- Autotyper Window now is hidden while autotyping is occurring.
- Design for ways of notifying the user via countdown or something similar is underway.

####**[Minor/Internal]**
- JFXRT removed since Java 8 support for Eclipse should mean it is no longer required (for Luna at least)
 * Main Development Environment moved to IntelliJ IDEA
- Input Delay, Wait Time and Current File now stored as JavaFX Properties instead of instance variables
- New FXAutoTypingTask object that extends JavaFX's Task to more logically be executed "sort of asynchronously" (as in not at all). 
 * Since the Glass UI Robot must be executed on the Event Thread, we are limited in solutions unless we rollback to the limited AWT Robot
- LocationHandlers now require a Tag. This is exactly as described above and includes the semicolon at the end. 
- New Builder.setSize(double, double) method for FXOptionPane to ensure that the dialog will be a specific size.
- AutoComplete should now recognize that listView.getItems().size() may be zero and will not display the popup in that case.

---

###v2.0a Patch
---
####**[Notes]**
- Resolves Issue #3, Fixes Fatal UI Thread Bug
- Moves Autotyping on to the main UI Thread. The GUI will be unresponsive during autotyping.
 * Not a huge deal, Input was blocked anyway when it was run off of the UI thread before.
- Now released with JRE 1.8.0 update 20. Update 25 broke Auto-Completion due to a JavaFX change.
 * Will be resolved in a future update.
- The Do/Do Not Button actually does things now.
 
####**[Internal]**
- FXKeyboard now executes on the main JavaFX Application Thread instead of on a background thread. This should be resolved later.
 * This will make the GUI unresponsive but that is not a huge change since setInputDisabled is called before execution anyway.
- Keyboard now executed as a JavaFX Concurrent Task so it can be checked for completion.
- Bundling JRE 1.8.0 update 20 since a bug in the latest version of JFX caused automatic completion (unexpectedly) due to a change in how the scene
graph synchronization works. This should be fixed in a later version to fit in this new framework. [TODO]
- the Do/Do Not Button (referred to as cBtn) now has a selectedProperty ChangeListener that actually affects doConfirm now.
- setInput changed to setInputDisabled for FXAutotyperWindow
 
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
- Many Changes, Please See Complete Commit Log
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
