[//]: # (This readme is partly copied from other extension readmes to ensure consistency in the ExplorViz project)
# ExplorViz-Backend-Extension-VR

This extension adds features to the backend of ExplorViz to enable a multi-user VR-experience. 
The related frontend extension is [explorviz-frontend-extension-vr](https://github.com/ExplorViz/explorviz-frontend-extension-vr).

## Requirements
- [HTC Vive](https://www.vive.com) or [Oculus Rift CV1](https://www.oculus.com/rift/) with controllers and their respective firmware
- [Steam](https://store.steampowered.com/) for [SteamVR](https://store.steampowered.com/steamvr) which enables VR-functionalities
- A powerful computer that can handle VR
- Latest version of [Mozilla Firefox](https://www.mozilla.org/)
- [ExplorViz Backend](https://github.com/ExplorViz/explorviz-backend)
- [ExplorViz Frontend](https://github.com/ExplorViz/explorviz-frontend)
- [ExplorViz Frontend-VR-extension](https://github.com/ExplorViz/explorviz-frontend-extension-vr)

## Installation
1. Follow the [Eclipse Setup](https://github.com/ExplorViz/explorviz-backend#eclipse-setup) of the [ExplorViz Backend](https://github.com/ExplorViz/explorviz-backend)
2. Clone this repository
3. Import project into eclipse: via `Import -> Gradle -> Existing Gradle project -> path/to/explorviz-backend-extension-vr`
4. Start **explorviz-backend-extension-vr** via Eclipse Tab: `Gradle Tasks -> explorviz-backend-extension-vr -> gretty -> appStart`
5. Setup and start [explorviz-frontend](https://github.com/ExplorViz/explorviz-frontend) with the installed [explorviz-frontend-extension-vr](https://github.com/ExplorViz/explorviz-frontend-extension-vr)
