<!-- Template credit: https://github.com/othneildrew/Best-README-Template -->

<!-- PROJECT LOGO -->
<br />
<p align="center">
  <a href="https://github.com/Rover656/EnderIO-Rewrite">
    <img src="doc/img/enderface.png" alt="Logo" width="80" height="80">
  </a>

  <h3 align="center">EnderIO</h3>

  <p align="center">
    The full-fat tech mod for Minecraft 1.18.
    <br />
    <a href="https://github.com/Rover656/EnderIO-Rewrite/wiki"><strong>Browse the Wiki »</strong></a>
    <br />
    <br />
    <a href="https://www.curseforge.com/minecraft/mc-mods/ender-io">Curseforge</a>
    ·
    <a href="https://discord.gg/sgYk3Jr">Discord</a>
    ·
    <a href="https://github.com/Rover656/EnderIO-Rewrite/issues">Report Bug</a>
    ·
    <a href="https://github.com/Rover656/EnderIO-Rewrite/issues">Request Feature</a>
  </p>
</p>


<!-- TABLE OF CONTENTS -->
<details open="open">
  <summary>Table of Contents</summary>
  <ol>
    <li>
      <a href="#about-the-project">About The Project</a>
      <ul>
        <li><a href="#built-with">Built With</a></li>
      </ul>
    </li>
    <li>
      <a href="#getting-started">Getting Started</a>
      <ul>
        <li><a href="#for-players">For Players</a></li>
        <li><a href="#for-mod-developers">For Mod Developers</a></li>
      </ul>
    </li>
    <li><a href="#contributing">Contributing</a></li>
    <li><a href="#license">License</a></li>
  </ol>
</details>


<!-- ABOUT THE PROJECT -->
## About The Project

[![EnderIO Logo](doc/img/logo.png)](https://www.curseforge.com/minecraft/mc-mods/ender-io)

[![Gradle Build](https://github.com/Rover656/EnderIO-Rewrite/actions/workflows/gradle.yml/badge.svg)](https://github.com/Rover656/EnderIO-Rewrite/actions/workflows/gradle.yml)

TODO


<!-- GETTING STARTED -->
## Getting Started

This is an example of how you may give instructions on setting up your project locally.
To get a local copy up and running follow these simple example steps.

### For Players

Download the latest JAR file from GitHub releases or from [CurseForge](https://www.curseforge.com/minecraft/mc-mods/ender-io) and drop it into your `mods` folder.

### For Mod Developers

EnderIO (along with EnderCore) is available via our maven.

Update your `build.gradle` to contain the foillowing:

```groovy
repositories {
    maven { url 'https://maven.tterrag.com' }
}

dependencies {
    // TODO: Release an API package
    
    runtimeOnly fg.deobf("com.enderio:enderio-base:<VERSION>")
}
```


<!-- CONTRIBUTING -->
## Contributing

TODO: Contributing guidelines


<!-- LICENSE -->
## License

All code (excluding the bundled APIs from other mods, which are covered by their respective licenses) are released without restriction into the public domain under the CC0 1.0 license (http://creativecommons.org/publicdomain/zero/1.0/legalcode) FAQ (https://wiki.creativecommons.org/CC0_FAQ).
Do what you want with it, as long as you smile while doing so. While it is not a requirement, it would be nice to know if it is being used and how, so send me hello to **crazypants.mc at gmail.com**.

### Credits
- CrazyPants
- tterrag
- HenryLoenwind
- MatthiasM
- CyanideX
- EpicSquid
- Rover656
- HypherionSA
- agnor99
- ferriarnus

### Sound Credits

Below sounds are used under [CC BY 3.0](https://creativecommons.org/licenses/by/3.0/) or [CC BY-NC 3.0](https://creativecommons.org/licenses/by-nc/3.0/)

- https://freesound.org/people/Glaneur%20de%20sons/
- https://freesound.org/people/luffy/
- https://freesound.org/people/Anton/
- https://freesound.org/people/pj1s/
- https://freesound.org/people/Syna-Max/
- https://freesound.org/people/Robinhood76/
- https://freesound.org/people/zimbot/
- https://freesound.org/people/LiamG_SFX/
- https://freesound.org/people/kuchenanderung1/
- https://freesound.org/people/170048@virtualwindow.co.za/



<!-- MARKDOWN LINKS & IMAGES -->
<!-- https://www.markdownguide.org/basic-syntax/#reference-style-links -->