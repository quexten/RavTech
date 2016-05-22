# RavTech
![Alt text](/logo.png?raw=true "")

[![CircleCI](https://circleci.com/gh/Quexten/RavTech.svg?style=svg)](https://circleci.com/gh/Quexten/RavTech)
[![Build Status](https://travis-ci.org/Quexten/RavTech.svg?branch=master)](https://travis-ci.org/Quexten/RavTech)
[![License](http://img.shields.io/:license-mit-blue.svg)](http://mit-license.org)

RavTech is a Game Engine built for rapid, cross-platform, collaborative development.
It's still quite a way to the first stable version.

##Features
- Remote Editing(In Testing)
- Collaborative Editing(In Testing)
- Design once, export to many Platforms
- Plugins
- Lua Scripting

##Platforms
- Windows
- Mac
- Linux
- Android
- iOS
- HTML5

##License
The Engine and Development Kit are Licensed under the MIT License

##How to Build
In the projects root directory, run the command:
```
gradlew development-kit:dist
```
After it is done, navigate to 
```
development-kit/build/libs/
```
and run the generated application in the directory.
