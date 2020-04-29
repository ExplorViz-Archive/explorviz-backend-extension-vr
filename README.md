[//]: # (This readme is partly copied from other extension readmes to ensure consistency in the ExplorViz project)
# ExplorViz-Backend-Extension-VR

This extension adds features to the backend of ExplorViz to enable a multi-user VR-experience. 
The related frontend extension can be found [here](https://github.com/ExplorViz/explorviz-frontend-extension-vr).

## Requirements
- [HTC Vive (Pro)](https://www.vive.com) or [Oculus Rift CV1](https://www.oculus.com/rift/) (basically [Oculus Rift S](https://www.oculus.com/rift-s/) is also supported) with controllers and their respective firmware
- A powerful computer that can handle VR
- [Mozilla Firefox](https://www.mozilla.org/) Version 72.0.2
- [ExplorViz Backend](https://github.com/ExplorViz/explorviz-backend/tree/1.5.0) Version 1.5.0
- [ExplorViz Backend Extension VR](https://github.com/ExplorViz/explorviz-backend-extension-vr)
- [ExplorViz Frontend](https://github.com/ExplorViz/explorviz-frontend/tree/1.5.0) Version 1.5.0

## Installation
1. Follow the [README](https://github.com/ExplorViz/explorviz-backend/tree/1.5.0/README.md) of the [ExplorViz Backend](https://github.com/ExplorViz/explorviz-backend/tree/1.5.0)
2. Clone this repository
3. Import project into eclipse: via `Import -> Gradle -> Existing Gradle project -> path/to/explorviz-backend-extension-vr`
4. Start **explorviz-backend-extension-vr** via Eclipse Tab: `Gradle Tasks -> explorviz-backend-extension-vr -> application -> run`
5. Setup and start [ExplorViz Frontend](https://github.com/ExplorViz/explorviz-frontend/tree/1.5.0) with the installed [ExplorViz Frontend Extension VR](https://github.com/ExplorViz/explorviz-frontend-extension-vr)
