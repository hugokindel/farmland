# Farmland

Farmaland is a management game where you need to take care of your farm while being careful about the economy to stay in business while your competitors will try to put you out of business!

![Farmland logo](https://hk-backup.s3.eu-west-3.amazonaws.com/images/farmland-hr.png)

## Features

TODO

## Architecture

TODO

## How to use

### From sources

First of all, you need to clone the repository by using `git clone git@gaufre.informatique.univ-paris-diderot.fr:hugokindel/farmland.git` or `git clone https://gaufre.informatique.univ-paris-diderot.fr/hugokindel/farmland.git`

#### Compile with an IDE (if it supports Gradle):

1) Open the project's directory in your IDE.
2) Use the `run` gradle task.

#### Compile with the command line:

1) Open a terminal in your project directory.
2) Run gradle wrapper (it will download all dependencies, including gradle itself) `./gradlew build`.
3) You can finally run the project with `./gradlew run`.

### From a binary distribution

If you got a binary release (containing all the libs, assets and the bin directory), you can directly use `./cli` (on UNIX systems) or `./cli.bat` (on Windows).

## Third party libraries

- [LWJGL](https://www.glfw.org/)  
  Gives access to other low level libraries (GLFW, OpenGL, OpenAL).
- [GLFW](https://www.lwjgl.org/)  
  Handles the window and the input.
- [OpenGL](https://www.opengl.org/)  
  Creates the rendering context and a way to communicate with the GPU.
- [OpenAL](https://www.openal.org/)  
  Handles the audio system.
- [ImGui](https://github.com/ocornut/imgui) (with [imgui-java](https://github.com/SpaiR/imgui-java) as a binding)  
  Permits to rapidly develop GUI to use within development for tools.

## Contributors

- [LE CORRE Léo](https://gaufre.informatique.univ-paris-diderot.fr/lecorre)
- [KINDEL Hugo](https://gaufre.informatique.univ-paris-diderot.fr/hugokindel)
- [PAULAS VICTOR Francis](https://gaufre.informatique.univ-paris-diderot.fr/paulasvi)
- [JAUROYON Maxime](https://gaufre.informatique.univ-paris-diderot.fr/jauroyon)

## License

This project is made for educational purposes only and any part of it can be used freely.