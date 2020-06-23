# Songreporter
Songreporter is something like a "plugin" for the software Songbeamer to automatically report displayed songs.
It's written for everyone who is using Songbeamer as their tool to display songtexts and having to report the displayed songs at CCLI.

## Usage
Songreporter has one GUI to collect all the necessary information for reporting. It needs to know your credentials for the online-reporting of CCLI, 
the directory where all the song files (.sng) are saved in, the directory where the scripts (.col) are saved in and the script you want to report 
the songs it contains. Also you need to choose which browser you have installed and after clicking on the "Report" button this browser will open 
and do the reporting for you as long as you don't do anything inside this browser window. All given directories are saved for the next usage and you 
have the option to also save your E-Mail Adress. For safety reasons there is no option to save your password and obviously the script you reported is 
also not saved as I don't think you want to report one script multiple times.

## License
Songreporter is released under the Apache 2.0 license.

Included dependencies are:  
Gson: Copyright 2008 Google Inc.  
Selenium: Copyright 2020 Software Freedom Conservancy (SFC)  
bonigarcia.github.io: Copyright 2020 Boni Garcia.  
