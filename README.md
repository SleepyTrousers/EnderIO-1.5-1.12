EnderIO [![Build Status](http://ci.tterrag.com/buildStatus/icon?job=EnderIO)](http://ci.tterrag.com/job/EnderIO/)
=======

All code (excluding the bundled APIs from other mods, which are covered by their respective licenses) are released without restriction into the public domain under the CC0 1.0 license (http://creativecommons.org/publicdomain/zero/1.0/legalcode) FAQ (https://wiki.creativecommons.org/CC0_FAQ).
Do what you want with it, as long as you smile while doing so. While it is not a requirement, it would be nice to know if it is being used and how, so send me hello to **crazypants.mc at gmail.com**.



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

### How to compile

1. Clone this repository.
2. Download the matching EnderCore deobf jar from https://maven.tterrag.com/ and place it in `lib`.
3. Duplicate `user.properties.example` and rename it to `user.properties` in the root folder of this repository.
4. `./gradlew setupDecompWorkspace`
5. `./gradlew build`
