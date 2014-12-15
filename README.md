EnderIO
=======

All code (excluding the buindled APIs from other mods, which are covered by their respecitive licenses) are released without restriction into the public domain under the CC0 1.0 liscence (http://creativecommons.org/publicdomain/zero/1.0/legalcode) FAQ (https://wiki.creativecommons.org/CC0_FAQ).
Do what you want with it, as long as you smile while doing so. While it is not a requirement, it would be nice to know if it is being used and how, so send me hello to **crazypants.mc at gmail.com**.



###Sound Credits

Most sound attributed under Creative Commons Attribution 3.0 Unported (CC BY 3.0)

Credits:
- http://freesound.org/people/Glaneur%20de%20sons/
- http://freesound.org/people/luffy/
- http://freesound.org/people/Anton/
- http://freesound.org/people/pj1s/
- http://freesound.org/people/Syna-Max/


###Contributing

#####[**Fork**](https://github.com/CrazyPants/EnderIO/fork) the [CrazyPants/EnderIO](https://github.com/CrazyPants/EnderIO) repository.

#####Clone your fork of EnderIO to your local machine.
`git clone https://github.com/YOUR_USERNAME/EnderIO.git`

#####cd into the EnderIO project directory
`cd EnderIO`

#####Use gradle to setup forge and deobfuscate minecraft
For Eclipse:
`gradlew setupDecompWorkspace --refresh-dependencies eclipse`

For IntelliJ IDEA:
`gradlew setupDecompWorkspace --refresh-dependencies idea`

#####Set CrazyPants/EnderIO as the remote upstream
`git remote add upstream https://github.com/CrazyPants/EnderIO.git`

#####Open the project in your IDE.
For Eclipse, Open the *EnderIO/eclipse/* directory as your workspace.

For IntelliJ IDEA, use **File > Import Project** (or "Open Project" from the main window) and select the file *build.gradle*

`git checkout -b myEnderImprovements`

Write your code and improve EnderIO. Once you are satisfied with your code, merge to master

`git checkout master`

`git merge myEnderImprovements`

#####Keep the local copy of your fork in sync with CrazyPants upstream

`git fetch upstream`

`git merge upstream/master`

#####Commit your changes and push to github
`git push origin master`

#####Send a PR (Pull Request)
Go to your fork page on github and click the green "*Compare, review, create a pull request*" button.