# Magento2 PHPStorm Utilites

![Build](https://github.com/pepinmon/Magento2-Utilities/workflows/Release/badge.svg)
[![Version](https://img.shields.io/jetbrains/plugin/v/15972.svg)](https://plugins.jetbrains.com/plugin/15972)
[![Downloads](https://img.shields.io/jetbrains/plugin/d/15972.svg)](https://plugins.jetbrains.com/plugin/15972)

## Description

<!-- Plugin description -->
This plugin provides utilities that improve the workflow when working with Magento 2 Projects in PHPStorm.

[Documentation](https://github.com/pepinmon/Magento2-Utilities/blob/main/README.md).
<!-- Plugin description end -->

## Works With

- PhpStorm >= 2020.3
- JRE >= 11

## Features

- "Recompile" static files (.less, .css, .js, etc) by deleting only the files selected or currently open in editor from the `/pub/static` and `/var/view_preprocessed` folders. Thus removing the need to run magento cli commands to have the same effect, like running `bin/magento deploy:mode:set developer` or `bin/magento setup:upgrade`.
  - Can be used inside the project view, by selecting which files or folders to "recompile" or in the current open tab in the editor by:
    - Using the provided default keyboard shortcut <kbd>CTRL</kbd>+<kbd>ALT</kbd>+<kbd>R</kbd> (can be customized).
    - Searching for the action in the general search pressing <kbd>⇧</kbd><kbd>⇧</kbd>.
    - In the editor tab context menu.
  
## Installation

- Using IDE built-in plugin system:
  
  <kbd>Preferences</kbd> > <kbd>Plugins</kbd> > <kbd>Marketplace</kbd> > <kbd>Search for "Magento2-Utilities"</kbd> >
  <kbd>Install Plugin</kbd>
 
 
- Manually:
  
  Download the [latest release](https://github.com/pepinmon/Magento2-Utilities/releases/latest) and install it manually using
  <kbd>Preferences</kbd> > <kbd>Plugins</kbd> > <kbd>⚙️</kbd> > <kbd>Install plugin from disk...</kbd>
  
---

### Plugin Developers and Maintainers

[Pedro Faria](https://github.com/PedroFaria99)

[Pedro Monteiro](https://github.com/pedrofernando94)
