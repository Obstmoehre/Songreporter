# Songreporter
Songreporter is something like a "plugin" for the software Songbeamer to automatically report displayed songs.
It's written for everyone who is using Songbeamer as their tool to display songtexts and having to report the displayed songs at CCLI.

## Usage
<img src="https://github.com/Obstmoehre/Songreporter/blob/master/Songreporter%20Main%20GUI.png" />

Songreporter has one GUI (see the screenshot above) to collect all the necessary information for reporting. It needs to know your credentials for the online-reporting of CCLI, 
the directory where all the song files (.sng) are saved in, the directory where the scripts (.col) are saved in and the script you want to report 
the songs it contains. Also you need to choose which browser you have installed and after clicking on the "Report" button this browser will open 
and do the reporting for you as long as you don't do anything inside this browser window. All given directories are saved for the next usage and you 
have the option to also save your E-Mail Adress. For safety reasons there is no option to save your password and obviously the script you reported is 
also not saved as I don't think you want to report one script multiple times. After reporting the script it gets marked as reported and if you select a 
script previously reported by the program the name will be shown in green. If the name of your script files contain a date in this format [yyyy-mm-dd] 
the program can detect an unreported script of the last seven days.

**important**: The songs have to contain their ccli-songnumber as an attribute, because this is the way the "plugin" recognizes the songs and searches them
at the Online Reporting webpage.

## License
Songreporter is released under the Apache 2.0 license.

```
Copyright 2020 Obstmoehre

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```

Included dependencies are:  
Gson: Copyright 2008 Google Inc.  
Selenium: Copyright 2020 Software Freedom Conservancy (SFC)  
bonigarcia.github.io: Copyright 2020 Boni Garcia.  
