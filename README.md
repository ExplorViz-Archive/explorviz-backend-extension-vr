[//]: # (This readme is partly copied from other extension readmes to ensure consistency in the ExplorViz project)
# ExplorViz-Backend-Extension-VR

This extension adds features to the backend of ExplorViz to enable a multi-user VR-experience. 
The related frontend extension is [explorviz-frontend-extension-vr](https://github.com/ExplorViz/explorviz-frontend-extension-vr).

## Requirements
- [HTC Vive](https://www.vive.com) or [Oculus Rift CV1](https://www.oculus.com/rift/) with controllers and their respective firmware
- [Mozilla Firefox](https://www.mozilla.org/) Version 72.0.2
- [ExplorViz Backend](https://github.com/ExplorViz/explorviz-backend) Version 1.5.0
- [ExplorViz Frontend](https://github.com/ExplorViz/explorviz-frontend) Version 1.5.0
- [ExplorViz Frontend-VR-extension](https://github.com/ExplorViz/explorviz-frontend-extension-vr)

## Installation
1. Follow the [README](https://github.com/ExplorViz/explorviz-backend) of the [ExplorViz Backend](https://github.com/ExplorViz/explorviz-backend)
2. Clone this repository
3. Import project into eclipse: via `Import -> Gradle -> Existing Gradle project -> path/to/explorviz-backend-extension-vr`
4. Start **explorviz-backend-extension-vr** via Eclipse Tab: `Gradle Tasks -> explorviz-backend-extension-vr -> application -> run`
5. Setup and start [explorviz-frontend](https://github.com/ExplorViz/explorviz-frontend) with the installed [explorviz-frontend-extension-vr](https://github.com/ExplorViz/explorviz-frontend-extension-vr)
